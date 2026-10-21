package vn.edu.ptit.oop.nhom01.model;
import vn.edu.ptit.oop.nhom01.service.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class Invoice {
    public static final String COMPANY_NAME = "NHÓM 1 HOTEL BOOKING";
    private final Booking booking;
    private final PricingPolicy appliedPolicy;
    private final List<ServiceCharge> serviceCharges = new ArrayList<>();
    private final LocalDateTime issuedAt = LocalDateTime.now();

    public Invoice(Booking booking, PricingPolicy appliedPolicy) {
        this.booking = booking;
        this.appliedPolicy = appliedPolicy;
    }

    public void addServiceCharge(ServiceCharge charge) {
        if (charge != null) serviceCharges.add(charge);
    }

    public List<ServiceCharge> getServiceCharges() {
        return Collections.unmodifiableList(serviceCharges);
    }

    public Booking getBooking() { return booking; }
    public PricingPolicy getAppliedPolicy() { return appliedPolicy; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public double getRoomCharge() { return booking.totalCost(appliedPolicy); }
    public double getTotalServiceCharges() {
        return serviceCharges.stream().mapToDouble(ServiceCharge::getAmount).sum();
    }
    public double getGrandTotal() { return getRoomCharge() + getTotalServiceCharges(); }
}
