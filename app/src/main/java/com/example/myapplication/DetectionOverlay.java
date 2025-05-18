package com.example.myapplication;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DetectionOverlay extends View {
    private final Paint boxPaint = new Paint();
    private final Paint textPaint = new Paint();
    private List<YOLODetection> detections = new ArrayList<>();

    public DetectionOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(6);

        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(40);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setDetections(List<YOLODetection> detections) {
        this.detections = detections;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (YOLODetection d : detections) {
            float left = d.box_x - d.box_width / 2f;
            float top = d.box_y - d.box_height / 2f;
            float right = d.box_x + d.box_width / 2f;
            float bottom = d.box_y + d.box_height / 2f;
            canvas.drawRect(left, top, right, bottom, boxPaint);
            canvas.drawText(d.className + " " + String.format("%.2f", d.confidence), left, top - 10, textPaint);
        }
    }
}