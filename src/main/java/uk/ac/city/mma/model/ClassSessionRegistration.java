package uk.ac.city.mma.model;

public class ClassSessionRegistration {

    private int registrationId;
    private int sessionId;
    private int memberId;
    private String registeredAt;

    public ClassSessionRegistration() {
    }

    public ClassSessionRegistration(int registrationId, int sessionId, int memberId, String registeredAt) {
        this.registrationId = registrationId;
        this.sessionId = sessionId;
        this.memberId = memberId;
        this.registeredAt = registeredAt;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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