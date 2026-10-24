package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.util.Normalizer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Unit tests for the strict-matching rule. Run with: right-click this file -> Run. */
public class MatchingEngineTest {

    private static PantryItem pantry(String name, double qty, String unit) {
        PantryItem p = new PantryItem();
        p.name = name;
        p.quantity = qty;
        p.unit = unit;
        return p;
    }

    private static RecipeIngredient need(String name, double qty, String unit) {
        RecipeIngredient r = new RecipeIngredient();
        r.name = name;
        r.quantity = qty;
        r.unit = unit;
        return r;
    }

    private static boolean canMake(List<PantryItem> p, RecipeIngredient... needed) {
        Map<String, Double> totals = MatchingEngine.buildPantryTotals(p);
        return MatchingEngine.canMake(Arrays.asList(needed), totals);
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<PantryItem> p = new ArrayList<>();
        p.add(pantry("pasta", 300, "g"));
        p.add(pantry("tomato", 5, "pcs"));
        p.add(pantry("garlic", 3, "pcs"));
        p.add(pantry("olive oil", 100, "ml"));
        assertFalse(canMake(p,
                need("pasta", 200, "g"), need("tomato", 4, "pcs"), need("garlic", 2, "pcs"),
                need("olive oil", 30, "ml"), need("parmesan", 20, "g")));
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        List<PantryItem> p = new ArrayList<>();
        p.add(pantry("egg", 6, "pcs"));
        p.add(pantry("cheese", 100, "g"));
        p.add(pantry("butter", 50, "g"));
        assertTrue(canMake(p, need("egg", 3, "pcs"), need("cheese", 50, "g"), need("butter", 10, "g")));
    }

    @Test
    public void notEnoughQuantity_isNotSuggested() {
        List<PantryItem> p = new ArrayList<>();
        p.add(pantry("egg", 2, "pcs"));
        assertFalse(canMake(p, need("egg", 3, "pcs")));
    }

    @Test
    public void pluralAndSingularNamesMatch() {
        List<PantryItem> p = new ArrayList<>();
        p.add(pantry(Normalizer.normalizeName("Tomatoes"), 4, "pcs"));
        assertTrue(canMake(p, need("tomato", 4, "pcs")));
        assertEquals("tomato", Normalizer.normalizeName("  TOMATOES "));
        assertEquals("potato", Normalizer.normalizeName("Potatoes"));
        assertEquals("chicken breast", Normalizer.normalizeName("Chicken Breasts"));
    }

    @Test
    public void kilogramsAreConvertedToGrams() {
        List<PantryItem> p = new ArrayList<>();
        p.add(pantry("pasta", 1, "kg"));
        assertTrue(canMake(p, need("pasta", 200, "g")));
    }

    @Test
    public void duplicatePantryRowsAreAddedTogether() {
        List<PantryItem> p = new ArrayList<>();
        p.add(pantry("egg", 2, "pcs"));
        p.add(pantry("egg", 2, "pcs"));
        assertTrue(canMake(p, need("egg", 3, "pcs")));
    }

    @Test
    public void recipeWithNoIngredients_isNeverSuggested() {
        assertFalse(canMake(new ArrayList<PantryItem>()));
    }
}
