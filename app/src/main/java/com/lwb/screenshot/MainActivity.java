package com.lwb.screenshot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    ScreenShotView screenShotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenShotView = new ScreenShotView(MainActivity.this);
        screenShotView.setColors(new int[]{0xffffff00, 0xffff0000, 0xff00ff00, 0xff0000ff});
        screenShotView.setVisibility(View.GONE);
        findViewById(R.id.test_screenshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenShotView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (screenShotView.getVisibility() == View.VISIBLE) {
            screenShotView.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }
}
