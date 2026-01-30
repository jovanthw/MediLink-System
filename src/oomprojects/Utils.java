package oomprojects;

public class Utils {
    public static String maskId(String id) {
        if (id == null) return "";
        if (id.length() <= 4) return id;
        int n = id.length();
        String last = id.substring(n - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 4; i++) sb.append('*');
        sb.append(last);
        return sb.toString();
    }
}
