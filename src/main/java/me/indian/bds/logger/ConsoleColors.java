package me.indian.bds.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class ConsoleColors {

    private static final Map<String, String> COLOR_MAP = new HashMap<>();
    private final static Map<String, ColorSet> COLORS = new HashMap<>();

    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[0;33m";
    private static final String DARK_BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String LIGHT_PURPLE = "\u001B[0;95m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BLUE = "\u001B[94m";
    private static final String BRIGHT_PURPLE = "\u001B[95m";
    private static final String BRIGHT_CYAN = "\u001B[96m";
    private static final String BRIGHT_WHITE = "\u001B[97m";

    private static final String BRIGHT_GRAY = "\u001B[37m";
    private static final String DARK_GRAY = "\u001B[90m";
    private static final String LIGHT_GRAY = "\u001B[37;1m";
    private static final String SILVER = "\u001B[90;1m";
    private static final String DARK_RED = "\u001B[31;1m";

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\033[1m";
    private static final String OBFUSCATED = "\033[8m";
    private static final String ITALIC = "\033[3m";
    private static final String UNDERLINE = "\033[4m";
    private static final String STRIKETHROUGH = "\033[9m";
    private static final String BLACK_BACKGROUND = "\u001B[40m";
    private static final String RED_BACKGROUND = "\u001B[41m";
    private static final String GREEN_BACKGROUND = "\u001B[42m";
    private static final String YELLOW_BACKGROUND = "\u001B[43m";
    private static final String BLUE_BACKGROUND = "\u001B[44m";
    private static final String PURPLE_BACKGROUND = "\u001B[45m";
    private static final String CYAN_BACKGROUND = "\u001B[46m";
    private static final String WHITE_BACKGROUND = "\u001B[47m";
    private static final String BRIGHT_BLACK_BACKGROUND = "\u001B[100m";
    private static final String BRIGHT_RED_BACKGROUND = "\u001B[101m";
    private static final String BRIGHT_GREEN_BACKGROUND = "\u001B[102m";
    private static final String BRIGHT_YELLOW_BACKGROUND = "\u001B[103m";
    private static final String BRIGHT_BLUE_BACKGROUND = "\u001B[104m";
    private static final String DARK_GRAY_BACKGROUND = "\u001B[100m";
    private static final String BRIGHT_PURPLE_BACKGROUND = "\u001B[105m";
    private static final String BRIGHT_CYAN_BACKGROUND = "\u001B[106m";
    private static final String BRIGHT_WHITE_BACKGROUND = "\u001B[107m";

    static {
        COLOR_MAP.put("&0", BLACK);
        COLOR_MAP.put("&1", DARK_BLUE);
        COLOR_MAP.put("&2", GREEN);
        COLOR_MAP.put("&3", CYAN);
        COLOR_MAP.put("&4", RED);
        COLOR_MAP.put("&5", PURPLE);
        COLOR_MAP.put("&6", YELLOW);
        COLOR_MAP.put("&7", LIGHT_GRAY);
        COLOR_MAP.put("&8", DARK_GRAY);
        COLOR_MAP.put("&9", BLUE);
        COLOR_MAP.put("&a", BRIGHT_GREEN);
        COLOR_MAP.put("&b", BRIGHT_CYAN);
        COLOR_MAP.put("&c", BRIGHT_RED);
        COLOR_MAP.put("&d", LIGHT_PURPLE);
        COLOR_MAP.put("&e", BRIGHT_YELLOW);
        COLOR_MAP.put("&f", WHITE);

        COLOR_MAP.put("&i", SILVER);

        COLOR_MAP.put("&r", RESET);
        COLOR_MAP.put("&k", OBFUSCATED);
        COLOR_MAP.put("&l", BOLD);
        COLOR_MAP.put("&m", STRIKETHROUGH);
        COLOR_MAP.put("&n", UNDERLINE);
        COLOR_MAP.put("&o", ITALIC);

        COLOR_MAP.put("#0", BLACK_BACKGROUND);
        COLOR_MAP.put("#1", BLUE_BACKGROUND);
        COLOR_MAP.put("#2", GREEN_BACKGROUND);
        COLOR_MAP.put("#3", CYAN_BACKGROUND);
        COLOR_MAP.put("#4", RED_BACKGROUND);
        COLOR_MAP.put("#5", PURPLE_BACKGROUND);
        COLOR_MAP.put("#6", YELLOW_BACKGROUND);
        COLOR_MAP.put("#7", DARK_GRAY_BACKGROUND);
        COLOR_MAP.put("#8", BRIGHT_BLACK_BACKGROUND);
        COLOR_MAP.put("#9", BLUE_BACKGROUND);
        COLOR_MAP.put("#a", BRIGHT_GREEN_BACKGROUND);
        COLOR_MAP.put("#b", BRIGHT_CYAN_BACKGROUND);
        COLOR_MAP.put("#c", BRIGHT_RED_BACKGROUND);
        COLOR_MAP.put("#d", BRIGHT_PURPLE_BACKGROUND);
        COLOR_MAP.put("#e", BRIGHT_YELLOW_BACKGROUND);
        COLOR_MAP.put("#f", BRIGHT_WHITE_BACKGROUND);


        // WYMAGA TO WIELKICH USPRAWNIEŃ
      COLORS.put(BLACK, new ColorSet(0, 0, 0));
        COLORS.put(RED, new ColorSet(255, 0, 0));
        COLORS.put(GREEN, new ColorSet(0, 255, 0));
        COLORS.put(YELLOW, new ColorSet(255, 255, 0));
        COLORS.put(DARK_BLUE, new ColorSet(0, 0, 255));
        COLORS.put(PURPLE, new ColorSet(255, 0, 255));
        COLORS.put(LIGHT_PURPLE, new ColorSet(255, 182, 193)); // Ustalona wartość RGB dla LIGHT_PURPLE
        COLORS.put(CYAN, new ColorSet(0, 255, 255));
        COLORS.put(WHITE, new ColorSet(255, 255, 255));
        COLORS.put(BRIGHT_RED, new ColorSet(255, 0, 0));
        COLORS.put(BRIGHT_GREEN, new ColorSet(0, 255, 0));
        COLORS.put(BRIGHT_YELLOW, new ColorSet(255, 255, 0));
        COLORS.put(BLUE, new ColorSet(0, 0, 255));
        COLORS.put(BRIGHT_PURPLE, new ColorSet(255, 0, 255));
        COLORS.put(BRIGHT_CYAN, new ColorSet(0, 255, 255));
        COLORS.put(BRIGHT_WHITE, new ColorSet(255, 255, 255));
        COLORS.put(BRIGHT_GRAY, new ColorSet(211, 211, 211)); // Ustalona wartość RGB dla BRIGHT_GRAY
        COLORS.put(DARK_GRAY, new ColorSet(169, 169, 169)); // Ustalona wartość RGB dla DARK_GRAY
        COLORS.put(LIGHT_GRAY, new ColorSet(211, 211, 211));
        COLORS.put(SILVER, new ColorSet(192, 192, 192)); // Ustalona wartość RGB dla SILVER
        COLORS.put(DARK_RED, new ColorSet(139, 0, 0)); // Ustalona wartość RGB dla DARK_RED
}

    public static String getMinecraftColorFromANSI(final String ansi) {
        for (final Map.Entry<String, String> entry : COLOR_MAP.entrySet()) {
            if (ansi.equals(entry.getValue())) return entry.getKey();
        }
        return "";
    }

    public static String convertMinecraftColors(final Object input) {
        if (input instanceof String in) {
            for (final Map.Entry<String, String> entry : COLOR_MAP.entrySet()) {
                in = in.replaceAll(entry.getKey(), entry.getValue());
            }
            return in + RESET;
        }
        return input.toString();
    }

    public static String removeColors(final Object input) {
        if (input instanceof String in) {
            for (final Map.Entry<String, String> entry : COLOR_MAP.entrySet()) {
                in = in.replace(entry.getValue(), "")
                        .replaceAll(entry.getKey(), "");
            }
            return in;
        }
        return input.toString();
    }

    // Source: https://gist.github.com/mikroskeem/428f82fbf12f52f29cc6199482c77fb5
    public static String getMinecraftColorFromRGB(final int r, final int g, final int b) {
        // WYMAGA TO WIELKICH USPRAWNIEŃ
        final TreeMap<Integer, String> closest = new TreeMap<>();
        COLORS.forEach((color, set) -> {
            final int red = Math.abs(r - set.red);
            final int green = Math.abs(g - set.green);
            final int blue = Math.abs(b - set.blue);
            closest.put(red + green + blue, color);
        });
        return getMinecraftColorFromANSI(closest.firstEntry().getValue());
    }

    private record ColorSet(int red, int green, int blue) {

    }
}
