package uk.ac.city.mma.model;

public class LiveEventState {

    private int eventId;
    private Integer currentMatchId;
    private int currentRound;
    private int roundTimeSeconds;
    private int remainingSeconds;
    private boolean timerRunning;

    public LiveEventState() {
    }

    public LiveEventState(int eventId, Integer currentMatchId, int currentRound,
                          int roundTimeSeconds, int remainingSeconds, boolean timerRunning) {
        this.eventId = eventId;
        this.currentMatchId = currentMatchId;
        this.currentRound = currentRound;
        this.roundTimeSeconds = roundTimeSeconds;
        this.remainingSeconds = remainingSeconds;
        this.timerRunning = timerRunning;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Integer getCurrentMatchId() {
        return currentMatchId;
    }

    public void setCurrentMatchId(Integer currentMatchId) {
        this.currentMatchId = currentMatchId;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getRoundTimeSeconds() {
        return roundTimeSeconds;
    }

    public void setRoundTimeSeconds(int roundTimeSeconds) {
        this.roundTimeSeconds = roundTimeSeconds;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        this.timerRunning = timerRunning;
    }
}