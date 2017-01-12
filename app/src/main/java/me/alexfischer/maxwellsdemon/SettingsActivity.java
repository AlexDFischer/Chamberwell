package me.alexfischer.maxwellsdemon;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setTitle(getString(R.string.settings_activity_title));

        NumberPicker sensitivityPicker = (NumberPicker)(findViewById(R.id.sensitivityPicker));
        sensitivityPicker.setMinValue(1);
        sensitivityPicker.setMaxValue(100);
        sensitivityPicker.setValue(Static.pref.getInt(Static.sensitivityString, 50));
        Log.d("adf", "does shared preferences have sensitivity? " + Static.pref.contains(Static.sensitivityString));
        sensitivityPicker.setOnValueChangedListener(new SensitivityListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    public void onLockAllLevelsClick(View view)
    {
        for (int level = 2; Static.pref.contains("level" + level); level++)
        {
            Static.prefEditor.putBoolean("level" + level, false);
        }
        Static.prefEditor.apply();
    }

    private class SensitivityListener implements NumberPicker.OnValueChangeListener
    {
        @Override
        public void onValueChange(NumberPicker sensitivityPicker, int oldVal, int newVal)
        {
            Static.prefEditor.putInt(Static.sensitivityString, sensitivityPicker.getValue());
            Static.prefEditor.apply();
            Static.setSensitivity(sensitivityPicker.getValue());
            Log.d("adf", "does shared preferences have sensitivity? " + Static.pref.contains(Static.sensitivityString));
        }
    }
}
