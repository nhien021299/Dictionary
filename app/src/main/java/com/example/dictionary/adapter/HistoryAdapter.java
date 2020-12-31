package com.example.dictionary.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.MainActivity;
import com.example.dictionary.database.DatabaseHelper;
import com.example.dictionary.model.History;
import com.example.dictionary.R;
import com.example.dictionary.ui.WordMeaningAcitvity;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> histories;
    private Context context;
    DatabaseHelper databaseHelper;

    public HistoryAdapter(List<History> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView engWord;

        public HistoryViewHolder(View v) {
            super(v);
            engWord = v.findViewById(R.id.english_Word);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String text = histories.get(position).getEnglish_word();

                    Intent intent = new Intent(context, WordMeaningAcitvity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }

    }

    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent,false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.engWord.setText(histories.get(position).getEnglish_word());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }
}