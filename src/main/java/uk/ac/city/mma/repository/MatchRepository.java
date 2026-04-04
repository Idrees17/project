package uk.ac.city.mma.repository;

import uk.ac.city.mma.config.MySQLConnection;
import uk.ac.city.mma.model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchRepository {

    public void createMatch(Match match) {

        String sql = "INSERT INTO matches " +
                "(event_id, participant1_id, participant2_id, status, result, round_number) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, match.getEventId());
            stmt.setInt(2, match.getParticipant1Id());
            stmt.setInt(3, match.getParticipant2Id());
            stmt.setString(4, match.getStatus());
            stmt.setString(5, match.getResult());
            stmt.setInt(6, match.getRoundNumber());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMatchesForEvent(int eventId) {

        String sql = "DELETE FROM matches WHERE event_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Match> getMatchesForEvent(int eventId) {

        List<Match> matches = new ArrayList<>();

        String sql = "SELECT m.*, " +
                "p1.first_name AS p1_first, p1.last_name AS p1_last, " +
                "p2.first_name AS p2_first, p2.last_name AS p2_last " +
                "FROM matches m " +
                "JOIN member_profiles p1 ON m.participant1_id = p1.member_id " +
                "JOIN member_profiles p2 ON m.participant2_id = p2.member_id " +
                "WHERE m.event_id = ? " +
                "ORDER BY m.round_number, m.match_id";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Match match = new Match(
                        rs.getInt("match_id"),
                        rs.getInt("event_id"),
                        rs.getInt("participant1_id"),
                        rs.getInt("participant2_id"),
                        rs.getString("status"),
                        rs.getString("result"),
                        rs.getInt("round_number")
                );

                match.setParticipant1Name(
                        rs.getString("p1_first") + " " + rs.getString("p1_last")
                );
                match.setParticipant2Name(
                        rs.getString("p2_first") + " " + rs.getString("p2_last")
                );

                matches.add(match);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matches;
    }
}