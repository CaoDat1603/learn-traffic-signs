package com.example.myapplication.controller;

import com.example.myapplication.data.database.TrafficSignDBHelper;
import com.example.myapplication.data.model.TrafficSign;
import com.example.myapplication.view.HomeActivity;

import java.util.List;

public class HomeController {
    private TrafficSignDBHelper dbHelper;

    public HomeController(HomeActivity context) {
        this.dbHelper = new TrafficSignDBHelper(context);
    }

    public List<TrafficSign> getTrafficSigns(String type) {
        return dbHelper.getTrafficSigns(type);
    }
}
