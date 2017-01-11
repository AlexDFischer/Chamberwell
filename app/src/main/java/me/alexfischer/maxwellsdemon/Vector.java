package me.alexfischer.maxwellsdemon;

public class Vector
{
    public double x, y;

    public Vector()
    {
        this(0, 0);
    }

    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void add(Vector v)
    {
        this.x += v.x;
        this.y += v.y;
    }

    public double argument()
    {
        return Math.atan2(y, x);
    }

    public double norm()
    {
        return Math.sqrt(x*x+y*y);
    }

    public void setArgument(double argument)
    {
        double norm = norm();
        this.x = norm * Math.cos(argument);
        this.y = norm * Math.sin(argument);
    }

    public void reflectAcrossXAxis()
    {
        this.y *= -1;
    }

    public void reflectAcrossYAxis()
    {
        this.x *= -1;
    }
}
