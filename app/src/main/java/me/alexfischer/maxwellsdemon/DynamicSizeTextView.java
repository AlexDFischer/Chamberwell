package me.alexfischer.maxwellsdemon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class DynamicSizeTextView extends View
{
    private float margin;

    private int WIDTH, HEIGHT, TEXT_HEIGHT;

    private String text;

    private Paint textPaint = new Paint();

    private boolean dimensionsSetUp = false;

    private Rect RECT = new Rect();

    public DynamicSizeTextView(Context context)
    {
        super(context);
        init(null);
    }

    public DynamicSizeTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public DynamicSizeTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {
        textPaint.setARGB(255, 0, 0, 0);
        if (attrs != null)
        {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.DynamicSizeTextView,
                    0, 0);
            this.text = a.getString(R.styleable.DynamicSizeTextView_text);
            if (this.text == null)
            {
                this.text = "";
            }
            this.margin = a.getFloat(R.styleable.DynamicSizeTextView_margin, 0f);
            if (this.margin < 0f || this.margin >= 1.0f)
            {
                throw new IllegalArgumentException("margin must be a proportion of the width, so it must be nonnegative and less than 1: " + this.margin);
            }
        } else
        {
            this.text = "";
            this.margin = 0f;
        }
    }

    void setText(String text)
    {
        this.text = text;
        this.postInvalidate();
    }

    String getText()
    {
        return this.text;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (dimensionsSetUp)
        {
            canvas.drawText(text, WIDTH / 2, (HEIGHT + TEXT_HEIGHT) / 2, textPaint);
        } else
        {
            dimensionsSetUp = true;
            WIDTH = canvas.getWidth();
            HEIGHT = canvas.getHeight();
            int marginInt = (int)(this.margin * WIDTH);

            // determine proper size of text
            for (textPaint.setTextSize(1.0f); RECT.width() < WIDTH - 2 * marginInt; textPaint.setTextSize(textPaint.getTextSize() + 1.0f))
            {
                textPaint.getTextBounds(this.text, 0, this.text.length(), RECT);
            }
            textPaint.setTextSize(textPaint.getTextSize() - 1.0f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            TEXT_HEIGHT = RECT.height();

            this.draw(canvas);
        }
    }
}
