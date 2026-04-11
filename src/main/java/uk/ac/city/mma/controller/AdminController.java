package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.*;
import uk.ac.city.mma.service.*;
import uk.ac.city.mma.util.TemplateEngine;

import java.util.*;

public class AdminController {

    private ClassService classService               = new ClassService();
    private ClassSessionService classSessionService = new ClassSessionService();
    private TimetableService timetableService       = new TimetableService();
    private CoachService coachService               = new CoachService();
    private RoomService roomService                 = new RoomService();
    private MemberService memberService             = new MemberService();
    private EventService eventService               = new EventService();
    private MatchmakingService matchmakingService   = new MatchmakingService();
    private LiveEventService liveEventService       = new LiveEventService();
    private MembershipService membershipService     = new MembershipService();

    /*
    CLASSES
    */

    public String getClassesPage() {

        List<GymClass> classes = classService.getAllClasses();

        StringBuilder rows = new StringBuilder();
        for (GymClass c : classes) {
            rows.append("<tr>")
                    .append("<td>").append(c.getClassId()).append("</td>")
                    .append("<td>").append(c.getClassName()).append("</td>")
                    .append("<td><span class='badge bg-primary'>").append(c.getClassType()).append("</span></td>")
                    .append("<td><span class='badge bg-secondary'>").append(c.getSkillLevel()).append("</span></td>")
                    .append("<td>").append(c.getCapacity()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-class?classId=").append(c.getClassId())
                    .append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<a href='/admin/add-session?classId=").append(c.getClassId())
                    .append("' class='btn btn-sm btn-outline-secondary'><i class='bi bi-plus-circle me-1'></i>Add Session</a>")
                    .append("<form method='POST' action='/admin/delete-class' style='display:inline;'>")
                    .append("<input type='hidden' name='classId' value='").append(c.getClassId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' ")
                    .append("onclick='return confirm(\"Delete this class?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-classes.html")
                .set("PAGE_TITLE",    "Classes")
                .set("NAV_CLASSES",   "active")
                .set("ROWS_CLASSES",  rows.toString())
                .clearRemaining()
                .render();
    }

    public void createClass(String name, String description, String skillLevel,
                            String classType, int capacity) {
        classService.createClass(name, description, skillLevel, classType, capacity);
    }

    public void deleteClass(int classId) {
        classService.deleteClass(classId);
    }

    public String getEditClassPage(int classId) {

        GymClass c = classService.getClassById(classId);

        String[] types  = {"MMA","Boxing","Kickboxing","Muay Thai","Jiu Jitsu","Wrestling","Hyrox"};
        String[] skills = {"Beginner","Intermediate/Advanced"};

        StringBuilder typeOpts = new StringBuilder();
        for (String t : types) {
            typeOpts.append("<option value='").append(t).append("'")
                    .append(t.equals(c.getClassType()) ? " selected" : "")
                    .append(">").append(t).append("</option>");
        }

        StringBuilder skillOpts = new StringBuilder();
        for (String s : skills) {
            skillOpts.append("<option value='").append(s).append("'")
                    .append(s.equals(c.getSkillLevel()) ? " selected" : "")
                    .append(">").append(s).append("</option>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-edit-class.html")
                .set("PAGE_TITLE",          "Edit Class")
                .set("NAV_CLASSES",         "active")
                .set("CLASS_ID",            c.getClassId())
                .set("CLASS_NAME",          c.getClassName())
                .set("DESCRIPTION",         c.getDescription())
                .set("OPTIONS_CLASS_TYPE",  typeOpts.toString())
                .set("OPTIONS_SKILL_LEVEL", skillOpts.toString())
                .set("CAPACITY",            c.getCapacity())
                .clearRemaining()
                .render();
    }

    public void updateClass(int classId, String name, String description,
                            String skillLevel, String classType, int capacity) {
        classService.updateClass(classId, name, description, skillLevel, classType, capacity);
    }

    /*
    TIMETABLE
    */

    public String getTimetablePage() {

        List<ClassSession> sessions = classSessionService.getAllSessions();
        List<String> genMessages    = timetableService.getLastGenerationMessages();

        String generationLog = "";
        if (!genMessages.isEmpty()) {
            StringBuilder log = new StringBuilder(
                    "<div class='alert alert-info'><strong>Generation Log:</strong><ul class='mb-0 mt-1'>");
            for (String msg : genMessages) log.append("<li>").append(msg).append("</li>");
            log.append("</ul></div>");
            generationLog = log.toString();
        }

        String[] dayNames = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        /*
        TIME SLOT ROWS
        */
        StringBuilder timeRows = new StringBuilder();

        for (int hour = 6; hour < 22; hour++) {
            for (int min = 0; min < 60; min += 30) {
                String slotTime = String.format("%02d:%02d", hour, min);

                boolean hasAny = false;
                for (ClassSession s : sessions) {
                    if (s.getStartTime().equals(slotTime)) { hasAny = true; break; }
                }

                timeRows.append("<tr>");
                timeRows.append("<td class='time-cell'>").append(slotTime).append("</td>");

                for (String dayName : dayNames) {
                    timeRows.append("<td class='timetable-cell'>");

                    for (ClassSession s : sessions) {
                        if (!s.getDayOfWeek().equalsIgnoreCase(dayName)) continue;
                        if (!s.getStartTime().equals(slotTime)) continue;

                        String bgColor = s.isGenerated() ? "#6f42c1" : "#0d6efd";

                        timeRows.append("<div class='session-block' style='background:").append(bgColor).append("'>")
                                .append("<span class='session-name'>").append(s.getClassName()).append("</span>")
                                .append("<span class='session-meta'>")
                                .append(s.getStartTime()).append(" \u00b7 ").append(s.getDurationMinutes()).append("m")
                                .append(" \u00b7 ").append(s.getCoachName())
                                .append("</span>")
                                .append("<span class='session-meta'>").append(s.getRoom()).append("</span>")
                                .append("<div class='session-actions'>")
                                .append("<a href='/admin/edit-session?sessionId=").append(s.getSessionId())
                                .append("' class='btn btn-sm btn-light' style='font-size:0.7rem;padding:2px 6px'>")
                                .append("<i class='bi bi-pencil'></i></a>")
                                .append("<form method='POST' action='/admin/delete-session' style='display:inline;'>")
                                .append("<input type='hidden' name='sessionId' value='").append(s.getSessionId()).append("'>")
                                .append("<button class='btn btn-sm btn-light' style='font-size:0.7rem;padding:2px 6px' ")
                                .append("type='submit' onclick='return confirm(\"Delete session?\")'>")
                                .append("<i class='bi bi-trash'></i></button></form>")
                                .append("</div>")
                                .append("</div>");
                    }

                    timeRows.append("</td>");
                }

                timeRows.append("</tr>");
            }
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-timetable.html")
                .set("PAGE_TITLE",     "Timetable")
                .set("NAV_TIMETABLE",  "active")
                .set("GENERATION_LOG", generationLog)
                .set("TIME_ROWS",      timeRows.toString())
                .clearRemaining()
                .render();
    }

    public void deleteSession(int sessionId) {
        classSessionService.deleteSession(sessionId);
    }

    public void updateSession(int id, String day, String time, int dur, String coach, String room) {
        classSessionService.updateSession(id, day, time, dur, coach, room);
    }

    /*
    ADD SESSION
    */

    public String getAddSessionPage() {

        return TemplateEngine.load("admin-layout.html", "content/admin-add-session.html")
                .set("PAGE_TITLE",      "Add Session")
                .set("NAV_TIMETABLE",   "active")
                .set("OPTIONS_CLASSES", classOptions(classService.getAllClasses()))
                .set("OPTIONS_COACHES", coachOptions(coachService.getAllCoaches(), null))
                .set("OPTIONS_ROOMS",   roomOptions(roomService.getAllRooms(), null))
                .clearRemaining()
                .render();
    }

    public void createSession(int classId, String day, String time, int dur, String coach, String room) {
        classSessionService.createSession(classId, day, time, dur, coach, room);
    }

    /*
    EDIT SESSION
    */

    public String getEditSessionPage(int sessionId) {

        ClassSession s  = classSessionService.getSessionById(sessionId);
        String[] allDays = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        StringBuilder dayOpts = new StringBuilder();
        for (String d : allDays) {
            dayOpts.append("<option value='").append(d).append("'")
                    .append(d.equals(s.getDayOfWeek()) ? " selected" : "")
                    .append(">").append(d).append("</option>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-edit-session.html")
                .set("PAGE_TITLE",      "Edit Session")
                .set("NAV_TIMETABLE",   "active")
                .set("SESSION_ID",      s.getSessionId())
                .set("CLASS_NAME",      s.getClassName())
                .set("START_TIME",      s.getStartTime())
                .set("DURATION",        s.getDurationMinutes())
                .set("OPTIONS_DAYS",    dayOpts.toString())
                .set("OPTIONS_COACHES", coachOptions(coachService.getAllCoaches(), s.getCoachName()))
                .set("OPTIONS_ROOMS",   roomOptions(roomService.getAllRooms(), s.getRoom()))
                .clearRemaining()
                .render();
    }

    /*
    GENERATE TIMETABLE
    */

    public String getGeneratorPage() {

        List<GymClass> classes = classService.getAllClasses();
        List<Coach>    coaches = coachService.getAllCoaches();
        List<Room>     rooms   = roomService.getAllRooms();

        return TemplateEngine.load("admin-layout.html", "content/admin-generate-timetable.html")
                .set("PAGE_TITLE",         "Generate Timetable")
                .set("NAV_TIMETABLE",      "active")
                .set("FIRST_CONFIG",       buildConfigBlock("0", classes, coaches, rooms))
                .set("CONFIG_TEMPLATE_JS", escapeForJs(buildConfigBlock("__INDEX__", classes, coaches, rooms)))
                .clearRemaining()
                .render();
    }

    public void generateSmartTimetable(Map<String, String> params) {
        List<GenerationRequest> requests = new ArrayList<>();
        int maxIndex = Integer.parseInt(params.getOrDefault("maxIndex", "0"));
        for (int i = 0; i <= maxIndex; i++) {
            if (params.get("classId_" + i) == null) continue;
            GenerationRequest req = new GenerationRequest();
            req.classId         = Integer.parseInt(params.get("classId_" + i));
            req.skillLevel      = params.get("skillLevel_" + i);
            req.sessionsPerWeek = Integer.parseInt(params.get("sessionsPerWeek_" + i));
            req.durationMinutes = Integer.parseInt(params.get("durationMinutes_" + i));
            req.afterTime       = params.get("afterTime_" + i);
            req.beforeTime      = params.get("beforeTime_" + i);
            req.coachName       = params.get("coachName_" + i);
            req.roomName        = params.get("roomName_" + i);
            requests.add(req);
        }
        timetableService.generateMultiple(requests);
    }

    /*
    COACHES
    */

    public String getCoachesPage() {

        List<Coach> coaches = coachService.getAllCoaches();
        StringBuilder rows = new StringBuilder();

        for (Coach c : coaches) {
            rows.append("<tr>")
                    .append("<td>").append(c.getCoachId()).append("</td>")
                    .append("<td>").append(c.getName()).append("</td>")
                    .append("<td>").append(c.getSpecialty()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-coach?coachId=").append(c.getCoachId())
                    .append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-coach' style='display:inline;'>")
                    .append("<input type='hidden' name='coachId' value='").append(c.getCoachId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' ")
                    .append("onclick='return confirm(\"Delete this coach?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-coaches.html")
                .set("PAGE_TITLE",   "Coaches")
                .set("NAV_COACHES",  "active")
                .set("ROWS_COACHES", rows.toString())
                .clearRemaining()
                .render();
    }

    public void addCoach(String name, String specialty) { coachService.addCoach(name, specialty); }
    public void deleteCoach(int coachId)                { coachService.deleteCoach(coachId); }

    public String getEditCoachPage(int coachId) {
        Coach c = coachService.getCoachById(coachId);

        String[] specialties = {"MMA","Boxing","Kickboxing","Muay Thai","Jiu Jitsu","Wrestling","Hyrox"};
        StringBuilder specialtyOpts = new StringBuilder();
        for (String s : specialties) {
            specialtyOpts.append("<option value='").append(s).append("'")
                    .append(s.equals(c.getSpecialty()) ? " selected" : "")
                    .append(">").append(s).append("</option>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-edit-coach.html")
                .set("PAGE_TITLE",         "Edit Coach")
                .set("NAV_COACHES",        "active")
                .set("COACH_ID",           c.getCoachId())
                .set("COACH_NAME",         c.getName())
                .set("OPTIONS_SPECIALTY",  specialtyOpts.toString())
                .clearRemaining()
                .render();
    }

    public void updateCoach(int id, String name, String specialty) { coachService.updateCoach(id, name, specialty); }

    /*
    ROOMS
    */

    public String getRoomsPage() {

        List<Room> rooms = roomService.getAllRooms();
        StringBuilder rows = new StringBuilder();

        for (Room r : rooms) {
            rows.append("<tr>")
                    .append("<td>").append(r.getRoomId()).append("</td>")
                    .append("<td>").append(r.getName()).append("</td>")
                    .append("<td>").append(r.getCapacity()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-room?roomId=").append(r.getRoomId())
                    .append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-room' style='display:inline;'>")
                    .append("<input type='hidden' name='roomId' value='").append(r.getRoomId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' ")
                    .append("onclick='return confirm(\"Delete this room?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-rooms.html")
                .set("PAGE_TITLE", "Rooms")
                .set("NAV_ROOMS",  "active")
                .set("ROWS_ROOMS", rows.toString())
                .clearRemaining()
                .render();
    }

    public void addRoom(String name, int capacity) { roomService.addRoom(name, capacity); }
    public void deleteRoom(int roomId)             { roomService.deleteRoom(roomId); }

    public String getEditRoomPage(int roomId) {
        Room r = roomService.getRoomById(roomId);
        return TemplateEngine.load("admin-layout.html", "content/admin-edit-room.html")
                .set("PAGE_TITLE",    "Edit Room")
                .set("NAV_ROOMS",     "active")
                .set("ROOM_ID",       r.getRoomId())
                .set("ROOM_NAME",     r.getName())
                .set("ROOM_CAPACITY", r.getCapacity())
                .clearRemaining()
                .render();
    }

    public void updateRoom(int id, String name, int capacity) { roomService.updateRoom(id, name, capacity); }

    /*
    MEMBERS
    */

    public String getMembersPage() {

        List<MemberProfile> members = memberService.getAllProfiles();
        StringBuilder rows = new StringBuilder();

        for (MemberProfile m : members) {
            rows.append("<tr>")
                    .append("<td>").append(m.getMemberId()).append("</td>")
                    .append("<td>").append(m.getFirstName()).append(" ").append(m.getLastName()).append("</td>")
                    .append("<td>").append(m.getAge()).append("</td>")
                    .append("<td>").append(m.getHeightCm()).append(" cm</td>")
                    .append("<td>").append(m.getWeightKg()).append(" kg</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-member?memberId=").append(m.getMemberId())
                    .append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-member' style='display:inline;'>")
                    .append("<input type='hidden' name='memberId' value='").append(m.getMemberId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' ")
                    .append("onclick='return confirm(\"Delete this member?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-members.html")
                .set("PAGE_TITLE",   "Members")
                .set("NAV_MEMBERS",  "active")
                .set("ROWS_MEMBERS", rows.toString())
                .clearRemaining()
                .render();
    }

    public void deleteMember(int memberId) { memberService.deleteProfile(memberId); }

    public String getEditMemberPage(int memberId) {
        MemberProfile m = memberService.getProfileByMemberId(memberId);
        return TemplateEngine.load("admin-layout.html", "content/admin-edit-member.html")
                .set("PAGE_TITLE", "Edit Member")
                .set("NAV_MEMBERS","active")
                .set("MEMBER_ID",  m.getMemberId())
                .set("FIRST_NAME", m.getFirstName())
                .set("LAST_NAME",  m.getLastName())
                .set("AGE",        m.getAge())
                .set("HEIGHT_CM",  m.getHeightCm())
                .set("WEIGHT_KG",  m.getWeightKg())
                .clearRemaining()
                .render();
    }

    public void updateMember(int id, String fn, String ln, int age, int h, double w) {
        memberService.updateProfileByMemberId(id, fn, ln, age, h, w);
    }

    /*
    MEMBERSHIPS
    */

    public String getMembershipsPage() {

        List<Membership> memberships = membershipService.getAllMemberships();
        StringBuilder rows = new StringBuilder();

        for (Membership m : memberships) {
            rows.append("<tr>")
                    .append("<td>").append(m.getMembershipId()).append("</td>")
                    .append("<td class='fw-semibold'>").append(m.getMembershipName()).append("</td>")
                    .append("<td>").append(m.getDescription()).append("</td>")
                    .append("<td>").append(m.getAllowedMartialArts()).append("</td>")
                    .append("<td>").append(m.getAllowedSkillLevels()).append("</td>")
                    .append("<td class='d-flex gap-2'>")
                    .append("<a href='/admin/edit-membership?membershipId=").append(m.getMembershipId())
                    .append("' class='btn btn-sm btn-outline-primary'><i class='bi bi-pencil me-1'></i>Edit</a>")
                    .append("<form method='POST' action='/admin/delete-membership' style='display:inline;'>")
                    .append("<input type='hidden' name='membershipId' value='").append(m.getMembershipId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' ")
                    .append("onclick='return confirm(\"Delete membership?\")'><i class='bi bi-trash me-1'></i>Delete</button>")
                    .append("</form></td></tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-memberships.html")
                .set("PAGE_TITLE",       "Memberships")
                .set("NAV_MEMBERSHIPS",  "active")
                .set("ROWS_MEMBERSHIPS", rows.toString())
                .clearRemaining()
                .render();
    }

    public void createMembership(String name, String desc, String arts, String skills) {
        membershipService.createMembership(name, desc, arts, skills);
    }

    public void deleteMembership(int id) { membershipService.deleteMembership(id); }

    public String getEditMembershipPage(int membershipId) {

        Membership m         = membershipService.getMembershipById(membershipId);
        String allowedArts   = m.getAllowedMartialArts()  == null ? "" : m.getAllowedMartialArts();
        String allowedSkills = m.getAllowedSkillLevels()  == null ? "" : m.getAllowedSkillLevels();

        StringBuilder artCheckboxes = new StringBuilder();
        for (String a : new String[]{"MMA","Boxing","Kickboxing","Muay Thai","Jiu Jitsu","Wrestling"}) {
            boolean checked = csvContains(allowedArts, a);
            artCheckboxes.append("<div class='form-check'>")
                    .append("<input class='form-check-input arts-cb' type='checkbox' value='").append(a).append("'")
                    .append(checked ? " checked" : "")
                    .append("><label class='form-check-label'>").append(a).append("</label></div>");
        }

        StringBuilder skillCheckboxes = new StringBuilder();
        for (String sk : new String[]{"Beginner","Intermediate/Advanced"}) {
            boolean checked = csvContains(allowedSkills, sk);
            skillCheckboxes.append("<div class='form-check'>")
                    .append("<input class='form-check-input skills-cb' type='checkbox' value='").append(sk).append("'")
                    .append(checked ? " checked" : "")
                    .append("><label class='form-check-label'>").append(sk).append("</label></div>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-edit-membership.html")
                .set("PAGE_TITLE",        "Edit Membership")
                .set("NAV_MEMBERSHIPS",   "active")
                .set("MEMBERSHIP_ID",     m.getMembershipId())
                .set("MEMBERSHIP_NAME",   m.getMembershipName())
                .set("DESCRIPTION",       m.getDescription())
                .set("CHECKBOXES_ARTS",   artCheckboxes.toString())
                .set("CHECKBOXES_SKILLS", skillCheckboxes.toString())
                .set("ALLOWED_ARTS",      allowedArts)
                .set("ALLOWED_SKILLS",    allowedSkills)
                .clearRemaining()
                .render();
    }

    public void updateMembership(int id, String name, String desc, String arts, String skills) {
        membershipService.updateMembership(id, name, desc, arts, skills);
    }

    /*
    EVENTS / TOURNAMENTS
    */

    public String getTournamentsPage() {

        List<Event> events = eventService.getAllEvents();
        StringBuilder rows = new StringBuilder();

        for (Event e : events) {
            String sc = "bg-secondary";
            if ("Live".equals(e.getStatus()))          sc = "bg-danger";
            else if ("Open".equals(e.getStatus()))     sc = "bg-success";
            else if ("Completed".equals(e.getStatus())) sc = "bg-dark";

            rows.append("<tr>")
                    .append("<td>").append(e.getEventId()).append("</td>")
                    .append("<td class='fw-semibold'>").append(e.getEventName()).append("</td>")
                    .append("<td>").append(e.getEventDate()).append("</td>")
                    .append("<td>").append(e.getLocation()).append("</td>")
                    .append("<td><span class='badge ").append(sc).append("'>").append(e.getStatus()).append("</span></td>")
                    .append("<td>").append(e.getAllowedMartialArts() == null ? "" : e.getAllowedMartialArts()).append("</td>")
                    .append("<td><div class='d-flex flex-wrap gap-1'>")
                    .append("<a href='/admin/edit-tournament?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-primary'>Edit</a>")
                    .append("<a href='/admin/view-entrants?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-secondary'>Entrants</a>")
                    .append("<a href='/admin/view-matches?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-secondary'>Matches</a>")
                    .append("<a href='/admin/event-results?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-outline-info'>Results</a>")
                    .append("<a href='/admin/live-control?eventId=").append(e.getEventId()).append("' class='btn btn-sm btn-danger'>Live</a>")
                    .append("<form method='POST' action='/admin/start-event' style='display:inline;'>")
                    .append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>")
                    .append("<button class='btn btn-sm btn-success' type='submit'>Start</button></form>")
                    .append("<form method='POST' action='/admin/delete-tournament' style='display:inline;'>")
                    .append("<input type='hidden' name='eventId' value='").append(e.getEventId()).append("'>")
                    .append("<button class='btn btn-sm btn-outline-danger' type='submit' onclick='return confirm(\"Delete event?\")'>Delete</button>")
                    .append("</form></div></td></tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-tournaments.html")
                .set("PAGE_TITLE",  "Events")
                .set("NAV_EVENTS",  "active")
                .set("ROWS_EVENTS", rows.toString())
                .clearRemaining()
                .render();
    }

    public void createEvent(String name, String date, String loc, String status, String format, String arts) {
        eventService.createEvent(name, date, loc, status, format, arts);
    }

    public void deleteEvent(int eventId) { eventService.deleteEvent(eventId); }

    public void updateEvent(int id, String name, String date, String loc,
                            String status, String format, String arts) {
        eventService.updateEvent(id, name, date, loc, status, format, arts);
    }

    public String getEditTournamentPage(int eventId) {

        Event e        = eventService.getEventById(eventId);
        String allowed = e.getAllowedMartialArts() == null ? "" : e.getAllowedMartialArts();

        StringBuilder statusOpts = new StringBuilder();
        for (String s : new String[]{"Upcoming","Open","Closed","Completed"}) {
            statusOpts.append("<option value='").append(s).append("'")
                    .append(s.equals(e.getStatus()) ? " selected" : "")
                    .append(">").append(s).append("</option>");
        }

        StringBuilder formatOpts = new StringBuilder();
        for (String f : new String[]{"MATCHES","BRACKET"}) {
            formatOpts.append("<option value='").append(f).append("'")
                    .append(f.equals(e.getFormat()) ? " selected" : "")
                    .append(">").append("MATCHES".equals(f) ? "Matches" : "Bracket").append("</option>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-edit-tournament.html")
                .set("PAGE_TITLE",     "Edit Event")
                .set("NAV_EVENTS",     "active")
                .set("EVENT_ID",       e.getEventId())
                .set("EVENT_NAME",     e.getEventName())
                .set("EVENT_DATE",     e.getEventDate())
                .set("EVENT_LOCATION", e.getLocation())
                .set("OPTIONS_STATUS", statusOpts.toString())
                .set("OPTIONS_FORMAT", formatOpts.toString())
                .set("ALLOWED_ARTS",   allowed)
                .clearRemaining()
                .render();
    }

    /*
    ENTRANTS
    */

    public String getEntrantsPage(int eventId) {

        Event event              = eventService.getEventById(eventId);
        List<MemberProfile> list = eventService.getEntrantsForEvent(eventId);
        StringBuilder rows       = new StringBuilder();

        for (MemberProfile m : list) {
            rows.append("<tr>")
                    .append("<td>").append(m.getMemberId()).append("</td>")
                    .append("<td>").append(m.getFirstName()).append(" ").append(m.getLastName()).append("</td>")
                    .append("<td>").append(m.getAge()).append("</td>")
                    .append("<td>").append(m.getWeightKg()).append(" kg</td>")
                    .append("</tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-entrants.html")
                .set("PAGE_TITLE",    "Entrants")
                .set("NAV_EVENTS",    "active")
                .set("EVENT_NAME",    event.getEventName())
                .set("ROWS_ENTRANTS", rows.toString())
                .clearRemaining()
                .render();
    }

    public void generateMatchesForEvent(int eventId) { matchmakingService.generateMatchesForEvent(eventId); }

    /*
    MATCHES
    */

    public String getMatchesPage(int eventId) {

        Event event         = eventService.getEventById(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);
        List<String> msgs   = matchmakingService.getLastGenerationMessages();

        String log = "";
        if (!msgs.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    "<div class='alert alert-info'><strong>Matchmaking Log:</strong><ul class='mb-0 mt-1'>");
            for (String msg : msgs) sb.append("<li>").append(msg).append("</li>");
            sb.append("</ul></div>");
            log = sb.toString();
        }

        StringBuilder rows = new StringBuilder();
        for (Match m : matches) {
            rows.append("<tr>")
                    .append("<td>#").append(m.getMatchId()).append("</td>")
                    .append("<td>").append(m.getParticipant1Name()).append("</td>")
                    .append("<td>").append(m.getParticipant2Name()).append("</td>")
                    .append("<td><span class='badge bg-secondary'>").append(m.getStatus()).append("</span></td>")
                    .append("<td>").append(m.getRoundNumber()).append("</td>")
                    .append("</tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-matches.html")
                .set("PAGE_TITLE",      "Matches")
                .set("NAV_EVENTS",      "active")
                .set("EVENT_NAME",      event.getEventName())
                .set("EVENT_ID",        eventId)
                .set("MATCHMAKING_LOG", log)
                .set("ROWS_MATCHES",    rows.toString())
                .clearRemaining()
                .render();
    }

    /*
    EVENT RESULTS
    */

    public String getAdminEventResultsPage(int eventId) {

        Event event         = eventService.getEventById(eventId);
        List<Match> matches = matchmakingService.getMatchesForEvent(eventId);
        StringBuilder rows  = new StringBuilder();

        for (Match m : matches) {
            String sc = "Live".equals(m.getStatus()) ? "bg-danger"
                    : "Completed".equals(m.getStatus()) ? "bg-dark" : "bg-secondary";

            rows.append("<tr>")
                    .append("<td>#").append(m.getMatchId()).append("</td>")
                    .append("<td>").append(m.getParticipant1Name()).append("</td>")
                    .append("<td>").append(m.getParticipant2Name()).append("</td>")
                    .append("<td><span class='badge ").append(sc).append("'>").append(m.getStatus()).append("</span></td>")
                    .append("<td>").append(m.getRoundNumber()).append("</td>")
                    .append("<td>").append(m.getWinnerName() == null
                            ? "<span class='text-muted'>-</span>"
                            : "<strong>" + m.getWinnerName() + "</strong>").append("</td>")
                    .append("<td>").append(m.getResult() == null || m.getResult().isBlank()
                            ? "<span class='text-muted'>-</span>"
                            : m.getResult()).append("</td>")
                    .append("<td><a href='/admin/live-control?eventId=").append(eventId)
                    .append("&matchId=").append(m.getMatchId())
                    .append("' class='btn btn-sm btn-danger'>Control</a></td>")
                    .append("</tr>");
        }

        return TemplateEngine.load("admin-layout.html", "content/admin-event-results.html")
                .set("PAGE_TITLE",   "Results")
                .set("NAV_EVENTS",   "active")
                .set("EVENT_NAME",   event.getEventName())
                .set("ROWS_RESULTS", rows.toString())
                .clearRemaining()
                .render();
    }

    /*
    LIVE CONTROL
    */

    public String getAdminLiveControlPage(int eventId, Integer selectedMatchId) {

        Event event          = eventService.getEventById(eventId);
        LiveEventState state = liveEventService.getStateForEvent(eventId);
        List<Match> matches  = matchmakingService.getMatchesForEvent(eventId);

        if (selectedMatchId != null) {
            liveEventService.setCurrentMatch(eventId, selectedMatchId);
            state = liveEventService.getStateForEvent(eventId);
        }

        String autoRefresh = state.isTimerRunning()
                ? "<meta http-equiv='refresh' content='1;url=/admin/live-control/tick-and-return?eventId=" + eventId + "'>"
                : "";

        StringBuilder matchOpts = new StringBuilder();
        for (Match m : matches) {
            boolean sel = state.getCurrentMatchId() != null && state.getCurrentMatchId() == m.getMatchId();
            matchOpts.append("<option value='").append(m.getMatchId()).append("'")
                    .append(sel ? " selected" : "").append(">")
                    .append("Match #").append(m.getMatchId()).append(": ")
                    .append(m.getParticipant1Name()).append(" vs ").append(m.getParticipant2Name())
                    .append("</option>");
        }

        String resultSection = "";
        if (state.getCurrentMatchId() != null) {
            Match cur = null;
            for (Match m : matches) {
                if (m.getMatchId() == state.getCurrentMatchId()) { cur = m; break; }
            }
            if (cur != null) {
                resultSection =
                        "<div class='card'><div class='card-header fw-semibold'>Enter Result &mdash; Match #"
                                + cur.getMatchId() + "</div><div class='card-body'>"
                                + "<form method='POST' action='/admin/live-control/result' class='row g-3'>"
                                + "<input type='hidden' name='eventId' value='" + eventId + "'>"
                                + "<input type='hidden' name='matchId' value='" + cur.getMatchId() + "'>"
                                + "<div class='col-md-3'><label class='form-label'>Status</label>"
                                + "<select class='form-select' name='status'>"
                                + "<option>Scheduled</option><option>Live</option><option>Completed</option>"
                                + "</select></div>"
                                + "<div class='col-md-3'><label class='form-label'>Winner</label>"
                                + "<select class='form-select' name='winnerMemberId'>"
                                + "<option value=''>No winner / Draw</option>"
                                + "<option value='" + cur.getParticipant1Id() + "'>" + cur.getParticipant1Name() + "</option>"
                                + "<option value='" + cur.getParticipant2Id() + "'>" + cur.getParticipant2Name() + "</option>"
                                + "</select></div>"
                                + "<div class='col-md-3'><label class='form-label'>Decision</label>"
                                + "<select class='form-select' name='result'>"
                                + "<option>Decision</option><option>KO/TKO</option>"
                                + "<option>Submission</option><option>Draw</option>"
                                + "</select></div>"
                                + "<div class='col-md-2'><label class='form-label'>Round No.</label>"
                                + "<input type='number' class='form-control' name='roundNumber' value='"
                                + state.getCurrentRound() + "'></div>"
                                + "<div class='col-12'><button class='btn btn-success' type='submit'>"
                                + "<i class='bi bi-check-lg me-1'></i>Save Result</button></div>"
                                + "</form></div></div>";
            }
        }

        String timerBadge = state.isTimerRunning()
                ? "<span class='badge bg-light text-danger'>LIVE</span>"
                : "<span class='badge bg-light text-dark'>PAUSED</span>";

        return TemplateEngine.load("admin-layout.html", "content/admin-live-control.html")
                .set("PAGE_TITLE",           "Live Control")
                .set("NAV_EVENTS",           "active")
                .set("AUTO_REFRESH",         autoRefresh)
                .set("EVENT_NAME",           event.getEventName())
                .set("EVENT_ID",             eventId)
                .set("TIMER_BG",             state.isTimerRunning() ? "bg-danger" : "bg-dark")
                .set("TIMER_DISPLAY",        formatSeconds(state.getRemainingSeconds()))
                .set("TIMER_STATUS_BADGE",   timerBadge)
                .set("CURRENT_ROUND",        state.getCurrentRound())
                .set("ROUND_TIME_SECONDS",   state.getRoundTimeSeconds())
                .set("OPTIONS_MATCHES",      matchOpts.toString())
                .set("RESULT_ENTRY_SECTION", resultSection)
                .clearRemaining()
                .render();
    }

    public void setCurrentMatch(int eventId, int matchId)       { liveEventService.setCurrentMatch(eventId, matchId); }
    public void setCurrentRound(int eventId, int round)         { liveEventService.setRound(eventId, round); }
    public void setRoundTime(int eventId, int seconds)          { liveEventService.setRoundTime(eventId, seconds); }
    public void startLiveTimer(int eventId)                     { liveEventService.startTimer(eventId); }
    public void pauseLiveTimer(int eventId)                     { liveEventService.pauseTimer(eventId); }
    public void resetLiveTimer(int eventId)                     { liveEventService.resetTimer(eventId); }
    public void tickLiveTimer(int eventId)                      { liveEventService.tickTimer(eventId); }

    public void saveMatchResult(int matchId, String status, String result,
                                Integer winnerMemberId, int roundNumber) {
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
    PRIVATE HELPERS
    */

    private String classOptions(List<GymClass> classes) {
        StringBuilder sb = new StringBuilder();
        for (GymClass c : classes) {
            sb.append("<option value='").append(c.getClassId()).append("'>")
                    .append(c.getClassName()).append("</option>");
        }
        return sb.toString();
    }

    private String coachOptions(List<Coach> coaches, String selected) {
        StringBuilder sb = new StringBuilder();
        for (Coach c : coaches) {
            sb.append("<option value='").append(c.getName()).append("'")
                    .append(c.getName().equals(selected) ? " selected" : "")
                    .append(">").append(c.getName()).append("</option>");
        }
        return sb.toString();
    }

    private String roomOptions(List<Room> rooms, String selected) {
        StringBuilder sb = new StringBuilder();
        for (Room r : rooms) {
            sb.append("<option value='").append(r.getName()).append("'")
                    .append(r.getName().equals(selected) ? " selected" : "")
                    .append(">").append(r.getName()).append("</option>");
        }
        return sb.toString();
    }

    private String buildConfigBlock(String idx, List<GymClass> classes,
                                    List<Coach> coaches, List<Room> rooms) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='card config-card mb-3'><div class='card-body row g-3'>")
                .append("<div class='col-12 d-flex justify-content-between align-items-center'>")
                .append("<span class='fw-semibold text-muted'>Configuration ").append(idx).append("</span>")
                .append("<button type='button' class='btn btn-sm btn-outline-danger' onclick='removeConfig(this)'>Remove</button>")
                .append("</div>")
                .append("<div class='col-md-3'><label class='form-label'>Class</label>")
                .append("<select class='form-select' name='classId_").append(idx).append("'>");
        for (GymClass c : classes) {
            html.append("<option value='").append(c.getClassId()).append("'>").append(c.getClassName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-2'><label class='form-label'>Skill Level</label>")
                .append("<select class='form-select' name='skillLevel_").append(idx).append("'>")
                .append("<option value='Beginner'>Beginner</option>")
                .append("<option value='Intermediate/Advanced'>Intermediate/Advanced</option>")
                .append("</select></div>")
                .append("<div class='col-md-1'><label class='form-label'>Sessions/wk</label>")
                .append("<input type='number' class='form-control' min='1' max='5' name='sessionsPerWeek_").append(idx).append("'></div>")
                .append("<div class='col-md-2'><label class='form-label'>Duration (min)</label>")
                .append("<input type='number' class='form-control' min='30' step='30' name='durationMinutes_").append(idx).append("'></div>")
                .append("<div class='col-md-1'><label class='form-label'>After</label>")
                .append("<input type='time' class='form-control' name='afterTime_").append(idx).append("'></div>")
                .append("<div class='col-md-1'><label class='form-label'>Before</label>")
                .append("<input type='time' class='form-control' name='beforeTime_").append(idx).append("'></div>")
                .append("<div class='col-md-2'><label class='form-label'>Coach</label>")
                .append("<select class='form-select' name='coachName_").append(idx).append("'>");
        for (Coach c : coaches) {
            html.append("<option value='").append(c.getName()).append("'>").append(c.getName()).append("</option>");
        }
        html.append("</select></div>")
                .append("<div class='col-md-2'><label class='form-label'>Room</label>")
                .append("<select class='form-select' name='roomName_").append(idx).append("'>");
        for (Room r : rooms) {
            html.append("<option value='").append(r.getName()).append("'>").append(r.getName()).append("</option>");
        }
        html.append("</select></div></div></div>");
        return html.toString();
    }

    private String formatSeconds(int total) {
        return String.format("%02d:%02d", total / 60, total % 60);
    }

    private boolean csvContains(String csv, String value) {
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