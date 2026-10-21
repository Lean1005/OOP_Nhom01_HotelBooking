// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.service;

public interface PricingPolicy {

    double calculateRoomTotal(double basePricePerNight, long nights);

    String getPolicyName();

    double getRateMultiplier();
}