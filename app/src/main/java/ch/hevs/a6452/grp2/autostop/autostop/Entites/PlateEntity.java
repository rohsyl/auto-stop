package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Plate;

public class PlateEntity implements Plate, Serializable {

    @NotNull
    @Exclude
    private String uid;

    private String plateNumber;
    private List<ReportEntity> reports = new ArrayList<ReportEntity>();
    private byte[] picture;

public PlateEntity(){}

    public PlateEntity(Plate plate){
        this.uid = plate.getUid();
        this.plateNumber = plate.getPlateNumber();
        reports = new ArrayList<ReportEntity>();
        this.reports = plate.getReports();
        this.picture = plate.getPicture();
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
    public List<ReportEntity> getReports() {
        return reports;
    }

    @Exclude
    @Override
    public byte[] getPicture(){
        return picture;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setReports(List<ReportEntity> reports) {
        this.reports = reports;
    }

    @Exclude
    public void addReport(ReportEntity p){this.reports.add(p);}

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    @Exclude
    public static byte[] convertPicture(Bitmap pictureBmp){
        if(pictureBmp != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pictureBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("plate_number", getPlateNumber());
        return result;
    }
}
