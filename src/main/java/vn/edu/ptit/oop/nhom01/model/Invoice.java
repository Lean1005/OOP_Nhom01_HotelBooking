package vn.edu.ptit.oop.nhom01.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import vn.edu.ptit.oop.nhom01.*;
import vn.edu.ptit.oop.nhom01.common.*;
import vn.edu.ptit.oop.nhom01.exception.*;
import vn.edu.ptit.oop.nhom01.model.*;
import vn.edu.ptit.oop.nhom01.repository.*;
import vn.edu.ptit.oop.nhom01.service.*;
import vn.edu.ptit.oop.nhom01.ui.*;
import vn.edu.ptit.oop.nhom01.ui.dialogs.*;
import vn.edu.ptit.oop.nhom01.ui.panels.*;
import vn.edu.ptit.oop.nhom01.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Invoice {
    public static final String COMPANY_NAME = "NHÓM8HOTELBOOKING";
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
