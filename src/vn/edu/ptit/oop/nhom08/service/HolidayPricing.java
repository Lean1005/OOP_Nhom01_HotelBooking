// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.service;

public class HolidayPricing implements PricingPolicy {

    @Override
    public double calculateRoomTotal(double basePricePerNight, long nights) {
        return basePricePerNight * nights * getRateMultiplier();
    }

    @Override
    public String getPolicyName() {
        return "Ngày Lễ / Tết (+50%)";
    }

    @Override
    public double getRateMultiplier() {
        return 1.5;
    }
}