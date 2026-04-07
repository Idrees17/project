package uk.ac.city.mma.service;

import uk.ac.city.mma.model.LiveEventState;
import uk.ac.city.mma.repository.LiveEventStateRepository;
import uk.ac.city.mma.repository.MatchRepository;

public class LiveEventService {

    private LiveEventStateRepository stateRepository = new LiveEventStateRepository();
    private MatchRepository matchRepository = new MatchRepository();

    public LiveEventState getStateForEvent(int eventId) {
        LiveEventState state = stateRepository.getByEventId(eventId);

        if (state == null) {
            stateRepository.createInitialState(eventId);
            state = stateRepository.getByEventId(eventId);
        }

        return state;
    }

    public void setCurrentMatch(int eventId, int matchId) {
        LiveEventState state = getStateForEvent(eventId);
        state.setCurrentMatchId(matchId);
        stateRepository.upsertState(state);
    }

    public void startTimer(int eventId) {
        LiveEventState state = getStateForEvent(eventId);
        state.setTimerRunning(true);
        stateRepository.upsertState(state);
    }

    public void pauseTimer(int eventId) {
        LiveEventState state = getStateForEvent(eventId);
        state.setTimerRunning(false);
        stateRepository.upsertState(state);
    }

    public void resetTimer(int eventId) {
        LiveEventState state = getStateForEvent(eventId);
        state.setRemainingSeconds(state.getRoundTimeSeconds());
        state.setTimerRunning(false);
        stateRepository.upsertState(state);
    }

    public void setRound(int eventId, int round) {
        LiveEventState state = getStateForEvent(eventId);
        state.setCurrentRound(round);
        stateRepository.upsertState(state);
    }

    public void setRoundTime(int eventId, int roundTimeSeconds) {
        LiveEventState state = getStateForEvent(eventId);
        state.setRoundTimeSeconds(roundTimeSeconds);
        state.setRemainingSeconds(roundTimeSeconds);
        stateRepository.upsertState(state);
    }

    public void tickTimer(int eventId) {
        LiveEventState state = getStateForEvent(eventId);

        if (state.isTimerRunning() && state.getRemainingSeconds() > 0) {
            state.setRemainingSeconds(state.getRemainingSeconds() - 1);

            if (state.getRemainingSeconds() <= 0) {
                state.setRemainingSeconds(0);
                state.setTimerRunning(false);
            }

            stateRepository.upsertState(state);
        }
    }

    public void updateMatchResult(int matchId, String status, String result, Integer winnerMemberId, int roundNumber) {
        matchRepository.updateMatchResult(matchId, status, result, winnerMemberId, roundNumber);
    }
}