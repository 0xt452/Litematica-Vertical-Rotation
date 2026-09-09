package fi.dy.masa.litematica.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Runs without Minecraft dependencies; throws on failure even when JVM assertions are disabled. */
public class OrientationTransformTest
{
    private static int checks;
    private static void check(boolean value, String message)
    {
        ++checks;
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args)
    {
        OrientationTransform[] transforms = {OrientationTransform.UP, OrientationTransform.NORTH,
                OrientationTransform.SOUTH, OrientationTransform.EAST, OrientationTransform.WEST, OrientationTransform.DOWN};
        int[][] normals = {{0,1,0}, {0,0,-1}, {0,0,1}, {1,0,0}, {-1,0,0}, {0,-1,0}};
        for (int index = 0; index < transforms.length; ++index)
        {
            OrientationTransform t = transforms[index];
            check(Arrays.equals(normals[index], t.transform(0, 1, 0)), "Wrong outward face");
            int[] xAxis = t.transform(1,0,0), yAxis = t.transform(0,1,0), zAxis = t.transform(0,0,1);
            int[] cross = {xAxis[1]*yAxis[2]-xAxis[2]*yAxis[1], xAxis[2]*yAxis[0]-xAxis[0]*yAxis[2], xAxis[0]*yAxis[1]-xAxis[1]*yAxis[0]};
            check(Arrays.equals(cross, zAxis), "Rotation must not reflect/mirror the artwork");
            for (int x = -17; x <= 17; x += 3)
            for (int y = -9; y <= 9; y += 2)
            for (int z = -20; z <= 20; z += 4)
            {
                int[] dest = t.transform(x,y,z);
                check(Arrays.equals(new int[]{x,y,z}, t.inverse(dest[0],dest[1],dest[2])), "Inverse failed");
            }
            // Signed subregion extents, including one-block axes and multi-layer map art.
            for (int sx : new int[]{-5,-1,1,5})
            for (int sy : new int[]{-3,-1,1,3})
            for (int sz : new int[]{-7,-1,1,7})
            {
                int[] size = {sx,sy,sz};
                int[] newSize = t.transform(sx,sy,sz);
                int[] min = minimum(size), newMin = minimum(newSize);
                Set<String> visited = new HashSet<>();
                for (int x = 0; x < Math.abs(sx); ++x)
                for (int y = 0; y < Math.abs(sy); ++y)
                for (int z = 0; z < Math.abs(sz); ++z)
                {
                    int[] dest = t.transform(x+min[0], y+min[1], z+min[2]);
                    for (int axis = 0; axis < 3; ++axis)
                    {
                        dest[axis] -= newMin[axis];
                        check(dest[axis] >= 0 && dest[axis] < Math.abs(newSize[axis]), "Block outside rotated container");
                    }
                    check(visited.add(Arrays.toString(dest)), "Two blocks collided");
                }
                check(visited.size() == Math.abs(sx*sy*sz), "Block count changed");
            }
        }
        // An asymmetric 3x1x2 panel: front remains distinguishable from its back.
        check(Arrays.equals(OrientationTransform.NORTH.transform(2,0,1), new int[]{2,1,0}), "North panel");
        check(Arrays.equals(OrientationTransform.EAST.transform(2,0,1), new int[]{0,-2,1}), "East panel");
        check(Arrays.equals(OrientationTransform.DOWN.transform(2,0,1), new int[]{2,0,-1}), "Upside-down panel");
        System.out.println("PASS: " + checks + " orientation geometry checks");
    }

    private static int[] minimum(int[] size)
    {
        int[] min = new int[3];
        for (int i = 0; i < 3; ++i) min[i] = Math.min(0, size[i] - Integer.signum(size[i]));
        return min;
    }
}
