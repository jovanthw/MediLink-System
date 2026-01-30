package oomprojects;

public class Appointment {
    private String id;
    private String type;
    private String patientId;
    private String doctorCode;
    private String status;
    private String time;
    private String fulfilmentType;
    private String fulfilmentInfo;

    public Appointment(String id, String type, String patientId, String doctorCode, String status, String time, String fulfilmentType, String fulfilmentInfo) {
        this.id = id;
        this.type = type;
        this.patientId = patientId;
        this.doctorCode = doctorCode;
        this.status = status;
        this.time = time;
        this.fulfilmentType = fulfilmentType;
        this.fulfilmentInfo = fulfilmentInfo;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getPatientId() { return patientId; }
    public String getDoctorCode() { return doctorCode; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
    public String getFulfilmentType() { return fulfilmentType; }
    public String getFulfilmentInfo() { return fulfilmentInfo; }

    public void setStatus(String s) { this.status = s; }
    public void setFulfilmentType(String t) { this.fulfilmentType = t; }
    public void setFulfilmentInfo(String i) { this.fulfilmentInfo = i; }

    public String simpleLine() {
        return id + " | " + Utils.maskId(patientId) + " → " + doctorCode + " (" + type + ") [" + status + "]";
    }
}
