package com.example.smartpantrymanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pantry_items")
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;        // stored normalised, e.g. "tomato"
    public double quantity;
    public String unit;        // "g", "ml" or "pcs"
    public String expiryDate;  // optional, may be null
}
