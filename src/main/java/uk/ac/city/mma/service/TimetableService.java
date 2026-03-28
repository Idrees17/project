package uk.ac.city.mma.service;

import uk.ac.city.mma.model.ClassSession;
import uk.ac.city.mma.model.GenerationRequest;
import uk.ac.city.mma.repository.ClassSessionRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimetableService {

    private ClassSessionRepository sessionRepo = new ClassSessionRepository();

    public void generateMultiple(List<GenerationRequest> requests) {

        for (GenerationRequest req : requests) {

            if (req.sessionsPerWeek > 5) {
                throw new IllegalArgumentException("A repeating weekly timetable cannot place the same class/skill on more than 5 days.");
            }

            List<String> targetDays = getSpreadDays(req.sessionsPerWeek);
            Set<String> usedDaysForThisConfig = new HashSet<>();

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

                        sessionRepo.createSession(session);

                        usedDaysForThisConfig.add(day);
                        placed = true;
                        break;
                    }

                    candidateStart = candidateStart.plusMinutes(30);
                }

                if (!placed) {
                    System.out.println("Could not place session for classId " + req.classId + " on " + day);
                }
            }
        }
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