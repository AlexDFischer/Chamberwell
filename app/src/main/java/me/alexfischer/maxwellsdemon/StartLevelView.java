package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class StartLevelView extends View
{
    private boolean unlocked = false;
    public int level, numRedBalls, numPurpleBalls, numBlueBalls;
    public String chambers;
    public float doorWidth;

    private Rect rect = new Rect(0, 0, 0, 0);

    private Paint unlockedPaint = new Paint(), lockedPaint = new Paint(), secondaryBackgroundPaint = new Paint();

    public StartLevelView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public StartLevelView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Initiates the startLevelView
     * @param attrs the AttributeSet of this View
     */
    private void init(AttributeSet attrs)
    {
        unlockedPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.textColor));
        unlockedPaint.setTypeface(Typeface.MONOSPACE);
        /*Typeface calibri = Typeface.createFromFile("fonts/calibri.ttf");
        unlockedPaint.setTypeface(calibri);*/ // TODO font is not working
        lockedPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.secondaryTextColor));
        secondaryBackgroundPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.secondaryBackgroundColor));

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StartLevelView,
                0, 0);
        this.level = a.getInt(R.styleable.StartLevelView_level, 0);
        this.numRedBalls = a.getInt(R.styleable.StartLevelView_numRedBalls, 0);
        this.numPurpleBalls = a.getInt(R.styleable.StartLevelView_numPurpleBalls, 0);
        this.numBlueBalls = a.getInt(R.styleable.StartLevelView_numBlueBalls, 0);
        this.chambers = a.getString(R.styleable.StartLevelView_chambers);
        this.doorWidth = a.getFloat(R.styleable.StartLevelView_doorWidth, 0.5f);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1, secondaryBackgroundPaint);
        if (!unlocked)
        {
            int stroke = (int)(0.05 * canvas.getWidth());
            int minWidthHeight = Math.min(canvas.getHeight(), canvas.getWidth());
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, minWidthHeight / 2 - 4 * stroke, lockedPaint);
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, minWidthHeight / 2 - 5 * stroke, secondaryBackgroundPaint);
            canvas.drawRect(canvas.getWidth() / 2 - minWidthHeight / 2 + 2 * stroke, canvas.getHeight() / 2, canvas.getWidth() / 2 + minWidthHeight / 2 - 2 * stroke, canvas.getHeight() / 2 + minWidthHeight / 2 - 2 *stroke, lockedPaint);
        } else
        {
            // find the right font size to draw the level number
            unlockedPaint.setTextSize(1.0f);
            while (-1 * unlockedPaint.getFontMetrics().ascent < 0.6 * canvas.getHeight())
            {
                unlockedPaint.setTextSize(unlockedPaint.getTextSize() + 5.0f);
            }
            unlockedPaint.setTextAlign(Paint.Align.CENTER);
            float textHeight;
            unlockedPaint.getTextBounds(Integer.toString(getLevel()), 0, 1, rect);
            textHeight = rect.height();
            float margin = (canvas.getHeight() - textHeight) / 2;
            canvas.drawText(Integer.toString(getLevel()), canvas.getWidth() / 2, canvas.getHeight() - 1 - margin, unlockedPaint);

        }
    }

    public boolean isUnlocked()
    {
        return unlocked;
    }

    public int getLevel()
    {
        return level;
    }

    public void setUnlocked(boolean unlocked)
    {
        this.unlocked = unlocked;
    }
}
