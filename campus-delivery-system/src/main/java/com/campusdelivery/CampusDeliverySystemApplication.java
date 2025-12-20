package com.campusdelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CampusDeliverySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusDeliverySystemApplication.class, args);
        System.out.println("success!");
    }
    //C:\Users\hp\Desktop\database\campus-delivery-system\frontend
    //npm run dev
}
