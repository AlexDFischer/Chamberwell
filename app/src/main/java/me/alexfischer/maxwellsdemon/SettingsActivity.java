package me.alexfischer.maxwellsdemon;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity
{
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

        SeekBar seekBar = (SeekBar)(findViewById(R.id.sensitivityBar));
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SensitivityListener());
        seekBar.setProgress(Static.pref.getInt(Static.sensitivityString, 50));

        CheckBox checkBox = (CheckBox)(findViewById(R.id.backPauseCheckBox));
        checkBox.setOnCheckedChangeListener(new BackPauseListener());
        checkBox.setChecked(Static.pref.getBoolean(Static.backPauseString, true));
        checkBox.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

    private class BackPauseListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton button, boolean value)
        {
            CheckBox checkBox = (CheckBox)(findViewById(R.id.backPauseCheckBox));
            Static.prefEditor.putBoolean(Static.backPauseString, checkBox.isChecked());
            Static.prefEditor.apply();
            Static.backButtonPauses = checkBox.isChecked();
        }
    }

    private class SensitivityListener implements SeekBar.OnSeekBarChangeListener
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int sensitivity, boolean bool)
        {
            Static.prefEditor.putInt(Static.sensitivityString, sensitivity);
            Static.prefEditor.apply();
            Static.setSensitivity(sensitivity);
            ((TextView)findViewById(R.id.sensitivityLabel)).setText(getString(R.string.settings_sensitivity_text).replace("*", Integer.toString(sensitivity)));
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
    }
}
