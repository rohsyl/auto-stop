package ch.hevs.a6452.grp2.autostop.autostop.entities;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.models.Alert;

/**
 * The alert entity
 */
public class AlertEntity implements Alert {

    /**
     * The unique id
     */
    @NonNull
    @Exclude
    private String uid;

    /**
     * When the alert was sent
     */
    private Long timestamp;

    /**
     * To which Trip this alert belongs to
     */
    private String tripUid;

    /**
     * The position where the alert was sent
     */
    private PositionEntity lastPosition;

    /**
     * Alert is send to
     */
    private String sendTo;

    /**
     * Is this alert red by an admin
     */
    private boolean readByAdmin = false;

    /**
     * When this alert was red by an admin
     */
    private long readDate = 0L;

    /**
     * Contructor
     */
    public AlertEntity(){

    }

    /**
     * Constructor
     * @param alert
     */
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

    /**
     * Convert the entity to a String, Object Map
     * @return
     */
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
