package oomprojects;

public class Dependent {
    private String id;
    private String name;
    private String nric;
    private String caregiverNric;

    public Dependent(String id, String name, String nric, String caregiverNric) {
        this.id = id;
        this.name = name;
        this.nric = nric;
        this.caregiverNric = caregiverNric;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNric() {
        return nric;
    }

    public String getCaregiverNric() {
        return caregiverNric;
    }
}
