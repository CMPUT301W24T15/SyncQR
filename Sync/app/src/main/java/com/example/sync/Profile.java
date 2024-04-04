package com.example.sync;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Profile represents user profile information.
 */
public class Profile implements Parcelable {
    private String profileID;
    private String name;
    private String imageUrl;
    private String email;
    private String phoneNumber;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Profile.class).
     */
    public Profile() {
    }

    /**
     * Constructor to initialize profile information.
     *
     * @param profileID   The ID of the profile.
     * @param name        The name of the user.
     * @param imageUrl    The URL of the user's profile image.
     * @param email       The email address of the user.
     * @param phoneNumber The phone number of the user.
     */
    public Profile(String profileID, String name, String imageUrl, String email, String phoneNumber) {
        this.profileID = profileID;
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


    protected Profile(Parcel in) {
        profileID = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(profileID);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(email);
        dest.writeString(phoneNumber);
    }

    /**
     * Retrieves all profiles from the "Accounts" collection in the database.
     * @param callback Callback to handle the retrieved profile list.
     */
    public static void getAllProfilesFromDatabase(Callback callback) {
        db.collection("Accounts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Profile> profileList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve profile data
                                Map<String, Object> data = document.getData();
                                Map<String, Object> profileData = (Map<String, Object>) data.get("profile");
                                if (profileData != null) {
                                    String id = (String) profileData.get("profileID");
                                    String name = (String) profileData.get("name");
                                    String imageUrl = (String) profileData.get("imageUrl");
                                    String email = (String) profileData.get("email");
                                    String phoneNumber = (String) profileData.get("phoneNumber");

                                    // Create Profile instance
                                    if (!name.isEmpty()) {
                                        // Add profile to list
                                        Profile profile = new Profile(id, name, imageUrl, email, phoneNumber);
                                        profileList.add(profile);
                                    }
                                }
                            }
                            // Notify callback with the list of profiles
                            callback.onSuccess(profileList);
                        }
                    }
                });
    }

    public interface Callback{
        default void onSuccess(Profile profile) {
        }

        default void onSuccess(ArrayList<Profile> profileArrayList) {
        }
    }

}
