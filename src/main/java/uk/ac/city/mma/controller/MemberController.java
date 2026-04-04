package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.*;
import uk.ac.city.mma.service.*;

import java.util.List;

public class MemberController {

    private MemberService memberService = new MemberService();
    private EventService eventService = new EventService();

    public String getProfilePage(int userId) {

        MemberProfile profile = memberService.getProfileByUserId(userId);

        String firstName = "";
        String lastName = "";
        String age = "";
        String heightCm = "";
        String weightKg = "";
        String experienceLevel = "";
        String preferredMartialArt = "";

        if (profile != null) {
            firstName = safe(profile.getFirstName());
            lastName = safe(profile.getLastName());
            age = String.valueOf(profile.getAge());
            heightCm = String.valueOf(profile.getHeightCm());
            weightKg = String.valueOf(profile.getWeightKg());
            experienceLevel = safe(profile.getExperienceLevel());
            preferredMartialArt = safe(profile.getPreferredMartialArt());
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

        html.append("Experience Level: <select name='experienceLevel'>");

        html.append("<option value='Beginner'");
        if ("Beginner".equals(experienceLevel)) {
            html.append(" selected");
        }
        html.append(">Beginner</option>");

        html.append("<option value='Intermediate'");
        if ("Intermediate".equals(experienceLevel)) {
            html.append(" selected");
        }
        html.append(">Intermediate</option>");

        html.append("<option value='Advanced'");
        if ("Advanced".equals(experienceLevel)) {
            html.append(" selected");
        }
        html.append(">Advanced</option>");

        html.append("</select><br>");

        html.append("Preferred Martial Art: <input name='preferredMartialArt' value='")
                .append(preferredMartialArt)
                .append("'><br>");

        html.append("<button type='submit'>Save Profile</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/member-dashboard'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void saveProfile(int userId, String firstName, String lastName,
                            int age, int heightCm, double weightKg,
                            String experienceLevel, String preferredMartialArt) {

        memberService.saveProfile(
                userId,
                firstName,
                lastName,
                age,
                heightCm,
                weightKg,
                experienceLevel,
                preferredMartialArt
        );
    }

    public String getTournamentsPage(int memberId) {

        List<Event> events = eventService.getAllEvents();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Tournament Registration</h1>");

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Date</th>");
        html.append("<th>Location</th>");
        html.append("<th>Status</th>");
        html.append("<th>Action</th>");
        html.append("</tr>");

        for (Event e : events) {
            html.append("<tr>");
            html.append("<td>").append(e.getEventName()).append("</td>");
            html.append("<td>").append(e.getEventDate()).append("</td>");
            html.append("<td>").append(e.getLocation()).append("</td>");
            html.append("<td>").append(e.getStatus()).append("</td>");

            html.append("<td>");
            html.append("<form method='POST' action='/member/tournaments' style='display:inline;'>");
            html.append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>");
            html.append("<button type='submit'>Register</button>");
            html.append("</form>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/member-dashboard'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void registerForTournament(int memberId, int eventId) {
        eventService.registerMemberForEvent(eventId, memberId);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}