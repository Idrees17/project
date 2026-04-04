package uk.ac.city.mma.service;

import uk.ac.city.mma.model.ClassSession;
import uk.ac.city.mma.model.GenerationRequest;
import uk.ac.city.mma.repository.ClassSessionRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimetableService {

    private ClassSessionRepository sessionRepo = new ClassSessionRepository();
    private List<String> lastGenerationMessages = new ArrayList<>();

    public List<String> getLastGenerationMessages() {
        return lastGenerationMessages;
    }

    public List<String> generateMultiple(List<GenerationRequest> requests) {

        List<String> messages = new ArrayList<>();

        // Clear previous generated timetable only
        sessionRepo.deleteGeneratedSessions();
        messages.add("Previous generated sessions were cleared before creating the new repeating timetable.");

        for (GenerationRequest req : requests) {

            if (req.sessionsPerWeek > 5) {
                messages.add("Could not place class ID " + req.classId +
                        " (" + req.skillLevel + "): cannot schedule more than 5 repeating weekly sessions without repeating a day.");
                continue;
            }

            List<String> targetDays = getSpreadDays(req.sessionsPerWeek);
            Set<String> usedDaysForThisConfig = new HashSet<>();

            int createdForThisConfig = 0;

            for (String day : targetDays) {

                if (usedDaysForThisConfig.contains(day)) {
                    continue;
                }

                LocalTime after = LocalTime.parse(req.afterTime);
                LocalTime before = LocalTime.parse(req.beforeTime);

                LocalTime candidateStart = after;
                LocalTime latestStart = before.minusMinutes(req.durationMinutes);

                boolean placed = false;

                while (!candidateStart.isAfter(latestStart)) {

                    if (!hasConflict(day, candidateStart, req.durationMinutes,
                            req.coachName, req.roomName)) {

                        ClassSession session = new ClassSession();
                        session.setClassId(req.classId);
                        session.setDayOfWeek(day);
                        session.setStartTime(candidateStart.format(DateTimeFormatter.ofPattern("HH:mm")));
                        session.setDurationMinutes(req.durationMinutes);
                        session.setCoachName(req.coachName);
                        session.setRoom(req.roomName);
                        session.setGenerated(true);

                        sessionRepo.createSession(session);

                        usedDaysForThisConfig.add(day);
                        createdForThisConfig++;
                        placed = true;
                        break;
                    }

                    candidateStart = candidateStart.plusMinutes(30);
                }

                if (!placed) {
                    messages.add("Could not place one " + req.skillLevel +
                            " session for class ID " + req.classId +
                            " on " + day +
                            " between " + req.afterTime + " and " + req.beforeTime +
                            " using coach " + req.coachName +
                            " and room " + req.roomName + ".");
                }
            }

            messages.add("Created " + createdForThisConfig + " out of " +
                    req.sessionsPerWeek + " requested sessions for class ID " +
                    req.classId + " (" + req.skillLevel + ").");
        }

        lastGenerationMessages = messages;
        return messages;
    }

    private List<String> getSpreadDays(int sessionsPerWeek) {

        List<String> allDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

        switch (sessionsPerWeek) {
            case 1:
                return List.of("Wednesday");
            case 2:
                return List.of("Tuesday", "Thursday");
            case 3:
                return List.of("Monday", "Wednesday", "Friday");
            case 4:
                return List.of("Monday", "Tuesday", "Thursday", "Friday");
            case 5:
                return allDays;
            default:
                return Collections.emptyList();
        }
    }

    private boolean hasConflict(String day, LocalTime newStart, int newDuration,
                                String coachName, String roomName) {

        List<ClassSession> existingSessions = sessionRepo.getSessionsByDay(day);

        LocalTime newEnd = newStart.plusMinutes(newDuration);

        for (ClassSession existing : existingSessions) {

            LocalTime existingStart = LocalTime.parse(existing.getStartTime());
            LocalTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());

            boolean overlaps = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);

            if (!overlaps) {
                continue;
            }

            boolean sameCoach = existing.getCoachName().equalsIgnoreCase(coachName);
            boolean sameRoom = existing.getRoom().equalsIgnoreCase(roomName);

            if (sameCoach || sameRoom) {
                return true;
            }
        }

        return false;
    }
}