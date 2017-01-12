package me.alexfischer.maxwellsdemon;


class Static
{
    static final int CURRENT_LEVELS_VERSION = 0;

    static final int LEVEL_STATUS_REQUEST = 0;
    static final int LEVEL_STATUS_SUCCESS = 1;
    static final int LEVEL_STATUS_FAILURE = 2;

    /**
     * Amount of time, in milliseconds, to wait in between updating the game
     */
    static final int UPDATE_DELAY = 50;

    /**
     * The amount of tilt of the phone that means the door should be all the way to the left/right. Currently 45 degrees
     */
    static final double MAX_TILT = Math.PI / 4;

    /**
     * All of these are proportions of the canvas width
     */
    static final double BALL_RADIUS_PROPORTION = 0.03, WALL_WIDTH_PROPORTION = 0.01;

    /**
     * For ad purposes
     */
    static final boolean TESTING = true;
}
