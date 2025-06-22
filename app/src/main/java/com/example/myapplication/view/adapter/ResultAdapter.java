package com.example.myapplication.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private final List<Boolean> answerResults;

    public ResultAdapter(List<Boolean> answerResults) {
        this.answerResults = answerResults;
    }

    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultAdapter.ViewHolder holder, int position) {
        holder.resultText.setText("Câu " + (position + 1) + ": " + (answerResults.get(position) ? "Đúng" : "Sai"));
        int color = answerResults.get(position)
                ? R.color.green
                : R.color.red;
        holder.resultText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), color));
    }

    @Override
    public int getItemCount() {
        return answerResults.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resultText;

        ViewHolder(View itemView) {
            super(itemView);
            resultText = itemView.findViewById(R.id.result_text);
        }
    }
}
