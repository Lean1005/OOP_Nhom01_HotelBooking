// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.model;

import vn.edu.ptit.oop.nhom08.service.PricingPolicy;
import vn.edu.ptit.oop.nhom08.service.StandardPricing;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Invoice {
    private String invoiceId;
    private Booking booking;
    private PricingPolicy pricingPolicy;
    private List<ServiceCharge> serviceCharges;
    private LocalDateTime issuedAt;

    public Invoice(String invoiceId, Booking booking) {
        this.invoiceId = invoiceId;
        this.booking = booking;
        this.pricingPolicy = new StandardPricing(); // Mặc định là giá ngày thường
        this.serviceCharges = new ArrayList<>();
        this.issuedAt = LocalDateTime.now();
    }

    public Invoice(String invoiceId, Booking booking, PricingPolicy pricingPolicy) {
        this.invoiceId = invoiceId;
        this.booking = booking;
        this.pricingPolicy = (pricingPolicy != null) ? pricingPolicy : new StandardPricing();
        this.serviceCharges = new ArrayList<>();
        this.issuedAt = LocalDateTime.now();
    }

    public void addServiceCharge(ServiceCharge charge) {
        if (charge != null) {
            this.serviceCharges.add(charge);
        }
    }

    public double calculateRoomTotal() {
        if (booking == null || booking.getRoom() == null) {
            return 0.0;
        }
        double basePrice = booking.getRoom().getPricePerNight();
        long nights = booking.nights();
        return pricingPolicy.calculateRoomTotal(basePrice, nights);
    }

    public double calculateServicesTotal() {
        double sum = 0.0;
        for (ServiceCharge sc : serviceCharges) {
            sum += sc.getAmount();
        }
        return sum;
    }

    public double calculateGrandTotal() {
        return calculateRoomTotal() + calculateServicesTotal();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public Booking getBooking() {
        return booking;
    }

    public PricingPolicy getPricingPolicy() {
        return pricingPolicy;
    }

    public void setPricingPolicy(PricingPolicy pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    public List<ServiceCharge> getServiceCharges() {
        return Collections.unmodifiableList(serviceCharges);
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}