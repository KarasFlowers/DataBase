package com.campusdelivery.dao;

import com.campusdelivery.model.Merchant;
import java.util.List;

public class MerchantDao {

    public void addMerchant(Merchant merchant) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding merchant " + merchant.getName());
    }

    public Merchant getMerchantById(int merchantId) {
        // TODO: Implement SQL SELECT by ID
        System.out.println("DAO: Getting merchant by ID " + merchantId);
        return null;
    }

    public List<Merchant> getAllMerchants() {
        // TODO: Implement SQL SELECT all
        System.out.println("DAO: Getting all merchants");
        return null;
    }

    public void updateMerchant(Merchant merchant) {
        // TODO: Implement SQL UPDATE
        System.out.println("DAO: Updating merchant " + merchant.getMerchantId());
    }

    public void deleteMerchant(int merchantId) {
        // TODO: Implement SQL DELETE
        System.out.println("DAO: Deleting merchant " + merchantId);
    }
}
