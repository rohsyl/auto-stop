package ch.hevs.a6452.grp2.autostop.autostop.Entites;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ch.hevs.a6452.grp2.autostop.autostop.Models.Position;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Trip;

public class TripEntity implements Trip, Serializable {




    private String uid;
    private String status;
    private PositionEntity destination;
    private String ownerUid;
    private String plateUid;
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

    public void setPlateUid(String plateUid) {
        this.plateUid = plateUid;
    }

    public void setPositions(List<PositionEntity> positions) {
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
