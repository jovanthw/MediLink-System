package oomprojects;

import java.util.List;

public class IDGenerator {
    public static String nextCaregiverId(List<Caregiver> list) {
        int max = 0;
        for (Caregiver c : list) {
            try {
                String num = c.getId().replaceAll("[^0-9]", "");
                int v = Integer.parseInt(num);
                if (v > max) max = v;
            } catch (Exception e) {}
        }
        return String.format("C%03d", max + 1);
    }

    public static String nextDependentId(List<Dependent> list) {
        int max = 0;
        for (Dependent d : list) {
            try {
                String num = d.getId().replaceAll("[^0-9]", "");
                int v = Integer.parseInt(num);
                if (v > max) max = v;
            } catch (Exception e) {}
        }
        return String.format("P%03d", max + 1);
    }

    public static String nextAppointmentId(List<Appointment> list) {
        int max = 0;
        for (Appointment a : list) {
            try {
                String num = a.getId().replaceAll("[^0-9]", "");
                int v = Integer.parseInt(num);
                if (v > max) max = v;
            } catch (Exception e) {}
        }
        return String.format("A%03d", max + 1);
    }

    public static String nextRxId(List<Prescription> list) {
        int max = 0;
        for (Prescription r : list) {
            try {
                String num = r.getId().replaceAll("[^0-9]", "");
                int v = Integer.parseInt(num);
                if (v > max) max = v;
            } catch (Exception e) {}
        }
        return String.format("RX%03d", max + 1);
    }
}
