package com.cdh.nestedscrolling.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cdh.nestedscrolling.R;

import java.util.List;

/**
 * Created by chidehang on 2020-01-12
 */
public class BannerRecyclerAdapter extends RecyclerView.Adapter<BannerViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<String> data;

    public BannerRecyclerAdapter(Context context, List<String> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_header_view, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        final String color = data.get(position);
        holder.textView.setBackgroundColor(Color.parseColor(color));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "banner clicked: " + color, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

class BannerViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textview);
    }
}
