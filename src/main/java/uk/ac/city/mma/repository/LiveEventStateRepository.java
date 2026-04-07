package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.LiveEventState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LiveEventStateRepository {

    public LiveEventState getByEventId(int eventId) {

        String sql = "SELECT * FROM live_event_state WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int currentMatchId = rs.getInt("current_match_id");
                Integer nullableMatchId = rs.wasNull() ? null : currentMatchId;

                return new LiveEventState(
                        rs.getInt("event_id"),
                        nullableMatchId,
                        rs.getInt("current_round"),
                        rs.getInt("round_time_seconds"),
                        rs.getInt("remaining_seconds"),
                        rs.getBoolean("timer_running")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void createInitialState(int eventId) {

        String sql = "INSERT INTO live_event_state (event_id, current_round, round_time_seconds, remaining_seconds, timer_running) " +
                "VALUES (?, 1, 180, 180, FALSE)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upsertState(LiveEventState state) {

        String checkSql = "SELECT COUNT(*) FROM live_event_state WHERE event_id = ?";
        String insertSql = "INSERT INTO live_event_state (event_id, current_match_id, current_round, round_time_seconds, remaining_seconds, timer_running) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE live_event_state SET current_match_id = ?, current_round = ?, round_time_seconds = ?, remaining_seconds = ?, timer_running = ? WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, state.getEventId());
            ResultSet rs = checkStmt.executeQuery();

            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

            if (!exists) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, state.getEventId());

                    if (state.getCurrentMatchId() == null) {
                        insertStmt.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        insertStmt.setInt(2, state.getCurrentMatchId());
                    }

                    insertStmt.setInt(3, state.getCurrentRound());
                    insertStmt.setInt(4, state.getRoundTimeSeconds());
                    insertStmt.setInt(5, state.getRemainingSeconds());
                    insertStmt.setBoolean(6, state.isTimerRunning());
                    insertStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    if (state.getCurrentMatchId() == null) {
                        updateStmt.setNull(1, java.sql.Types.INTEGER);
                    } else {
                        updateStmt.setInt(1, state.getCurrentMatchId());
                    }

                    updateStmt.setInt(2, state.getCurrentRound());
                    updateStmt.setInt(3, state.getRoundTimeSeconds());
                    updateStmt.setInt(4, state.getRemainingSeconds());
                    updateStmt.setBoolean(5, state.isTimerRunning());
                    updateStmt.setInt(6, state.getEventId());
                    updateStmt.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}