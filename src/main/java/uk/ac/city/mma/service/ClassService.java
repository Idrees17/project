package uk.ac.city.mma.service;

import uk.ac.city.mma.model.GymClass;
import uk.ac.city.mma.repository.GymClassRepository;

import java.util.List;

public class ClassService {

    private GymClassRepository repository = new GymClassRepository();

    public void createClass(String name, String description, String skillLevel,
                            String classType, int capacity) {
        GymClass gymClass = new GymClass();
        gymClass.setClassName(name);
        gymClass.setDescription(description);
        gymClass.setSkillLevel(skillLevel);
        gymClass.setClassType(classType);
        gymClass.setCapacity(capacity);
        repository.createClass(gymClass);
    }

    public List<GymClass> getAllClasses() {
        return repository.getAllClasses();
    }

    public GymClass getClassById(int classId) {
        return repository.getClassById(classId);
    }

    public void updateClass(int classId, String name, String description,
                            String skillLevel, String classType, int capacity) {
        GymClass gymClass = new GymClass();
        gymClass.setClassId(classId);
        gymClass.setClassName(name);
        gymClass.setDescription(description);
        gymClass.setSkillLevel(skillLevel);
        gymClass.setClassType(classType);
        gymClass.setCapacity(capacity);
        repository.updateClass(gymClass);
    }

    public void deleteClass(int classId) {
        repository.deleteClass(classId);
    }
}