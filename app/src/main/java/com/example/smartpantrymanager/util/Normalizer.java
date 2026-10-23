package com.example.smartpantrymanager.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Cleans up ingredient names and units so that simple real-world differences
 * ("Tomatoes" vs "tomato", "1 kg" vs "1000 g") do not break the strict matching.
 */
public class Normalizer {

    // Small synonym list, applied word by word after singularising.
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("coriander", "cilantro");
        SYNONYMS.put("broth", "stock");
        SYNONYMS.put("garbanzo", "chickpea");
        SYNONYMS.put("spaghetti", "pasta");
        SYNONYMS.put("penne", "pasta");
        SYNONYMS.put("macaroni", "pasta");
        SYNONYMS.put("fusilli", "pasta");
    }

    /** "Tomatoes " -> "tomato", "Chicken Breasts" -> "chicken breast". */
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String cleaned = raw.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (cleaned.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        for (String word : cleaned.split(" ")) {
            String w = singularize(word);
            String synonym = SYNONYMS.get(w);
            if (synonym != null) w = synonym;
            if (result.length() > 0) result.append(' ');
            result.append(w);
        }
        return result.toString();
    }

    /** Very small English singulariser - good enough for food words. */
    static String singularize(String w) {
        if (w.length() <= 3) return w;
        if (w.endsWith("ies") && w.length() > 4) return w.substring(0, w.length() - 3) + "y"; // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);                          // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("sses") || w.endsWith("xes")) {
            return w.substring(0, w.length() - 2);                                             // peaches -> peach
        }
        if (w.endsWith("ss") || w.endsWith("us") || w.endsWith("is")) return w;                // hummus stays hummus
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);                            // eggs -> egg
        return w;
    }

    /** The unit everything is stored in: g, ml or pcs. */
    public static String baseUnit(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "g":
            case "kg":
                return "g";
            case "ml":
            case "l":
                return "ml";
            default:
                return "pcs";
        }
    }

    /** Converts a quantity to its base unit (kg -> g, l -> ml). */
    public static double toBase(double quantity, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("kg") || u.equals("l")) return quantity * 1000.0;
        return quantity;
    }
}
