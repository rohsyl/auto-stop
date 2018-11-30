package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Plate;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Report;

public class PlateEntity implements Plate, Serializable {

    @NotNull
    @Exclude
    private String uid;

    private String plateNumber;
    private List<Report> reports;

    public PlateEntity(){

    }

    public PlateEntity(Plate plate){
        this.uid = plate.getUid();
        this.plateNumber = plate.getPlateNumber();
        this.reports = plate.getReports();
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getPlateNumber() {
        return plateNumber;
    }

    @Override
    public List<Report> getReports() {
        return reports;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("plate_number", getPlateNumber());
        return result;
    }
}
