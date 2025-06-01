package com.example.myapplication.data.model;
import android.content.Context;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class YOLOModel {
    public Interpreter interpreter;
    public List<String> classes = new ArrayList<>();
    public int[] inputShape;
    public int[] outputShape;

    public YOLOModel(Context context, String modelPath, String classesPath) {
        try {
            interpreter = new Interpreter(loadModelFile(context, modelPath));
            inputShape = interpreter.getInputTensor(0).shape();
            outputShape = interpreter.getOutputTensor(0).shape();
            if (classesPath != null) loadClasses(context, classesPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,
                context.getAssets().openFd(modelPath).getStartOffset(),
                context.getAssets().openFd(modelPath).getDeclaredLength());
    }

    private void loadClasses(Context context, String classesPath) {
        try {
            InputStream inputStream = context.getAssets().open(classesPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                classes.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
