package com.meeting.schedulars;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meeting.schedulars.MeetingConstants.*;

public class Meeting {

    public static void main(String[] args) {

            init();
            executeTask(args);

    }


    private static void init() {

        List<String> reservedSlots = Arrays.asList(new String[]{"09:00", "13:15", "13:30", "18:45"});

        int minimumDuration = 15;
        int slots = ((int) Duration.ofHours(24).toMinutes() / minimumDuration);
        List<LocalTime> times = new ArrayList<>(slots);

        LocalTime time = LocalTime.MIN;
        for (int i = 1; i <= slots; i++) {
            times.add(time);
            if (reservedSlots.contains(time.toString())) {
                MeetingConstants.C_CALENDER.put(time, BOOKED);
                MeetingConstants.D_CALENDER.put(time, BOOKED);
                MeetingConstants.G_CALENDER.put(time, BOOKED);
            } else {
                MeetingConstants.C_CALENDER.put(time, VACANT);
                MeetingConstants.D_CALENDER.put(time, VACANT);
                MeetingConstants.G_CALENDER.put(time, VACANT);
            }
            time = time.plusMinutes(minimumDuration);
        }
    }

    public static void executeTask(String[] args)  {

        String inputFile = null;
        try {
            inputFile = args[0];
            List<String> messages;
            messages = Files.readAllLines(Paths.get(inputFile),
                    StandardCharsets.UTF_8);

            messages = messages.stream()
                    .filter(line -> (line != null && !line.isBlank() && !line.isEmpty()))
                    .filter(line -> line.contains(" "))
                    .map(line -> {
                        String[] words = line.split(" ");
                        return allocateRoom(Arrays.asList(words));
                    }).collect(Collectors.toList());
            messages.forEach(m -> System.out.println(m));


        } catch (IOException e) {
            System.out.println(" Specified file " + inputFile + "  not found. Supply valid file");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(MeetingConstants.INCORRECT_INPUT);
        } catch (Exception e) {
            System.out.println(" Exception:" + e.getLocalizedMessage());
        }

    }

    private static String allocateRoom(List<String> meetingRequests) {
        if ("BOOK".equalsIgnoreCase(meetingRequests.get(0))) {
            return handleBooking(meetingRequests);
        } else if ("VACANCY".equalsIgnoreCase(meetingRequests.get(0))) {
            return handleVacancy(meetingRequests);
        }
        return NO_VACANT_ROOM;
    }

    private static String handleBooking(List<String> meetingRequests) {
        String result = checkForValidTimings(meetingRequests);
        if (MeetingConstants.INCORRECT_INPUT == result)
            return result;

        int slots = slotsRequired(meetingRequests);
        int persons = Integer.parseInt(meetingRequests.get(3));
        if(persons > 20){
            return NO_VACANT_ROOM;
        }
        if (persons > 7) {
            if (bookMansion(meetingRequests, slots))
                return G_MANSION;
        }
        if (persons <= 7 && persons > 3) {
            String dTower = bookTowerOrMansion(meetingRequests, slots);
            if (dTower != null)
                return dTower;
        }
        if (persons <= 3) {
            boolean isCaveAvailable = checkAvailability(meetingRequests, slots, C_CAVE);
            if(isCaveAvailable){
                bookRooms(meetingRequests.get(1), slots, C_CALENDER);
                return C_CAVE;
            }
            else {
                String dTower = bookTowerOrMansion(meetingRequests, slots);
                if (dTower != null)
                    return dTower;
            }

        }
        return NO_VACANT_ROOM;
    }

    private static String bookTowerOrMansion(List<String> meetingRequests, int slots) {
        boolean isTowerAvailable = checkAvailability(meetingRequests, slots, D_TOWER);
        if (isTowerAvailable) {
            bookRooms(meetingRequests.get(1), slots, D_CALENDER);
            return D_TOWER;
        }
        if (bookMansion(meetingRequests, slots))
            return G_MANSION;
        return null;
    }

    private static boolean bookMansion(List<String> meetingRequests, int slots) {
        boolean isAvailableMansion = checkAvailability(meetingRequests, slots, G_MANSION);
        if (isAvailableMansion) {
            bookRooms(meetingRequests.get(1), slots, G_CALENDER);
            return true;
        }
        return false;
    }

