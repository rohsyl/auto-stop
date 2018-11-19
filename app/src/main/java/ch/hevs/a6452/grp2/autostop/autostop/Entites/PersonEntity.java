package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Person;

public class PersonEntity implements Person{

    @Exclude
    @NonNull
    private String uid;


    private String firstName;


    private String lastName;


    public PersonEntity(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
    }

    public PersonEntity() {

    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String idAuthor) {
        this.uid = idAuthor;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        return result;
    }

}



