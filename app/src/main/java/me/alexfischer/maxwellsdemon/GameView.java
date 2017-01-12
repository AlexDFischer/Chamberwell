package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameView extends View
{
    private GameController gc;

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
    }

    public void setGameController(GameController controller)
    {
        this.gc = controller;
    }
}
