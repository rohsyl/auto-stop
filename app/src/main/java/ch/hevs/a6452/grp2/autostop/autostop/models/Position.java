package ch.hevs.a6452.grp2.autostop.autostop.models;

public interface Position {
    String getUid();
    Double getLatitude();
    Double getLongitude();
    String getName();
    Long getTimestamp();
}
