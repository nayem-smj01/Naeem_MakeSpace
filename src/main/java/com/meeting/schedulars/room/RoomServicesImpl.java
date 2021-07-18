package com.meeting.schedulars.room;



import com.meeting.schedulars.MeetingConstants;
import com.meeting.schedulars.SingletonCalender;
import com.meeting.schedulars.room.model.Room;
import com.meeting.schedulars.services.WorkspaceCalender;
import com.meeting.schedulars.services.WorkspaceCalenderImpl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static com.meeting.schedulars.MeetingConstants.*;

public class RoomServicesImpl implements RoomServices {

    WorkspaceCalender workspaceCalender;

    public RoomServicesImpl() {
        this.workspaceCalender = new WorkspaceCalenderImpl();
    }

    @Override
    public String allocateRoom(List<String> meetingRequests) {
        if (BOOK.equalsIgnoreCase(meetingRequests.get(0))) {
            return handleBooking(meetingRequests);
        } else if (VACANCY.equalsIgnoreCase(meetingRequests.get(0))) {
            return handleVacancy(meetingRequests);
        }
        return NO_VACANT_ROOM;

    }

    @Override
    public String handleBooking(List<String> meetingRequests) {
        Room cave = new Room(C_CAVE,false,3);
        Room tower = new Room(D_TOWER,false,7);
        Room mansion = new Room(G_MANSION,false,20);
        SingletonCalender singletonCalender = SingletonCalender.getInstance();
        String result = workspaceCalender.checkForValidTimings(meetingRequests);
        if (MeetingConstants.INCORRECT_INPUT.equalsIgnoreCase(result))
            return result;

        int slots = slotsRequired(meetingRequests);
        int persons = Integer.parseInt(meetingRequests.get(3));

        if(persons > MAX_PERSONS || persons < MIX_PERSONS){
            return NO_VACANT_ROOM;
        }

        // Booking for Mansion
        if (persons > tower.getCapacity()) {
            if (bookMansion(meetingRequests, slots))
                return mansion.getName();
        }
        // Booking for Tower or Mansion
        if (twoRoomsAvailable(persons <= tower.getCapacity(), persons > cave.getCapacity())) {
            String dTower = bookTowerOrMansion(meetingRequests, slots);
            if (dTower != null)
                return dTower;
        }

        // Booking for Cave or Tower or Mansion
        if (persons <= cave.getCapacity()) {
            boolean isCaveAvailable = workspaceCalender.checkAvailability(meetingRequests, slots, cave.getName());
            if(isCaveAvailable){
                bookRooms(meetingRequests.get(1), slots, singletonCalender.CAVE_CALENDER);
                return cave.getName();
            }
            else {
                String dTower = bookTowerOrMansion(meetingRequests, slots);
                if (dTower != null)
                    return dTower;
            }

        }
        return NO_VACANT_ROOM;
    }

    @Override
    public String handleVacancy(List<String> meetingRequests) {
        String result = workspaceCalender.checkForValidTimings(meetingRequests);
        if (INCORRECT_INPUT.equalsIgnoreCase(result))
            return result;

        int slots = slotsRequired(meetingRequests);
        Room cave = new Room(C_CAVE,false,3);
        Room tower = new Room(D_TOWER,false,7);
        Room mansion = new Room(G_MANSION,false,20);


        cave.setAvailable( workspaceCalender.checkAvailability(meetingRequests, slots, C_CAVE));
        tower.setAvailable(workspaceCalender.checkAvailability(meetingRequests, slots, D_TOWER));
        mansion.setAvailable(workspaceCalender.checkAvailability(meetingRequests, slots, G_MANSION));

        if (allRoomsAvaliable(cave, tower, mansion))
            return cave.getName() + " " + tower.getName() + " " + mansion.getName();

        if (twoRoomsAvailable(cave.isAvailable(), mansion.isAvailable()))
            return cave.getName() + " " + mansion.getName();

        if (twoRoomsAvailable(cave.isAvailable(), tower.isAvailable()))
            return cave.getName() + " " + tower.getName();

        if (cave.isAvailable())
            return cave.getName();

        if (tower.isAvailable())
            return tower.getName();

        if (mansion.isAvailable())
            return mansion.getName();

        return NO_VACANT_ROOM;
    }

    private boolean twoRoomsAvailable(boolean available, boolean available2) {
        return available && available2;
    }

    private boolean allRoomsAvaliable(Room cave, Room tower, Room mansion) {
        return cave.isAvailable() && mansion.isAvailable() && tower.isAvailable();
    }

    @Override
    public String bookTowerOrMansion(List<String> meetingRequests, int slots) {
        boolean isTowerAvailable = workspaceCalender.checkAvailability(meetingRequests, slots, D_TOWER);
        if (isTowerAvailable) {
            bookRooms(meetingRequests.get(1), slots, SingletonCalender.getInstance().TOWER_CALENDER);
            return D_TOWER;
        }
        if (bookMansion(meetingRequests, slots))
            return G_MANSION;
        return null;
    }

    @Override
    public boolean bookMansion(List<String> meetingRequests, int slots) {
        boolean isAvailableMansion = workspaceCalender.checkAvailability(meetingRequests, slots, G_MANSION);
        if (isAvailableMansion) {
            bookRooms(meetingRequests.get(1), slots, SingletonCalender.getInstance().MANSION_CALENDER);
            return true;
        }
        return false;
    }

    @Override
    public void bookRooms(String beginTime, int slots, Map<LocalTime, String> cCalender) {
        LocalTime timeKey = SingletonCalender.getInstance().getLocalTime(beginTime);
        for (int i = 0; i < slots; i++) {
            cCalender.replace(timeKey, VACANT, BOOKED);
            timeKey = timeKey.plusMinutes(15);
        }

    }

    public int slotsRequired(List<String> meetingRequests) {
        LocalTime firstDate = LocalTime.parse(meetingRequests.get(1), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime secondDate = LocalTime.parse(meetingRequests.get(2), DateTimeFormatter.ofPattern("HH:mm"));
        long slotsNeeded = ChronoUnit.MINUTES.between(firstDate, secondDate) / 15;
        return Integer.parseInt(String.valueOf(slotsNeeded));
    }
}
