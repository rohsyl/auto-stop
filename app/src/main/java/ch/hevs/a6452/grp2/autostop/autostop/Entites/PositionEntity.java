package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Position;

public class PositionEntity implements Position {

    @NotNull
    @Exclude
    private String uid;

    private Double latitude;
    private Double longitude;
    private String name;
    private Long timestamp;

    public PositionEntity(){

    }

    public PositionEntity(Position position){
        this.uid = position.getUid();
        this.latitude = position.getLatitude();
        this.longitude = position.getLongitude();
        this.name = position.getName();
        this.timestamp = position.getTimestamp();
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", getLatitude());
        result.put("longitude", getLongitude());
        result.put("timestamp", getTimestamp());
        result.put("name", getName());
        return result;
    }
}
