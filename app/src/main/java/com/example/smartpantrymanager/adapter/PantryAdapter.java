package com.example.smartpantrymanager.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.util.ExpiryUtil;
import com.example.smartpantrymanager.util.Format;

import java.util.ArrayList;
import java.util.List;

/** Custom adapter that shows each PantryItem from the database as a card. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private boolean highlightExpiring = true;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems, boolean highlightExpiring) {
        items.clear();
        items.addAll(newItems);
        this.highlightExpiring = highlightExpiring;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PantryItem item = items.get(position);
        holder.tvName.setText(Format.capitalise(item.name));
        holder.tvAmount.setText(Format.qty(item.quantity) + " " + item.unit);

        if (item.expiryDate == null || item.expiryDate.isEmpty()) {
            holder.tvExpiry.setVisibility(View.GONE);
        } else {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            long days = ExpiryUtil.daysUntil(item.expiryDate);
            if (highlightExpiring && days < 0) {
                holder.tvExpiry.setText("Expired on " + item.expiryDate);
                holder.tvExpiry.setTextColor(Color.RED);
            } else if (highlightExpiring && days <= 3) {
                holder.tvExpiry.setText("Expires in " + days + (days == 1 ? " day" : " days")
                        + " (" + item.expiryDate + ")");
                holder.tvExpiry.setTextColor(Color.parseColor("#E65100"));
            } else {
                holder.tvExpiry.setText("Expires: " + item.expiryDate);
                holder.tvExpiry.setTextColor(0xFF666666);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvAmount, tvExpiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
        }
    }
}
