package uk.ac.city.mma.service;

import uk.ac.city.mma.model.MemberProfile;
import uk.ac.city.mma.repository.ClassSessionRegistrationRepository;

import java.util.List;

public class ClassSessionRegistrationService {

    private ClassSessionRegistrationRepository repository = new ClassSessionRegistrationRepository();

    public void registerMemberForSession(int sessionId, int memberId, String weekStartDate) {
        if (!repository.isRegistered(sessionId, memberId, weekStartDate)) {
            repository.registerMember(sessionId, memberId, weekStartDate);
        }
    }

    public void unregisterMemberFromSession(int sessionId, int memberId, String weekStartDate) {
        repository.unregisterMember(sessionId, memberId, weekStartDate);
    }

    public boolean isRegistered(int sessionId, int memberId, String weekStartDate) {
        return repository.isRegistered(sessionId, memberId, weekStartDate);
    }

    public List<Integer> getRegisteredSessionIdsForMemberAndWeek(int memberId, String weekStartDate) {
        return repository.getRegisteredSessionIdsForMemberAndWeek(memberId, weekStartDate);
    }

    public int getRegistrationCountForWeek(int sessionId, String weekStartDate) {
        return repository.getRegistrationCountForWeek(sessionId, weekStartDate);
    }

    public List<MemberProfile> getRegistrantsForSessionAndWeek(int sessionId, String weekStartDate) {
        return repository.getRegistrantsForSessionAndWeek(sessionId, weekStartDate);
    }
}