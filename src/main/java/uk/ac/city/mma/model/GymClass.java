package uk.ac.city.mma.model;

public class GymClass {

    private int classId;
    private String className;
    private String description;
    private String skillLevel;
    private int capacity;

    public GymClass() {
    }

    public GymClass(int classId, String className, String description, String skillLevel, int capacity) {
        this.classId = classId;
        this.className = className;
        this.description = description;
        this.skillLevel = skillLevel;
        this.capacity = capacity;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}