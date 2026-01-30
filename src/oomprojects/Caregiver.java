package oomprojects;
public class Caregiver {
    private String id;
    private String name;
    private String nric;

    public Caregiver(String id, String name, String nric) {
        this.id = id;
        this.name = name;
        this.nric = nric;
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
}

