package uk.ac.city.mma.model;

public class Match {

    private int matchId;
    private int eventId;
    private int participant1Id;
    private int participant2Id;
    private String participant1Name;
    private String participant2Name;
    private String status;
    private String result;
    private int roundNumber;

    public Match() {
    }

    public Match(int matchId, int eventId, int participant1Id, int participant2Id,
                 String status, String result, int roundNumber) {
        this.matchId = matchId;
        this.eventId = eventId;
        this.participant1Id = participant1Id;
        this.participant2Id = participant2Id;
        this.status = status;
        this.result = result;
        this.roundNumber = roundNumber;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getParticipant1Id() {
        return participant1Id;
    }

    public void setParticipant1Id(int participant1Id) {
        this.participant1Id = participant1Id;
    }

    public int getParticipant2Id() {
        return participant2Id;
    }

    public void setParticipant2Id(int participant2Id) {
        this.participant2Id = participant2Id;
    }

    public String getParticipant1Name() {
        return participant1Name;
    }

    public void setParticipant1Name(String participant1Name) {
        this.participant1Name = participant1Name;
    }

    public String getParticipant2Name() {
        return participant2Name;
    }

    public void setParticipant2Name(String participant2Name) {
        this.participant2Name = participant2Name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
}