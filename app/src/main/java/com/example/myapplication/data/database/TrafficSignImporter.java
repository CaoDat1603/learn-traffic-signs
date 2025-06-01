package com.example.myapplication.data.database;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.data.model.TrafficSign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TrafficSignImporter {

    public static void importFromAssets(Context context) {
        try {
            TrafficSignDBHelper dbHelper = new TrafficSignDBHelper(context);

            // Nếu đã có dữ liệu thì không import nữa
            if (!dbHelper.getTrafficSigns("all").isEmpty()) {
                Log.d("SIGN_CHECK", "Dữ liệu đã tồn tại, bỏ qua import.");
                return;
            }

            InputStream is = context.getAssets().open("traffic_signs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray signsArray = jsonObject.getJSONArray("traffic_signs");

            for (int i = 0; i < signsArray.length(); i++) {
                JSONObject obj = signsArray.getJSONObject(i);
                TrafficSign sign = new TrafficSign(
                        obj.getString("id"),
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getString("image"),
                        obj.getString("type"),
                        obj.getString("status")
                );
                dbHelper.insertTrafficSign(sign);
            }

            Log.d("SIGN_CHECK", "Import hoàn tất: " + signsArray.length() + " biển báo.");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}