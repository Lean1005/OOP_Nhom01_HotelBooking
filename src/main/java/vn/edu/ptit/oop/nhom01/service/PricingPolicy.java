package vn.edu.ptit.oop.nhom01.service;
import vn.edu.ptit.oop.nhom01.model.*;

public interface PricingPolicy {
    String getPolicyName();
    double calculatePrice(Room room, long nights);
}
