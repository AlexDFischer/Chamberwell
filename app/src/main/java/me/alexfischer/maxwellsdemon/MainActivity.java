package me.alexfischer.maxwellsdemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ((MainBackgroundView)findViewById(R.id.mainBackgroundView)).pauseAnimation();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ((MainBackgroundView)findViewById(R.id.mainBackgroundView)).startAnimation();
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
