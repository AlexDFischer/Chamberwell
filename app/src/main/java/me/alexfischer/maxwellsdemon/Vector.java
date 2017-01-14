package me.alexfischer.maxwellsdemon;

class Vector
{
    double x, y;

    Vector()
    {
        this(0, 0);
    }

    Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    void add(Vector v)
    {
        this.x += v.x;
        this.y += v.y;
    }

    void setArgument(double argument)
    {
        double norm = Math.sqrt(x*x+y*y);
        this.x = norm * Math.cos(argument);
        this.y = norm * Math.sin(argument);
    }

    void reflectAcrossXAxis()
    {
        this.y *= -1;
    }

    void reflectAcrossYAxis()
    {
        this.x *= -1;
    }
}
