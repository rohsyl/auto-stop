package ch.hevs.a6452.grp2.autostop.autostop.Models;

import java.util.List;

public interface Trip {

    String STATUS_NOT_STARTED = "not_started";
    String STATUS_IN_PROGRESS = "in_progress";
    String STATUS_FINISHED = "finished";

    String getUid();
    String getStatus();
    Position getDestination();
    String getOwnerUid();
    String getPlateUid();
    List<Position> getPositions();
}
