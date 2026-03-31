package uk.ac.city.mma.model;

public class MemberProfile {

    private int memberId;
    private int userId;
    private String firstName;
    private String lastName;
    private int age;
    private int heightCm;
    private double weightKg;
    private String experienceLevel;
    private String preferredMartialArt;

    public MemberProfile() {
    }

    public MemberProfile(int memberId, int userId, String firstName, String lastName,
                         int age, int heightCm, double weightKg,
                         String experienceLevel, String preferredMartialArt) {
        this.memberId = memberId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.experienceLevel = experienceLevel;
        this.preferredMartialArt = preferredMartialArt;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(int heightCm) {
        this.heightCm = heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPreferredMartialArt() {
        return preferredMartialArt;
    }

    public void setPreferredMartialArt(String preferredMartialArt) {
        this.preferredMartialArt = preferredMartialArt;
    }
}