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
                "p2.first_name AS p2_first, p2.last_name AS p2_last, " +
                "w.first_name AS w_first, w.last_name AS w_last " +
                "FROM matches m " +
                "JOIN member_profiles p1 ON m.participant1_id = p1.member_id " +
                "JOIN member_profiles p2 ON m.participant2_id = p2.member_id " +
                "LEFT JOIN member_profiles w ON m.winner_member_id = w.member_id " +
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

                int winnerId = rs.getInt("winner_member_id");
                if (!rs.wasNull()) {
                    match.setWinnerMemberId(winnerId);

                    String winnerFirst = rs.getString("w_first");
                    String winnerLast = rs.getString("w_last");

                    if (winnerFirst != null && winnerLast != null) {
                        match.setWinnerName(winnerFirst + " " + winnerLast);
                    }
                }

                matches.add(match);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matches;
    }


    public Match getMatchById(int matchId) {

        String sql = "SELECT m.*, " +
                "p1.first_name AS p1_first, p1.last_name AS p1_last, " +
                "p2.first_name AS p2_first, p2.last_name AS p2_last " +
                "FROM matches m " +
                "JOIN member_profiles p1 ON m.participant1_id = p1.member_id " +
                "JOIN member_profiles p2 ON m.participant2_id = p2.member_id " +
                "WHERE m.match_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Match match = new Match(
                        rs.getInt("match_id"),
                        rs.getInt("event_id"),
                        rs.getInt("participant1_id"),
                        rs.getInt("participant2_id"),
                        rs.getString("status"),
                        rs.getString("result"),
                        rs.getInt("round_number")
                );

                match.setParticipant1Name(rs.getString("p1_first") + " " + rs.getString("p1_last"));
                match.setParticipant2Name(rs.getString("p2_first") + " " + rs.getString("p2_last"));

                return match;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateMatchResult(int matchId, String status, String result, Integer winnerMemberId, int roundNumber) {

        String sql = "UPDATE matches SET status = ?, result = ?, winner_member_id = ?, round_number = ? WHERE match_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, result);

            if (winnerMemberId == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, winnerMemberId);
            }

            stmt.setInt(4, roundNumber);
            stmt.setInt(5, matchId);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}