package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View
{
    private GameController gc;

    public GameView(Context context)
    {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
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
