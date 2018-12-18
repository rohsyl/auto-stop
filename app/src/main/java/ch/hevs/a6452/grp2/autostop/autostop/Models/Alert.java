package ch.hevs.a6452.grp2.autostop.autostop.Models;

import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;

public interface Alert {
    String getUid();
    Long getTimestamp();
    String getTripUid();
    PositionEntity getLastPosition();
    boolean getReadByAdmin();
    Long getReadDate();
}
