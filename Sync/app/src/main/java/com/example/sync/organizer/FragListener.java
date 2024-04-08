package com.example.sync.organizer;

import androidx.fragment.app.Fragment;

/**
 * A listener that fragments use to communicate with their parent
 */
public interface FragListener {
    /**
     * Ask their parent to shut them down
     * @param frag
     */
    void notifyShutDown(Fragment frag);
}
