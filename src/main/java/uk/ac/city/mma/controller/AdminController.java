package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.*;
import uk.ac.city.mma.service.*;

import java.util.*;

public class AdminController {

    private ClassService classService = new ClassService();
    private ClassSessionService classSessionService = new ClassSessionService();
    private TimetableService timetableService = new TimetableService();
    private CoachService coachService = new CoachService();
    private RoomService roomService = new RoomService();
    private MemberService memberService = new MemberService();
    private EventService eventService = new EventService();
    private MatchmakingService matchmakingService = new MatchmakingService();
    private LiveEventService liveEventService = new LiveEventService();
    private MembershipService membershipService = new MembershipService();

    /*
     SHARED HTML HELPERS
    */

    private String pageStart(String title, String activeNav) {
        return "<!DOCTYPE html><html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<title>" + title + " | MMA Gym Admin</title>" +
                "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet'>" +
                "<link href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css' rel='stylesheet'>" +
                "<style>" +
                "body { background-color: #f8f9fa; }" +
                ".navbar-brand { font-weight: 700; letter-spacing: 1px; }" +
                ".sidebar { min-height: calc(100vh - 56px); background: #212529; padding-top: 1rem; }" +
                ".sidebar .nav-link { color: #adb5bd; padding: .5rem 1rem; border-radius: 6px; margin: 2px 8px; }" +
                ".sidebar .nav-link:hover { color: #fff; background: #343a40; }" +
                ".sidebar .nav-link.active { color: #fff; background: #0d6efd; }" +
                ".sidebar .nav-link i { margin-right: 8px; }" +
                ".main-content { padding: 2rem; }" +
                ".page-header { border-bottom: 2px solid #dee2e6; padding-bottom: 1rem; margin-bottom: 1.5rem; }" +
                ".card { border: none; box-shadow: 0 1px 4px rgba(0,0,0,.08); }" +
                ".table thead th { background-color: #212529; color: #fff; border-color: #212529; }" +
                ".badge-generated { background-color: #6f42c1; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<nav class='navbar navbar-dark bg-dark'>" +
                "<div class='container-fluid'>" +
                "<span class='navbar-brand'><i class='bi bi-shield-fill-check me-2'></i>MMA Gym Admin</span>" +
                "<a href='/login' class='btn btn-sm btn-outline-light'>Logout</a>" +
                "</div>" +
                "</nav>" +
                "<div class='container-fluid'><div class='row'>" +
                "<nav class='col-md-2 d-none d-md-block sidebar'>" +
                "<ul class='nav flex-column'>" +
                navLink("/admin-dashboard",    "bi-speedometer2", "Dashboard",   activeNav) +
                navLink("/admin/classes",      "bi-journal-text", "Classes",      activeNav) +
                navLink("/admin/timetable",    "bi-calendar3",    "Timetable",    activeNav) +
                navLink("/admin/coaches",      "bi-person-badge", "Coaches",      activeNav) +
                navLink("/admin/rooms",        "bi-door-open",    "Rooms",        activeNav) +
                navLink("/admin/members",      "bi-people",       "Members",      activeNav) +
                navLink("/admin/memberships",  "bi-card-checklist","Memberships", activeNav) +
                navLink("/admin/tournaments",  "bi-trophy",       "Events",       activeNav) +
                "</ul></nav>" +
                "<main class='col-md-10 main-content'>";
    }

    private String navLink(String href, String icon, String label, String active) {
        String cls = label.equals(active) ? "nav-link active" : "nav-link";
        return "<li class='nav-item'><a href='" + href + "' class='" + cls + "'>" +
                "<i class='bi " + icon + "'></i>" + label + "</a></li>";
    }

    private String pageEnd() {
        return "</main></div></div>" +
                "<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js'></script>" +
                "</body></html>";
    }

    private String alertInfo(String msg) {
        return "<div class='alert alert-info'>" + msg + "</div>";
    }

    /*
     CLASSES PAGE
    */

