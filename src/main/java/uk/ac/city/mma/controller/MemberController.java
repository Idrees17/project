package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.MemberProfile;
import uk.ac.city.mma.service.MemberService;

public class MemberController {

    private MemberService memberService = new MemberService();

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
        html.append(option("Beginner", experienceLevel));
        html.append(option("Intermediate", experienceLevel));
        html.append(option("Advanced", experienceLevel));
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String option(String value, String selectedValue) {
        if (value.equals(selectedValue)) {
            return "<option value='" + value + "' selected>" + value + "</option>";
        }
        return "<option value='" + value + "'>" + value + "</option>";
    }
}