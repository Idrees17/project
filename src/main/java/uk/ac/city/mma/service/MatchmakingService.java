package uk.ac.city.mma.service;

import uk.ac.city.mma.model.EventRegistration;
import uk.ac.city.mma.model.Match;
import uk.ac.city.mma.model.MemberProfile;
import uk.ac.city.mma.repository.EventRegistrationRepository;
import uk.ac.city.mma.repository.MatchRepository;
import uk.ac.city.mma.repository.MemberProfileRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchmakingService {

    private EventRegistrationRepository registrationRepository = new EventRegistrationRepository();
    private MemberProfileRepository memberProfileRepository    = new MemberProfileRepository();
    private MatchRepository matchRepository                    = new MatchRepository();
    private List<String> lastGenerationMessages                = new ArrayList<>();

    public List<String> generateMatchesForEvent(int eventId) {

        List<String> messages = new ArrayList<>();

        matchRepository.deleteMatchesForEvent(eventId);
        messages.add("Previous generated matches for this event were cleared.");

        List<EventRegistration> registrations = registrationRepository.getRegistrationsForEvent(eventId);
        List<CompetitionEntry> entrants = new ArrayList<>();

        for (EventRegistration reg : registrations) {
            MemberProfile profile = memberProfileRepository.getProfileByMemberId(reg.getMemberId());
            if (profile != null) {
                CompetitionEntry entry = new CompetitionEntry();
                entry.memberId         = profile.getMemberId();
                entry.firstName        = profile.getFirstName();
                entry.lastName         = profile.getLastName();
                entry.age              = profile.getAge();
                entry.weightKg         = profile.getWeightKg();
                entry.chosenMartialArt = reg.getChosenMartialArt();
                entry.experienceLevel  = reg.getExperienceLevel();
                entrants.add(entry);
            }
        }

        if (entrants.size() < 2) {
            messages.add("Not enough entrants to generate matches.");
            lastGenerationMessages = messages;
            return messages;
        }

        /*
        STEP 1 — Build all valid candidate pairs
        */
        List<CandidatePair> candidates = new ArrayList<>();

        for (int i = 0; i < entrants.size(); i++) {
            for (int j = i + 1; j < entrants.size(); j++) {

                CompetitionEntry a = entrants.get(i);
                CompetitionEntry b = entrants.get(j);

                if (!safeEquals(a.chosenMartialArt, b.chosenMartialArt)) continue;
                if (!isCompatibleExperience(a.experienceLevel, b.experienceLevel)) continue;

                double weightDiff = Math.abs(a.weightKg - b.weightKg);
                if (weightDiff > 15.0) continue;

                int expGap   = Math.abs(experienceRank(a.experienceLevel) - experienceRank(b.experienceLevel));
                double ageDiff = Math.abs(a.age - b.age);

                /*
                Composite fitness score — lower is better.
                Weight difference is worth 10x age difference,
                experience gap is penalised at 5kg equivalent.
                */
                double fitness = (weightDiff * 10.0) + (expGap * 50.0) + ageDiff;

                candidates.add(new CandidatePair(i, j, fitness, weightDiff));
            }
        }

        /*
        STEP 2 — Sort all candidate pairs globally by fitness (best first)
        */
        candidates.sort(Comparator.comparingDouble(p -> p.fitness));

        /*
        STEP 3 — Pick pairs from the sorted list
        Skip any pair where either entrant is already matched.
        */
        boolean[] used = new boolean[entrants.size()];
        int createdMatches = 0;

        for (CandidatePair pair : candidates) {

            if (used[pair.indexA] || used[pair.indexB]) continue;

            CompetitionEntry p1 = entrants.get(pair.indexA);
            CompetitionEntry p2 = entrants.get(pair.indexB);

            Match match = new Match();
            match.setEventId(eventId);
            match.setParticipant1Id(p1.memberId);
            match.setParticipant2Id(p2.memberId);
            match.setStatus("Scheduled");
            match.setResult("");
            match.setRoundNumber(1);

            matchRepository.createMatch(match);

            used[pair.indexA] = true;
            used[pair.indexB] = true;
            createdMatches++;

            messages.add(
                    "Matched " + fullName(p1) + " vs " + fullName(p2) +
                            " (" + p1.chosenMartialArt +
                            ", " + p1.experienceLevel + " vs " + p2.experienceLevel +
                            ", weight diff " + String.format("%.1f", pair.weightDiff) + " kg)"
            );
        }

        /*
        STEP 4 — Report any entrants who could not be matched
        */
        for (int i = 0; i < entrants.size(); i++) {
            if (!used[i]) {
                messages.add("No suitable opponent found for " + fullName(entrants.get(i))
                        + " (" + entrants.get(i).chosenMartialArt
                        + ", " + entrants.get(i).experienceLevel + ")");
            }
        }

        messages.add("Total matches created: " + createdMatches);
        lastGenerationMessages = messages;
        return messages;
    }

    public List<Match> getMatchesForEvent(int eventId) {
        return matchRepository.getMatchesForEvent(eventId);
    }

    public List<String> getLastGenerationMessages() {
        return lastGenerationMessages;
    }

    /*
    HELPERS
    */

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    private String fullName(CompetitionEntry e) {
        return e.firstName + " " + e.lastName;
    }

    private int experienceRank(String level) {
        if (level == null) return 999;
        switch (level.trim().toLowerCase()) {
            case "beginner":     return 1;
            case "intermediate": return 2;
            case "advanced":     return 3;
            default:             return 999;
        }
    }

    private boolean isCompatibleExperience(String level1, String level2) {
        int r1 = experienceRank(level1);
        int r2 = experienceRank(level2);
        if (r1 == 999 || r2 == 999) return false;
        return Math.abs(r1 - r2) <= 1;
    }

    /*
    DATA CLASSES
    */

    private static class CompetitionEntry {
        int memberId;
        String firstName;
        String lastName;
        int age;
        double weightKg;
        String chosenMartialArt;
        String experienceLevel;
    }

    private static class CandidatePair {
        int indexA;
        int indexB;
        double fitness;
        double weightDiff;

        CandidatePair(int indexA, int indexB, double fitness, double weightDiff) {
            this.indexA    = indexA;
            this.indexB    = indexB;
            this.fitness   = fitness;
            this.weightDiff = weightDiff;
        }
    }
}