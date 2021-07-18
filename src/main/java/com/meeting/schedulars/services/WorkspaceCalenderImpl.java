package com.meeting.schedulars.services;

import com.meeting.schedulars.MeetingConstants;
import com.meeting.schedulars.SingletonCalender;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.meeting.schedulars.MeetingConstants.*;


public class WorkspaceCalenderImpl implements WorkspaceCalender{

    SingletonCalender singletonCalender;

    public WorkspaceCalenderImpl() {
        this.singletonCalender = SingletonCalender.getInstance();
    }

    @Override
    public boolean checkAvailability(List<String> meetingRequests, int slots, String roomName) {
        String meetingStartTime = meetingRequests.get(1);
        LocalTime startTime = singletonCalender.getLocalTime(meetingStartTime);
        switch (roomName) {
            case C_CAVE: {
                if (singletonCalender.CAVE_CALENDER.get(startTime).equals(BOOKED))
                    return false;
                return checkAvailability(meetingStartTime, slots, singletonCalender.CAVE_CALENDER);
            }
            case D_TOWER: {
                if (singletonCalender.TOWER_CALENDER.get(startTime).equals(BOOKED))
                    return false;
                return checkAvailability(meetingStartTime, slots, singletonCalender.TOWER_CALENDER);
            }
            case G_MANSION: {
                if (singletonCalender.MANSION_CALENDER.get(startTime).equals(BOOKED))
                    return false;
                return checkAvailability(meetingStartTime, slots, singletonCalender.MANSION_CALENDER);
            }
            default:
                return false;
        }

    }

    @Override
    public boolean checkAvailability(String meetingStartTime, int slots, Map<LocalTime, String> cCalender) {
        LocalTime timeKey = singletonCalender.getLocalTime(meetingStartTime);
        for (int i = 0; i < slots-1; i++) {
            timeKey = timeKey.plusMinutes(15);
            if (cCalender.get(timeKey).equalsIgnoreCase(BOOKED)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String checkForValidTimings(List<String> meetingRequests) {
        int startTimeMinutes =  singletonCalender.extractMinutes(meetingRequests.get(1));
        int endTimeMinutes = singletonCalender.extractMinutes(meetingRequests.get(2));
        if (startTimeMinutes % 15 != 0 || endTimeMinutes % 15 != 0)
            return MeetingConstants.INCORRECT_INPUT;

        LocalTime startTime = singletonCalender.getLocalTime(meetingRequests.get(1));
        LocalTime endTime = singletonCalender.getLocalTime(meetingRequests.get(2));

        if (startTime.isAfter(endTime))
            return INCORRECT_INPUT;
        return CONTINUE_FLOW;

    }
}
