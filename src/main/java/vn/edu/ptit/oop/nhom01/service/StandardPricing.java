package vn.edu.ptit.oop.nhom01.service;
import vn.edu.ptit.oop.nhom01.model.*;

public class StandardPricing implements PricingPolicy {
    @Override public String getPolicyName() { return "Giá Tiêu chuẩn (Ngày thường)"; }
    @Override public double calculatePrice(Room room, long nights) { return room.getPricePerNight() * nights; }
    @Override public String toString() { return getPolicyName(); }
}
