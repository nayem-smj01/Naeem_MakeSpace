package com.meeting.schedulars.room;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;


public interface RoomServices {
    String allocateRoom(List<String> meetingRequests);

    String handleBooking(List<String> meetingRequests);

    String handleVacancy(List<String> meetingRequests);

    String bookTowerOrMansion(List<String> meetingRequests, int slots);

    boolean bookMansion(List<String> meetingRequests, int slots) ;

    void bookRooms(String beginTime, int slots, Map<LocalTime, String> cCalender);

}
