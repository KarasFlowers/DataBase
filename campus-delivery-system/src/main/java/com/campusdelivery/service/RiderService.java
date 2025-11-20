package com.campusdelivery.service;

import com.campusdelivery.dao.RiderDao;
import com.campusdelivery.entity.Rider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiderService {

    @Autowired
    private RiderDao riderDao;

    public void addRider(Rider rider) {
        riderDao.addRider(rider);
    }

    public Rider getRiderById(int riderId) {
        return riderDao.getRiderById(riderId);
    }

    public List<Rider> getAllRiders() {
        return riderDao.getAllRiders();
    }

    public void updateRider(Rider rider) {
        riderDao.updateRider(rider);
    }

    public void deleteRider(int riderId) {
        riderDao.deleteRider(riderId);
    }
}
