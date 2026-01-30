package oomprojects;

public class Prescription {
    private String id;
    private String apptId;
    private String patientId;
    private String medicine;
    private String dosage;
    private int qty;
    private double unitPrice;

    public Prescription(String id, String apptId, String patientId, String medicine, String dosage, int qty, double unitPrice) {
        this.id = id;
        this.apptId = apptId;
        this.patientId = patientId;
        this.medicine = medicine;
        this.dosage = dosage;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    public String getId() { return id; }
    public String getApptId() { return apptId; }
    public String getPatientId() { return patientId; }
    public String getMedicine() { return medicine; }
    public String getDosage() { return dosage; }
    public int getQty() { return qty; }
    public double getUnitPrice() { return unitPrice; }

    public double lineTotal() {
        return qty * unitPrice;
    }

    public String displayLine() {
        return id + " | " + apptId + " | " + medicine + " | " + qty + " x RM" + String.format("%.2f", unitPrice) + " = RM" + String.format("%.2f", lineTotal());
    }
}
