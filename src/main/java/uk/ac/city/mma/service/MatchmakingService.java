package uk.ac.city.mma.service;

import uk.ac.city.mma.model.Match;
import uk.ac.city.mma.model.MemberProfile;
import uk.ac.city.mma.repository.EventRegistrationRepository;
import uk.ac.city.mma.repository.MatchRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchmakingService {

    private EventRegistrationRepository registrationRepository = new EventRegistrationRepository();
    private MatchRepository matchRepository = new MatchRepository();
    private List<String> lastGenerationMessages = new ArrayList<>();

    public List<String> getLastGenerationMessages() {
        return lastGenerationMessages;
    }

    public List<Match> getMatchesForEvent(int eventId) {
        return matchRepository.getMatchesForEvent(eventId);
    }

    public List<String> generateMatchesForEvent(int eventId) {

        List<String> messages = new ArrayList<>();

        // Clear old matches for this event so regeneration doesn't duplicate
        matchRepository.deleteMatchesForEvent(eventId);
        messages.add("Previous generated matches for this event were cleared.");

        List<MemberProfile> entrants = registrationRepository.getEntrantsForEvent(eventId);

        if (entrants.size() < 2) {
            messages.add("Not enough entrants to generate matches.");
            return messages;
        }

        // Sort for more predictable grouping
        entrants.sort(
                Comparator.comparing(MemberProfile::getPreferredMartialArt, Comparator.nullsLast(String::compareTo))
                        .thenComparing(MemberProfile::getExperienceLevel, Comparator.nullsLast(String::compareTo))
                        .thenComparingDouble(MemberProfile::getWeightKg)
        );

        boolean[] used = new boolean[entrants.size()];
        int createdMatches = 0;

        for (int i = 0; i < entrants.size(); i++) {

            if (used[i]) {
                continue;
            }

            MemberProfile p1 = entrants.get(i);
            int bestIndex = -1;
            double bestWeightDifference = Double.MAX_VALUE;

            for (int j = i + 1; j < entrants.size(); j++) {

                if (used[j]) {
                    continue;
                }

                MemberProfile p2 = entrants.get(j);

                boolean sameMartialArt =
                        safeEquals(p1.getPreferredMartialArt(), p2.getPreferredMartialArt());

                boolean sameExperience =
                        safeEquals(p1.getExperienceLevel(), p2.getExperienceLevel());

                if (!sameMartialArt || !sameExperience) {
                    continue;
                }

                double weightDifference = Math.abs(p1.getWeightKg() - p2.getWeightKg());

                // Simple fairness threshold for prototype
                if (weightDifference <= 10.0 && weightDifference < bestWeightDifference) {
                    bestWeightDifference = weightDifference;
                    bestIndex = j;
                }
            }

            if (bestIndex != -1) {
                MemberProfile p2 = entrants.get(bestIndex);

                Match match = new Match();
                match.setEventId(eventId);
                match.setParticipant1Id(p1.getMemberId());
                match.setParticipant2Id(p2.getMemberId());
                match.setStatus("Scheduled");
                match.setResult("");
                match.setRoundNumber(1);

                matchRepository.createMatch(match);

                used[i] = true;
                used[bestIndex] = true;
                createdMatches++;

                messages.add("Matched " + fullName(p1) + " vs " + fullName(p2));
            } else {
                messages.add("No suitable opponent found for " + fullName(p1));
            }
        }

        messages.add("Total matches created: " + createdMatches);

        lastGenerationMessages = messages;
        return messages;
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    private String fullName(MemberProfile member) {
        return member.getFirstName() + " " + member.getLastName();
    }
}