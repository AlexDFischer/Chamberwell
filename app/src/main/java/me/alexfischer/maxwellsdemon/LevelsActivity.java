package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static me.alexfischer.maxwellsdemon.Static.*;

public class LevelsActivity extends AppCompatActivity
{
    /**
     * Number of levels. Determined by the number of StartLevelViews in the GridLayout
     */
    private int NUM_LEVELS;

    /**
     * true = unlocked, false = locked
     */
    private boolean[] levelStatuses;

    /**
     * Locks all the levels, except for level 1. To be called when the app is
     * launched for the first time.
     */
    private void lockAllLevels()
    {
        SharedPreferences.Editor pref = getPreferences(Context.MODE_PRIVATE).edit();
        pref.putInt("levelsVersion", CURRENT_LEVELS_VERSION);
        // true = unlocked, false = locked
        pref.putBoolean("level1", true);
        for (int level = 2; level <= NUM_LEVELS; level++)
        {
            lockLevel(level);
        }
        pref.apply();
    }

    /**
     * Tells whether the given level is locked or unlocked
     * @param level the level to check
     * @return true if unlocked, false if locked
     */
    private boolean isUnlocked(int level)
    {
        if (level < 1 || level > NUM_LEVELS)
        {
            throw new IllegalArgumentException("No such level: " + level);
        }
        return levelStatuses[level];
    }

    /**
     * Locks the given level
     * @param level level number to lock, from 1 to NUM_LEVELS
     */
    private void lockLevel(int level)
    {
        if (level < 1 || level > NUM_LEVELS)
        {
            throw new IllegalArgumentException("Can't lock level " + level);
        }
        SharedPreferences.Editor pref = getPreferences(Context.MODE_PRIVATE).edit();
        pref.putBoolean("level" + level, false);
        pref.apply();
        levelStatuses[level] = false;
        StartLevelView startLevelView = (StartLevelView)((GridLayout)(this.findViewById(R.id.levelsGrid))).getChildAt(level - 1);
        startLevelView.setUnlocked(false);
        startLevelView.invalidate();
    }

    /**
     * Unlocks the given level
     * @param level the level number to unlock, from 1 to NUM_LEVELS
     */
    private void unlockLevel(int level)
    {
        if (level < 1 || level > NUM_LEVELS)
        {
            throw new IllegalArgumentException("Can't lock level " + level);
        }
        SharedPreferences.Editor pref = getPreferences(Context.MODE_PRIVATE).edit();
        pref.putBoolean("level" + level, true);
        pref.apply();
        levelStatuses[level] = true;
        StartLevelView startLevelView = (StartLevelView)((GridLayout)(this.findViewById(R.id.levelsGrid))).getChildAt(level - 1);
        startLevelView.setUnlocked(true);
        startLevelView.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Static.LEVEL_STATUS_REQUEST && data != null)
        {
            int levelNum = data.getIntExtra("level", -1);
            if (resultCode == Static.LEVEL_STATUS_SUCCESS) // succeeded at the level
            {
                Log.d("adf", "succeeded at level " + levelNum);
                if (levelNum < NUM_LEVELS)
                {
                    unlockLevel(levelNum + 1);
                }
            } else // failed at the level
            {
                Log.d("adf", "failed at level " + levelNum);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        // set up advertising
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

        // Back button
        ActionBar actionBar = getSupportActionBar();
        Log.d("adf", "is action bar null? " + ((actionBar == null) ? "yes" : "no"));
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setTitle("Levels");

        // get number of levels
        this.NUM_LEVELS = ((GridLayout)findViewById(R.id.levelsGrid)).getChildCount();
        levelStatuses = new boolean[NUM_LEVELS + 1];
        Log.d("adf", "There are " + NUM_LEVELS + " levels.");

        // manage level (un)locking

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if (pref.contains("levelsVersion")) // app should have level statuses stored
        {
            if (pref.getInt("levelsVersion", 0) == CURRENT_LEVELS_VERSION)
            {
                for (int level = 1; level <= NUM_LEVELS; level++)
                {
                    if (pref.getBoolean("level" + level, false))
                    {
                        unlockLevel(level);
                    } else
                    {
                        lockLevel(level);
                    }
                }
            } else
            {
                lockAllLevels();
                unlockLevel(1);
            }
            Log.d("adf", "locked levels not for first time");
        } else // no level statuses stored
        {
            lockAllLevels();
            unlockLevel(1);
            Log.d("adf", "locked levels for first time");
        }
        Log.d("adf", "done with oncreate");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    public void onStartLevelClick(View view)
    {
        if (!(view instanceof StartLevelView))
        {
            return;
        }
        StartLevelView mView = (StartLevelView)view;
        if (mView.isUnlocked())
        {
            Log.d("adf", "playing level " + mView.getLevel());
            Intent gameIntent = new Intent(this, GameActivity.class);
            gameIntent.putExtra("level", mView.getLevel());
            gameIntent.putExtra("numRedBalls", mView.numRedBalls);
            gameIntent.putExtra("numPurpleBalls", mView.numPurpleBalls);
            gameIntent.putExtra("numBlueBalls", mView.numBlueBalls);
            gameIntent.putExtra("chambers", mView.chambers);
            gameIntent.putExtra("doorWidth", mView.doorWidth);
            startActivityForResult(gameIntent, Static.LEVEL_STATUS_REQUEST);
        }
    }

    public void onClickLockLevelsButton(View view)
    {
        lockAllLevels();
        unlockLevel(1);
    }
}
