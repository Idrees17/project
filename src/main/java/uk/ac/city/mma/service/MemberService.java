package uk.ac.city.mma.service;

import uk.ac.city.mma.model.MemberProfile;
import uk.ac.city.mma.repository.MemberProfileRepository;

public class MemberService {

    private MemberProfileRepository repository = new MemberProfileRepository();

    public MemberProfile getProfileByUserId(int userId) {
        return repository.getByUserId(userId);
    }

    public void saveProfile(int userId, String firstName, String lastName,
                            int age, int heightCm, double weightKg,
                            String experienceLevel, String preferredMartialArt) {

        MemberProfile profile = new MemberProfile();
        profile.setUserId(userId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setAge(age);
        profile.setHeightCm(heightCm);
        profile.setWeightKg(weightKg);
        profile.setExperienceLevel(experienceLevel);
        profile.setPreferredMartialArt(preferredMartialArt);

        if (repository.existsForUser(userId)) {
            repository.updateProfile(profile);
        } else {
            repository.createProfile(profile);
        }
    }
}