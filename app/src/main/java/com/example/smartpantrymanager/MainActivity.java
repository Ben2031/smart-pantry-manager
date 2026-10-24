package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.AppDatabase;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.util.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/** Pantry List screen (the home screen): READ part of CRUD. */
public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPantry;
    private TextView tvEmpty;
    private PantryAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        rvPantry = findViewById(R.id.rvPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        BottomNavigationView nav = findViewById(R.id.bottomNav);

        // Tapping a row opens the edit screen and passes the item id with an Intent extra.
        adapter = new PantryAdapter(item -> {
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.id);
            startActivity(intent);
        });
        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        rvPantry.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddEditItemActivity.class)));

        NavHelper.setup(this, nav, R.id.nav_pantry);
    }

    // onResume runs every time we come back to this screen, so the list is always fresh.
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<PantryItem> items = db.pantryDao().getAll();
        adapter.setItems(items, Prefs.expiryAlertsEnabled(this));
        boolean empty = items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvPantry.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
