package me.alexfischer.maxwellsdemon;

class Ball
{
    private final BallColor color;

    Vector loc, v;

    private Ball(BallColor color, Vector loc, Vector v)
    {
        this.color = color;
        this.loc = loc;
        this.v = v;
    }

    Ball(BallColor color)
    {
        this(color, new Vector(), new Vector());
    }

    public BallColor getColor()
    {
        return color;
    }
}
