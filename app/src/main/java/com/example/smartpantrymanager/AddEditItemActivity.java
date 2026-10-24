package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.AppDatabase;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.util.Format;
import com.example.smartpantrymanager.util.Normalizer;
import com.example.smartpantrymanager.util.Prefs;

import java.util.Calendar;
import java.util.Locale;

/** Add / Edit Ingredient screen: CREATE, UPDATE and DELETE parts of CRUD, with input validation. */
public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";
    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs"};

    private EditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private Button btnDelete;
    private AppDatabase db;
    private PantryItem editingItem; // null when adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        db = AppDatabase.getInstance(this);

        TextView tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnClearDate = findViewById(R.id.btnClearDate);
        Button btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        // Did the previous screen pass us an item id? Then we are editing.
        int id = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (id != -1) {
            editingItem = db.pantryDao().getById(id);
        }

        if (editingItem != null) {
            tvTitle.setText("Edit ingredient");
            etName.setText(Format.capitalise(editingItem.name));
            etQuantity.setText(Format.qty(editingItem.quantity));
            spUnit.setSelection(indexOfUnit(editingItem.unit));
            if (editingItem.expiryDate != null) etExpiry.setText(editingItem.expiryDate);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("Add ingredient");
            spUnit.setSelection(indexOfUnit(Prefs.defaultUnit(this)));
            btnDelete.setVisibility(View.GONE);
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> save());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnBack.setOnClickListener(v -> finish());
    }

    private int indexOfUnit(String unit) {
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(unit)) return i;
        }
        return 0;
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> etExpiry.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        String rawName = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        boolean valid = true;

        // ---- Input validation ----
        if (rawName.isEmpty()) {
            etName.setError("Please enter an ingredient name");
            valid = false;
        } else if (!rawName.matches("[\\p{L} '\\-]+")) {
            etName.setError("Use letters only (no numbers or symbols)");
            valid = false;
        } else if (rawName.length() > 40) {
            etName.setError("Name is too long (max 40 characters)");
            valid = false;
        }

        double quantity = 0;
        if (qtyText.isEmpty()) {
            etQuantity.setError("Please enter a quantity");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(qtyText);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        String normalised = Normalizer.normalizeName(rawName);
        if (valid && normalised.isEmpty()) {
            etName.setError("Please enter a valid ingredient name");
            valid = false;
        }
        if (!valid) return;

        // ---- Save (names and units are stored normalised) ----
        String chosenUnit = (String) spUnit.getSelectedItem();
        PantryItem item = (editingItem != null) ? editingItem : new PantryItem();
        item.name = normalised;
        item.quantity = Normalizer.toBase(quantity, chosenUnit); // kg -> g, l -> ml
        item.unit = Normalizer.baseUnit(chosenUnit);
        String expiry = etExpiry.getText().toString().trim();
        item.expiryDate = expiry.isEmpty() ? null : expiry;

        if (editingItem == null) {
            db.pantryDao().insert(item);      // CREATE
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.pantryDao().update(item);      // UPDATE
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + Format.capitalise(editingItem.name) + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.pantryDao().delete(editingItem);   // DELETE
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
