package ch.hevs.a6452.grp2.autostop.autostop.models;

public interface Report {
    String getUid();
    String getMessage();
    Long getTimestamp();
    String getTripUid();
    String getPlateNumber();
    boolean getReadByAdmin();
    Long getReadDate();
}
