package com.meeting.schedulars.room.model;

public class Room {
    String name;
    boolean available;
    int capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Room(){

    }
    public Room(String name, boolean available, int capacity) {
        this.name = name;
        this.available = available;
        this.capacity = capacity;
    }


}
