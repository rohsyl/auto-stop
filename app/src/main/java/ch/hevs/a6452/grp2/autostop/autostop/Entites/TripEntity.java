package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Position;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Trip;

public class TripEntity implements Trip, Serializable {




    private String uid;
    private String status;
    private Position destination;
    private String ownerUid;
    private String plateUid;
    private List<Position> positions;




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
    public List<Position> getPositions() {
        return positions;
    }

    public void addPosition(Position p){this.positions.add(p);}

    public void setDestination(Position destination) {
        this.destination = destination;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public void setPlateUid(String plateUid) {
        this.plateUid = plateUid;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
