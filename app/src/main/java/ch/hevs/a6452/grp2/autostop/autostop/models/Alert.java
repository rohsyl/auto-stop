package ch.hevs.a6452.grp2.autostop.autostop.models;

import ch.hevs.a6452.grp2.autostop.autostop.entities.PositionEntity;

public interface Alert {
    String getUid();
    Long getTimestamp();
    String getTripUid();
    PositionEntity getLastPosition();
    String getSendTo();
    boolean getReadByAdmin();
    Long getReadDate();
}
