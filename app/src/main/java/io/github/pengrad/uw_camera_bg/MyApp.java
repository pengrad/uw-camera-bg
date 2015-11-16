package io.github.pengrad.uw_camera_bg;

import android.app.Application;
import android.content.Intent;

/**
 * Stas Parshin
 * 16 November 2015
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, CameraService.class));
    }
}
