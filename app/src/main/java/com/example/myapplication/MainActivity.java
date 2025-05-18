package com.example.myapplication;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private LinearLayout layoutStart;
    private Button buttonStart;
    private RelativeLayout layoutDetect;
    private TextureView textureViewCamera;
    private ImageView imageViewDetection;

    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private YOLOModel yoloModel;
    private YOLODetector yoloDetector;

    private HandlerThread yoloThread;
    private Handler yoloHandler;
    private Handler mainHandler;

    private boolean isDetecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutStart = findViewById(R.id.linearLayout_start);
        buttonStart = findViewById(R.id.button_start);
        layoutDetect = findViewById(R.id.relativeLayout_detect);
        textureViewCamera = findViewById(R.id.textureView_camera);
        imageViewDetection = findViewById(R.id.imageView_detection);

        buttonStart.setOnClickListener(v -> start());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                start();
            } else {
                Snackbar.make(findViewById(R.id.button_start), "Camera permission denied!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    void start() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
            return;
        }
        layoutStart.setVisibility(View.GONE);
        layoutDetect.setVisibility(View.VISIBLE);
        initThreads();
        startCameraStream();
    }

    void initThreads() {
        yoloThread = new HandlerThread("YoloThread");
        yoloThread.start();
        yoloHandler = new Handler(yoloThread.getLooper());
        mainHandler = new Handler(getMainLooper());
    }

    void startCameraStream() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
            return;
        }

        textureViewCamera.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}
            @Override public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) { return true; }
            @Override public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
        });
    }

    void openCamera() {
        if (!checkCameraPermission()) return;
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreviewSession();
                    loadModel();
                }

                @Override public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }

                @Override public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                }
            }, mainHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createCameraPreviewSession() {
        imageViewDetection.setVisibility(View.INVISIBLE);
        textureViewCamera.setVisibility(View.VISIBLE);

        try {
            SurfaceTexture surfaceTexture = textureViewCamera.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(640, 640);
            Surface surface = new Surface(surfaceTexture);

            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) return;
                    cameraCaptureSession = session;
                    try {
                        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mainHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
            }, mainHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadModel() {
        if (yoloModel == null) yoloModel = new YOLOModel(this, Constants.MODEL_PATH, Constants.CLASSES_PATH);
        if (yoloDetector == null) yoloDetector = new YOLODetector(yoloModel);

        Snackbar.make(findViewById(R.id.button_start), "Model loaded", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        startRealtimeDetection();
    }

    void startRealtimeDetection() {
        isDetecting = true;
        yoloHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isDetecting) return;
                Bitmap frame = textureViewCamera.getBitmap(320, 320);
                if (frame != null && yoloDetector != null) {
                    List<YOLODetection> detections = yoloDetector.detectObjects(frame);
                    Bitmap outputImage = drawBoundingBox(frame, detections);
                    mainHandler.post(() -> drawDetectionBitmap(outputImage));
                }
                yoloHandler.postDelayed(this, 100); // ~10 FPS
            }
        });
    }

    void stopRealtimeDetection() {
        isDetecting = false;
        yoloHandler.removeCallbacksAndMessages(null);
    }

    public Bitmap drawBoundingBox(Bitmap bitmap, List<YOLODetection> detections) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        for (YOLODetection detection : detections) {
            float x = detection.box_x;
            float y = detection.box_y;
            float width = detection.box_width;
            float height = detection.box_height;

            float left = x - width / 2;
            float top = y - height / 2;
            float right = x + width / 2;
            float bottom = y + height / 2;

            paint.setStrokeWidth(8);
            canvas.drawRect(new RectF(left, top, right, bottom), paint);

            String label = detection.className + " (" + String.format("%.2f", detection.confidence * 100) + "%)";
            paint.setStrokeWidth(2);
            paint.setTextSize(28);
            canvas.drawText(label, left, top - 10, paint);
        }

        return mutableBitmap;
    }

    void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    boolean checkCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    void drawDetectionBitmap(Bitmap bitmap) {
        imageViewDetection.setVisibility(View.VISIBLE);
        imageViewDetection.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        stopRealtimeDetection();
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (yoloThread != null) {
            yoloThread.quitSafely();
        }
        super.onDestroy();
    }
}
