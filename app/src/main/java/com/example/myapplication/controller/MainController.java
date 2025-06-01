package com.example.myapplication.controller;

import com.example.myapplication.data.database.TrafficSignImporter;
import com.example.myapplication.view.MainActivity;

public class MainController {
    private MainActivity context;

    public MainController(MainActivity context) {
        this.context = context;
    }

    public void importFromAssets() {
        TrafficSignImporter.importFromAssets(context);
    }
}
