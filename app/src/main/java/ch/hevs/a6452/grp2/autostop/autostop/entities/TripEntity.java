package ch.hevs.a6452.grp2.autostop.autostop.entities;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.models.Position;
import ch.hevs.a6452.grp2.autostop.autostop.models.Trip;

/**
 * The trip entity
 */
public class TripEntity implements Trip, Serializable {

    /**
     * The unique id
     */
    private String uid;

    /**
     * The status of the trip
     */
    private String status;

    /**
     * The destination of the trip
     */
    private PositionEntity destination;

    /**
     * The owner of the trip
     */
    private String ownerUid;

    /**
     * The plate linked to this trip
     */
    @Nullable
    private String plateUid;

    /**
     * The set of positions of this trip
     */
    private List<PositionEntity> positions = new ArrayList<PositionEntity>();

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Position getDestination() {
        return destination;
    }

    @Override
    public String getOwnerUid() {
        return ownerUid;
    }

    @Override
    public String getPlateUid() {
        return plateUid;
    }

    @Override
    public List<PositionEntity> getPositions() { return positions; }

    public void addPosition(PositionEntity p){this.positions.add(p);}

    public void setDestination(PositionEntity destination) {
        this.destination = destination;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public void setPlateUid(@Nullable String plateUid) {
        this.plateUid = plateUid;
    }

    public void setPositions(List<PositionEntity> positions) {
        this.positions = positions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Convert the entity to a String, Object Map
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("destination", new PositionEntity(getDestination()).toMap());
        result.put("status", getStatus());
        result.put("owner", getOwnerUid());
        result.put("plate", getPlateUid());
        return result;
    }
}
