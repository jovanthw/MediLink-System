package oomprojects;

public class Payment {
    private String apptId;
    private String method;
    private double amount;
    private String datetime;

    public Payment(String apptId, String method, double amount, String datetime) {
        this.apptId = apptId;
        this.method = method;
        this.amount = amount;
        this.datetime = datetime;
    }

    public String getApptId() { return apptId; }
    public String getMethod() { return method; }
    public double getAmount() { return amount; }
    public String getDatetime() { return datetime; }
}
