package com.example.sync;

import java.util.ArrayList;

public class Organizer extends User{
    ArrayList<Event> events;
    public void addEvent(Event newEvent){
        events.add(newEvent);
    }
    public void removeEvent(Event chosenEvent){
        events.remove(chosenEvent);
    }
    public void createEvent(){
        Event newEvent = Event();
        addEvent(newEvent);
    }
    
}
