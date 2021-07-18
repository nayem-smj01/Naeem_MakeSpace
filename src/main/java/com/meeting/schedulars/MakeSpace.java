package com.meeting.schedulars;

import com.meeting.schedulars.room.RoomServices;
import com.meeting.schedulars.room.RoomServicesImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MakeSpace {
    public static void main(String[] args) {

        String inputFile = null;
        try {
            inputFile = args[0];
            List<String> bookingRequests;
            bookingRequests = Files.readAllLines(Paths.get(inputFile),
                    StandardCharsets.UTF_8);

            bookingRequests = bookingRequests.stream()
                    .filter(line -> (line != null && !line.isBlank() && !line.isEmpty()))
                    .filter(line -> line.contains(" "))
                    .map(line -> {
                        String[] words = line.split(" ");
                        return allocateRoom(Arrays.asList(words));
                    }).collect(Collectors.toList());
            for (String booking : bookingRequests) {
                System.out.println(booking);
            }


        } catch (IOException e) {
            System.out.println(" Specified file " + inputFile + "  not found. Supply valid file");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(MeetingConstants.INCORRECT_INPUT);
        } catch (Exception e) {
            System.out.println(" Exception:" + e.getLocalizedMessage());
        }


    }

    private static String allocateRoom(List<String> asList) {
        RoomServices roomServices = new RoomServicesImpl();
          return roomServices.allocateRoom(asList);
    }
}
