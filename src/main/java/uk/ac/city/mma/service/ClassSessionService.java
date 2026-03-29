package uk.ac.city.mma.service;

import uk.ac.city.mma.model.ClassSession;
import uk.ac.city.mma.repository.ClassSessionRepository;

import java.util.List;

public class ClassSessionService {

    private ClassSessionRepository repository = new ClassSessionRepository();

    public void createSession(int classId, String day, String time,
                              int durationMinutes, String coach, String room) {

        ClassSession session = new ClassSession();
        session.setClassId(classId);
        session.setDayOfWeek(day);
        session.setStartTime(time);
        session.setDurationMinutes(durationMinutes);
        session.setCoachName(coach);
        session.setRoom(room);
        session.setGenerated(false);

        repository.createSession(session);
    }

    public List<ClassSession> getAllSessions() {
        return repository.getAllSessions();
    }

    public List<ClassSession> getSessionsByDay(String day) {
        return repository.getSessionsByDay(day);
    }

    public void deleteSession(int sessionId){
        repository.deleteSession(sessionId);
    }

    public void updateSession(int id, String day, String time,
                              int durationMinutes, String coach, String room) {
        repository.updateSession(id, day, time, durationMinutes, coach, room);
    }
}