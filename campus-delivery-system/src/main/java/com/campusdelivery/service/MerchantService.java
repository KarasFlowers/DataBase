package com.campusdelivery.service;

import com.campusdelivery.dao.MerchantDao;
import com.campusdelivery.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    @Autowired
    private MerchantDao merchantDao;

    public void addMerchant(Merchant merchant) {
        merchantDao.addMerchant(merchant);
    }

    public Merchant getMerchantById(int merchantId) {
        return merchantDao.getMerchantById(merchantId);
    }

    public List<Merchant> getAllMerchants() {
        return merchantDao.getAllMerchants();
    }

    public void updateMerchant(Merchant merchant) {
        merchantDao.updateMerchant(merchant);
    }

    public void deleteMerchant(int merchantId) {
        merchantDao.deleteMerchant(merchantId);
    }
}
