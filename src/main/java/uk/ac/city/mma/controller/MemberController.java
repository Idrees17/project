package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.*;
import uk.ac.city.mma.service.*;

import java.util.List;

public class MemberController {

    private MemberService memberService = new MemberService();
    private EventService eventService = new EventService();
    private LiveEventService liveEventService = new LiveEventService();
    private MatchmakingService matchmakingService = new MatchmakingService();
    private MembershipService membershipService = new MembershipService();

    public String getProfilePage(int userId) {

        MemberProfile profile = memberService.getProfileByUserId(userId);

        String firstName = "";
        String lastName = "";
        String age = "";
        String heightCm = "";
        String weightKg = "";

        if (profile != null) {
            firstName = safe(profile.getFirstName());
            lastName = safe(profile.getLastName());
            age = String.valueOf(profile.getAge());
            heightCm = String.valueOf(profile.getHeightCm());
            weightKg = String.valueOf(profile.getWeightKg());
        }

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>My Profile</h1>");

        html.append("<form method='POST' action='/member/profile'>");

        html.append("First Name: <input name='firstName' value='").append(firstName).append("'><br>");
        html.append("Last Name: <input name='lastName' value='").append(lastName).append("'><br>");
        html.append("Age: <input type='number' name='age' value='").append(age).append("'><br>");
        html.append("Height (cm): <input type='number' name='heightCm' value='").append(heightCm).append("'><br>");
        html.append("Weight (kg): <input type='number' step='0.1' name='weightKg' value='").append(weightKg).append("'><br>");

        html.append("<button type='submit'>Save Profile</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/member-dashboard'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void saveProfile(int userId, String firstName, String lastName,
                            int age, int heightCm, double weightKg) {

        memberService.saveProfile(
                userId,
                firstName,
                lastName,
                age,
                heightCm,
                weightKg
        );
    }

    public String getEventsPage(int memberId) {

        List<Event> allEvents = eventService.getAllEvents();
        List<Integer> registeredEventIds = eventService.getRegisteredEventIdsForMember(memberId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Events</h1>");

        // Upcoming events not yet registered
        html.append("<h2>Upcoming Events</h2>");
        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Date</th>");
        html.append("<th>Location</th>");
        html.append("<th>Status</th>");
        html.append("<th>Allowed Martial Arts</th>");
        html.append("<th>Register</th>");
        html.append("</tr>");

        for (Event e : allEvents) {
            if (registeredEventIds.contains(e.getEventId())) {
                continue;
            }

            html.append("<tr>");
            html.append("<td>").append(e.getEventName()).append("</td>");
            html.append("<td>").append(e.getEventDate()).append("</td>");
            html.append("<td>").append(e.getLocation()).append("</td>");
            html.append("<td>").append(e.getStatus()).append("</td>");
            html.append("<td>").append(e.getAllowedMartialArts() == null ? "" : e.getAllowedMartialArts()).append("</td>");

            html.append("<td>");

            if (e.getAllowedMartialArts() == null || e.getAllowedMartialArts().isBlank()) {
                html.append("No martial arts configured");
            } else {
                html.append("<form method='POST' action='/member/events'>");
                html.append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>");

                html.append("Martial Art: <select name='chosenMartialArt'>");
                String[] arts = e.getAllowedMartialArts().split(",");
                for (String art : arts) {
                    String trimmed = art.trim();
                    if (!trimmed.isEmpty()) {
                        html.append("<option value='").append(trimmed).append("'>")
                                .append(trimmed)
                                .append("</option>");
                    }
                }
                html.append("</select><br>");

                html.append("Experience Level: <select name='experienceLevel'>");
                html.append("<option value='Beginner'>Beginner</option>");
                html.append("<option value='Intermediate'>Intermediate</option>");
                html.append("<option value='Advanced'>Advanced</option>");
                html.append("</select><br>");

                html.append("<button type='submit'>Register</button>");
                html.append("</form>");
            }

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        // My events
        html.append("<h2>My Events</h2>");
        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Date</th>");
        html.append("<th>Location</th>");
        html.append("<th>Status</th>");
        html.append("<th>View</th>");
        html.append("</tr>");

        for (Event e : allEvents) {
            if (!registeredEventIds.contains(e.getEventId())) {
                continue;
            }

            html.append("<tr>");
            html.append("<td>").append(e.getEventName()).append("</td>");
            html.append("<td>").append(e.getEventDate()).append("</td>");
            html.append("<td>").append(e.getLocation()).append("</td>");
            html.append("<td>").append(e.getStatus()).append("</td>");
            html.append("<td><a href='/member/event-results?eventId=").append(e.getEventId()).append("'>View Event</a></td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/member-dashboard'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void registerForEvent(int memberId, int eventId, String chosenMartialArt, String experienceLevel) {
        eventService.registerMemberForEvent(eventId, memberId, chosenMartialArt, experienceLevel);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public String getMemberEventResultsPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        LiveEventState state = liveEventService.getStateForEvent(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Event Results - ").append(event.getEventName()).append("</h1>");

        html.append("<h2>Live Event State</h2>");
        html.append("<p><strong>Current Round:</strong> ").append(state.getCurrentRound()).append("</p>");
        html.append("<p><strong>Timer:</strong> ").append(formatSeconds(state.getRemainingSeconds())).append("</p>");
        html.append("<p><strong>Status:</strong> ").append(state.isTimerRunning() ? "Round Live" : "Paused / Waiting").append("</p>");

        html.append("<h2>Matches</h2>");
        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Match ID</th>");
        html.append("<th>Participant 1</th>");
        html.append("<th>Participant 2</th>");
        html.append("<th>Status</th>");
        html.append("<th>Round</th>");
        html.append("<th>Winner</th>");
        html.append("<th>Decision</th>");
        html.append("</tr>");

        for (Match m : matches) {
            html.append("<tr>");
            html.append("<td>").append(m.getMatchId()).append("</td>");
            html.append("<td>").append(m.getParticipant1Name()).append("</td>");
            html.append("<td>").append(m.getParticipant2Name()).append("</td>");
            html.append("<td>").append(m.getStatus()).append("</td>");
            html.append("<td>").append(m.getRoundNumber()).append("</td>");
            html.append("<td>").append(m.getWinnerName() == null ? "-" : m.getWinnerName()).append("</td>");
            html.append("<td>").append(m.getResult() == null || m.getResult().isBlank() ? "-" : m.getResult()).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/member-dashboard'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    private String formatSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getMembershipsPage(int memberId) {

        List<Membership> memberships = membershipService.getAllMemberships();
        Membership currentMembership = membershipService.getMembershipForMember(memberId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Memberships</h1>");

        if (currentMembership != null) {
            html.append("<p><strong>Current Membership:</strong> ")
                    .append(currentMembership.getMembershipName())
                    .append("</p><br>");
        }

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Description</th>");
        html.append("<th>Allowed Martial Arts</th>");
        html.append("<th>Allowed Skill Levels</th>");
        html.append("<th>Action</th>");
        html.append("</tr>");

        for (Membership m : memberships) {
            html.append("<tr>");
            html.append("<td>").append(m.getMembershipName()).append("</td>");
            html.append("<td>").append(m.getDescription()).append("</td>");
            html.append("<td>").append(m.getAllowedMartialArts()).append("</td>");
            html.append("<td>").append(m.getAllowedSkillLevels()).append("</td>");

            html.append("<td>");
            html.append("<form method='POST' action='/member/memberships'>");
            html.append("<input type='hidden' name='membershipId' value='").append(m.getMembershipId()).append("'>");
            html.append("<button type='submit'>Select Membership</button>");
            html.append("</form>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/member-dashboard'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void chooseMembership(int memberId, int membershipId) {
        membershipService.assignMembershipToMember(memberId, membershipId);
    }

}