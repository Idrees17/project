package uk.ac.city.mma.service;

import uk.ac.city.mma.model.Membership;
import uk.ac.city.mma.repository.MemberMembershipRepository;
import uk.ac.city.mma.repository.MembershipRepository;

import java.util.List;

public class MembershipService {

    private MembershipRepository membershipRepository = new MembershipRepository();
    private MemberMembershipRepository memberMembershipRepository = new MemberMembershipRepository();

    public void createMembership(String name, String description, String allowedMartialArts, String allowedSkillLevels) {
        Membership membership = new Membership();
        membership.setMembershipName(name);
        membership.setDescription(description);
        membership.setAllowedMartialArts(allowedMartialArts);
        membership.setAllowedSkillLevels(allowedSkillLevels);

        membershipRepository.createMembership(membership);
    }

    public List<Membership> getAllMemberships() {
        return membershipRepository.getAllMemberships();
    }

    public Membership getMembershipById(int membershipId) {
        return membershipRepository.getMembershipById(membershipId);
    }

    public void updateMembership(int membershipId, String name, String description,
                                 String allowedMartialArts, String allowedSkillLevels) {
        Membership membership = new Membership();
        membership.setMembershipId(membershipId);
        membership.setMembershipName(name);
        membership.setDescription(description);
        membership.setAllowedMartialArts(allowedMartialArts);
        membership.setAllowedSkillLevels(allowedSkillLevels);

        membershipRepository.updateMembership(membership);
    }

    public void deleteMembership(int membershipId) {
        membershipRepository.deleteMembership(membershipId);
    }

    public void assignMembershipToMember(int memberId, int membershipId) {
        memberMembershipRepository.assignMembership(memberId, membershipId);
    }

    public Membership getMembershipForMember(int memberId) {
        Integer membershipId = memberMembershipRepository.getMembershipIdForMember(memberId);
        if (membershipId == null) {
            return null;
        }
        return membershipRepository.getMembershipById(membershipId);
    }
}