package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: document your custom view class.
 */
public class MainBackgroundView extends View
{
    private Paint myPaint = new Paint();

    private GameController customGC = new GameControllerImpl(3, 3, 3, "red;blue;", 0.333f);
    private Timer timer;
    private TimerTask task = new TimerTask()
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
        this.isRunning = true;
    }

    public void pauseAnimation()
    {
        this.isRunning = false;
    }
}
