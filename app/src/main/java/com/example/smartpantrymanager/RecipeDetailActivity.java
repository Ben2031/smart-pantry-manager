package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.AppDatabase;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.util.Format;

/** Recipe Detail screen: full ingredient list and method for the selected recipe. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Button btnBack = findViewById(R.id.btnBack);
        TextView tvName = findViewById(R.id.tvRecipeName);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);
        btnBack.setOnClickListener(v -> finish());

        AppDatabase db = AppDatabase.getInstance(this);
        int id = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = db.recipeDao().getRecipe(id);
        if (recipe == null) {
            finish();
            return;
        }

        tvName.setText(recipe.name);

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : db.recipeDao().getIngredients(recipe.id)) {
            sb.append("\u2022 ")
              .append(Format.qty(ing.quantity)).append(" ").append(ing.unit)
              .append(" ").append(Format.capitalise(ing.name)).append("\n");
        }
        tvIngredients.setText(sb.toString().trim());
        tvSteps.setText(recipe.steps);
    }
}
