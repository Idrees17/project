package uk.ac.city.mma.model;

public class Membership {

    private int membershipId;
    private String membershipName;
    private String description;
    private String allowedMartialArts;
    private String allowedSkillLevels;

    public Membership() {
    }

    public Membership(int membershipId, String membershipName, String description,
                      String allowedMartialArts, String allowedSkillLevels) {
        this.membershipId = membershipId;
        this.membershipName = membershipName;
        this.description = description;
        this.allowedMartialArts = allowedMartialArts;
        this.allowedSkillLevels = allowedSkillLevels;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public void setMembershipName(String membershipName) {
        this.membershipName = membershipName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAllowedMartialArts() {
        return allowedMartialArts;
    }

    public void setAllowedMartialArts(String allowedMartialArts) {
        this.allowedMartialArts = allowedMartialArts;
    }

    public String getAllowedSkillLevels() {
        return allowedSkillLevels;
    }

    public void setAllowedSkillLevels(String allowedSkillLevels) {
        this.allowedSkillLevels = allowedSkillLevels;
    }
}