    public String getClassesPage() {

        List<GymClass> classes = classService.getAllClasses();

        StringBuilder html = new StringBuilder(pageStart("Classes", "Classes"));

        html.append("<div class='page-header d-flex justify-content-between align-items-center'>")
                .append("<h2><i class='bi bi-journal-text me-2'></i>Manage Classes</h2>")
                .append("</div>");

        html.append("<div class='card mb-4'><div class='card-header fw-semibold'>Create New Class</div><div class='card-body'>")
                .append("<form method='POST' action='/admin/classes' class='row g-3'>")
                .append("<div class='col-md-3'><label class='form-label'>Class Name</label><input class='form-control' name='className' required></div>")
                .append("<div class='col-md-3'><label class='form-label'>Description</label><input class='form-control' name='description'></div>")
                .append("<div class='col-md-3'><label class='form-label'>Skill Level</label>")
                .append("<select class='form-select' name='skillLevel'>")
                .append("<option value='Beginner'>Beginner</option>")
                .append("<option value='Intermediate/Advanced'>Intermediate/Advanced</option>")
                .append("</select></div>")
                .append("<div class='col-md-2'><label class='form-label'>Capacity</label><input class='form-control' type='number' name='capacity' required></div>")
                .append("<div class='col-md-1 d-flex align-items-end'><button class='btn btn-primary w-100' type='submit'>Add</button></div>")
                .append("</form></div></div>");

        // Table
        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Skill Level</th><th>Capacity</th><th>Actions</th></tr></thead><tbody>");

        for (GymClass c : classes) {
            html.append("<tr>")
                    .append("<td>").append(c.getClassId()).append("</td>")
                    .append("<td>").append(c.getClassName()).append("</td>")
                    .append("<td><span class='badge bg-secondary'>").append(c.getSkillLevel()).append("</span></td>")
                    .append("<td>").append(c.getCapacity()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/add-session?classId=").append(c.getClassId()).append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-plus-circle me-1'></i>Add Session</a>")
                    .append("<form method='POST' action='/admin/delete-class' style='display:inline;'>")
                    .append("<input type='hidden' name='classId' value='").append(c.getClassId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete this class?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form>")
                    .append("</td></tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void createClass(String name, String description, String skill, int capacity) {
        classService.createClass(name, description, skill, capacity);
    }

    public void deleteClass(int classId) {
        classService.deleteClass(classId);
    }

    /*
     ADD SESSION PAGE
    */

    public String getAddSessionPage() {

        List<GymClass> classes = classService.getAllClasses();
        List<Coach> coaches = coachService.getAllCoaches();
        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder(pageStart("Add Session", "Timetable"));

        html.append("<div class='page-header'><h2><i class='bi bi-plus-circle me-2'></i>Add Session</h2></div>");

        html.append("<div class='card' style='max-width:600px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/add-session' class='row g-3'>")
                .append("<div class='col-12'><label class='form-label'>Class</label><select class='form-select' name='classId'>");
        for (GymClass c : classes) {
            html.append("<option value='").append(c.getClassId()).append("'>").append(c.getClassName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-6'><label class='form-label'>Day</label>")
                .append("<select class='form-select' name='day'>")
                .append("<option>Monday</option><option>Tuesday</option><option>Wednesday</option>")
                .append("<option>Thursday</option><option>Friday</option><option>Saturday</option><option>Sunday</option>")
                .append("</select></div>")
                .append("<div class='col-md-6'><label class='form-label'>Start Time</label><input type='time' class='form-control' name='time'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Duration (minutes)</label><input type='number' class='form-control' min='30' step='30' name='durationMinutes'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Coach</label><select class='form-select' name='coachName'>");
        for (Coach coach : coaches) {
            html.append("<option value='").append(coach.getName()).append("'>").append(coach.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-6'><label class='form-label'>Room</label><select class='form-select' name='roomName'>");
        for (Room room : rooms) {
            html.append("<option value='").append(room.getName()).append("'>").append(room.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Create Session</button>")
                .append("<a href='/admin/timetable' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append(pageEnd());
        return html.toString();
    }

    public void createSession(int classId, String day, String time,
                              int durationMinutes, String coachName, String roomName) {
        classSessionService.createSession(classId, day, time, durationMinutes, coachName, roomName);
    }

    /*
     TIMETABLE PAGE
    */

    public String getTimetablePage() {

        List<ClassSession> sessions = classSessionService.getAllSessions();

        StringBuilder html = new StringBuilder(pageStart("Timetable", "Timetable"));

        html.append("<div class='page-header d-flex justify-content-between align-items-center'>")
                .append("<h2><i class='bi bi-calendar3 me-2'></i>Timetable Management</h2>")
                .append("<div class='d-flex gap-2'>")
                .append("<a href='/admin/add-session' class='btn btn-primary'><i class='bi bi-plus-circle me-1'></i>Add Session</a>")
                .append("<a href='/admin/generate-timetable' class='btn btn-outline-secondary'><i class='bi bi-magic me-1'></i>Generate Timetable</a>")
                .append("</div></div>");

        List<String> generationMessages = timetableService.getLastGenerationMessages();
        if (!generationMessages.isEmpty()) {
            html.append("<div class='alert alert-info'><strong>Generation Log:</strong><ul class='mb-0 mt-1'>");
            for (String msg : generationMessages) {
                html.append("<li>").append(msg).append("</li>");
            }
            html.append("</ul></div>");
        }

        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        for (String day : days) {
            boolean hasDay = sessions.stream().anyMatch(s -> s.getDayOfWeek().equalsIgnoreCase(day));
            if (!hasDay) continue;

            html.append("<h5 class='mt-4 mb-2 text-muted text-uppercase fw-bold' style='letter-spacing:1px'>").append(day).append("</h5>")
                    .append("<div class='card mb-3'><div class='card-body p-0'>")
                    .append("<table class='table table-hover mb-0'>")
                    .append("<thead><tr><th>Class</th><th>Skill Level</th><th>Time</th><th>Duration</th><th>Coach</th><th>Room</th><th>Source</th><th>Actions</th></tr></thead><tbody>");

            for (ClassSession s : sessions) {
                if (!s.getDayOfWeek().equalsIgnoreCase(day)) continue;

                String sourceBadge = s.isGenerated()
                        ? "<span class='badge bg-purple' style='background:#6f42c1'>Generated</span>"
                        : "<span class='badge bg-secondary'>Manual</span>";

                html.append("<tr>")
                        .append("<td>").append(s.getClassName()).append("</td>")
                        .append("<td><span class='badge bg-secondary'>").append(s.getSkillLevel()).append("</span></td>")
                        .append("<td>").append(s.getStartTime()).append("</td>")
                        .append("<td>").append(s.getDurationMinutes()).append(" mins</td>")
                        .append("<td>").append(s.getCoachName()).append("</td>")
                        .append("<td>").append(s.getRoom()).append("</td>")
                        .append("<td>").append(sourceBadge).append("</td>")
                        .append("<td class='d-flex gap-2'>")
                        .append("<a href='/admin/edit-session?sessionId=").append(s.getSessionId()).append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil'></i></a>")
                        .append("<form method='POST' action='/admin/delete-session' style='display:inline;'>")
                        .append("<input type='hidden' name='sessionId' value='").append(s.getSessionId()).append("'>")
                        .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete session?\")'><i class='bi bi-trash'></i></button>")
                        .append("</form></td></tr>");
            }

            html.append("</tbody></table></div></div>");
        }

        html.append(pageEnd());
        return html.toString();
    }

    public void deleteSession(int sessionId) {
        classSessionService.deleteSession(sessionId);
    }

    public void updateSession(int sessionId, String day, String time,
                              int durationMinutes, String coach, String room) {
        classSessionService.updateSession(sessionId, day, time, durationMinutes, coach, room);
    }

    /*
     EDIT SESSION PAGE
    */

    public String getEditSessionPage(int sessionId) {

        ClassSession session = classSessionService.getSessionById(sessionId);
        List<Coach> coaches = coachService.getAllCoaches();
        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder(pageStart("Edit Session", "Timetable"));

        html.append("<div class='page-header'><h2><i class='bi bi-pencil me-2'></i>Edit Session</h2></div>");

        html.append("<div class='card' style='max-width:600px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/edit-session' class='row g-3'>")
                .append("<input type='hidden' name='sessionId' value='").append(session.getSessionId()).append("'>")
                .append("<div class='col-12'><label class='form-label fw-semibold'>Class</label>")
                .append("<p class='form-control-plaintext'>").append(session.getClassName()).append("</p></div>")
                .append("<div class='col-md-6'><label class='form-label'>Day</label><select class='form-select' name='day'>");

        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        for (String day : days) {
            html.append("<option value='").append(day).append("'").append(day.equals(session.getDayOfWeek()) ? " selected" : "").append(">").append(day).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-6'><label class='form-label'>Start Time</label>")
                .append("<input type='time' class='form-control' name='time' value='").append(session.getStartTime()).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Duration (minutes)</label>")
                .append("<input type='number' class='form-control' min='30' step='30' name='durationMinutes' value='").append(session.getDurationMinutes()).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Coach</label><select class='form-select' name='coach'>");

        for (Coach coach : coaches) {
            html.append("<option value='").append(coach.getName()).append("'")
                    .append(coach.getName().equals(session.getCoachName()) ? " selected" : "")
                    .append(">").append(coach.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-6'><label class='form-label'>Room</label><select class='form-select' name='room'>");

        for (Room room : rooms) {
            html.append("<option value='").append(room.getName()).append("'")
                    .append(room.getName().equals(session.getRoom()) ? " selected" : "")
                    .append(">").append(room.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Update Session</button>")
                .append("<a href='/admin/timetable' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append(pageEnd());
        return html.toString();
    }

    /*
     GENERATE TIMETABLE PAGE
    */

    public String getGeneratorPage() {

        List<GymClass> classes = classService.getAllClasses();
        List<Coach> coaches = coachService.getAllCoaches();
        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder(pageStart("Generate Timetable", "Timetable"));

        html.append("<div class='page-header'><h2><i class='bi bi-magic me-2'></i>Generate Weekly Timetable</h2></div>");
        html.append("<p class='text-muted'>Add one or more configurations below. Each configuration schedules a class for a set number of sessions per week, avoiding coach and room conflicts automatically.</p>");

        html.append("<form method='POST' action='/admin/generate-timetable'>")
                .append("<input type='hidden' id='maxIndex' name='maxIndex' value='0'>")
                .append("<div id='configContainer'>")
                .append(buildGeneratorConfigHtml(0, classes, coaches, rooms))
                .append("</div>")
                .append("<div class='d-flex gap-2 mt-3'>")
                .append("<button type='button' class='btn btn-outline-secondary' onclick='addConfig()'><i class='bi bi-plus-circle me-1'></i>Add Configuration</button>")
                .append("<button type='submit' class='btn btn-success'><i class='bi bi-magic me-1'></i>Generate Timetable</button>")
                .append("<a href='/admin/timetable' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form>");

        html.append("<script>")
                .append("let currentIndex = 0;")
                .append("function addConfig(){")
                .append("  currentIndex++;")
                .append("  document.getElementById('maxIndex').value = currentIndex;")
                .append("  const container = document.getElementById('configContainer');")
                .append("  const wrapper = document.createElement('div');")
                .append("  wrapper.innerHTML = `").append(escapeForJs(buildGeneratorConfigHtml("__INDEX__", classes, coaches, rooms))).append("`.replaceAll('__INDEX__', currentIndex);")
                .append("  container.appendChild(wrapper);")
                .append("}")
                .append("function removeConfig(btn){ btn.closest('.config-card').remove(); }")
                .append("</script>");

        html.append(pageEnd());
        return html.toString();
    }

    public void generateSmartTimetable(Map<String, String> params) {

        List<GenerationRequest> requests = new ArrayList<>();
        int maxIndex = Integer.parseInt(params.getOrDefault("maxIndex", "0"));

        for (int i = 0; i <= maxIndex; i++) {
            if (params.get("classId_" + i) == null) continue;

            GenerationRequest req = new GenerationRequest();
            req.classId = Integer.parseInt(params.get("classId_" + i));
            req.skillLevel = params.get("skillLevel_" + i);
            req.sessionsPerWeek = Integer.parseInt(params.get("sessionsPerWeek_" + i));
            req.durationMinutes = Integer.parseInt(params.get("durationMinutes_" + i));
            req.afterTime = params.get("afterTime_" + i);
            req.beforeTime = params.get("beforeTime_" + i);
            req.coachName = params.get("coachName_" + i);
            req.roomName = params.get("roomName_" + i);
            requests.add(req);
        }

        timetableService.generateMultiple(requests);
    }

    /*
    COACHES PAGE
    */

    public String getCoachesPage() {

        List<Coach> coaches = coachService.getAllCoaches();

        StringBuilder html = new StringBuilder(pageStart("Coaches", "Coaches"));

        html.append("<div class='page-header'><h2><i class='bi bi-person-badge me-2'></i>Coaches</h2></div>");

        html.append("<div class='card mb-4'><div class='card-header fw-semibold'>Add Coach</div><div class='card-body'>")
                .append("<form method='POST' action='/admin/coaches' class='row g-3'>")
                .append("<div class='col-md-5'><label class='form-label'>Name</label><input class='form-control' name='name' required></div>")
                .append("<div class='col-md-5'><label class='form-label'>Specialty</label><input class='form-control' name='specialty'></div>")
                .append("<div class='col-md-2 d-flex align-items-end'><button class='btn btn-primary w-100' type='submit'>Add</button></div>")
                .append("</form></div></div>");

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Specialty</th><th>Actions</th></tr></thead><tbody>");

        for (Coach c : coaches) {
            html.append("<tr>")
                    .append("<td>").append(c.getCoachId()).append("</td>")
                    .append("<td>").append(c.getName()).append("</td>")
                    .append("<td>").append(c.getSpecialty()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-coach?coachId=").append(c.getCoachId()).append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-coach' style='display:inline;'>")
                    .append("<input type='hidden' name='coachId' value='").append(c.getCoachId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete this coach?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void addCoach(String name, String specialty) {
        coachService.addCoach(name, specialty);
    }

    public void deleteCoach(int coachId) {
        coachService.deleteCoach(coachId);
    }

    public String getEditCoachPage(int coachId) {

        Coach coach = coachService.getCoachById(coachId);

        StringBuilder html = new StringBuilder(pageStart("Edit Coach", "Coaches"));

        html.append("<div class='page-header'><h2><i class='bi bi-pencil me-2'></i>Edit Coach</h2></div>");

        html.append("<div class='card' style='max-width:500px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/edit-coach' class='row g-3'>")
                .append("<input type='hidden' name='coachId' value='").append(coach.getCoachId()).append("'>")
                .append("<div class='col-12'><label class='form-label'>Name</label><input class='form-control' name='name' value='").append(coach.getName()).append("' required></div>")
                .append("<div class='col-12'><label class='form-label'>Specialty</label><input class='form-control' name='specialty' value='").append(coach.getSpecialty()).append("'></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Update</button>")
                .append("<a href='/admin/coaches' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append(pageEnd());
        return html.toString();
    }

    public void updateCoach(int coachId, String name, String specialty) {
        coachService.updateCoach(coachId, name, specialty);
    }

    /*
    ROOMS PAGE
    */

    public String getRoomsPage() {

        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder(pageStart("Rooms", "Rooms"));

        html.append("<div class='page-header'><h2><i class='bi bi-door-open me-2'></i>Rooms</h2></div>");

        html.append("<div class='card mb-4'><div class='card-header fw-semibold'>Add Room</div><div class='card-body'>")
                .append("<form method='POST' action='/admin/rooms' class='row g-3'>")
                .append("<div class='col-md-5'><label class='form-label'>Name</label><input class='form-control' name='name' required></div>")
                .append("<div class='col-md-5'><label class='form-label'>Capacity</label><input class='form-control' type='number' name='capacity' required></div>")
                .append("<div class='col-md-2 d-flex align-items-end'><button class='btn btn-primary w-100' type='submit'>Add</button></div>")
                .append("</form></div></div>");

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Capacity</th><th>Actions</th></tr></thead><tbody>");

        for (Room r : rooms) {
            html.append("<tr>")
                    .append("<td>").append(r.getRoomId()).append("</td>")
                    .append("<td>").append(r.getName()).append("</td>")
                    .append("<td>").append(r.getCapacity()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-room?roomId=").append(r.getRoomId()).append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-room' style='display:inline;'>")
                    .append("<input type='hidden' name='roomId' value='").append(r.getRoomId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete this room?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void addRoom(String name, int capacity) {
        roomService.addRoom(name, capacity);
    }

    public void deleteRoom(int roomId) {
        roomService.deleteRoom(roomId);
    }

    public String getEditRoomPage(int roomId) {

        Room room = roomService.getRoomById(roomId);

        StringBuilder html = new StringBuilder(pageStart("Edit Room", "Rooms"));

        html.append("<div class='page-header'><h2><i class='bi bi-pencil me-2'></i>Edit Room</h2></div>");

        html.append("<div class='card' style='max-width:500px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/edit-room' class='row g-3'>")
                .append("<input type='hidden' name='roomId' value='").append(room.getRoomId()).append("'>")
                .append("<div class='col-12'><label class='form-label'>Name</label><input class='form-control' name='name' value='").append(room.getName()).append("' required></div>")
                .append("<div class='col-12'><label class='form-label'>Capacity</label><input class='form-control' type='number' name='capacity' value='").append(room.getCapacity()).append("' required></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Update</button>")
                .append("<a href='/admin/rooms' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append(pageEnd());
        return html.toString();
    }

    public void updateRoom(int roomId, String name, int capacity) {
        roomService.updateRoom(roomId, name, capacity);
    }

    /*
     MEMBERS PAGE
    */

    public String getMembersPage() {

        List<MemberProfile> members = memberService.getAllProfiles();

        StringBuilder html = new StringBuilder(pageStart("Members", "Members"));

        html.append("<div class='page-header'><h2><i class='bi bi-people me-2'></i>Manage Members</h2></div>");

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Age</th><th>Height</th><th>Weight</th><th>Actions</th></tr></thead><tbody>");

        for (MemberProfile m : members) {
            html.append("<tr>")
                    .append("<td>").append(m.getMemberId()).append("</td>")
                    .append("<td>").append(m.getFirstName()).append(" ").append(m.getLastName()).append("</td>")
                    .append("<td>").append(m.getAge()).append("</td>")
                    .append("<td>").append(m.getHeightCm()).append(" cm</td>")
                    .append("<td>").append(m.getWeightKg()).append(" kg</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-member?memberId=").append(m.getMemberId()).append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-member' style='display:inline;'>")
                    .append("<input type='hidden' name='memberId' value='").append(m.getMemberId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete this member?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void deleteMember(int memberId) {
        memberService.deleteProfile(memberId);
    }

    public String getEditMemberPage(int memberId) {

        MemberProfile member = memberService.getProfileByMemberId(memberId);

        StringBuilder html = new StringBuilder(pageStart("Edit Member", "Members"));

        html.append("<div class='page-header'><h2><i class='bi bi-pencil me-2'></i>Edit Member</h2></div>");

        html.append("<div class='card' style='max-width:600px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/edit-member' class='row g-3'>")
                .append("<input type='hidden' name='memberId' value='").append(member.getMemberId()).append("'>")
                .append("<div class='col-md-6'><label class='form-label'>First Name</label><input class='form-control' name='firstName' value='").append(member.getFirstName()).append("' required></div>")
                .append("<div class='col-md-6'><label class='form-label'>Last Name</label><input class='form-control' name='lastName' value='").append(member.getLastName()).append("' required></div>")
                .append("<div class='col-md-4'><label class='form-label'>Age</label><input type='number' class='form-control' name='age' value='").append(member.getAge()).append("'></div>")
                .append("<div class='col-md-4'><label class='form-label'>Height (cm)</label><input type='number' class='form-control' name='heightCm' value='").append(member.getHeightCm()).append("'></div>")
                .append("<div class='col-md-4'><label class='form-label'>Weight (kg)</label><input type='number' step='0.1' class='form-control' name='weightKg' value='").append(member.getWeightKg()).append("'></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Update</button>")
                .append("<a href='/admin/members' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append(pageEnd());
        return html.toString();
    }

    public void updateMember(int memberId, String firstName, String lastName,
                             int age, int heightCm, double weightKg) {
        memberService.updateProfileByMemberId(memberId, firstName, lastName, age, heightCm, weightKg);
    }

    /*
     TOURNAMENTS / EVENTS PAGE
    */

    public String getTournamentsPage() {

        List<Event> events = eventService.getAllEvents();

        StringBuilder html = new StringBuilder(pageStart("Events", "Events"));

        html.append("<div class='page-header'><h2><i class='bi bi-trophy me-2'></i>Manage Events</h2></div>");

        // Create form
        html.append("<div class='card mb-4'><div class='card-header fw-semibold'>Create New Event</div><div class='card-body'>")
                .append("<form method='POST' action='/admin/tournaments' class='row g-3'>")
                .append("<div class='col-md-4'><label class='form-label'>Event Name</label><input class='form-control' name='eventName' required></div>")
                .append("<div class='col-md-2'><label class='form-label'>Date</label><input type='date' class='form-control' name='eventDate'></div>")
                .append("<div class='col-md-3'><label class='form-label'>Location</label><input class='form-control' name='location'></div>")
                .append("<div class='col-md-1'><label class='form-label'>Status</label><select class='form-select' name='status'>")
                .append("<option>Upcoming</option><option>Open</option><option>Live</option><option>Completed</option>")
                .append("</select></div>")
                .append("<div class='col-md-2'><label class='form-label'>Format</label><select class='form-select' name='format'>")
                .append("<option value='MATCHES'>Matches</option>")
                .append("</select></div>")
                .append("<div class='col-12'><label class='form-label fw-semibold'>Allowed Martial Arts</label><div class='d-flex flex-wrap gap-3'>");

        String[] arts = {"MMA","Boxing","Kickboxing","Muay Thai","Jiu Jitsu","Wrestling"};
        for (String art : arts) {
            html.append("<div class='form-check'><input class='form-check-input' type='checkbox' value='").append(art).append("' onchange='updateArts()'><label class='form-check-label'>").append(art).append("</label></div>");
        }
        html.append("</div><input type='hidden' id='allowedMartialArts' name='allowedMartialArts'></div>")
                .append("<div class='col-12'><button class='btn btn-primary' type='submit'><i class='bi bi-plus-circle me-1'></i>Create Event</button></div>")
                .append("</form></div></div>");

        html.append("<script>function updateArts(){const checked=document.querySelectorAll(\"input[type='checkbox']:checked\");document.getElementById('allowedMartialArts').value=Array.from(checked).map(c=>c.value).join(',');}</script>");

        // Events table
        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Date</th><th>Location</th><th>Status</th><th>Martial Arts</th><th>Actions</th></tr></thead><tbody>");

        for (Event e : events) {
            String statusClass = "bg-secondary";
            if ("Live".equals(e.getStatus())) statusClass = "bg-danger";
            else if ("Open".equals(e.getStatus())) statusClass = "bg-success";
            else if ("Completed".equals(e.getStatus())) statusClass = "bg-dark";

            html.append("<tr>")
                    .append("<td>").append(e.getEventId()).append("</td>")
                    .append("<td class='fw-semibold'>").append(e.getEventName()).append("</td>")
                    .append("<td>").append(e.getEventDate()).append("</td>")
                    .append("<td>").append(e.getLocation()).append("</td>")
                    .append("<td><span class='badge ").append(statusClass).append("'>").append(e.getStatus()).append("</span></td>")
                    .append("<td>").append(e.getAllowedMartialArts() == null ? "" : e.getAllowedMartialArts()).append("</td>")
                    .append("<td>")
                    .append("<div class='d-flex flex-wrap gap-1'>")
                    .append("<a href='/admin/edit-tournament?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-primary'>Edit</a>")
                    .append("<a href='/admin/view-entrants?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-secondary'>Entrants</a>")
                    .append("<a href='/admin/view-matches?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-secondary'>Matches</a>")
                    .append("<a href='/admin/event-results?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-info'>Results</a>")
                    .append("<a href='/admin/live-control?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-danger'>Live</a>")
                    .append("<form method='POST' action='/admin/start-event' style='display:inline;'>")
                    .append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>")
                    .append("<button class='btn btn-sm btn-success' type='submit'>Start</button>")
                    .append("</form>")
                    .append("<form method='POST' action='/admin/delete-tournament' style='display:inline;'>")
                    .append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete event?\")'>Delete</button>")
                    .append("</form>")
                    .append("</div></td></tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void createEvent(String eventName, String eventDate, String location,
                            String status, String format, String allowedMartialArts) {
        eventService.createEvent(eventName, eventDate, location, status, format, allowedMartialArts);
    }

    public void deleteEvent(int eventId) {
        eventService.deleteEvent(eventId);
    }

    public void updateEvent(int eventId, String eventName, String eventDate, String location,
                            String status, String format, String allowedMartialArts) {
        eventService.updateEvent(eventId, eventName, eventDate, location, status, format, allowedMartialArts);
    }

    public String getEditTournamentPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        String allowed = event.getAllowedMartialArts() == null ? "" : event.getAllowedMartialArts();

        StringBuilder html = new StringBuilder(pageStart("Edit Event", "Events"));

        html.append("<div class='page-header'><h2><i class='bi bi-pencil me-2'></i>Edit Event</h2></div>");

        html.append("<div class='card' style='max-width:700px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/edit-tournament' class='row g-3'>")
                .append("<input type='hidden' name='eventId' value='").append(event.getEventId()).append("'>")
                .append("<div class='col-md-6'><label class='form-label'>Event Name</label><input class='form-control' name='eventName' value='").append(event.getEventName()).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Date</label><input type='date' class='form-control' name='eventDate' value='").append(event.getEventDate()).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Location</label><input class='form-control' name='location' value='").append(event.getLocation()).append("'></div>")
                .append("<div class='col-md-3'><label class='form-label'>Status</label><select class='form-select' name='status'>");

        for (String s : new String[]{"Upcoming","Open","Closed","Completed"}) {
            html.append("<option value='").append(s).append("'").append(s.equals(event.getStatus()) ? " selected" : "").append(">").append(s).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-3'><label class='form-label'>Format</label><select class='form-select' name='format'>")
                .append("<option value='MATCHES'").append("MATCHES".equals(event.getFormat()) ? " selected" : "").append(">Matches</option>")
                .append("<option value='BRACKET'").append("BRACKET".equals(event.getFormat()) ? " selected" : "").append(">Bracket</option>")
                .append("</select></div>")
                .append("<div class='col-12'><label class='form-label'>Allowed Martial Arts (comma separated)</label>")
                .append("<input class='form-control' name='allowedMartialArts' value='").append(allowed).append("'></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Update</button>")
                .append("<a href='/admin/tournaments' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append(pageEnd());
        return html.toString();
    }

    /*
    ENTRANTS PAGE
    */

    public String getEntrantsPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        List<MemberProfile> entrants = eventService.getEntrantsForEvent(eventId);

        StringBuilder html = new StringBuilder(pageStart("Entrants", "Events"));

        html.append("<div class='page-header d-flex justify-content-between align-items-center'>")
                .append("<h2><i class='bi bi-people me-2'></i>Entrants — ").append(event.getEventName()).append("</h2>")
                .append("<a href='/admin/tournaments' class='btn btn-secondary'><i class='bi bi-arrow-left me-1'></i>Back</a>")
                .append("</div>");

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Age</th><th>Weight</th></tr></thead><tbody>");

        for (MemberProfile m : entrants) {
            html.append("<tr>")
                    .append("<td>").append(m.getMemberId()).append("</td>")
                    .append("<td>").append(m.getFirstName()).append(" ").append(m.getLastName()).append("</td>")
                    .append("<td>").append(m.getAge()).append("</td>")
                    .append("<td>").append(m.getWeightKg()).append(" kg</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void generateMatchesForEvent(int eventId) {
        matchmakingService.generateMatchesForEvent(eventId);
    }

    /*
     MATCHES PAGE
    */

    public String getMatchesPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);
        List<String> matchMessages = matchmakingService.getLastGenerationMessages();

        StringBuilder html = new StringBuilder(pageStart("Matches", "Events"));

        html.append("<div class='page-header d-flex justify-content-between align-items-center'>")
                .append("<h2><i class='bi bi-diagram-3 me-2'></i>Matches — ").append(event.getEventName()).append("</h2>")
                .append("<div class='d-flex gap-2'>")
                .append("<form method='POST' action='/admin/generate-matches' style='display:inline;'>")
                .append("<input type='hidden' name='eventId' value='").append(eventId).append("'>")
                .append("<button class='btn btn-success' type='submit'><i class='bi bi-magic me-1'></i>Generate Matches</button>")
                .append("</form>")
                .append("<a href='/admin/tournaments' class='btn btn-secondary'><i class='bi bi-arrow-left me-1'></i>Back</a>")
                .append("</div></div>");

        if (!matchMessages.isEmpty()) {
            html.append("<div class='alert alert-info'><strong>Matchmaking Log:</strong><ul class='mb-0 mt-1'>");
            for (String msg : matchMessages) html.append("<li>").append(msg).append("</li>");
            html.append("</ul></div>");
        }

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>Match</th><th>Participant 1</th><th>Participant 2</th><th>Status</th><th>Round</th></tr></thead><tbody>");

        for (Match m : matches) {
            html.append("<tr>")
                    .append("<td>#").append(m.getMatchId()).append("</td>")
                    .append("<td>").append(m.getParticipant1Name()).append("</td>")
                    .append("<td>").append(m.getParticipant2Name()).append("</td>")
                    .append("<td><span class='badge bg-secondary'>").append(m.getStatus()).append("</span></td>")
                    .append("<td>").append(m.getRoundNumber()).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    /*
     EVENT RESULTS PAGE
    */

    public String getAdminEventResultsPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);

        StringBuilder html = new StringBuilder(pageStart("Event Results", "Events"));

        html.append("<div class='page-header d-flex justify-content-between align-items-center'>")
                .append("<h2><i class='bi bi-bar-chart me-2'></i>Results — ").append(event.getEventName()).append("</h2>")
                .append("<a href='/admin/tournaments' class='btn btn-secondary'><i class='bi bi-arrow-left me-1'></i>Back</a>")
                .append("</div>");

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>Match</th><th>Participant 1</th><th>Participant 2</th><th>Status</th><th>Round</th><th>Winner</th><th>Decision</th><th>Control</th></tr></thead><tbody>");

        for (Match m : matches) {
            String statusClass = "Live".equals(m.getStatus()) ? "bg-danger" : "Completed".equals(m.getStatus()) ? "bg-dark" : "bg-secondary";

            html.append("<tr>")
                    .append("<td>#").append(m.getMatchId()).append("</td>")
                    .append("<td>").append(m.getParticipant1Name()).append("</td>")
                    .append("<td>").append(m.getParticipant2Name()).append("</td>")
                    .append("<td><span class='badge ").append(statusClass).append("'>").append(m.getStatus()).append("</span></td>")
                    .append("<td>").append(m.getRoundNumber()).append("</td>")
                    .append("<td>").append(m.getWinnerName() == null ? "<span class='text-muted'>-</span>" : "<strong>" + m.getWinnerName() + "</strong>").append("</td>")
                    .append("<td>").append(m.getResult() == null || m.getResult().isBlank() ? "<span class='text-muted'>-</span>" : m.getResult()).append("</td>")
                    .append("<td><a href='/admin/live-control?eventId=").append(eventId).append("&matchId=").append(m.getMatchId()).append("' class='btn btn-sm btn-danger'>Control</a></td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    /*
    LIVE CONTROL PAGE
    */

    public String getAdminLiveControlPage(int eventId, Integer selectedMatchId) {

        Event event = eventService.getEventById(eventId);
        LiveEventState state = liveEventService.getStateForEvent(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);

        if (selectedMatchId != null) {
            liveEventService.setCurrentMatch(eventId, selectedMatchId);
            state = liveEventService.getStateForEvent(eventId);
        }

        StringBuilder html = new StringBuilder(pageStart("Live Control", "Events"));

        // Auto-refresh tick when timer running
        if (state.isTimerRunning()) {
            html.append("<meta http-equiv='refresh' content='1;url=/admin/live-control/tick-and-return?eventId=").append(eventId).append("'>");
        }

        html.append("<div class='page-header d-flex justify-content-between align-items-center'>")
                .append("<h2><i class='bi bi-broadcast me-2'></i>Live Control — ").append(event.getEventName()).append("</h2>")
                .append("<a href='/admin/event-results?eventId=").append(eventId).append("' class='btn btn-secondary'><i class='bi bi-arrow-left me-1'></i>Back to Results</a>")
                .append("</div>");

        // Timer display card
        String timerColor = state.isTimerRunning() ? "bg-danger" : "bg-dark";
        html.append("<div class='row g-3 mb-4'>")
                .append("<div class='col-md-4'>")
                .append("<div class='card text-white ").append(timerColor).append(" text-center'>")
                .append("<div class='card-body'>")
                .append("<div style='font-size:3.5rem;font-weight:700;letter-spacing:4px'>").append(formatSeconds(state.getRemainingSeconds())).append("</div>")
                .append("<div class='mt-1'>Round <strong>").append(state.getCurrentRound()).append("</strong> &nbsp;|&nbsp; ")
                .append(state.isTimerRunning() ? "<span class='badge bg-light text-danger'>LIVE</span>" : "<span class='badge bg-light text-dark'>PAUSED</span>")
                .append("</div></div></div></div>")

                // Timer controls
                .append("<div class='col-md-8 d-flex flex-column justify-content-center gap-2'>")
                .append("<div class='d-flex gap-2 flex-wrap'>")
                .append(postBtn("/admin/live-control/start", eventId, "btn-success", "bi-play-fill", "Start"))
                .append(postBtn("/admin/live-control/pause", eventId, "btn-warning", "bi-pause-fill", "Pause"))
                .append(postBtn("/admin/live-control/reset", eventId, "btn-secondary", "bi-arrow-counterclockwise", "Reset"))
                .append(postBtn("/admin/live-control/tick",  eventId, "btn-outline-dark", "bi-skip-backward", "Tick -1s"))
                .append("</div>")

                .append("<form method='POST' action='/admin/live-control/set-round' class='d-flex gap-2 align-items-center'>")
                .append("<input type='hidden' name='eventId' value='").append(eventId).append("'>")
                .append("<label class='form-label mb-0'>Round:</label>")
                .append("<input type='number' class='form-control' style='width:80px' name='round' value='").append(state.getCurrentRound()).append("'>")
                .append("<button class='btn btn-outline-primary' type='submit'>Set Round</button>")
                .append("</form>")

                .append("<form method='POST' action='/admin/live-control/set-round-time' class='d-flex gap-2 align-items-center'>")
                .append("<input type='hidden' name='eventId' value='").append(eventId).append("'>")
                .append("<label class='form-label mb-0'>Round Time (s):</label>")
                .append("<input type='number' class='form-control' style='width:100px' name='roundTimeSeconds' value='").append(state.getRoundTimeSeconds()).append("'>")
                .append("<button class='btn btn-outline-primary' type='submit'>Set Time</button>")
                .append("</form>")
                .append("</div></div>");

        // Select current match
        html.append("<div class='card mb-4'><div class='card-header fw-semibold'>Select Current Match</div><div class='card-body'>")
                .append("<form method='POST' action='/admin/live-control/select-match' class='d-flex gap-2 align-items-center'>")
                .append("<input type='hidden' name='eventId' value='").append(eventId).append("'>")
                .append("<select class='form-select' name='matchId'>");

        for (Match m : matches) {
            boolean sel = state.getCurrentMatchId() != null && state.getCurrentMatchId() == m.getMatchId();
            html.append("<option value='").append(m.getMatchId()).append("'").append(sel ? " selected" : "").append(">")
                    .append("Match #").append(m.getMatchId()).append(": ").append(m.getParticipant1Name()).append(" vs ").append(m.getParticipant2Name())
                    .append("</option>");
        }

        html.append("</select><button class='btn btn-primary' type='submit'>Set Match</button></form></div></div>");

        // Result entry
        if (state.getCurrentMatchId() != null) {
            Match currentMatch = null;
            for (Match m : matches) {
                if (m.getMatchId() == state.getCurrentMatchId()) { currentMatch = m; break; }
            }

            if (currentMatch != null) {
                html.append("<div class='card'><div class='card-header fw-semibold'>Enter Result — Match #").append(currentMatch.getMatchId()).append("</div>")
                        .append("<div class='card-body'>")
                        .append("<form method='POST' action='/admin/live-control/result' class='row g-3'>")
                        .append("<input type='hidden' name='eventId' value='").append(eventId).append("'>")
                        .append("<input type='hidden' name='matchId' value='").append(currentMatch.getMatchId()).append("'>")

                        .append("<div class='col-md-3'><label class='form-label'>Status</label><select class='form-select' name='status'>")
                        .append("<option value='Scheduled'>Scheduled</option><option value='Live'>Live</option><option value='Completed'>Completed</option>")
                        .append("</select></div>")

                        .append("<div class='col-md-3'><label class='form-label'>Winner</label><select class='form-select' name='winnerMemberId'>")
                        .append("<option value=''>No winner / Draw</option>")
                        .append("<option value='").append(currentMatch.getParticipant1Id()).append("'>").append(currentMatch.getParticipant1Name()).append("</option>")
                        .append("<option value='").append(currentMatch.getParticipant2Id()).append("'>").append(currentMatch.getParticipant2Name()).append("</option>")
                        .append("</select></div>")

                        .append("<div class='col-md-3'><label class='form-label'>Decision</label><select class='form-select' name='result'>")
                        .append("<option value='Decision'>Decision</option><option value='KO/TKO'>KO/TKO</option>")
                        .append("<option value='Submission'>Submission</option><option value='Draw'>Draw</option>")
                        .append("</select></div>")

                        .append("<div class='col-md-2'><label class='form-label'>Round No.</label>")
                        .append("<input type='number' class='form-control' name='roundNumber' value='").append(state.getCurrentRound()).append("'></div>")

                        .append("<div class='col-12'>")
                        .append("<button class='btn btn-success' type='submit'><i class='bi bi-check-lg me-1'></i>Save Result</button>")
                        .append("</div></form></div></div>");
            }
        }

        html.append(pageEnd());
        return html.toString();
    }

    private String postBtn(String action, int eventId, String btnClass, String icon, String label) {
        return "<form method='POST' action='" + action + "' style='display:inline;'>" +
                "<input type='hidden' name='eventId' value='" + eventId + "'>" +
                "<button class='btn " + btnClass + "' type='submit'>" +
                "<i class='bi " + icon + " me-1'></i>" + label + "</button></form>";
    }

    private String formatSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setCurrentMatch(int eventId, int matchId) { liveEventService.setCurrentMatch(eventId, matchId); }
    public void setCurrentRound(int eventId, int round) { liveEventService.setRound(eventId, round); }
    public void setRoundTime(int eventId, int roundTimeSeconds) { liveEventService.setRoundTime(eventId, roundTimeSeconds); }
    public void startLiveTimer(int eventId) { liveEventService.startTimer(eventId); }
    public void pauseLiveTimer(int eventId) { liveEventService.pauseTimer(eventId); }
    public void resetLiveTimer(int eventId) { liveEventService.resetTimer(eventId); }
    public void tickLiveTimer(int eventId) { liveEventService.tickTimer(eventId); }

    public void saveMatchResult(int matchId, String status, String result, Integer winnerMemberId, int roundNumber) {
        liveEventService.updateMatchResult(matchId, status, result, winnerMemberId, roundNumber);
    }
    public void startEvent(int eventId) {
        Event event = eventService.getEventById(eventId);
        if (event != null) {
            eventService.updateEvent(eventId, event.getEventName(), event.getEventDate(),
                    event.getLocation(), "Live", event.getFormat(), event.getAllowedMartialArts());
        }
    }

    /*
    MEMBERSHIPS PAGE
    */

    public String getMembershipsPage() {

        List<Membership> memberships = membershipService.getAllMemberships();

        StringBuilder html = new StringBuilder(pageStart("Memberships", "Memberships"));

        html.append("<div class='page-header'><h2><i class='bi bi-card-checklist me-2'></i>Manage Memberships</h2></div>");

        html.append("<div class='card mb-4'><div class='card-header fw-semibold'>Create Membership</div><div class='card-body'>")
                .append("<form method='POST' action='/admin/memberships' class='row g-3'>")
                .append("<div class='col-md-4'><label class='form-label'>Membership Name</label><input class='form-control' name='membershipName' required></div>")
                .append("<div class='col-md-4'><label class='form-label'>Description</label><input class='form-control' name='description'></div>")
                .append("<div class='col-md-2'><label class='form-label'>Allowed Martial Arts</label>")
                .append("<select id='artsSelect' class='form-select' multiple size='6' onchange='syncArts()'>")
                .append("<option value='MMA'>MMA</option><option value='Boxing'>Boxing</option>")
                .append("<option value='Kickboxing'>Kickboxing</option><option value='Muay Thai'>Muay Thai</option>")
                .append("<option value='Jiu Jitsu'>Jiu Jitsu</option><option value='Wrestling'>Wrestling</option>")
                .append("</select><input type='hidden' id='allowedMartialArts' name='allowedMartialArts'></div>")
                .append("<div class='col-md-2'><label class='form-label'>Allowed Skill Levels</label>")
                .append("<select id='skillsSelect' class='form-select' multiple size='2' onchange='syncSkills()'>")
                .append("<option value='Beginner'>Beginner</option><option value='Intermediate/Advanced'>Intermediate/Advanced</option>")
                .append("</select><input type='hidden' id='allowedSkillLevels' name='allowedSkillLevels'></div>")
                .append("<div class='col-12'><button class='btn btn-primary' type='submit'><i class='bi bi-plus-circle me-1'></i>Create</button></div>")
                .append("</form></div></div>");

        html.append("<script>")
                .append("function syncArts(){document.getElementById('allowedMartialArts').value=Array.from(document.getElementById('artsSelect').selectedOptions).map(o=>o.value).join(',');}")
                .append("function syncSkills(){document.getElementById('allowedSkillLevels').value=Array.from(document.getElementById('skillsSelect').selectedOptions).map(o=>o.value).join(',');}")
                .append("</script>");

        html.append("<div class='card'><div class='card-body p-0'>")
                .append("<table class='table table-hover mb-0'>")
                .append("<thead><tr><th>ID</th><th>Name</th><th>Description</th><th>Martial Arts</th><th>Skill Levels</th><th>Actions</th></tr></thead><tbody>");

        for (Membership m : memberships) {
            html.append("<tr>")
                    .append("<td>").append(m.getMembershipId()).append("</td>")
                    .append("<td class='fw-semibold'>").append(m.getMembershipName()).append("</td>")
                    .append("<td>").append(m.getDescription()).append("</td>")
                    .append("<td>").append(m.getAllowedMartialArts()).append("</td>")
                    .append("<td>").append(m.getAllowedSkillLevels()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-membership?membershipId=").append(m.getMembershipId()).append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-membership' style='display:inline;'>")
                    .append("<input type='hidden' name='membershipId' value='").append(m.getMembershipId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete membership?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        html.append("</tbody></table></div></div>");
        html.append(pageEnd());
        return html.toString();
    }

    public void createMembership(String membershipName, String description,
                                 String allowedMartialArts, String allowedSkillLevels) {
        membershipService.createMembership(membershipName, description, allowedMartialArts, allowedSkillLevels);
    }

    public void deleteMembership(int membershipId) {
        membershipService.deleteMembership(membershipId);
    }

    public String getEditMembershipPage(int membershipId) {

        Membership membership = membershipService.getMembershipById(membershipId);
        String allowedArts   = membership.getAllowedMartialArts() == null ? "" : membership.getAllowedMartialArts();
        String allowedSkills = membership.getAllowedSkillLevels() == null ? "" : membership.getAllowedSkillLevels();

        StringBuilder html = new StringBuilder(pageStart("Edit Membership", "Memberships"));

        html.append("<div class='page-header'><h2><i class='bi bi-pencil me-2'></i>Edit Membership</h2></div>");

        html.append("<div class='card' style='max-width:700px'><div class='card-body'>")
                .append("<form method='POST' action='/admin/edit-membership' class='row g-3'>")
                .append("<input type='hidden' name='membershipId' value='").append(membership.getMembershipId()).append("'>")
                .append("<div class='col-md-6'><label class='form-label'>Name</label><input class='form-control' name='membershipName' value='").append(membership.getMembershipName()).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Description</label><input class='form-control' name='description' value='").append(membership.getDescription()).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Allowed Martial Arts</label>")
                .append("<select id='artsSelect' class='form-select' multiple size='6' onchange='syncArts()'>");

        for (String art : new String[]{"MMA","Boxing","Kickboxing","Muay Thai","Jiu Jitsu","Wrestling"}) {
            html.append("<option value='").append(art).append("'").append(containsCsv(allowedArts, art) ? " selected" : "").append(">").append(art).append("</option>");
        }
        html.append("</select><input type='hidden' id='allowedMartialArts' name='allowedMartialArts' value='").append(allowedArts).append("'></div>")
                .append("<div class='col-md-6'><label class='form-label'>Allowed Skill Levels</label>")
                .append("<select id='skillsSelect' class='form-select' multiple size='2' onchange='syncSkills()'>");

        for (String sk : new String[]{"Beginner","Intermediate/Advanced"}) {
            html.append("<option value='").append(sk).append("'").append(containsCsv(allowedSkills, sk) ? " selected" : "").append(">").append(sk).append("</option>");
        }
        html.append("</select><input type='hidden' id='allowedSkillLevels' name='allowedSkillLevels' value='").append(allowedSkills).append("'></div>")
                .append("<div class='col-12 d-flex gap-2'>")
                .append("<button class='btn btn-primary' type='submit'><i class='bi bi-check-lg me-1'></i>Update</button>")
                .append("<a href='/admin/memberships' class='btn btn-secondary'>Cancel</a>")
                .append("</div></form></div></div>");

        html.append("<script>")
                .append("function syncArts(){document.getElementById('allowedMartialArts').value=Array.from(document.getElementById('artsSelect').selectedOptions).map(o=>o.value).join(',');}")
                .append("function syncSkills(){document.getElementById('allowedSkillLevels').value=Array.from(document.getElementById('skillsSelect').selectedOptions).map(o=>o.value).join(',');}")
                .append("</script>");

        html.append(pageEnd());
        return html.toString();
    }

    public void updateMembership(int membershipId, String membershipName, String description,
                                 String allowedMartialArts, String allowedSkillLevels) {
        membershipService.updateMembership(membershipId, membershipName, description, allowedMartialArts, allowedSkillLevels);
    }

    /*
    PRIVATE HELPERS
    */

    private String buildGeneratorConfigHtml(Object index, List<GymClass> classes,
                                            List<Coach> coaches, List<Room> rooms) {
        String idx = String.valueOf(index);
        StringBuilder html = new StringBuilder();

        html.append("<div class='card config-card mb-3'><div class='card-body row g-3'>")
                .append("<div class='col-12 d-flex justify-content-between align-items-center'>")
                .append("<span class='fw-semibold text-muted'>Configuration ").append(idx).append("</span>")
                .append("<button type='button' class='btn btn-sm btn-outline-danger' onclick='removeConfig(this)'>Remove</button>")
                .append("</div>")
                .append("<div class='col-md-3'><label class='form-label'>Class</label><select class='form-select' name='classId_").append(idx).append("'>");
        for (GymClass c : classes) {
            html.append("<option value='").append(c.getClassId()).append("'>").append(c.getClassName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-2'><label class='form-label'>Skill Level</label><select class='form-select' name='skillLevel_").append(idx).append("'>")
                .append("<option value='Beginner'>Beginner</option><option value='Intermediate/Advanced'>Intermediate/Advanced</option>")
                .append("</select></div>")
                .append("<div class='col-md-1'><label class='form-label'>Sessions/week</label><input type='number' class='form-control' min='1' max='5' name='sessionsPerWeek_").append(idx).append("'></div>")
                .append("<div class='col-md-2'><label class='form-label'>Duration (min)</label><input type='number' class='form-control' min='30' step='30' name='durationMinutes_").append(idx).append("'></div>")
                .append("<div class='col-md-1'><label class='form-label'>After</label><input type='time' class='form-control' name='afterTime_").append(idx).append("'></div>")
                .append("<div class='col-md-1'><label class='form-label'>Before</label><input type='time' class='form-control' name='beforeTime_").append(idx).append("'></div>")
                .append("<div class='col-md-2'><label class='form-label'>Coach</label><select class='form-select' name='coachName_").append(idx).append("'>");
        for (Coach c : coaches) {
            html.append("<option value='").append(c.getName()).append("'>").append(c.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-2'><label class='form-label'>Room</label><select class='form-select' name='roomName_").append(idx).append("'>");
        for (Room r : rooms) {
            html.append("<option value='").append(r.getName()).append("'>").append(r.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("</div></div>");

        return html.toString();
    }

    private boolean containsCsv(String csv, String value) {
        if (csv == null || csv.isBlank()) return false;
        for (String part : csv.split(",")) {
            if (part.trim().equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    private String escapeForJs(String input) {
        return input.replace("\\", "\\\\").replace("`", "\\`").replace("\n", "").replace("\r", "");
    }
}