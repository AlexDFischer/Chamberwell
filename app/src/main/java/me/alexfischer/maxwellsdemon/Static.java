package me.alexfischer.maxwellsdemon;


import android.content.SharedPreferences;

class Static
{
    static final int CURRENT_LEVELS_VERSION = 0;

    static final int LEVEL_STATUS_REQUEST = 0;
    static final int LEVEL_STATUS_SUCCESS = 1;

    /**
     * Amount of time, in milliseconds, to wait in between updating the game
     */
    static final int UPDATE_DELAY = 33;

    /**
     * The amount of tilt of the phone that means the door should be all the way to the left/right.
     */
    static double MAX_TILT;

    /**
     * All of these are proportions of the canvas width
     */
    static final double BALL_RADIUS_PROPORTION = 0.03, WALL_WIDTH_PROPORTION = 0.02, BALL_SPEED_PROPORTION = 0.008;

    static boolean backButtonPauses = true;

    static void setSensitivity(int sensitivity)
    {
        if (sensitivity < 1 || sensitivity > 100)
        {
            throw new IllegalArgumentException("Sensitivity must be between 1 and 100 inclusive: " + sensitivity);
        }
        MAX_TILT = Math.PI / 2 * (1.0 - 0.9 * (sensitivity / 100.0));
    }

    static final String sensitivityString = "sensitivity", backPauseString = "backButtonPauses";

    static SharedPreferences pref;
    static SharedPreferences.Editor prefEditor;
}
