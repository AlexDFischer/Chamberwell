package me.alexfischer.maxwellsdemon;

import android.graphics.Canvas;

interface GameController
{
    boolean isVictorious();

    void update();

    /**
     * Makes the door width 0.
     */
    void closeDoor();

    /**
     * Sets the door position. 0 is all the way to the left, and 1 to the right.
     * @param pos the door position
     */
    void setDoorPosition(double pos);

    void paint(Canvas canvas);
}
