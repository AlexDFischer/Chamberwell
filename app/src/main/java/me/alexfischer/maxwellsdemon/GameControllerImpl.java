package me.alexfischer.maxwellsdemon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

import static me.alexfischer.maxwellsdemon.Static.*;

import static me.alexfischer.maxwellsdemon.BallColor.RED;

class GameControllerImpl implements GameController
{
    private Random r = new Random(System.currentTimeMillis());

    private Paint wallPaint = new Paint();

    private double doorPosition;



    private double doorWidth;

    /**
     * All of these are in pixels
     */
    private int WIDTH, HEIGHT, BALL_RADIUS, WALL_WIDTH, DOOR_RADIUS;

    private BallColor[] chamberColors;

    private int[] doorHeights;

    private Paint redFG = new Paint(), redBG = new Paint(), purpleFG = new Paint(), purpleBG = new Paint(), blueFG = new Paint(), blueBG = new Paint();

    private boolean dimensionsSetUp = false;

    private Ball[] balls;

    GameControllerImpl(int numRedBalls, int numPurpleBalls, int numBlueBalls, String chambers, double doorWidth)
    {
        if (chambers == null)
        {
            throw new NullPointerException("Can't create GameController with null chambers string");
        }

        wallPaint.setARGB(255, 0, 0, 0);
        wallPaint.setTextSize(50.0f);
        redFG.setARGB(255, 255, 0, 0);
        redBG.setARGB(255, 255, 192, 192);
        purpleFG.setARGB(255, 170, 0, 192);
        purpleBG.setARGB(255, 224, 170, 255);
        blueFG.setARGB(255, 0, 0, 255);
        blueBG.setARGB(255, 192, 192, 255);

        // parse the chambers string
        if (chambers.length() == 0)
        {
            throw new IllegalArgumentException("Invalid chambers string: " + chambers);
        }
        String[] chambersParsed = chambers.split(";");
        this.chamberColors = new BallColor[chambersParsed.length];
        for (int i = 0; i < chambersParsed.length; i++)
        {
            switch (chambersParsed[i])
            {
                case "red":
                    chamberColors[i] = RED;
                    break;
                case "purple":
                    chamberColors[i] = BallColor.PURPLE;
                    break;
                case "blue":
                    chamberColors[i] = BallColor.BLUE;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid chambers string: " + chambers);
            }
        }

        // set up door heights
        doorHeights = new int[chamberColors.length - 1];

        // set up the balls array
        this.balls = new Ball[numRedBalls + numPurpleBalls + numBlueBalls];
        for (int i = 0; i < numRedBalls; i++)
        {
            balls[i] = new Ball(BallColor.RED);
        }
        for (int i = numRedBalls; i < numRedBalls + numPurpleBalls; i++)
        {
            balls[i] = new Ball(BallColor.PURPLE);
        }
        for (int i = numRedBalls + numPurpleBalls; i < numRedBalls + numPurpleBalls + numBlueBalls; i++)
        {
            balls[i] = new Ball(BallColor.BLUE);
        }

        this.doorWidth = doorWidth;
    }

