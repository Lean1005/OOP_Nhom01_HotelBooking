package vn.edu.ptit.oop.nhom01.service;
import vn.edu.ptit.oop.nhom01.model.*;

public class HolidayPricing implements PricingPolicy {
    @Override public String getPolicyName() { return "Giá Ngày Lễ / Tết (+50%)"; }
    @Override public double calculatePrice(Room room, long nights) { return room.getPricePerNight() * 1.50 * nights; }
    @Override public String toString() { return getPolicyName(); }
}
