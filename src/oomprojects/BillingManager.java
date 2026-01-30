package oomprojects;

import java.util.List;

public class BillingManager {
    private double consultationFee;
    private double sstRate;

    public BillingManager(double consultationFee, double sstRate) {
        this.consultationFee = consultationFee;
        this.sstRate = sstRate;
    }

    public double calculateMedicineTotal(List<Prescription> prescriptions) {
        double sum = 0;
        for (Prescription p : prescriptions) sum += p.lineTotal();
        return sum;
    }

    public double calculateSubtotal(double consultation, List<Prescription> prescriptions) {
        return consultation + calculateMedicineTotal(prescriptions);
    }

    public double calculateSst(double subtotal) {
        return subtotal * sstRate;
    }

    public double calculateGrandTotal(double subtotal, double sst) {
        return subtotal + sst;
    }
}
