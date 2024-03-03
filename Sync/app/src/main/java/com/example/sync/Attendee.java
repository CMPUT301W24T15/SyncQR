package com.example.sync;

public class Attendee extends User{
    public void scanQRCode(){
        new QRCodeScanActivity();
    }
    public void editProfile(){
        Profile profile = new Profile();
    }
    public void pushNotification(){
        ;
    }
    public void browseEvent(){
        Event.get();
        Event.display();
    }

}
