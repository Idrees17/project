package uk.ac.city.mma.model;

public class Event {

    private int eventId;
    private String eventName;
    private String eventDate;
    private String location;
    private String status;
    private String allowedMartialArts;
    private String format;

    public Event() {
    }

    public Event(int eventId, String eventName, String eventDate,
                 String location, String status, String allowedMartialArts) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.location = location;
        this.status = status;
        this.allowedMartialArts = allowedMartialArts;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAllowedMartialArts() {
        return allowedMartialArts;
    }

    public void setAllowedMartialArts(String allowedMartialArts) {
        this.allowedMartialArts = allowedMartialArts;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}