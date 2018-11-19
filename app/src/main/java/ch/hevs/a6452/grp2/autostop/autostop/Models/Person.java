package ch.hevs.a6452.grp2.autostop.autostop.Models;

public interface Person {
    String getUid();
    String getEmail();
    String getFullname();
    String getSex();
    Long getBirthDate();
    EmergencyPerson getEmergencyPerson();


}
