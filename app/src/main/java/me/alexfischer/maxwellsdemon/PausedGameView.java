package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class PausedGameView extends View
{
    private boolean dimensionsSetUp = false;

    private int WALL_WIDTH, WIDTH, HEIGHT, TEXT_HEIGHT;

    private Paint wallPaint = new Paint(), bgPaint = new Paint(), textPaint = new Paint();

    private final String message;

    private Rect RECT = new Rect();

    public PausedGameView(Context context)
    {
        super(context);
        this.message = context.getString(R.string.pause_message);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        if (dimensionsSetUp)
        {
            canvas.drawRect(0f, 0f, (float)WIDTH, (float)HEIGHT, bgPaint);

            // paint the walls
            canvas.drawRect(0f, 0f,(float)WALL_WIDTH, (float)HEIGHT , wallPaint);
            canvas.drawRect(0f, 0f,(float)WIDTH, (float)WALL_WIDTH, wallPaint);
            canvas.drawRect(0f, HEIGHT - WALL_WIDTH, WIDTH, HEIGHT, wallPaint);
            canvas.drawRect(WIDTH - WALL_WIDTH, 0f,(float)WIDTH, (float)HEIGHT, wallPaint);

            // paint the text
            canvas.drawText(message, WIDTH / 2, (HEIGHT + TEXT_HEIGHT) / 2, textPaint);
        } else
        {
            dimensionsSetUp = true;
            WIDTH = canvas.getWidth();
            HEIGHT = canvas.getHeight();
            WALL_WIDTH = (int)(Static.WALL_WIDTH_PROPORTION * WIDTH);

            wallPaint.setARGB(255, 0, 0, 0);
            bgPaint.setARGB(255, 224, 170, 255);
            textPaint.setARGB(255, 0, 0, 0);

            // determine proper size of paused message
            for (textPaint.setTextSize(1.0f); RECT.width() < WIDTH - 4 * WALL_WIDTH; textPaint.setTextSize(textPaint.getTextSize() + 1.0f))
            {
                textPaint.getTextBounds(message, 0, message.length(), RECT);
            }
            textPaint.setTextSize(textPaint.getTextSize() - 1.0f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            TEXT_HEIGHT = RECT.height();

            this.draw(canvas);
        }
    }
}
