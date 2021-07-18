package com.meeting.schedulars;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MeetingConstants {

    //OutPut Values
    static final String INCORRECT_INPUT = "INCORRECT_INPUT";
    static final String NO_VACANT_ROOM ="NO_VACANT_ROOM";
    static final String CONTINUE_FLOW ="CONTINUE_FLOW";

    // Room Status
    static final String VACANT = "VACANT";
    static final String BOOKED = "BOOKED";

    // Room Names
    static final String C_CAVE = "C-Cave";
    static final String D_TOWER = "D-Tower";
    static final String G_MANSION = "G-Mansion";

    // Calender for each Room
    static Map<LocalTime,String > C_CALENDER;
    static Map<LocalTime,String > D_CALENDER;
    static Map<LocalTime,String > G_CALENDER;

    static{
        C_CALENDER =new TreeMap<>();
        D_CALENDER =new TreeMap<>();
        G_CALENDER =new TreeMap<>();

    }
}
