package vn.edu.ptit.oop.nhom01.service;
import vn.edu.ptit.oop.nhom01.model.*;

public class WeekendPricing implements PricingPolicy {
    @Override public String getPolicyName() { return "Giá Cuối tuần (+20%)"; }
    @Override public double calculatePrice(Room room, long nights) { return room.getPricePerNight() * 1.20 * nights; }
    @Override public String toString() { return getPolicyName(); }
}
