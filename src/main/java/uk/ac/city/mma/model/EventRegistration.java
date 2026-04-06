package uk.ac.city.mma.model;

public class EventRegistration {

    private int registrationId;
    private int eventId;
    private int memberId;
    private String chosenMartialArt;
    private String experienceLevel;
    private String registeredAt;

    public EventRegistration() {
    }

    public EventRegistration(int registrationId, int eventId, int memberId,
                             String chosenMartialArt, String experienceLevel,
                             String registeredAt) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.memberId = memberId;
        this.chosenMartialArt = chosenMartialArt;
        this.experienceLevel = experienceLevel;
        this.registeredAt = registeredAt;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getChosenMartialArt() {
        return chosenMartialArt;
    }

    public void setChosenMartialArt(String chosenMartialArt) {
        this.chosenMartialArt = chosenMartialArt;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }
}