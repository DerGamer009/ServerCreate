package net.devvoxel.ServerCreate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeParser {
    private static final Pattern PART = Pattern.compile("(\\d+)([dhms])", Pattern.CASE_INSENSITIVE);

    public static long parseDuration(String input) throws IllegalArgumentException {
        String normalized = input.replaceAll("\\s+", "").toLowerCase();
        Matcher m = PART.matcher(normalized);
        long total = 0L;
        int pos = 0;
        while (m.find()) {
            if (m.start() != pos) {
                throw new IllegalArgumentException("Invalid duration: " + input);
            }
            long value = Long.parseLong(m.group(1));
            String unit = m.group(2);
            switch (unit) {
                case "d":
                    total += value * 86400000L;
                    break;
                case "h":
                    total += value * 3600000L;
                    break;
                case "m":
                    total += value * 60000L;
                    break;
                case "s":
                    total += value * 1000L;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
            pos = m.end();
        }
        if (pos != normalized.length()) {
            throw new IllegalArgumentException("Invalid duration: " + input);
        }
        return total;
    }

    private TimeParser() {}
}
