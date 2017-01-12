package me.alexfischer.maxwellsdemon;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest;
        if (Static.TESTING)
        {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("EFDF1025DA1BE78404A3297B534892FE")
                    .build();
        } else
        {
            adRequest = new AdRequest.Builder().build();
        }
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ((MainBackgroundView) findViewById(R.id.mainBackgroundView)).pauseAnimation();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ((MainBackgroundView) findViewById(R.id.mainBackgroundView)).startAnimation();
    }

    public void onPlayButtonClick(View view)
    {
        Intent intent = new Intent(this, LevelsActivity.class);
        startActivity(intent);
    }

    public void onSettingsButtonClick(View view)
    {

    }

    public void onAboutButtonClick(View view)
    {

    }
}
