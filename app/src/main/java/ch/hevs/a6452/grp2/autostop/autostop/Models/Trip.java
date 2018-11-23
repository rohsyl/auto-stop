package ch.hevs.a6452.grp2.autostop.autostop.Models;

import java.util.List;

public interface Trip {
    String getUid();
    String getStatus();
    Position getDestination();
    String getOwnerUid();
    String getPlateUid();
    List<Position> getPositions();
}
