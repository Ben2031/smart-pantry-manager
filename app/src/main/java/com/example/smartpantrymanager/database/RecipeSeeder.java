package com.example.smartpantrymanager.database;

import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeSeeder {

    public static void seed(RecipeDao dao) {
        add(dao, "Tomato Pasta",
            "Boil pasta. Fry garlic in olive oil, add chopped tomato and simmer 10 min. Toss with pasta.",
            "pasta:200:g", "tomato:4:pcs", "garlic:2:pcs", "olive oil:30:ml");
        add(dao, "Cheese Omelette",
            "Beat eggs, pour into buttered pan, add cheese, fold and cook 3 min.",
            "egg:3:pcs", "cheese:50:g", "butter:10:g");
        add(dao, "Scrambled Eggs on Toast",
            "Toast bread. Scramble eggs in butter on low heat and serve on toast.",
            "egg:2:pcs", "bread:2:pcs", "butter:10:g");
        add(dao, "Egg Fried Rice",
            "Fry chopped onion, add cooked rice and egg, stir in soy sauce until hot.",
            "rice:200:g", "egg:2:pcs", "onion:1:pcs", "soy sauce:30:ml");
        add(dao, "Pancakes",
            "Mix flour, milk, egg and sugar into a batter. Fry spoonfuls until golden on both sides.",
            "flour:150:g", "milk:200:ml", "egg:1:pcs", "sugar:20:g");
        add(dao, "Mashed Potato",
            "Boil peeled potato until soft, drain, mash with butter and warm milk.",
            "potato:500:g", "butter:30:g", "milk:50:ml");
        add(dao, "Garlic Bread",
            "Mix crushed garlic into butter, spread on bread and bake at 200C for 8 min.",
            "bread:4:pcs", "butter:40:g", "garlic:2:pcs");
        add(dao, "Tuna Pasta",
            "Boil pasta. Fry onion, stir in drained tuna, toss with pasta.",
            "pasta:200:g", "tuna:1:pcs", "onion:1:pcs");
        add(dao, "Vegetable Soup",
            "Chop carrot, potato and onion. Simmer in stock for 25 min.",
            "carrot:2:pcs", "potato:2:pcs", "onion:1:pcs", "vegetable stock:500:ml");
        add(dao, "Cheese Toastie",
            "Butter the bread, add cheese between slices, grill until golden.",
            "bread:2:pcs", "cheese:60:g", "butter:10:g");
        add(dao, "Banana Smoothie",
            "Blend banana, milk and honey until smooth.",
            "banana:2:pcs", "milk:250:ml", "honey:15:ml");
        add(dao, "Chicken Rice Bowl",
            "Pan-fry sliced chicken with onion until cooked. Serve over cooked rice.",
            "chicken breast:300:g", "rice:200:g", "onion:1:pcs");
        add(dao, "Egg Noodles",
            "Boil noodles. Scramble egg in a pan, add noodles and soy sauce, toss.",
            "noodle:200:g", "egg:2:pcs", "soy sauce:20:ml");
        add(dao, "Loaded Baked Potato",
            "Bake potatoes at 200C for 1 hour. Split open, add butter and cheese.",
            "potato:2:pcs", "cheese:50:g", "butter:15:g");
        add(dao, "Banana Oatmeal",
            "Simmer oats in milk for 5 min. Top with sliced banana and honey.",
            "oat:80:g", "milk:250:ml", "banana:1:pcs", "honey:10:ml");
        add(dao, "French Toast",
            "Whisk egg, milk and sugar. Dip bread and fry until golden.",
            "bread:4:pcs", "egg:2:pcs", "milk:100:ml", "sugar:10:g");
        add(dao, "Tomato Soup",
            "Simmer tomato and onion in stock for 20 min, then blend smooth.",
            "tomato:6:pcs", "onion:1:pcs", "vegetable stock:500:ml");
        add(dao, "Garlic Butter Chicken",
            "Fry chicken in butter and garlic for 6 min per side until cooked through.",
            "chicken breast:300:g", "garlic:3:pcs", "butter:30:g");
    }

    private static void add(RecipeDao dao, String name, String steps, String... ingredients) {
        Recipe r = new Recipe();
        r.name = name;
        r.steps = steps;
        int recipeId = (int) dao.insertRecipe(r);

        List<RecipeIngredient> list = new ArrayList<>();
        for (String s : ingredients) {
            String[] p = s.split(":");
            RecipeIngredient ri = new RecipeIngredient();
            ri.recipeId = recipeId;
            ri.name = p[0];
            ri.quantity = Double.parseDouble(p[1]);
            ri.unit = p[2];
            list.add(ri);
        }
        dao.insertIngredients(list);
    }
}
