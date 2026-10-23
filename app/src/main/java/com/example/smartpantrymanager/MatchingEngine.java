package com.example.smartpantrymanager;

import com.example.smartpantrymanager.database.RecipeDao;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.util.Normalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE STRICT-MATCHING RULE (Section 2.3 of the brief):
 * a recipe is suggested only if EVERY ingredient it needs is in the pantry,
 * in at least the required quantity. One missing or short ingredient = not suggested.
 */
public class MatchingEngine {

    /** Key = normalised name + base unit, e.g. "tomato|pcs". */
    private static String key(String name, String unit) {
        return Normalizer.normalizeName(name) + "|" + Normalizer.baseUnit(unit);
    }

    /** Adds up the pantry per ingredient (two "egg" rows become one total). */
    public static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String k = key(item.name, item.unit);
            double amount = Normalizer.toBase(item.quantity, item.unit);
            Double current = totals.get(k);
            if (current == null) {
                totals.put(k, amount);
            } else {
                totals.put(k, current + amount);
            }
        }
        return totals;
    }

    /** True only when every ingredient is available in the required quantity. */
    public static boolean canMake(List<RecipeIngredient> needed, Map<String, Double> pantryTotals) {
        if (needed == null || needed.isEmpty()) return false;
        for (RecipeIngredient ing : needed) {
            Double have = pantryTotals.get(key(ing.name, ing.unit));
            double required = Normalizer.toBase(ing.quantity, ing.unit);
            if (have == null || have + 0.000001 < required) {
                return false; // missing or not enough -> whole recipe is rejected
            }
        }
        return true;
    }

    /** Returns only the recipes the user can make right now. */
    public static List<Recipe> getSuggestedRecipes(List<PantryItem> pantry, RecipeDao recipeDao) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipeDao.getAllRecipes()) {
            if (canMake(recipeDao.getIngredients(recipe.id), totals)) {
                result.add(recipe);
            }
        }
        return result;
    }
}
