package com.meeting.schedulars;


import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;

import static com.meeting.schedulars.MeetingConstants.BOOKED;
import static com.meeting.schedulars.MeetingConstants.VACANT;

public class SingletonCalender {

   public Map<LocalTime, String> CAVE_CALENDER;
   public Map<LocalTime, String> TOWER_CALENDER;
   public Map<LocalTime, String> MANSION_CALENDER;

    private static SingletonCalender instance;

    private SingletonCalender() {
        CAVE_CALENDER = new TreeMap<>();
        TOWER_CALENDER = new TreeMap<>();
        MANSION_CALENDER = new TreeMap<>();

        init();
    }

    private void init() {
        List<String> reservedSlots = Arrays.asList("09:00", "13:15", "13:30", "18:45");

        int minimumDuration = 15;
        int slots = ((int) Duration.ofHours(24).toMinutes() / minimumDuration);

        LocalTime time = LocalTime.MIN;

        for (int i = 1; i <= slots; i++) {

            if (reservedSlots.contains(time.toString())) {
                CAVE_CALENDER.put(time, BOOKED);
                TOWER_CALENDER.put(time, BOOKED);
                MANSION_CALENDER.put(time, BOOKED);
            } else {
                CAVE_CALENDER.put(time, VACANT);
                TOWER_CALENDER.put(time, VACANT);
                MANSION_CALENDER.put(time, VACANT);
            }
            time = time.plusMinutes(minimumDuration);
        }
    }

    public static SingletonCalender getInstance() {
        if (instance == null) {
            instance = new SingletonCalender();
        }
        return instance;
    }

    public int extractMinutes(String meetingTime) {
        LocalTime localTime = getLocalTime(meetingTime);
        return localTime.get(ChronoField.MINUTE_OF_HOUR);
    }

    public LocalTime getLocalTime(String meetingTime) {
        return LocalTime.parse(meetingTime, DateTimeFormatter.ofPattern("HH:mm"));
    }



}
