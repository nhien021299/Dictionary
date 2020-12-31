package com.example.dictionary.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.R;
import com.example.dictionary.database.DatabaseHelper;
import com.example.dictionary.model.BookMark;
import com.example.dictionary.ui.WordMeaningAcitvity;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder> {
    private List<BookMark> bookMarks;
    public Context context;
    private LayoutInflater layoutInflater;
    DatabaseHelper databaseHelper;
    public BookMarkAdapter(List<BookMark> bookMarks, Context context) {
        this.bookMarks = bookMarks;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public BookMarkAdapter.BookMarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.bookmark_item_layout,parent,false );
        return new BookMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkAdapter.BookMarkViewHolder holder, int position) {
        BookMark bookMark = bookMarks.get(position);
        holder.textView.setText(bookMark.getBookMark_word());
    }

    @Override
    public int getItemCount() {
        return bookMarks.size();
    }

    class BookMarkViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton imageButton;
        BookMarkViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.bookMark_Word);
            imageButton = itemView.findViewById(R.id.deleteBM);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String text = bookMarks.get(position).getBookMark_word();

                    Intent intent = new Intent(context, WordMeaningAcitvity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BookMark bookMark = bookMarks.get(position);
                    databaseHelper = new DatabaseHelper(v.getContext());
                    databaseHelper.deleteBookMark(bookMark);
                    bookMarks.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }
}