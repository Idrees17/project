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
    private MemberProfileRepository memberProfileRepository = new MemberProfileRepository();
    private MatchRepository matchRepository = new MatchRepository();
    private List<String> lastGenerationMessages = new ArrayList<>();

    public List<String> generateMatchesForEvent(int eventId) {

        List<String> messages = new ArrayList<>();

        // Clear old matches first so regenerate does not duplicate
        matchRepository.deleteMatchesForEvent(eventId);
        messages.add("Previous generated matches for this event were cleared.");

        List<EventRegistration> registrations = registrationRepository.getRegistrationsForEvent(eventId);
        List<CompetitionEntry> entrants = new ArrayList<>();

        for (EventRegistration registration : registrations) {
            MemberProfile profile = memberProfileRepository.getProfileByMemberId(registration.getMemberId());

            if (profile != null) {
                CompetitionEntry entry = new CompetitionEntry();
                entry.memberId = profile.getMemberId();
                entry.firstName = profile.getFirstName();
                entry.lastName = profile.getLastName();
                entry.age = profile.getAge();
                entry.weightKg = profile.getWeightKg();
                entry.chosenMartialArt = registration.getChosenMartialArt();
                entry.experienceLevel = registration.getExperienceLevel();

                entrants.add(entry);
            }
        }

        if (entrants.size() < 2) {
            messages.add("Not enough entrants to generate matches.");
            lastGenerationMessages = messages;
            return messages;
        }

        // Sort entrants to make matching more predictable
        entrants.sort(
                Comparator.comparing((CompetitionEntry e) -> e.chosenMartialArt, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(e -> experienceRank(e.experienceLevel))
                        .thenComparingDouble(e -> e.weightKg)
        );

        boolean[] used = new boolean[entrants.size()];
        int createdMatches = 0;

        for (int i = 0; i < entrants.size(); i++) {

            if (used[i]) {
                continue;
            }

            CompetitionEntry p1 = entrants.get(i);

            int bestIndex = -1;
            double bestWeightDifference = Double.MAX_VALUE;

            for (int j = i + 1; j < entrants.size(); j++) {

                if (used[j]) {
                    continue;
                }

                CompetitionEntry p2 = entrants.get(j);

                // Same chosen martial art only
                if (!safeEquals(p1.chosenMartialArt, p2.chosenMartialArt)) {
                    continue;
                }

                // Allow one skill level up/down
                if (!isCompatibleExperience(p1.experienceLevel, p2.experienceLevel)) {
                    continue;
                }

                double weightDifference = Math.abs(p1.weightKg - p2.weightKg);

                // Leeway for prototype
                double allowedWeightDifference = 15.0;

                if (weightDifference <= allowedWeightDifference && weightDifference < bestWeightDifference) {
                    bestWeightDifference = weightDifference;
                    bestIndex = j;
                }
            }

            if (bestIndex != -1) {
                CompetitionEntry p2 = entrants.get(bestIndex);

                Match match = new Match();
                match.setEventId(eventId);
                match.setParticipant1Id(p1.memberId);
                match.setParticipant2Id(p2.memberId);
                match.setStatus("Scheduled");
                match.setResult("");
                match.setRoundNumber(1);

                matchRepository.createMatch(match);

                used[i] = true;
                used[bestIndex] = true;
                createdMatches++;

                messages.add(
                        "Matched " + fullName(p1) + " vs " + fullName(p2) +
                                " (" + p1.chosenMartialArt +
                                ", " + p1.experienceLevel + " vs " + p2.experienceLevel +
                                ", weight diff " + String.format("%.1f", bestWeightDifference) + " kg)"
                );
            } else {
                messages.add("No suitable opponent found for " + fullName(p1));
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

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    private String fullName(CompetitionEntry entry) {
        return entry.firstName + " " + entry.lastName;
    }

    private int experienceRank(String level) {
        if (level == null) {
            return 999;
        }

        String normalized = level.trim().toLowerCase();

        if (normalized.equals("beginner")) {
            return 1;
        }
        if (normalized.equals("intermediate")) {
            return 2;
        }
        if (normalized.equals("advanced")) {
            return 3;
        }

        return 999;
    }

    private boolean isCompatibleExperience(String level1, String level2) {
        int rank1 = experienceRank(level1);
        int rank2 = experienceRank(level2);

        if (rank1 == 999 || rank2 == 999) {
            return false;
        }

        return Math.abs(rank1 - rank2) <= 1;
    }

    private static class CompetitionEntry {
        int memberId;
        String firstName;
        String lastName;
        int age;
        double weightKg;
        String chosenMartialArt;
        String experienceLevel;
    }
}