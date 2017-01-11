package me.alexfischer.maxwellsdemon;

public class Ball
{
    private final BallColor color;

    public Vector loc, v;

    public Ball(BallColor color, Vector loc, Vector v)
    {
        this.color = color;
        this.loc = loc;
        this.v = v;
    }

    public Ball(BallColor color)
    {
        this(color, new Vector(), new Vector());
    }

    public BallColor getColor()
    {
        return color;
    }
}
