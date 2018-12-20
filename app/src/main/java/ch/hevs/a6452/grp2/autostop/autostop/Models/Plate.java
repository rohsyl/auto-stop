package ch.hevs.a6452.grp2.autostop.autostop.Models;

import java.util.List;

import ch.hevs.a6452.grp2.autostop.autostop.Entites.ReportEntity;

public interface Plate {
    String getUid();
    String getPlateNumber();
    boolean isflaged();
    List<ReportEntity> getReports();
    byte[] getPicture();
}
