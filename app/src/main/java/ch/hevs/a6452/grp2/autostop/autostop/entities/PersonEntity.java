package ch.hevs.a6452.grp2.autostop.autostop.entities;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.models.Person;

/**
 * The Person entity
 */
public class PersonEntity implements Person {

    /**
     * The unique id
     */
    @Exclude
    @NonNull
    private String uid;

    /**
     * The fullname
     */
    private String fullname = null;

    /**
     * The email address
     */
    private String email = null;

    /**
     * The sex
     */
    private int sex = 999;

    /**
     * The birthdate as a long
     */
    private Long birthDate = null;

    /**
     * The email of the emergency contact
     */
    private String emergencyEmail;

    /**
     * The phone number of the emergency contact
     */
    private String emergencyPhone;

    /**
     * Constructor
     * @param person
     */
    public PersonEntity(Person person) {
        this.uid = person.getUid();
        this.fullname = person.getFullname();
        this.sex = person.getSex();
        this.email = person.getEmail();
        this.birthDate = person.getBirthDate();
        this.emergencyEmail = person.getEmergencyEmail();
        this.emergencyPhone = person.getEmergencyPhone();
    }

    /**
     * Constructor
     */
    public PersonEntity() {
        this.fullname = "";
        this.sex = 0;
        this.email = "";
        this.birthDate = 0L;
        this.emergencyEmail = null;
        this.emergencyPhone = null;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFullname() {
        return fullname;
    }

    @Override
    public int getSex() {
        return sex;
    }

    @Override
    public Long getBirthDate() {
        return birthDate;
    }

    @Override
    public String getEmergencyEmail() {
        return emergencyEmail;
    }

    @Override
    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmergencyEmail(String emergencyEmail) {
        this.emergencyEmail = emergencyEmail;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    /**
     * Convert the entity to a String, Object Map
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("fullname", getFullname());
        result.put("sex", getSex());
        result.put("email", getEmail());
        result.put("birthdate", getBirthDate());
        result.put("emergency_phone", getEmergencyPhone());
        result.put("emergency_email", getEmergencyEmail());
        return result;
    }

}



