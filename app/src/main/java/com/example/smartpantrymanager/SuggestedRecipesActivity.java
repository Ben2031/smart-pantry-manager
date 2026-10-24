package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.AppDatabase;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Suggested Recipes screen: runs the strict-matching rule against the current pantry. */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView rvRecipes;
    private TextView tvEmpty, tvSummary;
    private RecipeAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = AppDatabase.getInstance(this);

        rvRecipes = findViewById(R.id.rvRecipes);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvSummary = findViewById(R.id.tvSummary);
        BottomNavigationView nav = findViewById(R.id.bottomNav);

        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
            startActivity(intent);
        });
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        NavHelper.setup(this, nav, R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<PantryItem> pantry = db.pantryDao().getAll();
        List<Recipe> suggested = MatchingEngine.getSuggestedRecipes(pantry, db.recipeDao());

        Map<Integer, String> summaries = new HashMap<>();
        for (Recipe r : suggested) {
            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ing : db.recipeDao().getIngredients(r.id)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(ing.name);
            }
            summaries.put(r.id, sb.toString());
        }
        adapter.setRecipes(suggested, summaries);

        if (suggested.isEmpty()) {
            // Required feedback when nothing matches (instead of a blank screen)
            tvEmpty.setVisibility(View.VISIBLE);
            rvRecipes.setVisibility(View.GONE);
            tvSummary.setText("0 recipes you can make right now");
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvRecipes.setVisibility(View.VISIBLE);
            tvSummary.setText(suggested.size() + (suggested.size() == 1 ? " recipe" : " recipes")
                    + " you can make right now");
        }
    }
}
