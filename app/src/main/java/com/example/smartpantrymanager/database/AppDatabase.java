package com.example.smartpantrymanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

@Database(entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
          version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryDao pantryDao();
    public abstract RecipeDao recipeDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "smart_pantry.db")
                    .allowMainThreadQueries() // simple for this assignment
                    .build();
            // Seed recipes on first run only
            if (instance.recipeDao().countRecipes() == 0) {
                RecipeSeeder.seed(instance.recipeDao());
            }
        }
        return instance;
    }
}
