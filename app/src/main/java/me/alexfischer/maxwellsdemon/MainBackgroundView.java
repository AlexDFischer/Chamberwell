package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MainBackgroundView extends View
{

    private GameController customGC = new GameControllerImpl(3, 3, 3, "red;blue;", 0.333f);
    private Timer timer;
    private volatile boolean isRunning = false;

    public MainBackgroundView(Context context)
    {
        super(context);
        init();
    }

    public MainBackgroundView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MainBackgroundView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        customGC.setDoorPosition(0.5);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        customGC.paint(canvas);
    }

    public void startAnimation()
    {
        this.timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (isRunning)
                {
                    MainBackgroundView.this.customGC.update();
                    MainBackgroundView.this.postInvalidate();
                }
            }
        };
        this.timer.schedule(task, 0, Static.UPDATE_DELAY);
        this.isRunning = true;
    }

    public void pauseAnimation()
    {
        this.timer.cancel();
        this.isRunning = false;
    }
}
