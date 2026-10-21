// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.service;

public class WeekendPricing implements PricingPolicy {

    @Override
    public double calculateRoomTotal(double basePricePerNight, long nights) {
        return basePricePerNight * nights * getRateMultiplier();
    }

    @Override
    public String getPolicyName() {
        return "Cuối tuần (+20%)";
    }

    @Override
    public double getRateMultiplier() {
        return 1.2;
    }
}