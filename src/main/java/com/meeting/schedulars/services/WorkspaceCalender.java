package com.meeting.schedulars.services;

import com.meeting.schedulars.SingletonCalender;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface WorkspaceCalender {

    /**
     * If Rooms are available API return True
     *
     * @param meetingRequests
     * @param slots
     * @param roomName
     * @return
     */
    boolean checkAvailability(List<String> meetingRequests, int slots, String roomName);
    /**
     * API which checks the map for VACANT slot
     *
     * @param meetingStartTime
     * @param slots
     * @param cCalender
     * @return True implies availability of Slot
     */
    boolean checkAvailability(String meetingStartTime, int slots, Map<LocalTime, String> cCalender);

    /**
     * Handle case when
     * 1. Input or Output timings are not multiples of 15
     * 2. Start time is greater than End time
     *
     * @param meetingRequests
     * @return
     */
    String checkForValidTimings(List<String> meetingRequests);
}
