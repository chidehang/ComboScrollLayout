package com.cdh.nestedscrolling.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleHolder> {

    private Context context;
    private LayoutInflater inflater;
    public List<String> data;

    public SimpleRecyclerAdapter(Context context, List<String> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public SimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return new SimpleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleHolder holder, int position) {
        final String color = data.get(position);
        holder.textView.setBackgroundColor(Color.parseColor(color));
        holder.textView.setText("item " + position);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "item clicked: " + color, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

class SimpleHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public SimpleHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textview);
        textView.setGravity(Gravity.CENTER);
    }
}
