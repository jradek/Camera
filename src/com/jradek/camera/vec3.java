package com.jradek.camera;

public class vec3 {
    final public static double SMALL_EPSILON = 0.0000000001;

    public double x;
    public double y;
    public double z;

    public vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public vec3 add(final vec3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    public vec3 substract(final vec3 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        return this;
    }

    public vec3 addScaled(final vec3 other, float scale) {
        return new vec3(
                x + other.x * scale,
                y + other.y * scale,
                z + other.z * scale);
    }

    @Override
    public vec3 clone() {
        return new vec3(x, y, z);
    }

    public double dot(final vec3 rhs)
    {
        return x*rhs.x + y*rhs.y + z*rhs.z;
    }

    public vec3 cross(final vec3 rhs) {
        return new vec3(y*rhs.z - z*rhs.y, z*rhs.x - x*rhs.z, x*rhs.y - y*rhs.x);
    }

    public static vec3 cross(final vec3 lhs, final vec3 rhs) {
        return lhs.cross(rhs);
    }

    public double distanceTo(final vec3 v) {
        final double dx = x - v.x;
        final double dy = y - v.y;
        final double dz = z - v.z;

        return Math.sqrt(dx*dx + dy*dy + dz * dz);
    }

    public static double distance(final vec3 a, final vec3 b) {
       return a.distanceTo(b);
    }

    public vec3 normalize() {
        double len = length();
        if(Math.abs(len) < SMALL_EPSILON) throw new java.lang.Error();

        scale(1.0/len);
        return this;
    }

    public vec3 scale(double factor) {
        x *= factor;
        y *= factor;
        z *= factor;
        return this;
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }
}
