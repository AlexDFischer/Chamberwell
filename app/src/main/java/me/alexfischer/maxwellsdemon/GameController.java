package me.alexfischer.maxwellsdemon;

import android.graphics.Canvas;

public interface GameController
{
    boolean isVictorious();

    void update();

    /**
     * Returns the door position. 0 is all the way to the left, and 1 to the right.
     */
    double getDoorPosition();

    /**
     * Sets the door position. 0 is all the way to the left, and 1 to the right.
     * @param pos the door position
     */
    void setDoorPosition(double pos);

    void paint(Canvas canvas);
}
