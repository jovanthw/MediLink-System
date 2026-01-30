package oomprojects;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MediLinkSystem {
    private Scanner scanner = new Scanner(System.in);
    private List<Caregiver> caregivers = new ArrayList<>();
    private List<Dependent> dependents = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private FileStore store;
    private BillingManager billing;
    private boolean changed = false;

    public MediLinkSystem() throws IOException {
        store = new FileStore("data");
        loadAll();
        billing = new BillingManager(50.00, 0.10);
    }

    private void loadAll() throws IOException {
        caregivers = store.loadCaregivers();
        dependents = store.loadDependents();
        doctors = store.loadDoctors();
        appointments = store.loadAppointments();
        prescriptions = store.loadPrescriptions();
        payments = store.loadPayments();
    }

    private void saveAll() {
        try {
            store.saveCaregivers(caregivers);
            store.saveDependents(dependents);
            store.saveDoctors(doctors);
            store.saveAppointments(appointments);
            store.savePrescriptions(prescriptions);
            store.savePayments(payments);
            System.out.println("Data saved to data/ folder.");
            changed = false;
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            System.out.println("\nSelect role:");
            System.out.println("[1] Doctor");
            System.out.println("[2] Caregiver");
            System.out.println("[3] Dependent");
            System.out.println("[4] Save & Exit");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            if (c.equals("1")) {
                doctorMenu();
            } else if (c.equals("2")) {
                caregiverMenu();
            } else if (c.equals("3")) {
                dependentMenu();
            } else if (c.equals("4")) {
                saveAll();
                System.out.println("Exiting.");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    private void doctorMenu() {
        System.out.print("Enter doctor code: ");
        String code = scanner.nextLine().trim();
        Doctor dr = findDoctorByCode(code);
        if (dr == null) {
            System.out.println("Doctor not found. Do you want to add this doctor? (y/n)");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                System.out.print("Enter doctor name: ");
                String name = scanner.nextLine().trim();
                if (findDoctorByName(name) != null || findDoctorByCode(code) != null) {
                    System.out.println("Duplicate doctor code or name.");
                    return;
                }
                dr = new Doctor(code, name);
                doctors.add(dr);
                changed = true;
                System.out.println("Doctor added: [" + dr.getCode() + "] " + dr.getName());
            } else return;
        }
        while (true) {
            System.out.println("\nDoctor Menu - " + dr.getName() + " [" + dr.getCode() + "]");
            System.out.println("[1] List my appointments");
            System.out.println("[2] Update appointment status");
            System.out.println("[3] Add prescription for COMPLETED appointment");
            System.out.println("[4] List doctors");
            System.out.println("[5] Add doctor");
            System.out.println("[6] Back");
            System.out.print("Choice: ");
            String ch = scanner.nextLine().trim();
            if (ch.equals("1")) {
                listAppointmentsForDoctor(dr.getCode());
            } else if (ch.equals("2")) {
                updateAppointmentStatusDoctor(dr);
            } else if (ch.equals("3")) {
                addPrescriptionByDoctor(dr);
            } else if (ch.equals("4")) {
                listDoctors();
            } else if (ch.equals("5")) {
                addDoctorFlow();
            } else if (ch.equals("6")) break;
            else System.out.println("Invalid option.");
        }
    }

    private void caregiverMenu() {
        System.out.print("Enter caregiver ID (NRIC/Passport): ");
        String id = scanner.nextLine().trim();
        Caregiver c = findCaregiverByNric(id);
        if (c == null) {
            System.out.println("Caregiver not found. Register new caregiver? (y/n)");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                System.out.print("Enter caregiver name: ");
                String name = scanner.nextLine().trim();
                if (existsIdInPersons(id)) {
                    System.out.println("This ID is already used. Registration failed.");
                    return;
                }
                String cid = IDGenerator.nextCaregiverId(caregivers);
                c = new Caregiver(cid, name, id);
                caregivers.add(c);
                changed = true;
                System.out.println("Caregiver created: " + c.getName() + " (ID " + Utils.maskId(c.getNric()) + ")");
            } else return;
        }
        while (true) {
            System.out.println("\nCaregiver Menu - " + c.getName() + " (" + Utils.maskId(c.getNric()) + ")");
            System.out.println("[1] Add dependent");
            System.out.println("[2] List family");
            System.out.println("[3] Book appointment");
            System.out.println("[4] Show family appointments");
            System.out.println("[5] Pay appointment");
            System.out.println("[6] Request fulfilment");
            System.out.println("[7] Back");
            System.out.print("Choice: ");
            String ch = scanner.nextLine().trim();
            if (ch.equals("1")) {
                addDependentFlow(c);
            } else if (ch.equals("2")) {
                listFamily(c);
            } else if (ch.equals("3")) {
                bookAppointmentFlow(c);
            } else if (ch.equals("4")) {
                listAppointmentsForCaregiver(c);
            } else if (ch.equals("5")) {
                paymentFlowForCaregiver(c);
            } else if (ch.equals("6")) {
                fulfilmentFlowForCaregiver(c);
            } else if (ch.equals("7")) break;
            else System.out.println("Invalid choice.");
        }
    }

    private void dependentMenu() {
        System.out.print("Enter dependent ID (NRIC/Passport): ");
        String id = scanner.nextLine().trim();
        Dependent d = findDependentByNric(id);
        if (d == null) {
            System.out.println("Dependent not found.");
            return;
        }
        while (true) {
            System.out.println("\nDependent Menu - " + d.getName() + " (" + Utils.maskId(d.getNric()) + ")");
            System.out.println("[1] View my appointments");
            System.out.println("[2] View my prescriptions");
            System.out.println("[3] Pay appointment");
            System.out.println("[4] Request fulfilment");
            System.out.println("[5] Back");
            System.out.print("Choice: ");
            String ch = scanner.nextLine().trim();
            if (ch.equals("1")) {
                listAppointmentsForPatient(d.getNric());
            } else if (ch.equals("2")) {
                listPrescriptionsForPatient(d.getNric());
            } else if (ch.equals("3")) {
                paymentFlowForDependent(d);
            } else if (ch.equals("4")) {
                fulfilmentFlowForDependent(d);
            } else if (ch.equals("5")) break;
            else System.out.println("Invalid choice.");
        }
    }

    private void addDoctorFlow() {
        System.out.print("Enter doctor code: ");
        String code = scanner.nextLine().trim();
        if (findDoctorByCode(code) != null) {
            System.out.println("Doctor code exists.");
            return;
        }
        System.out.print("Enter doctor name: ");
        String name = scanner.nextLine().trim();
        if (findDoctorByName(name) != null) {
            System.out.println("Doctor name exists.");
            return;
        }
        Doctor d = new Doctor(code, name);
        doctors.add(d);
        changed = true;
        System.out.println("Doctor added: [" + d.getCode() + "] " + d.getName());
    }

    private void listDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("No doctors available.");
            return;
        }
        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            System.out.println("[" + (i + 1) + "] " + d.getName() + " (" + d.getCode() + ")");
        }
    }

    private void listAppointmentsForDoctor(String doctorCode) {
        boolean any = false;
        for (Appointment a : appointments) {
            if (a.getDoctorCode().equals(doctorCode)) {
                System.out.println(a.simpleLine());
                any = true;
            }
        }
        if (!any) System.out.println("No appointments for you.");
    }

    private void updateAppointmentStatusDoctor(Doctor dr) {
        System.out.print("Enter appointment ID: ");
        String aid = scanner.nextLine().trim();
        Appointment a = findAppointmentById(aid);
        if (a == null) {
            System.out.println("Appointment not found.");
            return;
        }
        if (!a.getDoctorCode().equals(dr.getCode())) {
            System.out.println("This appointment is not assigned to you.");
            return;
        }
        System.out.println("Current status: " + a.getStatus());
        System.out.println("Choose new status: [1] CONFIRMED [2] COMPLETED [3] CANCELLED");
        String ch = scanner.nextLine().trim();
        String newStatus = null;
        if (ch.equals("1")) newStatus = "CONFIRMED";
        if (ch.equals("2")) newStatus = "COMPLETED";
        if (ch.equals("3")) newStatus = "CANCELLED";
        if (newStatus == null) {
            System.out.println("Invalid input.");
            return;
        }
        a.setStatus(newStatus);
        changed = true;
        System.out.println(a.getId() + " " + newStatus);
    }

    private void addPrescriptionByDoctor(Doctor dr) {
        System.out.print("Enter appointment ID: ");
        String aid = scanner.nextLine().trim();
        Appointment a = findAppointmentById(aid);
        if (a == null) {
            System.out.println("Appointment not found.");
            return;
        }
        if (!a.getDoctorCode().equals(dr.getCode())) {
            System.out.println("This appointment is not assigned to you.");
            return;
        }
        if (!a.getStatus().equals("COMPLETED")) {
            System.out.println("Only COMPLETED appointments can have prescriptions.");
            return;
        }
        while (true) {
            String rxId = IDGenerator.nextRxId(prescriptions);
            System.out.print("Medicine name: ");
            String med = scanner.nextLine().trim();
            System.out.print("Dosage text: ");
            String dosage = scanner.nextLine().trim();
            System.out.print("Quantity (integer): ");
            int qty;
            try {
                qty = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid number.");
                continue;
            }
            System.out.print("Unit price (RM): ");
            double up;
            try {
                up = Double.parseDouble(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid price.");
                continue;
            }
            Prescription p = new Prescription(rxId, a.getId(), a.getPatientId(), med, dosage, qty, up);
            prescriptions.add(p);
            changed = true;
            System.out.println("Added: " + qty + " x " + med + " @RM" + String.format("%.2f", up));
            System.out.print("Add another? (y/n): ");
            String more = scanner.nextLine().trim();
            if (!more.equalsIgnoreCase("y")) break;
        }
    }

    private void addDependentFlow(Caregiver c) {
        System.out.print("Enter dependent name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter dependent ID (NRIC/Passport): ");
        String nid = scanner.nextLine().trim();
        if (existsIdInPersons(nid)) {
            System.out.println("ID already used.");
            return;
        }
        String pid = IDGenerator.nextDependentId(dependents);
        Dependent d = new Dependent(pid, name, nid, c.getNric());
        dependents.add(d);
        changed = true;
        System.out.println("Dependent added: " + d.getName() + " (ID " + Utils.maskId(d.getNric()) + ") linked to " + c.getName());
    }

    private void listFamily(Caregiver c) {
        System.out.println("Caregiver: " + c.getName() + " (" + Utils.maskId(c.getNric()) + ")");
        int idx = 1;
        for (Dependent d : dependents) {
            if (d.getCaregiverNric().equals(c.getNric())) {
                System.out.println("[" + (idx++) + "] " + d.getName() + " (" + Utils.maskId(d.getNric()) + ")");
            }
        }
    }

    private void bookAppointmentFlow(Caregiver c) {
        List<Dependent> fam = new ArrayList<>();
        for (Dependent d : dependents) if (d.getCaregiverNric().equals(c.getNric())) fam.add(d);
        if (fam.isEmpty()) {
            System.out.println("No dependents. Please add dependent.");
            return;
        }
        for (int i = 0; i < fam.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + fam.get(i).getName() + " (" + Utils.maskId(fam.get(i).getNric()) + ")");
        }
        System.out.print("Choose dependent: ");
        int sel;
        try {
            sel = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid selection.");
            return;
        }
        if (sel < 0 || sel >= fam.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Dependent chosen = fam.get(sel);
        if (doctors.isEmpty()) {
            System.out.println("No doctors available.Please add doctor.");
            return;
        }
        listDoctors();
        System.out.print("Enter doctor code: ");
        String dcode = scanner.nextLine().trim();
        Doctor doc = findDoctorByCode(dcode);
        if (doc == null) {
            System.out.println("Unknown doctor code.");
            return;
        }
        //hereerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
        System.out.print("Enter appointment type (VIDEO [V] / CLINIC [C] ): ");
        String type = scanner.nextLine().trim().toUpperCase();
        if (!type.equalsIgnoreCase("V") && !type.equalsIgnoreCase("C")) {
            
            System.out.println("Invalid type.");
            return;
        }
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        System.out.print("Enter time (HH:mm): ");
        String time = scanner.nextLine().trim();
        String dt = date + "T" + time;
        String aid = IDGenerator.nextAppointmentId(appointments);
        Appointment ap = new Appointment(aid, type, chosen.getNric(), doc.getCode(), "REQUESTED", dt, "", "");
        appointments.add(ap);
        changed = true;
        System.out.println(ap.getId() + " REQUESTED: " + Utils.maskId(ap.getPatientId()) + " → " + doc.getName() + " (" + type + ") @ " + time);
    }

    private void listAppointmentsForCaregiver(Caregiver c) {
        boolean any = false;
        for (Appointment a : appointments) {
            for (Dependent d : dependents) {
                if (d.getCaregiverNric().equals(c.getNric()) && d.getNric().equals(a.getPatientId())) {
                    System.out.println(a.simpleLine());
                    any = true;
                }
            }
        }
        if (!any) System.out.println("No appointments for your family.");
    }

    private void listAppointmentsForPatient(String patientNric) {
        boolean any = false;
        for (Appointment a : appointments) {
            if (a.getPatientId().equals(patientNric)) {
                System.out.println(a.simpleLine());
                any = true;
            }
        }
        if (!any) System.out.println("No appointments.");
    }

    private void listPrescriptionsForPatient(String patientNric) {
        boolean any = false;
        for (Prescription p : prescriptions) {
            if (p.getPatientId().equals(patientNric)) {
                System.out.println(p.displayLine());
                any = true;
            }
        }
        if (!any) System.out.println("No prescriptions.");
    }

    private void paymentFlowForCaregiver(Caregiver c) {
        List<Appointment> familyAppointments = new ArrayList<>();
        for (Appointment a : appointments) {
            for (Dependent d : dependents) {
                if (d.getCaregiverNric().equals(c.getNric()) && d.getNric().equals(a.getPatientId())) {
                    if (!a.getStatus().equals("PAID")) familyAppointments.add(a);
                }
            }
        }
        if (familyAppointments.isEmpty()) {
            System.out.println("No unpaid appointments for family.");
            return;
        }
        for (int i = 0; i < familyAppointments.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + familyAppointments.get(i).simpleLine());
        }
        System.out.print("Choose appointment to pay: ");
        int sel;
        try {
            sel = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid.");
            return;
        }
        if (sel < 0 || sel >= familyAppointments.size()) {
            System.out.println("Invalid.");
            return;
        }
        Appointment a = familyAppointments.get(sel);
        performPaymentForAppointment(a);
    }

    private void paymentFlowForDependent(Dependent d) {
        List<Appointment> my = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId().equals(d.getNric()) && !a.getStatus().equals("PAID")) my.add(a);
        }
        if (my.isEmpty()) {
            System.out.println("No unpaid appointments.");
            return;
        }
        for (int i = 0; i < my.size(); i++) System.out.println("[" + (i + 1) + "] " + my.get(i).simpleLine());
        System.out.print("Choose appointment to pay: ");
        int sel;
        try {
            sel = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid.");
            return;
        }
        if (sel < 0 || sel >= my.size()) {
            System.out.println("Invalid.");
            return;
        }
        performPaymentForAppointment(my.get(sel));
    }

    private void performPaymentForAppointment(Appointment a) {
        List<Prescription> rx = getPrescriptionsForAppointment(a.getId());
        if (rx.isEmpty()) {
            System.out.println("No prescription for this appointment. Cannot bill/pay.");
            return;
        }
        double subtotal = billing.calculateSubtotal(50.00, rx);
        double sst = billing.calculateSst(subtotal);
        double total = billing.calculateGrandTotal(subtotal, sst);
        System.out.println("Consultation: RM50.00");
        for (Prescription p : rx) {
            System.out.println(p.getMedicine() + " - " + p.getQty() + " x RM" + String.format("%.2f", p.getUnitPrice()) + " = RM" + String.format("%.2f", p.lineTotal()));
        }
        System.out.println("Subtotal: RM" + String.format("%.2f", subtotal));
        System.out.println("SST (10%): RM" + String.format("%.2f", sst));
        System.out.println("Grand Total: RM" + String.format("%.2f", total));
        System.out.print("Payment method ([1] CARD/EWALLET [2] INSURANCE): ");
        String m = scanner.nextLine().trim();
        String method = m.equals("2") ? "INSURANCE" : "CARD";
        System.out.print("Confirm payment RM" + String.format("%.2f", total) + " ? (y/n): ");
        String conf = scanner.nextLine().trim();
        if (!conf.equalsIgnoreCase("y")) {
            System.out.println("Payment cancelled.");
            return;
        }
        Payment pmt = new Payment(a.getId(), method, total, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        payments.add(pmt);
        a.setStatus("PAID");
        changed = true;
        System.out.println("Payment successful: " + method);
        printReceipt(a, rx, pmt);
    }

    private void printReceipt(Appointment a, List<Prescription> rx, Payment pmt) {
        System.out.println("----- RECEIPT -----");
        System.out.println("Date/Time: " + pmt.getDatetime());
        System.out.println("Patient: " + Utils.maskId(a.getPatientId()));
        System.out.println("Doctor: " + doctorNameForCode(a.getDoctorCode()) + " (" + a.getType() + ")");
        System.out.println("Consultation: RM50.00");
        for (Prescription pr : rx) {
            System.out.println(pr.getMedicine() + " - " + pr.getQty() + " x RM" + String.format("%.2f", pr.getUnitPrice()) + " = RM" + String.format("%.2f", pr.lineTotal()));
        }
        double subtotal = billing.calculateSubtotal(50.00, rx);
        double sst = billing.calculateSst(subtotal);
        double total = billing.calculateGrandTotal(subtotal, sst);
        System.out.println("Subtotal: RM" + String.format("%.2f", subtotal));
        System.out.println("SST (10%): RM" + String.format("%.2f", sst));
        System.out.println("Grand Total: RM" + String.format("%.2f", total));
        System.out.println("Payment Method: " + pmt.getMethod());
        System.out.println("-------------------");
    }

    private void fulfilmentFlowForCaregiver(Caregiver c) {
        List<Appointment> eligible = new ArrayList<>();
        for (Appointment a : appointments) {
            for (Dependent d : dependents) {
                if (d.getCaregiverNric().equals(c.getNric()) && d.getNric().equals(a.getPatientId()) && a.getStatus().equals("PAID")) {
                    eligible.add(a);
                }
            }
        }
        if (eligible.isEmpty()) {
            System.out.println("No paid appointments ready for fulfilment.");
            return;
        }
        for (int i = 0; i < eligible.size(); i++) System.out.println("[" + (i + 1) + "] " + eligible.get(i).simpleLine());
        System.out.print("Choose appointment for fulfilment: ");
        int sel;
        try {
            sel = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid.");
            return;
        }
        if (sel < 0 || sel >= eligible.size()) {
            System.out.println("Invalid.");
            return;
        }
        fulfilAppointment(eligible.get(sel));
    }

    private void fulfilmentFlowForDependent(Dependent d) {
        List<Appointment> eligible = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getPatientId().equals(d.getNric()) && a.getStatus().equals("PAID")) eligible.add(a);
        }
        if (eligible.isEmpty()) {
            System.out.println("No paid appointments ready for fulfilment.");
            return;
        }
        for (int i = 0; i < eligible.size(); i++) System.out.println("[" + (i + 1) + "] " + eligible.get(i).simpleLine());
        System.out.print("Choose appointment for fulfilment: ");
        int sel;
        try {
            sel = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid.");
            return;
        }
        if (sel < 0 || sel >= eligible.size()) {
            System.out.println("Invalid.");
            return;
        }
        fulfilAppointment(eligible.get(sel));
    }

    private void fulfilAppointment(Appointment a) {
        List<Prescription> rx = getPrescriptionsForAppointment(a.getId());
        if (rx.isEmpty()) {
            System.out.println("No prescription exists for this appointment.");
            return;
        }
        System.out.println("Fulfilment options: [1] Counter Pickup [2] Home Delivery");
        String ch = scanner.nextLine().trim();
        if (ch.equals("1")) {
            System.out.print("Enter counter location text (e.g., Counter 2): ");
            String info = scanner.nextLine().trim();
            String readyTime = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            a.setFulfilmentType("Counter Pickup");
            a.setFulfilmentInfo(info + " | Ready: " + readyTime);
        } else if (ch.equals("2")) {
            System.out.print("Enter delivery address: ");
            String addr = scanner.nextLine().trim();
            String eta = LocalDateTime.now().plusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            a.setFulfilmentType("Home Delivery");
            a.setFulfilmentInfo(addr + " | ETA: " + eta);
        } else {
            System.out.println("Invalid option.");
            return;
        }
        changed = true;
        System.out.println("Fulfilment recorded: " + a.getFulfilmentType() + " - " + a.getFulfilmentInfo());
    }

    private List<Prescription> getPrescriptionsForAppointment(String apptId) {
        List<Prescription> out = new ArrayList<>();
        for (Prescription p : prescriptions) if (p.getApptId().equals(apptId)) out.add(p);
        return out;
    }

    private List<Prescription> getPrescriptionsForPatient(String patientNric) {
        List<Prescription> out = new ArrayList<>();
        for (Prescription p : prescriptions) if (p.getPatientId().equals(patientNric)) out.add(p);
        return out;
    }

    private Doctor findDoctorByCode(String code) {
        for (Doctor d : doctors) if (d.getCode().equals(code)) return d;
        return null;
    }

    private Doctor findDoctorByName(String name) {
        for (Doctor d : doctors) if (d.getName().equalsIgnoreCase(name)) return d;
        return null;
    }

    private Caregiver findCaregiverByNric(String nric) {
        for (Caregiver c : caregivers) if (c.getNric().equals(nric)) return c;
        return null;
    }

    private Dependent findDependentByNric(String nric) {
        for (Dependent d : dependents) if (d.getNric().equals(nric)) return d;
        return null;
    }

    private boolean existsIdInPersons(String nric) {
        if (findCaregiverByNric(nric) != null) return true;
        if (findDependentByNric(nric) != null) return true;
        return false;
    }

    private Appointment findAppointmentById(String id) {
        for (Appointment a : appointments) if (a.getId().equals(id)) return a;
        return null;
    }

    private String doctorNameForCode(String code) {
        Doctor d = findDoctorByCode(code);
        return d == null ? code : d.getName();
    }
}
