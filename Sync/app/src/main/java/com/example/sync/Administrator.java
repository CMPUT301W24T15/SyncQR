package com.example.sync;

import java.util.ArrayList;

public class Administrator extends User{
    private Profile browseProfile(ArrayList<Integer> profileNumber){
        Profile profile = Profile.find(profileNumber);
    }
    private void editProfile(Profile editProfile){
        editProfile.set();
        editProfile.delete();
    }
}
