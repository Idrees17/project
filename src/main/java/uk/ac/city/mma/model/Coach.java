package uk.ac.city.mma.model;

public class Coach {

    private int coachId;
    private String name;
    private String specialty;

    public Coach() {}

    public Coach(int coachId, String name, String specialty) {
        this.coachId = coachId;
        this.name = name;
        this.specialty = specialty;
    }

    public int getCoachId() { return coachId; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }

    public void setCoachId(int coachId) { this.coachId = coachId; }
    public void setName(String name) { this.name = name; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}