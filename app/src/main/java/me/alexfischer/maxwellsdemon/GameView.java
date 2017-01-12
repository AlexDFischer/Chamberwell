package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class GameView extends View implements SensorEventListener
{
    private SensorManager sensorManager = (SensorManager)(getContext().getSystemService(Context.SENSOR_SERVICE));
    Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    private GameController gc;
    private Timer timer;
    private volatile boolean isRunning = false;

    public GameView(Context context)
    {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        Log.d("adf", "dimensions of GameView are " + getWidth() + " x " + getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        gc.paint(canvas);
        //Log.d("adf", "onDraw for GameView called");
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

    public void setGameController(GameController controller)
    {
        if (timer != null)
        {
            timer.cancel();
        }
        this.gc = controller;

        timer = new Timer();

    }

    public void start()
    {
        if (gc == null)
        {
            throw new IllegalStateException("Haven't set game controller yet, so can't start");
        }
        timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (isRunning)
                {
                    GameView.this.gc.update();
                    GameView.this.postInvalidate();
                }
            }
        };
        timer.schedule(task, 0, Static.UPDATE_DELAY);
        isRunning = true;
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop()
    {
        if (gc == null)
        {
            throw new IllegalStateException("Haven't set game controller yet, so can't stop");
        }
        timer.cancel();
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    public void pause()
    {
        if (gc == null)
        {
            throw new IllegalStateException("Haven't set game controller yet, so can't pause");
        }
        timer.cancel();
        isRunning = false;
        sensorManager.unregisterListener(this);
    }
}
