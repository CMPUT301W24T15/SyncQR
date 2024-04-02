package com.example.sync.Close;

import com.example.sync.Open.Event;

import java.util.List;

public interface EventCallback {
    void onCallback(List<Event> eventList);
}

