package com.example.sync;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public abstract class User extends AppCompatActivity implements EditProfileFragment.OnFragmentInteractionListener{
    String username;
    String password;

    public User() {
        this.username = "Visitor";
    }

    public void scanQRCode(){
        new QRCodeScanActivity();
    }
    public void editProfile(Profile profile){
        Button editProfileButton = findViewById(R.id.edit_File_Button);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new EditProfileFragment((Profile)profileList.getItemAtPosition(i)).show(getSupportFragmentManager(),"Add/Edit_City");

            }
        });
    }

}
