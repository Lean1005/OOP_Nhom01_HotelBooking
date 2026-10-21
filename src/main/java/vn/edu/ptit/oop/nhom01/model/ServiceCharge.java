package vn.edu.ptit.oop.nhom01.model;

public class ServiceCharge {
    private final String serviceName;
    private final double amount;
    private final ServiceType type;

    public ServiceCharge(String serviceName, double amount, ServiceType type) {
        this.serviceName = serviceName;
        this.amount = amount;
        this.type = type;
    }

    public String getServiceName() { return serviceName; }
    public double getAmount() { return amount; }
    public ServiceType getType() { return type; }
}
