package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.EmergencyPerson;

public class EmergencyPersonEntity implements EmergencyPerson {


    @Exclude
    @NonNull
    private String uid;

    private String email;
    private String phone;


    @Override
    public String getUid() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPhone() {
        return null;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> emergency = new HashMap<>();
        emergency.put("phone", getPhone());
        emergency.put("email", getEmail());
        return emergency;
    }
}
