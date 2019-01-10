package ch.hevs.a6452.grp2.autostop.autostop.entities;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.models.Alert;

public class AlertEntity implements Alert {

    @NonNull
    @Exclude
    private String uid;

    private Long timestamp;
    private String tripUid;
    private PositionEntity lastPosition;
    private String sendTo;
    private boolean readByAdmin = false;
    private long readDate = 0L;

    public AlertEntity(){

    }

    public AlertEntity(Alert alert){
        this.uid = alert.getUid();
        this.timestamp = alert.getTimestamp();
        this.tripUid = alert.getTripUid();
        this.lastPosition = alert.getLastPosition();
        this.sendTo = alert.getSendTo();
        this.readByAdmin = alert.getReadByAdmin();
        this.readDate = alert.getReadDate();
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getTripUid() {
        return tripUid;
    }

    @Override
    public PositionEntity getLastPosition() {
        return lastPosition;
    }

    @Override
    public String getSendTo() {
        return sendTo;
    }

    @Override
    public boolean getReadByAdmin() {
        return readByAdmin;
    }

    @Override
    public Long getReadDate() {
        return readDate;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTripUid(String tripUid) {
        this.tripUid = tripUid;
    }

    public void setLastPosition(PositionEntity lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void setSendTo(String sendTo){
        this.sendTo = sendTo;
    }

    public void setReadByAdmin(boolean readByAdmin){ this.readByAdmin = readByAdmin; }

    public void setReadDate(Long readDate){ this.readDate = readDate; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("timestamp", getTimestamp());
        result.put("trip", getTripUid());
        result.put("lastPosition", getLastPosition());
        result.put("sendTo", getSendTo());
        result.put("readByAdmin", getReadByAdmin());
        result.put("readDate", getReadDate());
        return result;
    }
}
