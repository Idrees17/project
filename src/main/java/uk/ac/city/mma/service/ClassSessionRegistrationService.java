package uk.ac.city.mma.service;

import uk.ac.city.mma.repository.ClassSessionRegistrationRepository;

import java.util.List;

public class ClassSessionRegistrationService {

    private ClassSessionRegistrationRepository repository = new ClassSessionRegistrationRepository();

    public void registerMemberForSession(int sessionId, int memberId) {
        if (!repository.isRegistered(sessionId, memberId)) {
            repository.registerMember(sessionId, memberId);
        }
    }

    public boolean isRegistered(int sessionId, int memberId) {
        return repository.isRegistered(sessionId, memberId);
    }

    public List<Integer> getRegisteredSessionIdsForMember(int memberId) {
        return repository.getRegisteredSessionIdsForMember(memberId);
    }

    public void unregisterMemberFromSession(int sessionId, int memberId) {
        repository.unregisterMember(sessionId, memberId);
    }

    public int getRegistrationCount(int sessionId) {
        return repository.getRegistrationCount(sessionId);
    }
}