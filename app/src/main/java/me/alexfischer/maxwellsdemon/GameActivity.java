package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements SensorEventListener
{
    private GameView gameView;
    private PausedGameView pausedGameView;
    private int level;
    private SensorManager sensorManager;
    Sensor sensor;
    private GameController gc;
    private Timer timer;
    private volatile boolean isRunning = false, alreadyKnowItsVictorious = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // keeps the screen from sleeping when playing the game
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.pausedGameView = new PausedGameView(this);

        sensorManager = (SensorManager)(getSystemService(Context.SENSOR_SERVICE));
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Log.d("adf", "gameFrameLayout size is " + findViewById(R.id.gameFrameLayout).getWidth() + " X " + findViewById(R.id.gameFrameLayout).getHeight());
        Intent intent = getIntent();
        this.level = intent.getIntExtra("level", -1);

        this.gc = new GameControllerImpl(
                    intent.getIntExtra("numRedBalls", 0),
                    intent.getIntExtra("numPurpleBalls", 0),
                    intent.getIntExtra("numBlueBalls", 0),
                    intent.getStringExtra("chambers"),
                    intent.getFloatExtra("doorWidth", 0.5f)
                    );

        setTitle(getString(R.string.game_activity_title).replace("*", Integer.toString(level)));
        this.gameView = new GameView(this);
        this.gameView.setGameController(gc);
        this.startGame();
    }

    @Override
    public void onBackPressed()
    {
        if (Static.backButtonPauses)
        {
            if (isRunning)
            {
                pauseGame();
            } else
            {
                startGame();
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (isRunning)
        {
            this.onPauseButtonClick(findViewById(R.id.pauseButton));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i) { }

    @Override
    public void onSensorChanged(SensorEvent e)
    {
        if (isRunning)
        {
            double gravityNorm = Math.sqrt(e.values[0]*e.values[0]+e.values[1]*e.values[1]+e.values[2]*e.values[2]); // should be fairly close to 9.8
            double angle = -Math.acos(-e.values[0] / gravityNorm) + Math.PI / 2;
            // only use values between -MAX_TILT and MAX_TILT, otherwise use min or max
            angle = Math.max(-Static.MAX_TILT, angle);
            angle = Math.min(Static.MAX_TILT, angle);
            double doorPosition = (angle + Static.MAX_TILT) / (2 * Static.MAX_TILT);
            // because floating point math can be weird:
            doorPosition = Math.max(0, doorPosition);
            doorPosition = Math.min(1, doorPosition);
            gc.setDoorPosition(doorPosition);
        }
    }

    public void startGame()
    {
        if (gc == null)
        {
            throw new IllegalStateException("Haven't set game controller yet, so can't start");
        }
        FrameLayout gameFrame = (FrameLayout)(findViewById(R.id.gameFrameLayout));
        gameFrame.removeAllViews();
        gameFrame.addView(gameView);
        timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (isRunning)
                {
                    gc.update();
                    gameView.postInvalidate();
                    if (!alreadyKnowItsVictorious && gc.isVictorious())
                    {
                        alreadyKnowItsVictorious = true;
                        gc.closeDoor();
                        Static.prefEditor.putBoolean("level" + level, true);
                        Static.prefEditor.apply();
                        new Timer().schedule(new TimerTask() { @Override public void run() { success(); } }, 2000);
                    }
                }
            }
        };
        timer.schedule(task, 0, Static.UPDATE_DELAY);
        isRunning = true;
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        Button button = (Button)findViewById(R.id.pauseButton);
        button.setText(getString(R.string.pause_button_text));
    }

    public void stop()
    {
        if (!isRunning)
        {
            throw new IllegalStateException("Can't stop while not running");
        }
        timer.cancel();
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    private void pauseGame()
    {
        if (!isRunning)
        {
            throw new IllegalStateException("Can only pauseGame when game is running");
        }
        timer.cancel();
        isRunning = false;
        sensorManager.unregisterListener(this);
        FrameLayout gameFrame = (FrameLayout)(findViewById(R.id.gameFrameLayout));
        gameFrame.removeAllViews();
        gameFrame.addView(pausedGameView);
        Button button = (Button)(findViewById(R.id.pauseButton));
        button.setText(getString(R.string.unpause_button_text));
    }

    public void success()
    {
        Intent data = new Intent();
        data.putExtra("level", level);
        setResult(Static.LEVEL_STATUS_SUCCESS, data);
        this.stop();
        finish();
    }

    public void onPauseButtonClick(View view)
    {
        if (isRunning)
        {
            pauseGame();
        } else
        {
            this.startGame();
        }
    }

    public void onQuitButtonClick(View view)
    {
        if (isRunning)
        {
            this.stop();
        }
        this.finish();
    }
}
