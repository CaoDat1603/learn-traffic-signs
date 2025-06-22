package com.example.myapplication.controller;

import android.content.Context;

import com.example.myapplication.data.database.TrafficSignDBHelper;
import com.example.myapplication.data.model.TrafficSign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExamController {
    private final TrafficSignDBHelper dbHelper;
    private List<TrafficSign> signList;

    public ExamController(Context context) {
        dbHelper = new TrafficSignDBHelper(context);
    }

    public List<TrafficSign> getTrafficSigns(String type) {
        if (type.equalsIgnoreCase("all")) {
            return dbHelper.getTrafficSigns("all");
        } else {
            return dbHelper.getTrafficSigns(type);
        }
    }

    public void setSignList(List<TrafficSign> list) {
        this.signList = new ArrayList<>(list);
    }

    public List<TrafficSign> getSignListCur(boolean isReset) {
        if (signList == null || signList.isEmpty()) return new ArrayList<>();

        Collections.shuffle(signList);
        int limit = Math.min(50, signList.size()); // Lấy tối đa 50 câu hỏi
        return new ArrayList<>(signList.subList(0, limit));
    }
}
