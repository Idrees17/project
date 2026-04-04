package uk.ac.city.mma.model;

public class EventRegistration {

    private int registrationId;
    private int eventId;
    private int memberId;
    private String registeredAt;

    public EventRegistration() {
    }

    public EventRegistration(int registrationId, int eventId, int memberId, String registeredAt) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.memberId = memberId;
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

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }
}