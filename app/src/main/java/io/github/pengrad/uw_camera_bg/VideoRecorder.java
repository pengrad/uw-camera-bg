package io.github.pengrad.uw_camera_bg;

import android.hardware.Camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Stas Parshin
 * 16 November 2015
 */
public class VideoRecorder implements Camera.PictureCallback {

    private List<VideoRecorderCallbacks> mCallbacksList = new ArrayList<>();
    private boolean isRecording = false;
    private Camera camera;
    private Subscription cameraSubscription;
    private final File fileDir;

    public VideoRecorder(File fileDir) {
        this.fileDir = fileDir;
    }

    public boolean init() {return true;}

    public boolean close() {return true;}

    public boolean isReady() {return true;}

    public List<Quality> getAvailableQualities() {
        throw new RuntimeException("not implemented");
    }

    public boolean isRecording() {
        return isRecording;
    }

    // Return null or throw exception if failed
    public void startRecording(Quality quality) {
        for (VideoRecorderCallbacks callback : mCallbacksList) {
            callback.startedStreaming(quality);
        }
        doStartRecording(quality);
    }


    public void stopRecording() {
        for (VideoRecorderCallbacks callback : mCallbacksList) {
            callback.stoppedStreaming();
        }
        doStopRecording();
    }

    protected void doStartRecording(Quality quality) {
        camera = openBackCamera();
        if (camera == null) {
            throw new RuntimeException("Camera not found");
        }
        cameraSubscription = Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    public void call(Long aLong) {
                        camera.takePicture(null, null, VideoRecorder.this);
                    }
                });
        isRecording = true;
    }

    protected void doStopRecording() {
        if (camera != null) {
            camera.release();
        }
        if (cameraSubscription != null && !cameraSubscription.isUnsubscribed()) {
            cameraSubscription.unsubscribe();
        }
        isRecording = false;
    }

    private Camera openBackCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cam = Camera.open(camIdx);
            }
        }
        return cam;
    }

    @Override
    public void onPictureTaken(final byte[] data, final Camera camera) {
        new Thread(new Runnable() {
            long time = new Date().getTime();

            @Override
            public void run() {
                try {
                    String filename = time + ".jpg";
                    FileOutputStream outputStream = new FileOutputStream(new File(fileDir, filename));
                    outputStream.write(data);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addVideoRecorderCallback(VideoRecorderCallbacks callback) {
        mCallbacksList.add(callback);
    }

    public void removeVideoRecorderCallback(VideoRecorderCallbacks callback) {
        mCallbacksList.remove(callback);
    }

    // Should be called from some inner thread of VideoRecorder
    interface VideoRecorderCallbacks {
        void startedStreaming(Quality quality);

        void stoppedStreaming();

        // In case of error, errorStreaming to be called before stoppedStreaming
        void errorStreaming();
    }

    class Quality {
        public int mHeight;
        public int mWidth;
        public int mFPS;
    }
}