    private static void bookRooms(String beginTime, int slots, Map<LocalTime, String> cCalender) {
        LocalTime timeKey = getLocalTime(beginTime);
        for (int i = 0; i < slots; i++) {
            cCalender.replace(timeKey, VACANT, BOOKED);
            timeKey = timeKey.plusMinutes(15);
        }
    }

    private static String handleVacancy(List<String> meetingRequests) {

        String result = checkForValidTimings(meetingRequests);
        if (INCORRECT_INPUT.equalsIgnoreCase(result))
            return result;

        int slots = slotsRequired(meetingRequests);
        boolean isCaveAvailable = checkAvailability(meetingRequests, slots, C_CAVE);
        boolean isTowerAvailable = checkAvailability(meetingRequests, slots, D_TOWER);
        boolean isMansionAvailable = checkAvailability(meetingRequests, slots, G_MANSION);

        if (isCaveAvailable && isMansionAvailable && isTowerAvailable)
            return C_CAVE + " " + D_TOWER + " " + G_MANSION;

        if (isCaveAvailable && isMansionAvailable)
            return C_CAVE + " " + G_MANSION;

        if (isCaveAvailable &&  isTowerAvailable)
            return C_CAVE + " " + D_TOWER;

        if (isCaveAvailable)
            return C_CAVE;

        if (isTowerAvailable)
            return D_TOWER;

        if (isMansionAvailable)
            return G_MANSION;

        return NO_VACANT_ROOM;
    }


    /**
     * If Rooms are available API return True
     *
     * @param meetingRequests
     * @param slots
     * @param roomName
     * @return
     */
    private static boolean checkAvailability(List<String> meetingRequests, int slots, String roomName) {
        String meetingStartTime = meetingRequests.get(1);
        LocalTime startTime = getLocalTime(meetingStartTime);
        switch (roomName) {
            case C_CAVE: {
                if (C_CALENDER.get(startTime).equalsIgnoreCase(BOOKED))
                    return false;
                return checkAvailability(meetingStartTime, slots, C_CALENDER);
            }
            case D_TOWER: {
                if (D_CALENDER.get(startTime).equalsIgnoreCase(BOOKED))
                    return false;
                return checkAvailability(meetingStartTime, slots, D_CALENDER);
            }
            case G_MANSION: {
                if (G_CALENDER.get(startTime).equalsIgnoreCase(BOOKED))
                    return false;
                return checkAvailability(meetingStartTime, slots, G_CALENDER);
            }
            default:
                return false;
        }

    }

    /**
     * API which checks the map for VACANT slot
     *
     * @param meetingStartTime
     * @param slots
     * @param cCalender
     * @return True implies availability of Slot
     */
    private static boolean checkAvailability(String meetingStartTime, int slots, Map<LocalTime, String> cCalender) {
        LocalTime timeKey = getLocalTime(meetingStartTime);
        for (int i = 0; i < slots-1; i++) {
            timeKey = timeKey.plusMinutes(15);
            if (cCalender.get(timeKey).equalsIgnoreCase(BOOKED)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handle case when
     * 1. Input or Output timings are not multiples of 15
     * 2. Start time is greater than End time
     *
     * @param meetingRequests
     * @return
     */
    private static String checkForValidTimings(List<String> meetingRequests) {
        int startTimeMinutes = extractMinutes(meetingRequests.get(1));
        int endTimeMinutes = extractMinutes(meetingRequests.get(2));
        if (startTimeMinutes % 15 != 0 || endTimeMinutes % 15 != 0)
            return MeetingConstants.INCORRECT_INPUT;

        LocalTime startTime = getLocalTime(meetingRequests.get(1));
        LocalTime endTime = getLocalTime(meetingRequests.get(2));

        if (startTime.isAfter(endTime))
            return INCORRECT_INPUT;
        return CONTINUE_FLOW;


    }

    private static int extractMinutes(String meetingTime) {
        LocalTime localTime = getLocalTime(meetingTime);
        return localTime.get(ChronoField.MINUTE_OF_HOUR);
    }

    private static LocalTime getLocalTime(String meetingTime) {
        return LocalTime.parse(meetingTime, DateTimeFormatter.ofPattern("HH:mm"));
    }


    private static int slotsRequired(List<String> meetingRequests) {
        LocalTime firstDate = getLocalTime(meetingRequests.get(1));
        LocalTime secondDate = getLocalTime(meetingRequests.get(2));
        long slotsNeeded = ChronoUnit.MINUTES.between(firstDate, secondDate) / 15;
        return Integer.parseInt(String.valueOf(slotsNeeded));
    }
}
