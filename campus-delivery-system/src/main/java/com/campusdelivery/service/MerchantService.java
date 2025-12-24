package com.campusdelivery.service;

import com.campusdelivery.dao.MerchantDao;
import com.campusdelivery.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;

@Service
public class MerchantService {

    @Autowired
    private MerchantDao merchantDao;

    public int addMerchant(Merchant merchant) {
        return merchantDao.addMerchant(merchant);
    }

    public Merchant getMerchantById(int merchantId) {
        return merchantDao.getMerchantById(merchantId);
    }

    public List<Merchant> getAllMerchants(String name, String sortBy, BigDecimal minPrice, BigDecimal maxPrice) {
        return merchantDao.getAllMerchants(name, sortBy, minPrice, maxPrice);
    }

    public void updateMerchant(Merchant merchant) {
        merchantDao.updateMerchant(merchant);
    }
    
    public void updateBusinessHours(int merchantId, Time openTime, Time closeTime) {
        merchantDao.updateBusinessHours(merchantId, openTime, closeTime);
    }

    public void updateManualStatus(int merchantId, boolean isClosed) {
        merchantDao.updateManualStatus(merchantId, isClosed);
    }

    public void deleteMerchant(int merchantId) {
        merchantDao.deleteMerchant(merchantId);
    }

    public List<Merchant> search(String query) {
        return merchantDao.search(query);
    }
}
