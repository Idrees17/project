package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.*;
import uk.ac.city.mma.service.*;
import uk.ac.city.mma.util.TemplateEngine;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class MemberController {

    private MemberService memberService                                = new MemberService();
    private EventService eventService                                  = new EventService();
    private LiveEventService liveEventService                          = new LiveEventService();
    private MatchmakingService matchmakingService                      = new MatchmakingService();
    private MembershipService membershipService                        = new MembershipService();
    private ClassSessionService classSessionService                    = new ClassSessionService();
    private ClassSessionRegistrationService sessionRegistrationService = new ClassSessionRegistrationService();

    /*
     PROFILE
    */

    public String getProfilePage(int userId) {

        MemberProfile profile = memberService.getProfileByUserId(userId);

        String firstName = "", lastName = "", age = "", heightCm = "", weightKg = "";
        if (profile != null) {
            firstName = safe(profile.getFirstName());
            lastName  = safe(profile.getLastName());
            age       = String.valueOf(profile.getAge());
            heightCm  = String.valueOf(profile.getHeightCm());
            weightKg  = String.valueOf(profile.getWeightKg());
        }

        return TemplateEngine.load("member-layout.html", "content/member-profile.html")
                .set("PAGE_TITLE",  "My Profile")
                .set("NAV_PROFILE", "active")
                .set("FIRST_NAME",  firstName)
                .set("LAST_NAME",   lastName)
                .set("AGE",         age)
                .set("HEIGHT_CM",   heightCm)
                .set("WEIGHT_KG",   weightKg)
                .clearRemaining()
                .render();
    }

    public void saveProfile(int userId, String firstName, String lastName,
                            int age, int heightCm, double weightKg) {
        memberService.saveProfile(userId, firstName, lastName, age, heightCm, weightKg);
    }

    /*
    TIMETABLE
    */

    public String getTimetablePage(int memberId, String weekParam) {

        List<ClassSession> sessions = classSessionService.getAllSessions();
        Membership membership       = membershipService.getMembershipForMember(memberId);

        /*
        WEEK CALCULATION
        */
        LocalDate today  = LocalDate.now();
        LocalDate monday;

        if (weekParam != null && !weekParam.isBlank()) {
            try {
                monday = LocalDate.parse(weekParam);
            } catch (Exception e) {
                monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }
        } else {
            monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        LocalDate sunday         = monday.plusDays(6);
        String weekStartDate     = monday.toString();
        String prevWeek          = monday.minusWeeks(1).toString();
        String nextWeek          = monday.plusWeeks(1).toString();

        DateTimeFormatter display = DateTimeFormatter.ofPattern("d MMM yyyy");
        String weekLabel = monday.format(display) + " \u2014 " + sunday.format(display);

        List<Integer> registeredIds = sessionRegistrationService
                .getRegisteredSessionIdsForMemberAndWeek(memberId, weekStartDate);

        String[] dayNames = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        /*
        MEMBERSHIP BADGE / ALERT
        */
        String membershipBadge = membership != null
                ? "<span class='badge bg-primary fs-6'><i class='bi bi-card-checklist me-1'></i>"
                + membership.getMembershipName() + "</span>"
                : "";

        String membershipAlert = membership == null
                ? "<div class='alert alert-warning d-flex align-items-center gap-3'>"
                + "<i class='bi bi-exclamation-triangle-fill fs-4'></i>"
                + "<div>You don't have a membership yet. "
                + "<a href='/member/memberships' class='alert-link'>Choose a membership</a> "
                + "to register for classes.</div></div>"
                : "";

        /*
        DAY HEADER ROW
        */
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEE d MMM");
        StringBuilder dayHeaders = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            boolean isToday = date.equals(today);
            String bg = isToday ? "style='background:#0d6efd;color:#fff'" : "";
            dayHeaders.append("<th class='text-center py-3' ").append(bg).append(">")
                    .append(date.format(dayFmt))
                    .append("</th>");
        }

        /*
        TIME SLOT ROWS
        One row per 30-minute slot from 06:00 to 22:00.
        */
        StringBuilder timeRows = new StringBuilder();

        for (int hour = 6; hour < 22; hour++) {
            for (int min = 0; min < 60; min += 30) {
                String slotTime = String.format("%02d:%02d", hour, min);

                timeRows.append("<tr>");
                timeRows.append("<td class='time-cell'>").append(slotTime).append("</td>");

                for (int d = 0; d < 7; d++) {
                    String dayName   = dayNames[d];
                    LocalDate sessionDate = monday.plusDays(d);
                    boolean isPast   = sessionDate.isBefore(today);
                    timeRows.append("<td class='timetable-cell'>");

                    for (ClassSession s : sessions) {
                        if (!s.getDayOfWeek().equalsIgnoreCase(dayName)) continue;
                        if (!s.getStartTime().equals(slotTime)) continue;

                        boolean registered = registeredIds.contains(s.getSessionId());
                        boolean allowed    = membership != null && isMembershipCompatible(membership, s);
                        int spots = sessionRegistrationService
                                .getRegistrationCountForWeek(s.getSessionId(), weekStartDate);

                        String bgColor;
                        if (isPast)            bgColor = "#adb5bd";
                        else if (registered)   bgColor = "#198754";
                        else if (!allowed)     bgColor = "#6c757d";
                        else                   bgColor = "#0d6efd";

                        timeRows.append("<div class='session-block' style='background:").append(bgColor)
                                .append(";color:#fff'>");
                        timeRows.append("<span class='session-name'>").append(s.getClassName()).append("</span>");
                        timeRows.append("<span class='session-time'>")
                                .append(s.getStartTime()).append(" \u00b7 ").append(s.getDurationMinutes()).append("m")
                                .append("</span>");
                        timeRows.append("<span class='session-spots'>").append(spots).append(" booked</span>");

                        if (isPast) {
                            timeRows.append("<span style='font-size:0.7rem;opacity:0.85'>"
                                    + "<i class='bi bi-clock-history me-1'></i>Past</span>");
                        } else if (registered) {
                            timeRows.append("<form method='POST' action='/member/timetable/unregister' class='mt-1'>")
                                    .append("<input type='hidden' name='sessionId' value='").append(s.getSessionId()).append("'>")
                                    .append("<input type='hidden' name='weekStartDate' value='").append(weekStartDate).append("'>")
                                    .append("<button class='btn btn-sm btn-light w-100' style='font-size:0.7rem;padding:2px 4px' type='submit'>Unregister</button>")
                                    .append("</form>");
                        } else if (allowed) {
                            timeRows.append("<form method='POST' action='/member/timetable' class='mt-1'>")
                                    .append("<input type='hidden' name='sessionId' value='").append(s.getSessionId()).append("'>")
                                    .append("<input type='hidden' name='weekStartDate' value='").append(weekStartDate).append("'>")
                                    .append("<button class='btn btn-sm btn-light w-100' style='font-size:0.7rem;padding:2px 4px' type='submit'>Register</button>")
                                    .append("</form>");
                        }

                        timeRows.append("</div>");
                    }

                    timeRows.append("</td>");
                }

                timeRows.append("</tr>");
            }
        }

        return TemplateEngine.load("member-layout.html", "content/member-timetable.html")
                .set("PAGE_TITLE",       "Timetable")
                .set("NAV_TIMETABLE",    "active")
                .set("MEMBERSHIP_BADGE", membershipBadge)
                .set("MEMBERSHIP_ALERT", membershipAlert)
                .set("WEEK_LABEL",       weekLabel)
                .set("PREV_WEEK",        prevWeek)
                .set("NEXT_WEEK",        nextWeek)
                .set("DAY_HEADERS",      dayHeaders.toString())
                .set("TIME_ROWS",        timeRows.toString())
                .clearRemaining()
                .render();
    }

    private boolean isMembershipCompatible(Membership membership, ClassSession session) {
        String allowedSkills = membership.getAllowedSkillLevels();
        String allowedArts   = membership.getAllowedMartialArts();

        if (allowedSkills != null && !allowedSkills.isBlank()) {
            boolean match = false;
            for (String s : allowedSkills.split(",")) {
                if (s.trim().equalsIgnoreCase(session.getSkillLevel())) { match = true; break; }
            }
            if (!match) return false;
        }

        if (allowedArts != null && !allowedArts.isBlank()) {
            boolean match = false;
            for (String a : allowedArts.split(",")) {
                if (a.trim().equalsIgnoreCase(session.getClassType())) { match = true; break; }
            }
            if (!match) return false;
        }
        return true;
    }

    public void registerForSession(int memberId, int sessionId, String weekStartDate) {
        sessionRegistrationService.registerMemberForSession(sessionId, memberId, weekStartDate);
    }

    public void unregisterFromSession(int memberId, int sessionId, String weekStartDate) {
        sessionRegistrationService.unregisterMemberFromSession(sessionId, memberId, weekStartDate);
    }

    /*
    EVENTS
    */

    public String getEventsPage(int memberId) {

        List<Event> allEvents       = eventService.getAllEvents();
        List<Integer> registeredIds = eventService.getRegisteredEventIdsForMember(memberId);

        StringBuilder availableRows = new StringBuilder();
        StringBuilder myRows        = new StringBuilder();

        for (Event e : allEvents) {
            String sc = "bg-secondary";
            if ("Open".equals(e.getStatus())) sc = "bg-success";
            if ("Live".equals(e.getStatus())) sc = "bg-danger";

            if (!registeredIds.contains(e.getEventId())) {
                String registerCell;
                if (e.getAllowedMartialArts() == null || e.getAllowedMartialArts().isBlank()) {
                    registerCell = "<span class='text-muted small'>No martial arts configured</span>";
                } else {
                    StringBuilder form = new StringBuilder(
                            "<form method='POST' action='/member/events'"
                                    + " class='d-flex flex-wrap gap-2 align-items-center'>"
                                    + "<input type='hidden' name='eventId' value='" + e.getEventId() + "'>"
                                    + "<select class='form-select form-select-sm' name='chosenMartialArt' style='width:auto'>");
                    for (String art : e.getAllowedMartialArts().split(",")) {
                        String t = art.trim();
                        if (!t.isEmpty()) form.append("<option value='").append(t).append("'>").append(t).append("</option>");
                    }
                    form.append("</select>")
                            .append("<select class='form-select form-select-sm' name='experienceLevel' style='width:auto'>")
                            .append("<option value='Beginner'>Beginner</option>")
                            .append("<option value='Intermediate'>Intermediate</option>")
                            .append("<option value='Advanced'>Advanced</option>")
                            .append("</select>")
                            .append("<button class='btn btn-sm btn-primary' type='submit'>")
                            .append("<i class='bi bi-plus-circle me-1'></i>Register</button></form>");
                    registerCell = form.toString();
                }

                availableRows.append("<tr>")
                        .append("<td class='fw-semibold'>").append(e.getEventName()).append("</td>")
                        .append("<td>").append(e.getEventDate()).append("</td>")
                        .append("<td>").append(e.getLocation()).append("</td>")
                        .append("<td><span class='badge ").append(sc).append("'>").append(e.getStatus()).append("</span></td>")
                        .append("<td>").append(e.getAllowedMartialArts() == null ? "" : e.getAllowedMartialArts()).append("</td>")
                        .append("<td>").append(registerCell).append("</td>")
                        .append("</tr>");
            } else {
                myRows.append("<tr>")
                        .append("<td class='fw-semibold'>").append(e.getEventName()).append("</td>")
                        .append("<td>").append(e.getEventDate()).append("</td>")
                        .append("<td>").append(e.getLocation()).append("</td>")
                        .append("<td><span class='badge ").append(sc).append("'>").append(e.getStatus()).append("</span></td>")
                        .append("<td><a href='/member/event-results?eventId=").append(e.getEventId())
                        .append("' class='btn btn-sm btn-outline-primary'>")
                        .append("<i class='bi bi-eye me-1'></i>View</a></td>")
                        .append("</tr>");
            }
        }

        return TemplateEngine.load("member-layout.html", "content/member-events.html")
                .set("PAGE_TITLE",            "Events")
                .set("NAV_EVENTS",            "active")
                .set("ROWS_AVAILABLE_EVENTS", availableRows.toString())
                .set("ROWS_MY_EVENTS",        myRows.toString())
                .clearRemaining()
                .render();
    }

    public void registerForEvent(int memberId, int eventId, String chosenMartialArt, String experienceLevel) {
        eventService.registerMemberForEvent(eventId, memberId, chosenMartialArt, experienceLevel);
    }

    /*
     EVENT RESULTS
    */

    public String getMemberEventResultsPage(int eventId) {

        Event event          = eventService.getEventById(eventId);
        LiveEventState state = liveEventService.getStateForEvent(eventId);
        List<Match> matches  = matchmakingService.getMatchesForEvent(eventId);

        String timerBadge = state.isTimerRunning()
                ? "<span class='badge bg-light text-danger'>LIVE</span>"
                : "<span class='badge bg-light text-dark'>PAUSED</span>";

        StringBuilder rows = new StringBuilder();
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
                    .append("</tr>");
        }

        return TemplateEngine.load("member-layout.html", "content/member-event-results.html")
                .set("PAGE_TITLE",         "Event Results")
                .set("NAV_EVENTS",         "active")
                .set("EVENT_NAME",         event.getEventName())
                .set("TIMER_BG",           state.isTimerRunning() ? "bg-danger" : "bg-dark")
                .set("TIMER_DISPLAY",      formatSeconds(state.getRemainingSeconds()))
                .set("CURRENT_ROUND",      state.getCurrentRound())
                .set("TIMER_STATUS_BADGE", timerBadge)
                .set("ROWS_MATCHES",       rows.toString())
                .clearRemaining()
                .render();
    }

    /*
    MEMBERSHIPS
    */

    public String getMembershipsPage(int memberId) {

        List<Membership> memberships = membershipService.getAllMemberships();
        Membership current           = membershipService.getMembershipForMember(memberId);

        String currentAlert = current != null
                ? "<div class='alert alert-success'><i class='bi bi-check-circle me-2'></i>"
                + "<strong>Current Membership:</strong> " + current.getMembershipName() + "</div>"
                : "<div class='alert alert-warning'><i class='bi bi-exclamation-triangle me-2'></i>"
                + "You do not have an active membership. Select one below.</div>";

        StringBuilder cards = new StringBuilder();
        for (Membership m : memberships) {
            boolean isCurrent = current != null && current.getMembershipId() == m.getMembershipId();

            cards.append("<div class='col-md-4'>")
                    .append("<div class='card h-100").append(isCurrent ? " border-primary border-2" : "").append("'>")
                    .append("<div class='card-body d-flex flex-column'>")
                    .append("<h5 class='card-title'>").append(m.getMembershipName())
                    .append(isCurrent ? " <span class='badge bg-primary ms-1'>Active</span>" : "")
                    .append("</h5>")
                    .append("<p class='card-text text-muted'>").append(m.getDescription()).append("</p>")
                    .append("<ul class='list-unstyled small mb-3'>")
                    .append("<li><i class='bi bi-check2 text-success me-1'></i>")
                    .append("<strong>Martial Arts:</strong> ").append(m.getAllowedMartialArts()).append("</li>")
                    .append("<li><i class='bi bi-check2 text-success me-1'></i>")
                    .append("<strong>Skill Levels:</strong> ").append(m.getAllowedSkillLevels()).append("</li>")
                    .append("</ul><div class='mt-auto'>")
                    .append("<form method='POST' action='/member/memberships'>")
                    .append("<input type='hidden' name='membershipId' value='").append(m.getMembershipId()).append("'>")
                    .append("<button class='btn ").append(isCurrent ? "btn-outline-primary" : "btn-primary")
                    .append(" w-100' type='submit'>")
                    .append(isCurrent
                            ? "<i class='bi bi-check-lg me-1'></i>Current Plan"
                            : "<i class='bi bi-arrow-right-circle me-1'></i>Select")
                    .append("</button></form></div></div></div></div>");
        }

        return TemplateEngine.load("member-layout.html", "content/member-memberships.html")
                .set("PAGE_TITLE",               "Membership")
                .set("NAV_MEMBERSHIP",           "active")
                .set("CURRENT_MEMBERSHIP_ALERT", currentAlert)
                .set("ROWS_MEMBERSHIP_CARDS",    cards.toString())
                .clearRemaining()
                .render();
    }

    public void chooseMembership(int memberId, int membershipId) {
        membershipService.assignMembershipToMember(memberId, membershipId);
    }

    /*
     HELPERS
    */

    private String formatSeconds(int total) {
        return String.format("%02d:%02d", total / 60, total % 60);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}