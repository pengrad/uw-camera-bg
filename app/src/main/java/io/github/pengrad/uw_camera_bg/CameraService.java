package io.github.pengrad.uw_camera_bg;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.widget.Toast;

public class CameraService extends Service {

    public class CameraBinder extends Binder {
        public CameraService getService() {
            return CameraService.this;
        }
    }

    VideoRecorder mVideoRecorder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mVideoRecorder = new VideoRecorder(getFilesDir());
        return START_STICKY;
    }

    @Override
    public CameraBinder onBind(Intent intent) {
        return new CameraBinder();
    }

    public void startRecording() {
        mVideoRecorder.startRecording(null);
        Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show();
    }

    public void stopRecording() {
        mVideoRecorder.stopRecording();
        Toast.makeText(this, "Stop recording", Toast.LENGTH_SHORT).show();
    }
}
