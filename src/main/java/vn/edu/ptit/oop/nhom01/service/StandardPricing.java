// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.service;

public class StandardPricing implements PricingPolicy {

    @Override
    public double calculateRoomTotal(double basePricePerNight, long nights) {
        return basePricePerNight * nights * getRateMultiplier();
    }

    @Override
    public String getPolicyName() {
        return "Ngày thường (Tiêu chuẩn 100%)";
    }

    @Override
    public double getRateMultiplier() {
        return 1.0;
    }
}