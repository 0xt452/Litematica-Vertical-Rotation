package fi.dy.masa.litematica.util;

/** Dependency-free signed axis permutation, shared by block and entity transforms. */
public record OrientationTransform(int xAxis, int yAxis, int zAxis)
{
    public static final OrientationTransform UP = new OrientationTransform(1, 2, 3);
    public static final OrientationTransform NORTH = new OrientationTransform(1, 3, -2);
    public static final OrientationTransform SOUTH = new OrientationTransform(1, -3, 2);
    public static final OrientationTransform EAST = new OrientationTransform(2, -1, 3);
    public static final OrientationTransform WEST = new OrientationTransform(-2, 1, 3);
    public static final OrientationTransform DOWN = new OrientationTransform(1, -2, -3);

    public OrientationTransform
    {
        int mask = 0;
        for (int axis : new int[] {xAxis, yAxis, zAxis})
        {
            if (axis == 0 || Math.abs(axis) > 3) throw new IllegalArgumentException("Invalid axis");
            mask |= 1 << Math.abs(axis);
        }
        if (mask != 14) throw new IllegalArgumentException("Repeated axis");
    }

    public static int component(int x, int y, int z, int axis)
    {
        int value = switch (Math.abs(axis)) { case 1 -> x; case 2 -> y; default -> z; };
        return value * Integer.signum(axis);
    }

    public static double component(double x, double y, double z, int axis)
    {
        double value = switch (Math.abs(axis)) { case 1 -> x; case 2 -> y; default -> z; };
        return value * Integer.signum(axis);
    }

    public int[] transform(int x, int y, int z)
    {
        return new int[] {component(x, y, z, xAxis), component(x, y, z, yAxis), component(x, y, z, zAxis)};
    }

    public int[] inverse(int x, int y, int z)
    {
        int[] result = new int[3];
        result[Math.abs(xAxis) - 1] = x * Integer.signum(xAxis);
        result[Math.abs(yAxis) - 1] = y * Integer.signum(yAxis);
        result[Math.abs(zAxis) - 1] = z * Integer.signum(zAxis);
        return result;
    }
}
