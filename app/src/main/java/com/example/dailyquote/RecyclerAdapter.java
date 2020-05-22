package com.example.dailyquote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    List<String> quoteList;
    List<String> authorList;

    public RecyclerAdapter(List<String> quoteList, List<String> authorList) {
        this.quoteList = quoteList;
        this.authorList = authorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.quoteTextView.setText(quoteList.get(position));
        holder.subQuoteTextView.setText(authorList.get(position));
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView quoteTextView, subQuoteTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quoteTextView = itemView.findViewById(R.id.quoteTextView);
            subQuoteTextView = itemView.findViewById(R.id.subQuoteTextView);


            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }
}
