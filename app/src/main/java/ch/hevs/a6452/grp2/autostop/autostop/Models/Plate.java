package ch.hevs.a6452.grp2.autostop.autostop.Models;

import java.util.List;

public interface Plate {
    String getUid();
    String getPlateNumber();
    List<Report> getReports();
}
