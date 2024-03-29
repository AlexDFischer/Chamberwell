package me.alexfischer.maxwellsdemon;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
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
        Static.prefEditor.putInt("levelsVersion", CURRENT_LEVELS_VERSION);
        // true = unlocked, false = locked
        Static.prefEditor.putBoolean("level1", true);
        for (int level = 2; level <= NUM_LEVELS; level++)
        {
            lockLevel(level);
        }
        Static.prefEditor.apply();
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
        Static.prefEditor.putBoolean("level" + level, false);
        Static.prefEditor.apply();
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
        Static.prefEditor.putBoolean("level" + level, true);
        Static.prefEditor.apply();
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
                if (levelNum < NUM_LEVELS)
                {
                    unlockLevel(levelNum + 1);
                }
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
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setTitle(getString(R.string.levels_activity_title));

        // get number of levels
        this.NUM_LEVELS = ((GridLayout)findViewById(R.id.levelsGrid)).getChildCount();
        levelStatuses = new boolean[NUM_LEVELS + 1];

        // manage level (un)locking
        if (Static.pref.contains("levelsVersion")) // app should have level statuses stored
        {
            if (Static.pref.getInt("levelsVersion", 0) == CURRENT_LEVELS_VERSION)
            {
                for (int level = 1; level <= NUM_LEVELS; level++)
                {
                    if (Static.pref.getBoolean("level" + level, false))
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
        } else // no level statuses stored
        {
            lockAllLevels();
            unlockLevel(1);
        }
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
}
