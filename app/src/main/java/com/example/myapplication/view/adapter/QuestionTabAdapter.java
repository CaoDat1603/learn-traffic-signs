package com.example.myapplication.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionTabAdapter extends RecyclerView.Adapter<QuestionTabAdapter.TabViewHolder> {

    private final List<String> questionNumbers;
    private final OnTabClickListener listener;
    private List<Boolean> answeredStatus;
    private int selectedIndex = 0;

    public boolean isAnswered(int position) {
        return answeredStatus.get(position);
    }


    public interface OnTabClickListener {
        void onTabClick(int position);
    }

    public QuestionTabAdapter(int totalQuestions, OnTabClickListener listener) {
        this.listener = listener;
        this.questionNumbers = new ArrayList<>();
        this.answeredStatus = new ArrayList<>(Collections.nCopies(totalQuestions, false));
        for (int i = 0; i < totalQuestions; i++) {
            questionNumbers.add(String.valueOf(i + 1));
        }
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_tab, parent, false);
        return new TabViewHolder(view);
    }
    public void markAnswered(int position) {
        answeredStatus.set(position, true);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        holder.textView.setText("Câu " + questionNumbers.get(position));

        // Xử lý màu nền theo trạng thái
        if (position == selectedIndex) {
            holder.textView.setBackgroundResource(R.drawable.bg_question_tab_selected); // đỏ
        } else if (answeredStatus.get(position)) {
            holder.textView.setBackgroundResource(R.drawable.bg_question_tab_answered); // xanh lá
        } else {
            holder.textView.setBackgroundResource(R.drawable.bg_question_tab_default); // xám
        }

        // Bấm để chọn
        holder.textView.setOnClickListener(v -> {
            int oldIndex = selectedIndex;
            selectedIndex = holder.getAdapterPosition();
            notifyItemChanged(oldIndex);
            notifyItemChanged(selectedIndex);
            listener.onTabClick(selectedIndex);
        });
    }


    @Override
    public int getItemCount() {
        return questionNumbers.size();
    }

    public void updateSelectedIndex(int index) {
        int oldIndex = selectedIndex;
        selectedIndex = index;
        notifyItemChanged(oldIndex);
        notifyItemChanged(index);
    }

    static class TabViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TabViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tab_text);
        }
    }
}
