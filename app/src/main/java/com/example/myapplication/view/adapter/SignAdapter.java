package com.example.myapplication.view.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.model.TrafficSign;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SignAdapter extends RecyclerView.Adapter<SignAdapter.SignViewHolder> {
    private Context context;
    private List<TrafficSign> signList;
    private OnSignClickListener listener;

    public SignAdapter(Context context, List<TrafficSign> signList, OnSignClickListener listener) {
        this.context = context;
        this.signList = signList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sign_item, parent, false);
        return new SignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignViewHolder holder, int position) {
        TrafficSign sign = signList.get(position);
        holder.id.setText(sign.getId());
        holder.name.setText(sign.getName());
        holder.description.setText(sign.getDescription());

        int imageResId = context.getResources().getIdentifier(
                sign.getImage(), "drawable", context.getPackageName());

        if (imageResId != 0) {
            holder.image.setImageResource(imageResId);
        } else {
            holder.image.setImageResource(R.drawable.logo);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSignClick(sign);
            }
        });
    }

    @Override
    public int getItemCount() {
        return signList.size();
    }

    public static class SignViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView id, name, description;

        public SignViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            id = itemView.findViewById(R.id.signId);
            name = itemView.findViewById(R.id.signName);
            description = itemView.findViewById(R.id.signDif);
        }
    }

    public interface OnSignClickListener {
        void onSignClick(TrafficSign sign);
    }
}