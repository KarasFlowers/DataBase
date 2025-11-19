package com.campusdelivery.dao;

import com.campusdelivery.model.Rider;
import java.util.List;

public class RiderDao {

    public void addRider(Rider rider) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding rider " + rider.getName());
    }

    public Rider getRiderById(int riderId) {
        // TODO: Implement SQL SELECT by ID
        System.out.println("DAO: Getting rider by ID " + riderId);
        return null;
    }

    public List<Rider> getAllRiders() {
        // TODO: Implement SQL SELECT all
        System.out.println("DAO: Getting all riders");
        return null;
    }

    public void updateRider(Rider rider) {
        // TODO: Implement SQL UPDATE
        System.out.println("DAO: Updating rider " + rider.getRiderId());
    }

    public void deleteRider(int riderId) {
        // TODO: Implement SQL DELETE
        System.out.println("DAO: Deleting rider " + riderId);
    }
}
