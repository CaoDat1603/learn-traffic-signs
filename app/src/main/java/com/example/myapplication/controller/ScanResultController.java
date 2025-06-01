package com.example.myapplication.controller;

import com.example.myapplication.data.database.TrafficSignDBHelper;
import com.example.myapplication.data.model.TrafficSign;
import com.example.myapplication.view.ScanResultActivity;

public class ScanResultController {
    private TrafficSignDBHelper dbHelper;
    public ScanResultController (ScanResultActivity context) {
        this.dbHelper = new TrafficSignDBHelper(context);
    }
    public TrafficSign getTrafficSignById(String idCur) {
        return dbHelper.getTrafficSignById(idCur);
    }
}