    /**
     * Tells whether or not the player has won the game.
     * @return true if every ball is in a chamber corresponding to its color, false otherwise.
     */
    @Override
    public boolean isVictorious()
    {
        if (!dimensionsSetUp)
        {
            return false;
        }
        for (Ball ball : balls)
        {
            int chamberNum = chamberColors.length * ((int)ball.loc.y - WALL_WIDTH) / (HEIGHT - 2 * WALL_WIDTH);
            if (chamberColors[chamberNum] != ball.getColor())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void update()
    {
        for (Ball ball : balls)
        {
            ball.loc.add(ball.v);
        }

        // do collision check for each wall individually
        // LEFT WALL
        for (Ball ball : balls)
        {
            if (ball.loc.x - BALL_RADIUS <= WALL_WIDTH)
            {
                double backtrack = WALL_WIDTH - ball.loc.x + BALL_RADIUS;
                ball.loc.x += 2 * backtrack;
                ball.v.reflectAcrossYAxis();
            }
        }
        // TOP WALL
        for (Ball ball : balls)
        {
            if (ball.loc.y - BALL_RADIUS <= WALL_WIDTH)
            {
                double backtrack = WALL_WIDTH - ball.loc.y + BALL_RADIUS;
                ball.loc.y += 2 * backtrack;
                ball.v.reflectAcrossXAxis();
            }
        }
        // RIGHT WALL
        for (Ball ball : balls)
        {
            if (ball.loc.x + BALL_RADIUS >= WIDTH - WALL_WIDTH)
            {
                double backTrack = ball.loc.x + BALL_RADIUS - WIDTH + WALL_WIDTH;
                ball.loc.x -= 2 * backTrack;
                ball.v.reflectAcrossYAxis();
            }
        }
        // BOTTOM WALL
        for (Ball ball : balls)
        {
            if (ball.loc.y + BALL_RADIUS >= HEIGHT - WALL_WIDTH)
            {
                double backTrack = ball.loc.y + BALL_RADIUS - HEIGHT + WALL_WIDTH;
                ball.loc.y -= 2 * backTrack;
                ball.v.reflectAcrossXAxis();
            }
        }

        // now, check each door individually
        int farthestLeftCenterCanBe = WALL_WIDTH + DOOR_RADIUS;
        int farthestRightCenterCanBe = WIDTH - WALL_WIDTH - DOOR_RADIUS;
        int center = (int)(farthestLeftCenterCanBe + doorPosition * (farthestRightCenterCanBe - farthestLeftCenterCanBe));
        int leftSurface = center - DOOR_RADIUS, rightSurface = center + DOOR_RADIUS;
        for (int doorHeight : doorHeights)
        {
            // SMALL LEFT SURFACE
            for (Ball ball : balls)
            {
                if  (
                        ball.loc.x - BALL_RADIUS <= leftSurface &&
                        ball.loc.y >= doorHeight - WALL_WIDTH / 2 &&
                        ball.loc.y <= doorHeight + WALL_WIDTH / 2
                    )
                {
                    double backtrack = leftSurface - ball.loc.x + BALL_RADIUS;
                    ball.loc.x += 2 * backtrack;
                    ball.v.reflectAcrossYAxis();
                }
            }
            // TOP SURFACE
            for (Ball ball : balls)
            {
                if  (
                        ball.loc.y + BALL_RADIUS >= doorHeight - WALL_WIDTH / 2 &&
                        ball.loc.y + BALL_RADIUS <= doorHeight + WALL_WIDTH / 2 &&
                        ball.v.y > 0 &&
                        (ball.loc.x <= leftSurface || ball.loc.x >= rightSurface)
                    )
                {
                    double backtrack = ball.loc.y + BALL_RADIUS - doorHeight + WALL_WIDTH / 2;
                    ball.loc.y -= 2 * backtrack;
                    ball.v.reflectAcrossXAxis();
                }
            }
            // BOTTOM SURFACE
            for (Ball ball : balls)
            {
                if  (
                        ball.loc.y - BALL_RADIUS <= doorHeight + WALL_WIDTH / 2 &&
                        ball.loc.y - BALL_RADIUS >= doorHeight - WALL_WIDTH / 2 &&
                        ball.v.y < 0 &&
                        (ball.loc.x <= leftSurface || ball.loc.x >= rightSurface)
                    )
                {
                    double backtrack = doorHeight + WALL_WIDTH / 2 - ball.loc.y + BALL_RADIUS;
                    ball.loc.y += 2 * backtrack;
                    ball.v.reflectAcrossXAxis();
                }
            }
        }
    }

    @Override
    public void setDoorPosition(double doorPosition)
    {
        if (0 <= doorPosition && doorPosition <= 1)
        {
            this.doorPosition = doorPosition;
        } else
        {
            throw new IllegalArgumentException("Invalid door position (must be between 0 and 1 inclusive): " + doorPosition);
        }
    }

    @Override
    public void paint(Canvas canvas)
    {
        if (dimensionsSetUp)
        {
            //Log.d("adf", "dimensions now are " + canvas.getWidth() + " x " + canvas.getHeight());

            // paint the background colors of the chambers
            int availableSpace = HEIGHT - 2 * WALL_WIDTH;
            for (int i = 0; i < chamberColors.length; i++)
            {
                Paint paint = null;
                switch (chamberColors[i])
                {
                    case RED:
                        paint = this.redBG;
                        break;
                    case PURPLE:
                        paint = this.purpleBG;
                        break;
                    case BLUE:
                        paint = this.blueBG;
                        break;
                }
                canvas.drawRect(WALL_WIDTH, WALL_WIDTH + availableSpace * (i) / (chamberColors.length), WIDTH - WALL_WIDTH, WALL_WIDTH + availableSpace * (i + 1) / (chamberColors.length), paint);
            }

            // paint the walls
            canvas.drawRect(0f, 0f,(float)WALL_WIDTH, (float)HEIGHT , wallPaint);
            canvas.drawRect(0f, 0f,(float)WIDTH, (float)WALL_WIDTH, wallPaint);
            canvas.drawRect(0f, HEIGHT - WALL_WIDTH, WIDTH, HEIGHT, wallPaint);
            canvas.drawRect(WIDTH - WALL_WIDTH, 0f,(float)WIDTH, (float)HEIGHT, wallPaint);

            // paint the doors
            int farthestLeftCenterCanBe = WALL_WIDTH + DOOR_RADIUS;
            int farthestRightCenterCanBe = WIDTH - WALL_WIDTH - DOOR_RADIUS;
            int center = (int)(farthestLeftCenterCanBe + doorPosition * (farthestRightCenterCanBe - farthestLeftCenterCanBe));
            for (int doorHeight : doorHeights)
            {
                canvas.drawRect(WALL_WIDTH, doorHeight - WALL_WIDTH / 2, center - DOOR_RADIUS, doorHeight + WALL_WIDTH / 2, wallPaint);
                canvas.drawRect(center + DOOR_RADIUS, doorHeight - WALL_WIDTH / 2, WIDTH - WALL_WIDTH, doorHeight + WALL_WIDTH / 2, wallPaint);
            }

            // paint the balls
            for (Ball ball : balls)
            {
                paintBall(canvas, ball);
            }
        } else // first time we get to the paint method, set up the dimensions of the game
        {
            Log.d("adf", "first dimensions are " + canvas.getWidth() + " x " + canvas.getHeight());
            WIDTH = canvas.getWidth();
            HEIGHT = canvas.getHeight();
            BALL_RADIUS = (int)(BALL_RADIUS_PROPORTION * WIDTH);
            double ballSpeed = Static.BALL_SPEED_PROPORTION * WIDTH;
            WALL_WIDTH = (int)(WALL_WIDTH_PROPORTION * WIDTH);
            DOOR_RADIUS = (int)(doorWidth * WIDTH / 2);
            dimensionsSetUp = true;

            // finish setting up door heights
            int availableSpace = HEIGHT - 2 * WALL_WIDTH;
            for (int i = 0; i < doorHeights.length; i++)
            {
                doorHeights[i] = WALL_WIDTH + availableSpace * (i + 1) / (doorHeights.length + 1);
            }

            // finish setting up balls
            int xRange = WIDTH - 2 * (WALL_WIDTH + BALL_RADIUS);
            int yRange = HEIGHT - 2 * (WALL_WIDTH + BALL_RADIUS);
            for (Ball ball : balls)
            {
                ball.loc = new Vector();
                do
                {
                    ball.loc.x = WALL_WIDTH + BALL_RADIUS + r.nextInt(xRange);
                    ball.loc.y = WALL_WIDTH + BALL_RADIUS + r.nextInt(yRange);
                } while (!isValidStart(ball));
               ball.v = new Vector(0, ballSpeed);
                // set the argument to be somewhat vertical so we don't get horizontally moving balls
                double argument = (Math.PI / 6 + 2 * Math.PI / 3 * r.nextDouble()) * (r.nextBoolean() ? 1 : -1);
                ball.v.setArgument(argument);
            }
            paint(canvas);
        }
    }

    /**
     * Determines whether or not the ball is in a valid starting location. In order for that to be
     * true, the ball must be in a chamber that is not its own color, and it must not be in a
     * position that could touch any walls (including some wiggle room) regardless of the door
     * position.
     *
     * Precondition: the ball is not in a position where it could touch any side walls.
     * @param ball the ball to check
     * @return true if it's a valid start, false otherwise
     */
    private boolean isValidStart(Ball ball)
    {
        // first make sure it's away from the doors
        for (int doorHeight : doorHeights)
        {
            if (doorHeight - WALL_WIDTH - BALL_RADIUS < ball.loc.y && ball.loc.y < doorHeight + WALL_WIDTH + BALL_RADIUS)
            {
                return false;
            }
        }
        // now make sure it's in a different colored chamber
        int chamberNum = chamberColors.length * ((int)ball.loc.y - WALL_WIDTH) / (HEIGHT - 2 * WALL_WIDTH);
        return chamberColors[chamberNum] != ball.getColor();
    }

    private void paintBall(Canvas canvas, Ball ball)
    {
        switch (ball.getColor())
        {
            case RED:
                canvas.drawCircle((float)(ball.loc.x), (float)(ball.loc.y), BALL_RADIUS, redFG);
                break;
            case PURPLE:
                canvas.drawCircle((float)(ball.loc.x), (float)(ball.loc.y), BALL_RADIUS, purpleFG);
                break;
            case BLUE:
                canvas.drawCircle((float)(ball.loc.x), (float)(ball.loc.y), BALL_RADIUS, blueFG);
                break;
        }
    }

    @Override
    public void closeDoor()
    {
        this.DOOR_RADIUS = 0;
    }
}
