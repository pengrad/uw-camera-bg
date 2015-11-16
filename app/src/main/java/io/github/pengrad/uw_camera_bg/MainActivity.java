package io.github.pengrad.uw_camera_bg;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    View buttonStart, buttonStop;
    CameraService cameraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        bindService(new Intent(this, CameraService.class), this, 0);
    }

    void start() {
        cameraService.startRecording();
    }

    void stop() {
        cameraService.stopRecording();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(true);
        cameraService = ((CameraService.CameraBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
        cameraService = null;
    }
}
