package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.content.Intent;
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

        // advertising setup
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest;
        if (Static.TESTING)
        {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("EFDF1025DA1BE78404A3297B534892FE") // my Galaxy S5
                    .build();
        } else
        {
            adRequest = new AdRequest.Builder().build();
        }
        mAdView.loadAd(adRequest);

        // shared preferences setup
        Static.pref = getPreferences(Context.MODE_PRIVATE);
        Static.prefEditor = Static.pref.edit();
        Static.prefEditor.apply(); // we don't actually need to do this, just so we don't get a warning for calling edit() without commit() or apply()

        // sensitivity setup
        int sensitivity = Static.pref.getInt(Static.sensitivityString, 50);
        Static.setSensitivity(sensitivity);

        // back button pausing.unpausing setup
        Static.backButtonPauses = Static.pref.getBoolean(Static.backPauseString, true);
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
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onAboutButtonClick(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
