package oomprojects;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileStore {
    private Path folder;

    public FileStore(String folderName) throws IOException {
        folder = Paths.get(folderName);
        if (!Files.exists(folder)) Files.createDirectories(folder);
        ensureFiles();
    }

    private void ensureFiles() throws IOException {
        String[] names = {"caregivers.txt","dependents.txt","doctors.txt","appointments.txt","prescriptions.txt","payments.txt"};
        for (String n : names) {
            Path p = folder.resolve(n);
            if (!Files.exists(p)) Files.createFile(p);
        }
    }

    public List<Caregiver> loadCaregivers() throws IOException {
        List<Caregiver> out = new ArrayList<>();
        Path p = folder.resolve("caregivers.txt");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|");
                if (a.length >= 3) out.add(new Caregiver(a[0], a[1], a[2]));
            }
        }
        return out;
    }

    public void saveCaregivers(List<Caregiver> list) throws IOException {
        Path p = folder.resolve("caregivers.txt");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (Caregiver c : list) pw.println(c.getId() + "|" + c.getName() + "|" + c.getNric());
        }
    }

    public List<Dependent> loadDependents() throws IOException {
        List<Dependent> out = new ArrayList<>();
        Path p = folder.resolve("dependents.txt");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|");
                if (a.length >= 4) out.add(new Dependent(a[0], a[1], a[2], a[3]));
            }
        }
        return out;
    }

    public void saveDependents(List<Dependent> list) throws IOException {
        Path p = folder.resolve("dependents.txt");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (Dependent d : list) pw.println(d.getId() + "|" + d.getName() + "|" + d.getNric() + "|" + d.getCaregiverNric());
        }
    }

    public List<Doctor> loadDoctors() throws IOException {
        List<Doctor> out = new ArrayList<>();
        Path p = folder.resolve("doctors.txt");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|");
                if (a.length >= 2) out.add(new Doctor(a[0], a[1]));
            }
        }
        return out;
    }

    public void saveDoctors(List<Doctor> list) throws IOException {
        Path p = folder.resolve("doctors.txt");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (Doctor d : list) pw.println(d.getCode() + "|" + d.getName());
        }
    }

    public List<Appointment> loadAppointments() throws IOException {
        List<Appointment> out = new ArrayList<>();
        Path p = folder.resolve("appointments.txt");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|");
                String id = a.length > 0 ? a[0] : "";
                String type = a.length > 1 ? a[1] : "";
                String patient = a.length > 2 ? a[2] : "";
                String doc = a.length > 3 ? a[3] : "";
                String status = a.length > 4 ? a[4] : "";
                String time = a.length > 5 ? a[5] : "";
                String ft = a.length > 6 ? a[6] : "";
                String fi = a.length > 7 ? a[7] : "";
                out.add(new Appointment(id, type, patient, doc, status, time, ft, fi));
            }
        }
        return out;
    }

    public void saveAppointments(List<Appointment> list) throws IOException {
        Path p = folder.resolve("appointments.txt");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (Appointment a : list) {
                pw.println(a.getId() + "|" + a.getType() + "|" + a.getPatientId() + "|" + a.getDoctorCode() + "|" + a.getStatus() + "|" + a.getTime() + "|" + safe(a.getFulfilmentType()) + "|" + safe(a.getFulfilmentInfo()));
            }
        }
    }

    public List<Prescription> loadPrescriptions() throws IOException {
        List<Prescription> out = new ArrayList<>();
        Path p = folder.resolve("prescriptions.txt");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|");
                if (a.length >= 7) {
                    String id = a[0];
                    String apptId = a[1];
                    String patientId = a[2];
                    String med = a[3];
                    String dosage = a[4];
                    int qty = Integer.parseInt(a[5]);
                    double up = Double.parseDouble(a[6]);
                    out.add(new Prescription(id, apptId, patientId, med, dosage, qty, up));
                }
            }
        }
        return out;
    }

    public void savePrescriptions(List<Prescription> list) throws IOException {
        Path p = folder.resolve("prescriptions.txt");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (Prescription pr : list) {
                pw.println(pr.getId() + "|" + pr.getApptId() + "|" + pr.getPatientId() + "|" + pr.getMedicine() + "|" + pr.getDosage() + "|" + pr.getQty() + "|" + String.format("%.2f", pr.getUnitPrice()));
            }
        }
    }

    public List<Payment> loadPayments() throws IOException {
        List<Payment> out = new ArrayList<>();
        Path p = folder.resolve("payments.txt");
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] a = line.split("\\|");
                if (a.length >= 4) {
                    String apptId = a[0];
                    String method = a[1];
                    double amt = Double.parseDouble(a[2]);
                    String dt = a[3];
                    out.add(new Payment(apptId, method, amt, dt));
                }
            }
        }
        return out;
    }

    public void savePayments(List<Payment> list) throws IOException {
        Path p = folder.resolve("payments.txt");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (Payment pm : list) {
                pw.println(pm.getApptId() + "|" + pm.getMethod() + "|" + String.format("%.2f", pm.getAmount()) + "|" + pm.getDatetime());
            }
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("|", " ");
    }
}
