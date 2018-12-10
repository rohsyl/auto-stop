package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Report;

public class ReportEntity implements Report, Serializable {

    @NonNull
    @Exclude
    private String uid;

    private String message;
    private Long timestamp;
    private String tripUid;

    public ReportEntity(){

    }

    public ReportEntity(Report report){
        this.uid = report.getUid();
        this.message = report.getUid();
        this.timestamp = report.getTimestamp();
        this.tripUid = report.getTripUid();
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getTripUid() {
        return tripUid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTripUid(String tripUid) {
        this.tripUid = tripUid;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", getMessage());
        result.put("timestamp", getTimestamp());
        result.put("trip", getTripUid());
        return result;
    }
}
