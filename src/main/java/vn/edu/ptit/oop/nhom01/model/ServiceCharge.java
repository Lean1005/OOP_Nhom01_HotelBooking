// Author: Nguyen Minh Tuan - MSSV: N24DCAT085
package vn.edu.ptit.oop.nhom08.model;

public class ServiceCharge {
    private ServiceType serviceType;
    private int quantity;
    private double unitPrice;

    public ServiceCharge(ServiceType serviceType, int quantity) {
        this.serviceType = serviceType;
        this.quantity = quantity;
        this.unitPrice = (serviceType != null) ? serviceType.getDefaultPrice() : 0.0;
    }

    public ServiceCharge(ServiceType serviceType, int quantity, double unitPrice) {
        this.serviceType = serviceType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getAmount() {
        return quantity * unitPrice;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}