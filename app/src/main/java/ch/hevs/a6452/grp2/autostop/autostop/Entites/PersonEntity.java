package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.EmergencyPerson;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Person;

public class PersonEntity implements Person{

    @Exclude
    @NonNull
    private String uid;


    private String fullname;
    private String email;
    private String sex;
    private Long birthDate;
    private EmergencyPerson emergencyPerson;

    public PersonEntity(Person person) {
        this.uid = person.getUid();
        this.fullname = person.getFullname();
        this.sex = person.getSex();
        this.email = person.getEmail();
        this.birthDate = person.getBirthDate();
        this.emergencyPerson = person.getEmergencyPerson();
    }

    public PersonEntity() {

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
    public String getSex() {
        return sex;
    }

    @Override
    public Long getBirthDate() {
        return birthDate;
    }

    @Override
    public EmergencyPerson getEmergencyPerson() {
        return emergencyPerson;
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

    public void setEmergencyPerson(EmergencyPerson emergencyPerson) {
        this.emergencyPerson = emergencyPerson;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> emergency = new HashMap<>();
        emergency.put("phone", getEmergencyPerson().getPhone());
        emergency.put("email", getEmergencyPerson().getEmail());

        HashMap<String, Object> result = new HashMap<>();
        result.put("fullname", getFullname());
        result.put("sex", getSex());
        result.put("email", getEmail());
        result.put("birthdate", getBirthDate());
        result.put("emergency_contact", emergency);
        return result;
    }

}



