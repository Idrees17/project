package uk.ac.city.mma;

import com.sun.net.httpserver.HttpServer;
import uk.ac.city.mma.controller.*;
import uk.ac.city.mma.model.*;
import uk.ac.city.mma.service.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        UserController authController = new UserController();
        AdminController adminController = new AdminController();
        MemberController memberController = new MemberController();

        Map<String, User> sessions = new HashMap<>();

        /*
          ROUTE
        */
        server.createContext("/login", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                byte[] response = Files.readAllBytes(
                        Paths.get("src/main/resources/templates/login.html")
                );

                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                User user = authController.authenticateUser(
                        params.get("username"),
                        params.get("password")
                );

                if (user == null) {
                    redirect(exchange, "/login-failed");
                } else {
                    String sessionId = UUID.randomUUID().toString();
                    sessions.put(sessionId, user);

                    exchange.getResponseHeaders().add("Set-Cookie", "SESSION_ID=" + sessionId + "; Path=/");

                    if (user.getRole() == User.Role.ADMIN) {
                        redirect(exchange, "/admin-dashboard");
                    } else {
                        redirect(exchange, "/member-dashboard");
                    }
                }
            }

            exchange.close();
        });


        /*
         DASHBOARD ROUTES
        */

        server.createContext("/admin-dashboard", e ->
                serveHtml(e,"admin-dashboard.html"));

        server.createContext("/member-dashboard", e ->
                serveHtml(e,"member-dashboard.html"));

        server.createContext("/login-failed", e ->
                serveHtml(e,"login-failed.html"));


        /*
         ADMIN CLASSES PAGE
        */

        server.createContext("/admin/classes", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = adminController.getClassesPage();

                byte[] response = html.getBytes();

                exchange.sendResponseHeaders(200,response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                adminController.createClass(
                        params.get("className"),
                        params.get("description"),
                        params.get("skillLevel"),
                        params.get("classType"),
                        Integer.parseInt(params.get("capacity"))
                );

                redirect(exchange,"/admin/classes");
            }

        });

        server.createContext("/admin/delete-class", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                int classId = Integer.parseInt(params.get("classId"));

                adminController.deleteClass(classId);

                redirect(exchange,"/admin/classes");
            }

            exchange.close();
        });

        server.createContext("/admin/edit-class", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                int classId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditClassPage(classId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                adminController.updateClass(
                        Integer.parseInt(params.get("classId")),
                        params.get("className"),
                        params.get("description"),
                        params.get("skillLevel"),
                        params.get("classType"),
                        Integer.parseInt(params.get("capacity"))
                );

                redirect(exchange, "/admin/classes");
            }

            exchange.close();
        });
        /*
         ADMIN TIMETABLE PAGES
        */

        server.createContext("/admin/timetable", exchange -> {

            String html = adminController.getTimetablePage();

            byte[] response = html.getBytes();
            exchange.sendResponseHeaders(200, response.length);

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        });

        server.createContext("/admin/generate-timetable", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = adminController.getGeneratorPage();

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                adminController.generateSmartTimetable(params);

                redirect(exchange,"/admin/timetable");
            }

            exchange.close();
        });


        /*
         ADMIN SESSION CONTROLS
        */

        server.createContext("/admin/add-session", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = adminController.getAddSessionPage();

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                adminController.createSession(
                        Integer.parseInt(params.get("classId")),
                        params.get("day"),
                        params.get("time"),
                        Integer.parseInt(params.get("durationMinutes")),
                        params.get("coachName"),
                        params.get("roomName")
                );

                redirect(exchange,"/admin/classes");
            }
        });

        server.createContext("/admin/delete-session", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                int sessionId = Integer.parseInt(params.get("sessionId"));

                adminController.deleteSession(sessionId);

                redirect(exchange,"/admin/timetable");
            }

            exchange.close();
        });


        server.createContext("/admin/edit-session", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                int sessionId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditSessionPage(sessionId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> params = parseFormData(body);

                adminController.updateSession(
                        Integer.parseInt(params.get("sessionId")),
                        params.get("day"),
                        params.get("time"),
                        Integer.parseInt(params.get("durationMinutes")),
                        params.get("coach"),
                        params.get("room")
                );

                redirect(exchange,"/admin/timetable");
            }
        });

        /*
         ADMIN COACHES CONTROLS
        */

        server.createContext("/admin/coaches", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String html = adminController.getCoachesPage();
                exchange.sendResponseHeaders(200, html.getBytes().length);
                exchange.getResponseBody().write(html.getBytes());
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                Map<String,String> params = parseFormData(
                        new String(exchange.getRequestBody().readAllBytes())
                );

                adminController.addCoach(
                        params.get("name"),
                        params.get("specialty")
                );

                redirect(exchange, "/admin/coaches");
            }

            exchange.close();
        });

        server.createContext("/admin/delete-coach", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> params = parseFormData(
                        new String(exchange.getRequestBody().readAllBytes())
                );

                adminController.deleteCoach(
                        Integer.parseInt(params.get("coachId"))
                );

                redirect(exchange, "/admin/coaches");
            }

            exchange.close();
        });

        server.createContext("/admin/edit-coach", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                int coachId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditCoachPage(coachId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> params = parseFormData(
                        new String(exchange.getRequestBody().readAllBytes())
                );

                adminController.updateCoach(
                        Integer.parseInt(params.get("coachId")),
                        params.get("name"),
                        params.get("specialty")
                );

                redirect(exchange, "/admin/coaches");
            }

            exchange.close();
        });

        /*
         ADMIN ROOM CONTROLS
        */

        server.createContext("/admin/rooms", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String html = adminController.getRoomsPage();
                exchange.sendResponseHeaders(200, html.getBytes().length);
                exchange.getResponseBody().write(html.getBytes());
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                Map<String,String> params = parseFormData(
                        new String(exchange.getRequestBody().readAllBytes())
                );

                adminController.addRoom(
                        params.get("name"),
                        Integer.parseInt(params.get("capacity"))
                );

                redirect(exchange, "/admin/rooms");
            }

            exchange.close();
        });

        server.createContext("/admin/delete-room", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> params = parseFormData(
                        new String(exchange.getRequestBody().readAllBytes())
                );

                adminController.deleteRoom(
                        Integer.parseInt(params.get("roomId"))
                );

                redirect(exchange, "/admin/rooms");
            }

            exchange.close();
        });

        server.createContext("/admin/edit-room", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                int roomId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditRoomPage(roomId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> params = parseFormData(
                        new String(exchange.getRequestBody().readAllBytes())
                );

                adminController.updateRoom(
                        Integer.parseInt(params.get("roomId")),
                        params.get("name"),
                        Integer.parseInt(params.get("capacity"))
                );

                redirect(exchange, "/admin/rooms");
            }

            exchange.close();
        });

        /*
         ADMIN MEMBER CONTROLS
        */

        server.createContext("/admin/members", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = adminController.getMembersPage();

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            exchange.close();
        });

        server.createContext("/admin/delete-member", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.deleteMember(
                        Integer.parseInt(params.get("memberId"))
                );

                redirect(exchange, "/admin/members");
            }

            exchange.close();
        });

        server.createContext("/admin/edit-member", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                int memberId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditMemberPage(memberId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.updateMember(
                        Integer.parseInt(params.get("memberId")),
                        params.get("firstName"),
                        params.get("lastName"),
                        Integer.parseInt(params.get("age")),
                        Integer.parseInt(params.get("heightCm")),
                        Double.parseDouble(params.get("weightKg"))
                );

                redirect(exchange, "/admin/members");
            }

            exchange.close();
        });

        /*
         ADMIN EVENT CONTROLS
        */

        server.createContext("/admin/tournaments", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = adminController.getTournamentsPage();

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.createEvent(
                        params.get("eventName"),
                        params.get("eventDate"),
                        params.get("location"),
                        params.get("status"),
                        params.get("format"),
                        params.get("allowedMartialArts")
                );

                redirect(exchange, "/admin/tournaments");
            }

            exchange.close();
        });

        server.createContext("/admin/delete-tournament", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.deleteEvent(
                        Integer.parseInt(params.get("eventId"))
                );

                redirect(exchange, "/admin/tournaments");
            }

            exchange.close();
        });

        server.createContext("/admin/edit-tournament", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                int eventId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditTournamentPage(eventId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.updateEvent(
                        Integer.parseInt(params.get("eventId")),
                        params.get("eventName"),
                        params.get("eventDate"),
                        params.get("location"),
                        params.get("status"),
                        params.get("format"),
                        params.get("allowedMartialArts")
                );

                redirect(exchange, "/admin/tournaments");
            }

            exchange.close();
        });

        server.createContext("/admin/view-entrants", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                int eventId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEntrantsPage(eventId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            exchange.close();
        });

        server.createContext("/admin/view-matches", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                int eventId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getMatchesPage(eventId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            exchange.close();
        });

        server.createContext("/admin/generate-matches", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.generateMatchesForEvent(eventId);

                redirect(exchange, "/admin/view-matches?eventId=" + eventId);
            }

            exchange.close();
        });

        server.createContext("/admin/event-results", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                int eventId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getAdminEventResultsPage(eventId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });

        server.createContext("/admin/live-control", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String query = exchange.getRequestURI().getQuery();
                Map<String, String> queryParams = parseFormData(query.replace("&", "&"));

                int eventId = Integer.parseInt(queryParams.get("eventId"));
                Integer matchId = queryParams.get("matchId") == null ? null : Integer.parseInt(queryParams.get("matchId"));

                String html = adminController.getAdminLiveControlPage(eventId, matchId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });

        server.createContext("/admin/live-control/select-match", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                int eventId = Integer.parseInt(params.get("eventId"));
                int matchId = Integer.parseInt(params.get("matchId"));

                adminController.setCurrentMatch(eventId, matchId);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/set-round", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                int eventId = Integer.parseInt(params.get("eventId"));
                int round = Integer.parseInt(params.get("round"));

                adminController.setCurrentRound(eventId, round);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/set-round-time", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                int eventId = Integer.parseInt(params.get("eventId"));
                int roundTimeSeconds = Integer.parseInt(params.get("roundTimeSeconds"));

                adminController.setRoundTime(eventId, roundTimeSeconds);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/start", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);
                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.startLiveTimer(eventId);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/pause", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);
                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.pauseLiveTimer(eventId);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/reset", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);
                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.resetLiveTimer(eventId);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/tick", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);
                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.tickLiveTimer(eventId);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/live-control/result", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                int eventId = Integer.parseInt(params.get("eventId"));
                int matchId = Integer.parseInt(params.get("matchId"));
                String status = params.get("status");
                String result = params.get("result");
                String winnerValue = params.get("winnerMemberId");
                Integer winnerMemberId = (winnerValue == null || winnerValue.isBlank()) ? null : Integer.parseInt(winnerValue);
                int roundNumber = Integer.parseInt(params.get("roundNumber"));

                adminController.saveMatchResult(matchId, status, result, winnerMemberId, roundNumber);
                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }
            exchange.close();
        });

        server.createContext("/admin/start-event", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.startEvent(eventId);

                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }

            exchange.close();
        });

        server.createContext("/admin/live-control/tick-and-return", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseFormData(query);

                int eventId = Integer.parseInt(params.get("eventId"));

                adminController.tickLiveTimer(eventId);

                redirect(exchange, "/admin/live-control?eventId=" + eventId);
            }

            exchange.close();
        });

        /*
         ADMIN MEMBERSHIP CONTROLS
        */

        server.createContext("/admin/memberships", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String html = adminController.getMembershipsPage();

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.createMembership(
                        params.get("membershipName"),
                        params.get("description"),
                        params.get("allowedMartialArts"),
                        params.get("allowedSkillLevels")
                );

                redirect(exchange, "/admin/memberships");
            }

            exchange.close();
        });

        server.createContext("/admin/delete-membership", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.deleteMembership(
                        Integer.parseInt(params.get("membershipId"))
                );

                redirect(exchange, "/admin/memberships");
            }

            exchange.close();
        });

        server.createContext("/admin/edit-membership", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                int membershipId = Integer.parseInt(query.split("=")[1]);

                String html = adminController.getEditMembershipPage(membershipId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                adminController.updateMembership(
                        Integer.parseInt(params.get("membershipId")),
                        params.get("membershipName"),
                        params.get("description"),
                        params.get("allowedMartialArts"),
                        params.get("allowedSkillLevels")
                );

                redirect(exchange, "/admin/memberships");
            }

            exchange.close();
        });

        /*
         MEMBER TIMETABLE CONTROLS
        */

        server.createContext("/member/timetable", exchange -> {

            User currentUser = getLoggedInUser(exchange, sessions);

            if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
                redirect(exchange, "/login");
                exchange.close();
                return;
            }

            MemberProfile profile = new MemberService().getProfileByUserId(currentUser.getId());

            if (profile == null) {
                String html = "<html><body>" +
                        "<h1>Please complete your profile first.</h1>" +
                        "<button onclick=\"location.href='/member/profile'\">Go to My Profile</button>" +
                        "</body></html>";

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = memberController.getTimetablePage(profile.getMemberId());

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                memberController.registerForSession(
                        profile.getMemberId(),
                        Integer.parseInt(params.get("sessionId"))
                );

                redirect(exchange, "/member/timetable");
            }

            exchange.close();
        });

        server.createContext("/member/timetable/unregister", exchange -> {

            User currentUser = getLoggedInUser(exchange, sessions);

            if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
                redirect(exchange, "/login");
                exchange.close();
                return;
            }

            MemberProfile profile = new MemberService().getProfileByUserId(currentUser.getId());

            if (profile == null) {
                redirect(exchange, "/member/profile");
                exchange.close();
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                memberController.unregisterFromSession(
                        profile.getMemberId(),
                        Integer.parseInt(params.get("sessionId"))
                );

                redirect(exchange, "/member/timetable");
            }

            exchange.close();
        });

        /*
         MEMBER EVENT CONTROLS
        */

        server.createContext("/member/events", exchange -> {

            User currentUser = getLoggedInUser(exchange, sessions);

            if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
                redirect(exchange, "/login");
                exchange.close();
                return;
            }

            MemberProfile profile = new MemberService().getProfileByUserId(currentUser.getId());

            if (profile == null) {
                String html = "<html><body>" +
                        "<h1>Please complete your profile first.</h1>" +
                        "<button onclick=\"location.href='/member/profile'\">Go to My Profile</button>" +
                        "</body></html>";

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = memberController.getEventsPage(profile.getMemberId());

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                memberController.registerForEvent(
                        profile.getMemberId(),
                        Integer.parseInt(params.get("eventId")),
                        params.get("chosenMartialArt"),
                        params.get("experienceLevel")
                );

                redirect(exchange, "/member/events");
            }

            exchange.close();
        });

        server.createContext("/member/event-results", exchange -> {

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                int eventId = Integer.parseInt(query.split("=")[1]);

                String html = memberController.getMemberEventResultsPage(eventId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });

        /*
         MEMBER PROFILE CONTROLS
        */

        server.createContext("/member/profile", exchange -> {

            User currentUser = getLoggedInUser(exchange, sessions);

            if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
                redirect(exchange, "/login");
                exchange.close();
                return;
            }

            int currentUserId = currentUser.getId();

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                String html = memberController.getProfilePage(currentUserId);

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                memberController.saveProfile(
                        currentUserId,
                        params.get("firstName"),
                        params.get("lastName"),
                        Integer.parseInt(params.get("age")),
                        Integer.parseInt(params.get("heightCm")),
                        Double.parseDouble(params.get("weightKg"))
                );

                redirect(exchange, "/member/profile");
            }

            exchange.close();
        });

        /*
         MEMBER MEMBERSHIP CONTROLS
        */

        server.createContext("/member/memberships", exchange -> {

            User currentUser = getLoggedInUser(exchange, sessions);

            if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
                redirect(exchange, "/login");
                exchange.close();
                return;
            }

            MemberProfile profile = new MemberService().getProfileByUserId(currentUser.getId());

            if (profile == null) {
                String html = "<html><body>" +
                        "<h1>Please complete your profile first.</h1>" +
                        "<button onclick=\"location.href='/member/profile'\">Go to My Profile</button>" +
                        "</body></html>";

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String html = memberController.getMembershipsPage(profile.getMemberId());

                byte[] response = html.getBytes();
                exchange.sendResponseHeaders(200, response.length);

                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseFormData(body);

                memberController.chooseMembership(
                        profile.getMemberId(),
                        Integer.parseInt(params.get("membershipId"))
                );

                redirect(exchange, "/member/memberships");
            }

            exchange.close();
        });


        server.start();
        System.out.println("Server running at http://localhost:8080/login");

    }


    /*
     SERVE STATIC HTML
    */

    private static void serveHtml(com.sun.net.httpserver.HttpExchange exchange, String file)
            throws IOException {

        byte[] response = Files.readAllBytes(
                Paths.get("src/main/resources/templates/" + file)
        );

        exchange.sendResponseHeaders(200,response.length);

        try(OutputStream os = exchange.getResponseBody()){
            os.write(response);
        }

        exchange.close();
    }


    /*
     REDIRECT HELPER
    */

    private static void redirect(com.sun.net.httpserver.HttpExchange exchange,String location)
            throws IOException{

        exchange.getResponseHeaders().add("Location",location);
        exchange.sendResponseHeaders(302,-1);
    }


    /*
     FORM DATA PARSER
    */

    private static Map<String,String> parseFormData(String body){

        Map<String,String> map = new HashMap<>();

        String[] pairs = body.split("&");

        for(String pair : pairs){

            String[] kv = pair.split("=");

            map.put(
                    URLDecoder.decode(kv[0],StandardCharsets.UTF_8),
                    URLDecoder.decode(kv[1],StandardCharsets.UTF_8)
            );
        }

        return map;
    }

    private static String getCookieValue(com.sun.net.httpserver.HttpExchange exchange, String cookieName) {
        List<String> cookieHeaders = exchange.getRequestHeaders().get("Cookie");

        if (cookieHeaders == null) {
            return null;
        }

        for (String header : cookieHeaders) {
            String[] cookies = header.split(";");
            for (String cookie : cookies) {
                String[] parts = cookie.trim().split("=", 2);
                if (parts.length == 2 && parts[0].equals(cookieName)) {
                    return parts[1];
                }
            }
        }

        return null;
    }

    private static User getLoggedInUser(com.sun.net.httpserver.HttpExchange exchange, Map<String, User> sessions) {
        String sessionId = getCookieValue(exchange, "SESSION_ID");

        if (sessionId == null) {
            return null;
        }

        return sessions.get(sessionId);
    }

}