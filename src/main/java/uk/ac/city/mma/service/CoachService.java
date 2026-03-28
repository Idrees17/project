package uk.ac.city.mma.service;

import uk.ac.city.mma.model.Coach;
import uk.ac.city.mma.repository.CoachRepository;

import java.util.*;

public class CoachService {

    private CoachRepository repo = new CoachRepository();

    public void addCoach(String name, String specialty) {
        repo.addCoach(new Coach(0, name, specialty));
    }

    public List<Coach> getAllCoaches() {
        return repo.getAllCoaches();
    }

    public void deleteCoach(int coachId) {
        repo.deleteCoach(coachId);
    }

    public Coach getCoachById(int coachId) {
        return repo.getCoachById(coachId);
    }

    public void updateCoach(int coachId, String name, String specialty) {
        repo.updateCoach(coachId, name, specialty);
    }
}