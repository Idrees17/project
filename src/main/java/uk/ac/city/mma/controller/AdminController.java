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


    public String getClassesPage() {

        List<GymClass> classes = classService.getAllClasses();

        StringBuilder html = new StringBuilder();

        html.append("<html>");
        html.append("<head><title>Class Management</title></head>");
        html.append("<body>");

        html.append("<h1>Manage Classes</h1>");

        html.append("<h2>Create New Class</h2>");

        html.append("<form method='POST' action='/admin/classes'>");

        html.append("Class Name: <input name='className'><br>");
        html.append("Description: <input name='description'><br>");
        html.append("Skill Level: <input name='skillLevel'><br>");
        html.append("Capacity: <input name='capacity' type='number'><br>");

        html.append("<button type='submit'>Create Class</button>");

        html.append("</form>");

        html.append("<h2>Existing Classes</h2>");

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>Skill Level</th>");
        html.append("<th>Capacity</th>");
        html.append("<th>Action</th>");
        html.append("<th>Session</th>");
        html.append("</tr>");


        for (GymClass c : classes) {

            html.append("<tr>");

            html.append("<td>").append(c.getClassId()).append("</td>");
            html.append("<td>").append(c.getClassName()).append("</td>");
            html.append("<td>").append(c.getSkillLevel()).append("</td>");
            html.append("<td>").append(c.getCapacity()).append("</td>");

            html.append("<td>");
            html.append("<form method='POST' action='/admin/delete-class' style='display:inline;'>");
            html.append("<input type='hidden' name='classId' value='")
                    .append(c.getClassId())
                    .append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form>");
            html.append("</td>");

            html.append("<td>");
            html.append("<a href='/admin/add-session?classId=")
                    .append(c.getClassId())
                    .append("'>Add Session</a>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><br>");
        html.append("<button onclick=\"location.href='/admin-dashboard'\">Back</button>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    public void createClass(String name,String description,String skill,int capacity){

        classService.createClass(name,description,skill,capacity);
    }

    public void deleteClass(int classId){
        classService.deleteClass(classId);
    }

    public String getAddSessionPage() {

        List<GymClass> classes = classService.getAllClasses();
        List<Coach> coaches = coachService.getAllCoaches();
        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Add Session</h1>");
        html.append("<form method='POST' action='/admin/add-session'>");

        html.append("Class: <select name='classId'>");
        for (GymClass c : classes) {
            html.append("<option value='").append(c.getClassId()).append("'>")
                    .append(c.getClassName()).append("</option>");
        }
        html.append("</select><br>");

        html.append("Day: <input name='day'><br>");
        html.append("Start Time: <input type='time' name='time'><br>");
        html.append("Duration (minutes): <input type='number' min='30' step='30' name='durationMinutes'><br>");

        html.append("Coach: <select name='coachName'>");
        for (Coach coach : coaches) {
            html.append("<option value='").append(coach.getName()).append("'>")
                    .append(coach.getName()).append("</option>");
        }
        html.append("</select><br>");

        html.append("Room: <select name='roomName'>");
        for (Room room : rooms) {
            html.append("<option value='").append(room.getName()).append("'>")
                    .append(room.getName()).append("</option>");
        }
        html.append("</select><br>");

        html.append("<button type='submit'>Create Session</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/timetable'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void createSession(int classId, String day, String time,
                              int durationMinutes, String coachName, String roomName) {
        classSessionService.createSession(classId, day, time, durationMinutes, coachName, roomName);
    }

    public String getTimetablePage() {

        List<ClassSession> sessions = classSessionService.getAllSessions();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Timetable Management</h1>");

        html.append("<button onclick=\"location.href='/admin/add-session'\">Add Session</button> ");
        html.append("<button onclick=\"location.href='/admin/generate-timetable'\">Generate Timetable</button>");
        html.append("<br><br>");

        List<String> generationMessages = timetableService.getLastGenerationMessages();

        if (!generationMessages.isEmpty()) {
            html.append("<h2>Generation Messages</h2>");
            html.append("<ul>");
            for (String msg : generationMessages) {
                html.append("<li>").append(msg).append("</li>");
            }
            html.append("</ul><br>");
        }

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Class</th>");
        html.append("<th>Skill Level</th>");
        html.append("<th>Day</th>");
        html.append("<th>Start Time</th>");
        html.append("<th>Duration</th>");
        html.append("<th>Coach</th>");
        html.append("<th>Room</th>");
        html.append("<th>Source</th>");
        html.append("<th>Actions</th>");
        html.append("</tr>");

        for (ClassSession s : sessions) {

            html.append("<tr>");

            html.append("<td>").append(s.getClassName()).append("</td>");
            html.append("<td>").append(s.getSkillLevel()).append("</td>");
            html.append("<td>").append(s.getDayOfWeek()).append("</td>");
            html.append("<td>").append(s.getStartTime()).append("</td>");
            html.append("<td>").append(s.getDurationMinutes()).append(" mins</td>");
            html.append("<td>").append(s.getCoachName()).append("</td>");
            html.append("<td>").append(s.getRoom()).append("</td>");
            html.append("<td>").append(s.isGenerated() ? "Generated" : "Manual").append("</td>");

            html.append("<td>");

            html.append("<form method='POST' action='/admin/delete-session' style='display:inline;'>");
            html.append("<input type='hidden' name='sessionId' value='")
                    .append(s.getSessionId())
                    .append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form> ");

            html.append("<a href='/admin/edit-session?sessionId=")
                    .append(s.getSessionId())
                    .append("'>Edit</a>");

            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><br>");
        html.append("<button onclick=\"location.href='/admin-dashboard'\">Back</button>");

        html.append("</body></html>");

        return html.toString();
    }

    public void deleteSession(int sessionId){
        classSessionService.deleteSession(sessionId);
    }

    public void updateSession(int sessionId, String day, String time,
                              int durationMinutes, String coach, String room) {
        classSessionService.updateSession(sessionId, day, time, durationMinutes, coach, room);
    }

    public String getEditSessionPage(int sessionId) {

        ClassSession session = classSessionService.getSessionById(sessionId);
        List<Coach> coaches = coachService.getAllCoaches();
        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Edit Session</h1>");

        html.append("<form method='POST' action='/admin/edit-session'>");

        html.append("<input type='hidden' name='sessionId' value='")
                .append(session.getSessionId())
                .append("'>");

        html.append("<p><strong>Class:</strong> ")
                .append(session.getClassName())
                .append("</p>");

        // Day dropdown
        html.append("Day: <select name='day'>");
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            html.append("<option value='").append(day).append("'");
            if (day.equals(session.getDayOfWeek())) {
                html.append(" selected");
            }
            html.append(">").append(day).append("</option>");
        }
        html.append("</select><br>");

        // Time input prefilled
        html.append("Start Time: <input type='time' name='time' value='")
                .append(session.getStartTime())
                .append("'><br>");

        // Duration input prefilled
        html.append("Duration (minutes): <input type='number' min='30' step='30' name='durationMinutes' value='")
                .append(session.getDurationMinutes())
                .append("'><br>");

        // Coach dropdown
        html.append("Coach: <select name='coach'>");
        for (Coach coach : coaches) {
            html.append("<option value='").append(coach.getName()).append("'");
            if (coach.getName().equals(session.getCoachName())) {
                html.append(" selected");
            }
            html.append(">").append(coach.getName()).append("</option>");
        }
        html.append("</select><br>");

        // Room dropdown
        html.append("Room: <select name='room'>");
        for (Room room : rooms) {
            html.append("<option value='").append(room.getName()).append("'");
            if (room.getName().equals(session.getRoom())) {
                html.append(" selected");
            }
            html.append(">").append(room.getName()).append("</option>");
        }
        html.append("</select><br>");

        html.append("<button type='submit'>Update Session</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/timetable'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public String getGeneratorPage() {

        List<GymClass> classes = classService.getAllClasses();
        List<Coach> coaches = coachService.getAllCoaches();
        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Generate Weekly Timetable</h1>");

        html.append("<form method='POST' action='/admin/generate-timetable'>");
        html.append("<input type='hidden' id='maxIndex' name='maxIndex' value='0'>");

        html.append("<div id='configContainer'>");
        html.append(buildGeneratorConfigHtml(0, classes, coaches, rooms));
        html.append("</div>");

        html.append("<br>");
        html.append("<button type='button' onclick='addConfig()'>Add Configuration</button> ");
        html.append("<button type='submit'>Generate Timetable</button> ");
        html.append("<button type=\"button\" onclick=\"location.href='/admin/timetable'\">Back</button>");

        html.append("</form>");

        html.append("<script>");
        html.append("let currentIndex = 0;");
        html.append("function addConfig(){");
        html.append("  currentIndex++;");
        html.append("  document.getElementById('maxIndex').value = currentIndex;");
        html.append("  const container = document.getElementById('configContainer');");
        html.append("  const wrapper = document.createElement('div');");
        html.append("  wrapper.innerHTML = `").append(escapeForJs(buildGeneratorConfigHtml("__INDEX__", classes, coaches, rooms))).append("`.replaceAll('__INDEX__', currentIndex);");
        html.append("  container.appendChild(wrapper);");
        html.append("}");
        html.append("function removeConfig(btn){");
        html.append("  btn.parentElement.remove();");
        html.append("}");
        html.append("</script>");

        html.append("</body></html>");

        return html.toString();
    }

    public void generateSmartTimetable(Map<String, String> params) {

        List<GenerationRequest> requests = new ArrayList<>();

        int maxIndex = Integer.parseInt(params.getOrDefault("maxIndex", "0"));

        for (int i = 0; i <= maxIndex; i++) {

            if (params.get("classId_" + i) == null) {
                continue;
            }

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



    public String getCoachesPage() {

        List<Coach> coaches = coachService.getAllCoaches();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Coaches</h1>");

        html.append("<form method='POST' action='/admin/coaches'>");
        html.append("Name: <input name='name'><br>");
        html.append("Specialty: <input name='specialty'><br>");
        html.append("<button type='submit'>Add Coach</button>");
        html.append("</form><br>");

        html.append("<table border='1'>");
        html.append("<tr><th>ID</th><th>Name</th><th>Specialty</th><th>Actions</th></tr>");

        for (Coach c : coaches) {
            html.append("<tr>");

            html.append("<td>").append(c.getCoachId()).append("</td>");
            html.append("<td>").append(c.getName()).append("</td>");
            html.append("<td>").append(c.getSpecialty()).append("</td>");

            html.append("<td>");

            html.append("<a href='/admin/edit-coach?coachId=")
                    .append(c.getCoachId())
                    .append("'>Edit</a> ");

            html.append("<form method='POST' action='/admin/delete-coach' style='display:inline;'>");
            html.append("<input type='hidden' name='coachId' value='").append(c.getCoachId()).append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form>");

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin-dashboard'\">Back to Dashboard</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public String getRoomsPage() {

        List<Room> rooms = roomService.getAllRooms();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Rooms</h1>");

        html.append("<form method='POST' action='/admin/rooms'>");
        html.append("Name: <input name='name'><br>");
        html.append("Capacity: <input name='capacity' type='number'><br>");
        html.append("<button type='submit'>Add Room</button>");
        html.append("</form><br>");

        html.append("<table border='1'>");
        html.append("<tr><th>ID</th><th>Name</th><th>Capacity</th><th>Actions</th></tr>");

        for (Room r : rooms) {
            html.append("<tr>");

            html.append("<td>").append(r.getRoomId()).append("</td>");
            html.append("<td>").append(r.getName()).append("</td>");
            html.append("<td>").append(r.getCapacity()).append("</td>");

            html.append("<td>");

            html.append("<a href='/admin/edit-room?roomId=")
                    .append(r.getRoomId())
                    .append("'>Edit</a> ");

            html.append("<form method='POST' action='/admin/delete-room' style='display:inline;'>");
            html.append("<input type='hidden' name='roomId' value='").append(r.getRoomId()).append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form>");

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin-dashboard'\">Back to Dashboard</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void addCoach(String name, String specialty) {
        coachService.addCoach(name, specialty);
    }

    public void addRoom(String name, int capacity) {
        roomService.addRoom(name, capacity);
    }

    public void deleteCoach(int coachId) {
        coachService.deleteCoach(coachId);
    }

    public String getEditCoachPage(int coachId) {

        Coach coach = coachService.getCoachById(coachId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Edit Coach</h1>");

        html.append("<form method='POST' action='/admin/edit-coach'>");
        html.append("<input type='hidden' name='coachId' value='").append(coach.getCoachId()).append("'>");
        html.append("Name: <input name='name' value='").append(coach.getName()).append("'><br>");
        html.append("Specialty: <input name='specialty' value='").append(coach.getSpecialty()).append("'><br>");
        html.append("<button type='submit'>Update Coach</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/coaches'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void updateCoach(int coachId, String name, String specialty) {
        coachService.updateCoach(coachId, name, specialty);
    }

    public void deleteRoom(int roomId) {
        roomService.deleteRoom(roomId);
    }

    public String getEditRoomPage(int roomId) {

        Room room = roomService.getRoomById(roomId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Edit Room</h1>");

        html.append("<form method='POST' action='/admin/edit-room'>");
        html.append("<input type='hidden' name='roomId' value='").append(room.getRoomId()).append("'>");
        html.append("Name: <input name='name' value='").append(room.getName()).append("'><br>");
        html.append("Capacity: <input name='capacity' type='number' value='").append(room.getCapacity()).append("'><br>");
        html.append("<button type='submit'>Update Room</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/rooms'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void updateRoom(int roomId, String name, int capacity) {
        roomService.updateRoom(roomId, name, capacity);
    }


    public String getMembersPage() {

        List<MemberProfile> members = memberService.getAllProfiles();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Manage Members</h1>");

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>Age</th>");
        html.append("<th>Height</th>");
        html.append("<th>Weight</th>");
        html.append("<th>Actions</th>");
        html.append("</tr>");

        for (MemberProfile m : members) {
            html.append("<tr>");

            html.append("<td>").append(m.getMemberId()).append("</td>");
            html.append("<td>").append(m.getFirstName()).append(" ").append(m.getLastName()).append("</td>");
            html.append("<td>").append(m.getAge()).append("</td>");
            html.append("<td>").append(m.getHeightCm()).append(" cm</td>");
            html.append("<td>").append(m.getWeightKg()).append(" kg</td>");

            html.append("<td>");

            html.append("<a href='/admin/edit-member?memberId=")
                    .append(m.getMemberId())
                    .append("'>Edit</a> ");

            html.append("<form method='POST' action='/admin/delete-member' style='display:inline;'>");
            html.append("<input type='hidden' name='memberId' value='")
                    .append(m.getMemberId())
                    .append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form>");

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin-dashboard'\">Back to Dashboard</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void deleteMember(int memberId) {
        memberService.deleteProfile(memberId);
    }

    public String getEditMemberPage(int memberId) {

        MemberProfile member = memberService.getProfileByMemberId(memberId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Edit Member</h1>");

        html.append("<form method='POST' action='/admin/edit-member'>");

        html.append("<input type='hidden' name='memberId' value='")
                .append(member.getMemberId())
                .append("'>");

        html.append("First Name: <input name='firstName' value='")
                .append(member.getFirstName())
                .append("'><br>");

        html.append("Last Name: <input name='lastName' value='")
                .append(member.getLastName())
                .append("'><br>");

        html.append("Age: <input type='number' name='age' value='")
                .append(member.getAge())
                .append("'><br>");

        html.append("Height (cm): <input type='number' name='heightCm' value='")
                .append(member.getHeightCm())
                .append("'><br>");

        html.append("Weight (kg): <input type='number' step='0.1' name='weightKg' value='")
                .append(member.getWeightKg())
                .append("'><br>");

        html.append("<button type='submit'>Update Member</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/members'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void updateMember(int memberId, String firstName, String lastName,
                             int age, int heightCm, double weightKg) {

        memberService.updateProfileByMemberId(
                memberId,
                firstName,
                lastName,
                age,
                heightCm,
                weightKg
        );
    }

    private String buildGeneratorConfigHtml(Object index, List<GymClass> classes,
                                            List<Coach> coaches, List<Room> rooms) {

        String idx = String.valueOf(index);
        StringBuilder html = new StringBuilder();

        html.append("<fieldset style='margin-bottom:20px; padding:10px;'>");
        html.append("<legend>Configuration ").append(idx).append("</legend>");

        html.append("Class: <select name='classId_").append(idx).append("'>");
        for (GymClass c : classes) {
            html.append("<option value='").append(c.getClassId()).append("'>")
                    .append(c.getClassName())
                    .append("</option>");
        }
        html.append("</select><br>");

        html.append("Skill Level: <select name='skillLevel_").append(idx).append("'>");
        html.append("<option value='Beginner'>Beginner</option>");
        html.append("<option value='Intermediate/Advanced'>Intermediate/Advanced</option>");
        html.append("</select><br>");

        html.append("Sessions per week: <input type='number' min='1' max='5' name='sessionsPerWeek_")
                .append(idx).append("'><br>");

        html.append("Duration (minutes): <input type='number' min='30' step='30' name='durationMinutes_")
                .append(idx).append("'><br>");

        html.append("After time: <input type='time' name='afterTime_").append(idx).append("'><br>");
        html.append("Before time: <input type='time' name='beforeTime_").append(idx).append("'><br>");

        html.append("Coach: <select name='coachName_").append(idx).append("'>");
        for (Coach coach : coaches) {
            html.append("<option value='").append(coach.getName()).append("'>")
                    .append(coach.getName())
                    .append("</option>");
        }
        html.append("</select><br>");

        html.append("Room: <select name='roomName_").append(idx).append("'>");
        for (Room room : rooms) {
            html.append("<option value='").append(room.getName()).append("'>")
                    .append(room.getName())
                    .append("</option>");
        }
        html.append("</select><br>");

        html.append("<button type='button' onclick='removeConfig(this)'>Remove Configuration</button>");
        html.append("</fieldset>");

        return html.toString();
    }

    public String getTournamentsPage() {

        List<Event> events = eventService.getAllEvents();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Manage Events</h1>");

        html.append("<h2>Create Event</h2>");
        html.append("<form method='POST' action='/admin/tournaments'>");

        html.append("Event Name: <input name='eventName'><br>");
        html.append("Date: <input type='date' name='eventDate'><br>");
        html.append("Location: <input name='location'><br>");

        html.append("Status: <select name='status'>");
        html.append("<option value='Upcoming'>Upcoming</option>");
        html.append("<option value='Open'>Open</option>");
        html.append("<option value='Live'>Live</option>");
        html.append("<option value='Completed'>Completed</option>");
        html.append("</select><br>");

        html.append("Format: <select name='format'>");
        html.append("<option value='MATCHES'>Matches</option>");
        html.append("</select><br>");

        html.append("<p>Allowed Martial Arts:</p>");

        html.append("<label><input type='checkbox' value='MMA' onchange='updateArts()'> MMA</label><br>");
        html.append("<label><input type='checkbox' value='Boxing' onchange='updateArts()'> Boxing</label><br>");
        html.append("<label><input type='checkbox' value='Kickboxing' onchange='updateArts()'> Kickboxing</label><br>");
        html.append("<label><input type='checkbox' value='Muay Thai' onchange='updateArts()'> Muay Thai</label><br>");
        html.append("<label><input type='checkbox' value='Jiu Jitsu' onchange='updateArts()'> Jiu Jitsu</label><br>");
        html.append("<label><input type='checkbox' value='Wrestling' onchange='updateArts()'> Wrestling</label><br>");

        html.append("<input type='hidden' id='allowedMartialArts' name='allowedMartialArts'>");

        html.append("<br><button type='submit'>Create Event</button>");
        html.append("</form><br>");

        html.append("<script>");
        html.append("function updateArts(){");
        html.append("  const checked = document.querySelectorAll(\"input[type='checkbox']:checked\");");
        html.append("  const values = Array.from(checked).map(cb => cb.value);");
        html.append("  document.getElementById('allowedMartialArts').value = values.join(',');");
        html.append("}");
        html.append("</script>");

        html.append("<h2>Existing Events</h2>");
        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>Date</th>");
        html.append("<th>Location</th>");
        html.append("<th>Status</th>");
        html.append("<th>Format</th>");
        html.append("<th>Allowed Martial Arts</th>");
        html.append("<th>Actions</th>");
        html.append("</tr>");

        for (Event e : events) {
            html.append("<tr>");
            html.append("<td>").append(e.getEventId()).append("</td>");
            html.append("<td>").append(e.getEventName()).append("</td>");
            html.append("<td>").append(e.getEventDate()).append("</td>");
            html.append("<td>").append(e.getLocation()).append("</td>");
            html.append("<td>").append(e.getStatus()).append("</td>");
            html.append("<td>").append(e.getFormat()).append("</td>");
            html.append("<td>").append(e.getAllowedMartialArts()).append("</td>");

            html.append("<td>");
            html.append("<a href='/admin/edit-tournament?eventId=").append(e.getEventId()).append("'>Edit</a> ");
            html.append("<a href='/admin/view-entrants?eventId=").append(e.getEventId()).append("'>Entrants</a> ");
            html.append("<a href='/admin/view-matches?eventId=").append(e.getEventId()).append("'>Matches</a> ");
            html.append("<a href='/admin/event-results?eventId=").append(e.getEventId()).append("'>Live Results</a> ");
            html.append("<a href='/admin/live-control?eventId=").append(e.getEventId()).append("'>Live Controls</a> ");

            html.append("<form method='POST' action='/admin/start-event' style='display:inline;'>");
            html.append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>");
            html.append("<button type='submit'>Start Event</button>");
            html.append("</form> ");


            html.append("<form method='POST' action='/admin/delete-tournament' style='display:inline;'>");
            html.append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form>");

            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin-dashboard'\">Back to Dashboard</button>");
        html.append("</body></html>");

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

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Edit Tournament</h1>");

        html.append("<form method='POST' action='/admin/edit-tournament'>");
        html.append("<input type='hidden' name='eventId' value='").append(event.getEventId()).append("'>");

        html.append("Event Name: <input name='eventName' value='").append(event.getEventName()).append("'><br>");
        html.append("Date: <input type='date' name='eventDate' value='").append(event.getEventDate()).append("'><br>");
        html.append("Location: <input name='location' value='").append(event.getLocation()).append("'><br>");

        html.append("Status: <select name='status'>");

        html.append("<option value='Upcoming'");
        if ("Upcoming".equals(event.getStatus())) html.append(" selected");
        html.append(">Upcoming</option>");

        html.append("<option value='Open'");
        if ("Open".equals(event.getStatus())) html.append(" selected");
        html.append(">Open</option>");

        html.append("<option value='Closed'");
        if ("Closed".equals(event.getStatus())) html.append(" selected");
        html.append(">Closed</option>");

        html.append("<option value='Completed'");
        if ("Completed".equals(event.getStatus())) html.append(" selected");
        html.append(">Completed</option>");

        html.append("</select><br>");

        html.append("Format: <select name='format'>");
        html.append("<option value='MATCHES'");
        if ("MATCHES".equals(event.getFormat())) html.append(" selected");
        html.append(">Matches</option>");

        html.append("<option value='BRACKET'");
        if ("BRACKET".equals(event.getFormat())) html.append(" selected");
        html.append(">Bracket</option>");
        html.append("</select><br>");

        html.append("Allowed Martial Arts (comma separated): ");
        html.append("<input name='allowedMartialArts' value='").append(allowed).append("'><br>");

        html.append("<button type='submit'>Update Event</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/tournaments'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public String getEntrantsPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        List<MemberProfile> entrants = eventService.getEntrantsForEvent(eventId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Entrants for ").append(event.getEventName()).append("</h1>");

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>Age</th>");
        html.append("<th>Weight</th>");
        html.append("</tr>");

        for (MemberProfile m : entrants) {
            html.append("<tr>");
            html.append("<td>").append(m.getMemberId()).append("</td>");
            html.append("<td>").append(m.getFirstName()).append(" ").append(m.getLastName()).append("</td>");
            html.append("<td>").append(m.getAge()).append("</td>");
            html.append("<td>").append(m.getWeightKg()).append(" kg</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin/tournaments'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void generateMatchesForEvent(int eventId) {
        matchmakingService.generateMatchesForEvent(eventId);
    }

    public String getMatchesPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Matches for ").append(event.getEventName()).append("</h1>");

        List<String> matchMessages = matchmakingService.getLastGenerationMessages();

        if (!matchMessages.isEmpty()) {
            html.append("<h2>Generation Messages</h2>");
            html.append("<ul>");
            for (String msg : matchMessages) {
                html.append("<li>").append(msg).append("</li>");
            }
            html.append("</ul>");
        }

        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Match ID</th>");
        html.append("<th>Participant 1</th>");
        html.append("<th>Participant 2</th>");
        html.append("<th>Status</th>");
        html.append("<th>Round</th>");
        html.append("</tr>");

        for (Match m : matches) {
            html.append("<tr>");
            html.append("<td>").append(m.getMatchId()).append("</td>");
            html.append("<td>").append(m.getParticipant1Name()).append("</td>");
            html.append("<td>").append(m.getParticipant2Name()).append("</td>");
            html.append("<td>").append(m.getStatus()).append("</td>");
            html.append("<td>").append(m.getRoundNumber()).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br>");
        html.append("<form method='POST' action='/admin/generate-matches'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("<button type='submit'>Generate Matches</button>");
        html.append("</form>");

        html.append("<br><button onclick=\"location.href='/admin/tournaments'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public String getAdminEventResultsPage(int eventId) {

        Event event = eventService.getEventById(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Event Results - ").append(event.getEventName()).append("</h1>");

        html.append("<h2>Matches</h2>");
        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>Match ID</th>");
        html.append("<th>Participant 1</th>");
        html.append("<th>Participant 2</th>");
        html.append("<th>Status</th>");
        html.append("<th>Round</th>");
        html.append("<th>Winner</th>");
        html.append("<th>Decision</th>");
        html.append("<th>Actions</th>");
        html.append("</tr>");

        for (Match m : matches) {
            html.append("<tr>");
            html.append("<td>").append(m.getMatchId()).append("</td>");
            html.append("<td>").append(m.getParticipant1Name()).append("</td>");
            html.append("<td>").append(m.getParticipant2Name()).append("</td>");
            html.append("<td>").append(m.getStatus()).append("</td>");
            html.append("<td>").append(m.getRoundNumber()).append("</td>");
            html.append("<td>").append(m.getWinnerName() == null ? "-" : m.getWinnerName()).append("</td>");
            html.append("<td>").append(m.getResult() == null || m.getResult().isBlank() ? "-" : m.getResult()).append("</td>");

            html.append("<td>");
            html.append("<a href='/admin/live-control?eventId=").append(eventId).append("&matchId=").append(m.getMatchId()).append("'>Control</a>");
            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin/tournaments'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public String getAdminLiveControlPage(int eventId, Integer selectedMatchId) {

        Event event = eventService.getEventById(eventId);
        LiveEventState state = liveEventService.getStateForEvent(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);

        if (selectedMatchId != null) {
            liveEventService.setCurrentMatch(eventId, selectedMatchId);
            state = liveEventService.getStateForEvent(eventId);
        }

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Live Control - ").append(event.getEventName()).append("</h1>");

        if (state.isTimerRunning()) {
            html.append("<meta http-equiv='refresh' content='1;url=/admin/live-control/tick-and-return?eventId=")
                    .append(eventId)
                    .append("'>");
        }

        html.append("<p><strong>Current Round:</strong> ").append(state.getCurrentRound()).append("</p>");
        html.append("<p><strong>Timer:</strong> ").append(formatSeconds(state.getRemainingSeconds())).append("</p>");
        html.append("<p><strong>Running:</strong> ").append(state.isTimerRunning() ? "Yes" : "No").append("</p>");

        html.append("<h2>Select Current Match</h2>");
        html.append("<form method='POST' action='/admin/live-control/select-match'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("<select name='matchId'>");

        for (Match m : matches) {
            html.append("<option value='").append(m.getMatchId()).append("'");
            if (state.getCurrentMatchId() != null && state.getCurrentMatchId() == m.getMatchId()) {
                html.append(" selected");
            }
            html.append(">");
            html.append("Match ").append(m.getMatchId()).append(": ")
                    .append(m.getParticipant1Name()).append(" vs ")
                    .append(m.getParticipant2Name());
            html.append("</option>");
        }

        html.append("</select>");
        html.append("<button type='submit'>Set Current Match</button>");
        html.append("</form>");

        html.append("<h2>Round / Timer Controls</h2>");

        html.append("<form method='POST' action='/admin/live-control/set-round'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("Round: <input type='number' name='round' value='").append(state.getCurrentRound()).append("'>");
        html.append("<button type='submit'>Update Round</button>");
        html.append("</form><br>");

        html.append("<form method='POST' action='/admin/live-control/set-round-time'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("Round Time (seconds): <input type='number' name='roundTimeSeconds' value='").append(state.getRoundTimeSeconds()).append("'>");
        html.append("<button type='submit'>Set Round Time</button>");
        html.append("</form><br>");

        html.append("<form method='POST' action='/admin/live-control/start' style='display:inline;'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("<button type='submit'>Start</button>");
        html.append("</form> ");

        html.append("<form method='POST' action='/admin/live-control/pause' style='display:inline;'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("<button type='submit'>Pause</button>");
        html.append("</form> ");

        html.append("<form method='POST' action='/admin/live-control/reset' style='display:inline;'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("<button type='submit'>Reset</button>");
        html.append("</form> ");

        html.append("<form method='POST' action='/admin/live-control/tick' style='display:inline;'>");
        html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
        html.append("<button type='submit'>Tick -1s</button>");
        html.append("</form>");

        if (state.getCurrentMatchId() != null) {
            Match currentMatch = null;
            for (Match m : matches) {
                if (m.getMatchId() == state.getCurrentMatchId()) {
                    currentMatch = m;
                    break;
                }
            }

            if (currentMatch != null) {
                html.append("<h2>Result Entry</h2>");
                html.append("<form method='POST' action='/admin/live-control/result'>");
                html.append("<input type='hidden' name='eventId' value='").append(eventId).append("'>");
                html.append("<input type='hidden' name='matchId' value='").append(currentMatch.getMatchId()).append("'>");

                html.append("Status: <select name='status'>");
                html.append("<option value='Scheduled'>Scheduled</option>");
                html.append("<option value='Live'>Live</option>");
                html.append("<option value='Completed'>Completed</option>");
                html.append("</select><br>");

                html.append("Winner: <select name='winnerMemberId'>");
                html.append("<option value=''>No winner / Draw</option>");
                html.append("<option value='").append(currentMatch.getParticipant1Id()).append("'>").append(currentMatch.getParticipant1Name()).append("</option>");
                html.append("<option value='").append(currentMatch.getParticipant2Id()).append("'>").append(currentMatch.getParticipant2Name()).append("</option>");
                html.append("</select><br>");

                html.append("Decision: <select name='result'>");
                html.append("<option value='Decision'>Decision</option>");
                html.append("<option value='KO/TKO'>KO/TKO</option>");
                html.append("<option value='Submission'>Submission</option>");
                html.append("<option value='Draw'>Draw</option>");
                html.append("</select><br>");

                html.append("Round Number: <input type='number' name='roundNumber' value='").append(state.getCurrentRound()).append("'><br>");

                html.append("<button type='submit'>Save Result</button>");
                html.append("</form>");
            }
        }

        html.append("<br><button onclick=\"location.href='/admin/event-results?eventId=").append(eventId).append("'\">Back to Results</button>");
        html.append("</body></html>");

        return html.toString();
    }

    private String formatSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setCurrentMatch(int eventId, int matchId) {
        liveEventService.setCurrentMatch(eventId, matchId);
    }

    public void setCurrentRound(int eventId, int round) {
        liveEventService.setRound(eventId, round);
    }

    public void setRoundTime(int eventId, int roundTimeSeconds) {
        liveEventService.setRoundTime(eventId, roundTimeSeconds);
    }

    public void startLiveTimer(int eventId) {
        liveEventService.startTimer(eventId);
    }

    public void pauseLiveTimer(int eventId) {
        liveEventService.pauseTimer(eventId);
    }

    public void resetLiveTimer(int eventId) {
        liveEventService.resetTimer(eventId);
    }

    public void tickLiveTimer(int eventId) {
        liveEventService.tickTimer(eventId);
    }

    public void saveMatchResult(int matchId, String status, String result, Integer winnerMemberId, int roundNumber) {
        liveEventService.updateMatchResult(matchId, status, result, winnerMemberId, roundNumber);
    }

    public void startEvent(int eventId) {
        Event event = eventService.getEventById(eventId);

        if (event != null) {
            eventService.updateEvent(
                    eventId,
                    event.getEventName(),
                    event.getEventDate(),
                    event.getLocation(),
                    "Live",
                    event.getFormat(),
                    event.getAllowedMartialArts()
            );
        }
    }

    private String escapeForJs(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("`", "\\`")
                .replace("\n", "")
                .replace("\r", "");
    }

    public String getMembershipsPage() {

        List<Membership> memberships = membershipService.getAllMemberships();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Manage Memberships</h1>");

        html.append("<h2>Create Membership</h2>");
        html.append("<form method='POST' action='/admin/memberships'>");

        html.append("Membership Name: <input name='membershipName'><br>");
        html.append("Description: <input name='description'><br>");

        html.append("<p>Allowed Martial Arts:</p>");
        html.append("<select id='allowedMartialArtsSelect' multiple size='6' onchange='updateMembershipSelections()'>");
        html.append("<option value='MMA'>MMA</option>");
        html.append("<option value='Boxing'>Boxing</option>");
        html.append("<option value='Kickboxing'>Kickboxing</option>");
        html.append("<option value='Muay Thai'>Muay Thai</option>");
        html.append("<option value='Jiu Jitsu'>Jiu Jitsu</option>");
        html.append("<option value='Wrestling'>Wrestling</option>");
        html.append("</select><br>");

        html.append("<p>Allowed Skill Levels:</p>");
        html.append("<select id='allowedSkillLevelsSelect' multiple size='3' onchange='updateMembershipSelections()'>");
        html.append("<option value='Beginner'>Beginner</option>");
        html.append("<option value='Intermediate/Advanced'>Intermediate/Advanced</option>");
        html.append("</select><br>");

        html.append("<input type='hidden' id='allowedMartialArts' name='allowedMartialArts'>");
        html.append("<input type='hidden' id='allowedSkillLevels' name='allowedSkillLevels'>");

        html.append("<button type='submit'>Create Membership</button>");
        html.append("</form><br>");

        html.append("<script>");
        html.append("function updateMembershipSelections(){");

        html.append("  const artsSelect = document.getElementById('allowedMartialArtsSelect');");
        html.append("  const selectedArts = Array.from(artsSelect.selectedOptions).map(o => o.value);");
        html.append("  document.getElementById('allowedMartialArts').value = selectedArts.join(',');");

        html.append("  const skillsSelect = document.getElementById('allowedSkillLevelsSelect');");
        html.append("  const selectedSkills = Array.from(skillsSelect.selectedOptions).map(o => o.value);");
        html.append("  document.getElementById('allowedSkillLevels').value = selectedSkills.join(',');");

        html.append("}");
        html.append("</script>");

        html.append("<h2>Existing Memberships</h2>");
        html.append("<table border='1'>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>Description</th>");
        html.append("<th>Allowed Martial Arts</th>");
        html.append("<th>Allowed Skill Levels</th>");
        html.append("<th>Actions</th>");
        html.append("</tr>");

        for (Membership m : memberships) {
            html.append("<tr>");
            html.append("<td>").append(m.getMembershipId()).append("</td>");
            html.append("<td>").append(m.getMembershipName()).append("</td>");
            html.append("<td>").append(m.getDescription()).append("</td>");
            html.append("<td>").append(m.getAllowedMartialArts()).append("</td>");
            html.append("<td>").append(m.getAllowedSkillLevels()).append("</td>");

            html.append("<td>");
            html.append("<a href='/admin/edit-membership?membershipId=").append(m.getMembershipId()).append("'>Edit</a> ");

            html.append("<form method='POST' action='/admin/delete-membership' style='display:inline;'>");
            html.append("<input type='hidden' name='membershipId' value='").append(m.getMembershipId()).append("'>");
            html.append("<button type='submit'>Delete</button>");
            html.append("</form>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br><button onclick=\"location.href='/admin-dashboard'\">Back</button>");
        html.append("</body></html>");

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

        String allowedArts = membership.getAllowedMartialArts() == null ? "" : membership.getAllowedMartialArts();
        String allowedSkills = membership.getAllowedSkillLevels() == null ? "" : membership.getAllowedSkillLevels();

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Edit Membership</h1>");

        html.append("<form method='POST' action='/admin/edit-membership'>");
        html.append("<input type='hidden' name='membershipId' value='").append(membership.getMembershipId()).append("'>");

        html.append("Membership Name: <input name='membershipName' value='").append(membership.getMembershipName()).append("'><br>");
        html.append("Description: <input name='description' value='").append(membership.getDescription()).append("'><br>");

        html.append("<p>Allowed Martial Arts:</p>");
        html.append("<select id='allowedMartialArtsSelect' multiple size='6' onchange='updateMembershipSelections()'>");

        appendSelectedOption(html, "MMA", allowedArts);
        appendSelectedOption(html, "Boxing", allowedArts);
        appendSelectedOption(html, "Kickboxing", allowedArts);
        appendSelectedOption(html, "Muay Thai", allowedArts);
        appendSelectedOption(html, "Jiu Jitsu", allowedArts);
        appendSelectedOption(html, "Wrestling", allowedArts);

        html.append("</select><br>");

        html.append("<p>Allowed Skill Levels:</p>");
        html.append("<select id='allowedSkillLevelsSelect' multiple size='2' onchange='updateMembershipSelections()'>");

        appendSelectedOption(html, "Beginner", allowedSkills);
        appendSelectedOption(html, "Intermediate/Advanced", allowedSkills);

        html.append("</select><br>");

        html.append("<input type='hidden' id='allowedMartialArts' name='allowedMartialArts' value='").append(allowedArts).append("'>");
        html.append("<input type='hidden' id='allowedSkillLevels' name='allowedSkillLevels' value='").append(allowedSkills).append("'>");

        html.append("<button type='submit'>Update Membership</button>");
        html.append("</form>");

        html.append("<script>");
        html.append("function updateMembershipSelections(){");

        html.append("  const artsSelect = document.getElementById('allowedMartialArtsSelect');");
        html.append("  const selectedArts = Array.from(artsSelect.selectedOptions).map(o => o.value);");
        html.append("  document.getElementById('allowedMartialArts').value = selectedArts.join(',');");

        html.append("  const skillsSelect = document.getElementById('allowedSkillLevelsSelect');");
        html.append("  const selectedSkills = Array.from(skillsSelect.selectedOptions).map(o => o.value);");
        html.append("  document.getElementById('allowedSkillLevels').value = selectedSkills.join(',');");

        html.append("}");
        html.append("</script>");

        html.append("<br><button onclick=\"location.href='/admin/memberships'\">Back</button>");
        html.append("</body></html>");

        return html.toString();
    }

    public void updateMembership(int membershipId, String membershipName, String description,
                                 String allowedMartialArts, String allowedSkillLevels) {
        membershipService.updateMembership(membershipId, membershipName, description, allowedMartialArts, allowedSkillLevels);
    }

    private void appendSelectedOption(StringBuilder html, String value, String csvValues) {
        html.append("<option value='").append(value).append("'");

        if (containsCsvValue(csvValues, value)) {
            html.append(" selected");
        }

        html.append(">").append(value).append("</option>");
    }

    private boolean containsCsvValue(String csv, String value) {
        if (csv == null || csv.isBlank()) {
            return false;
        }

        String[] parts = csv.split(",");

        for (String part : parts) {
            if (part.trim().equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

}