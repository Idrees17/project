package uk.ac.city.mma.service;

import uk.ac.city.mma.model.Event;
import uk.ac.city.mma.model.MemberProfile;
import uk.ac.city.mma.repository.EventRegistrationRepository;
import uk.ac.city.mma.repository.EventRepository;

import java.util.List;

public class EventService {

    private EventRepository eventRepository = new EventRepository();
    private EventRegistrationRepository registrationRepository = new EventRegistrationRepository();

    public void createEvent(String eventName, String eventDate, String location,
                            String status, String format, String allowedMartialArts) {
        Event event = new Event();
        event.setEventName(eventName);
        event.setEventDate(eventDate);
        event.setLocation(location);
        event.setStatus(status);
        event.setFormat(format);
        event.setAllowedMartialArts(allowedMartialArts);

        eventRepository.createEvent(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.getAllEvents();
    }

    public Event getEventById(int eventId) {
        return eventRepository.getEventById(eventId);
    }

    public void updateEvent(int eventId, String eventName, String eventDate, String location,
                            String status, String format, String allowedMartialArts) {
        Event event = new Event();
        event.setEventId(eventId);
        event.setEventName(eventName);
        event.setEventDate(eventDate);
        event.setLocation(location);
        event.setStatus(status);
        event.setFormat(format);
        event.setAllowedMartialArts(allowedMartialArts);

        eventRepository.updateEvent(event);
    }

    public void deleteEvent(int eventId) {
        registrationRepository.deleteRegistrationsForEvent(eventId);
        eventRepository.deleteEvent(eventId);
    }

    public void registerMemberForEvent(int eventId, int memberId, String chosenMartialArt, String experienceLevel) {
        if (!registrationRepository.isMemberRegistered(eventId, memberId)) {
            registrationRepository.registerMember(eventId, memberId, chosenMartialArt, experienceLevel);
        }
    }

    public List<MemberProfile> getEntrantsForEvent(int eventId) {
        return registrationRepository.getEntrantsForEvent(eventId);
    }
}