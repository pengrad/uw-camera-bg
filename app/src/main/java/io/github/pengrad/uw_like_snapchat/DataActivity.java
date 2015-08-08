package io.github.pengrad.uw_like_snapchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * stas
 * 8/7/15
 */
public class DataActivity extends AppCompatActivity {

    public static final String EXTRA_BITMAP_INFO = "bitmap_info";

    public static Intent newIntent(Context context, BitmapColorInfo bitmapColorInfo) {
        Intent intent = new Intent(context, DataActivity.class);
        intent.putExtra(EXTRA_BITMAP_INFO, bitmapColorInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setTitle("Result activity");

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        BitmapColorInfo bitmapColorInfo = (BitmapColorInfo) getIntent().getSerializableExtra(EXTRA_BITMAP_INFO);

        initPixelsCount(bitmapColorInfo);
        initAvgColor(bitmapColorInfo);
        initCheckOnline();
    }

    private void initPixelsCount(BitmapColorInfo bitmapColorInfo) {
        TextView textView = (TextView) findViewById(R.id.textPixelsCount);
        textView.setText(String.valueOf(bitmapColorInfo.getPixelsConditionCount()));
    }

    private void initAvgColor(BitmapColorInfo bitmapColorInfo) {
        int avgColor = bitmapColorInfo.getAvgColor();
        View viewAvgColor = findViewById(R.id.viewAvgColor);
        viewAvgColor.setBackgroundColor(avgColor);

        TextView textAvgRGB = (TextView) findViewById(R.id.textAvgRGB);
        int red = Color.red(avgColor);
        int green = Color.green(avgColor);
        int blue = Color.blue(avgColor);
        textAvgRGB.setText(String.format("RGB (%d, %d, %d)", red, green, blue));
    }

    private void initCheckOnline() {
        final View progressView = findViewById(R.id.progressOnline);
        final TextView textOnline = (TextView) findViewById(R.id.textOnline);

        new AsyncTask<Void, NetworkStatus.Status, NetworkStatus.Status>() {
            @Override
            protected NetworkStatus.Status doInBackground(Void... params) {
                return NetworkStatus.checkNetwork(getApplicationContext());
            }

            @Override
            protected void onPostExecute(NetworkStatus.Status status) {
                progressView.setVisibility(View.GONE);
                if (status.networkType != null) {
                    if (status.isInternet) {
                        textOnline.setTextColor(getResources().getColor(R.color.green));
                        textOnline.setText("Online with " + status.networkType);
                    } else {
                        textOnline.setTextColor(getResources().getColor(R.color.red));
                        textOnline.setText("Offline with " + status.networkType);
                    }
                } else {
                    textOnline.setTextColor(getResources().getColor(R.color.red));
                    textOnline.setText("Offline");
                }
            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
