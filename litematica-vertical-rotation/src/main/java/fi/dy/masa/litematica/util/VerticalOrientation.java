package fi.dy.masa.litematica.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/** Right-handed rotations taking the original upward normal to the selected face. */
public enum VerticalOrientation
{
    UP(OrientationTransform.UP),
    NORTH(OrientationTransform.NORTH),
    SOUTH(OrientationTransform.SOUTH),
    EAST(OrientationTransform.EAST),
    WEST(OrientationTransform.WEST),
    DOWN(OrientationTransform.DOWN);

    private final OrientationTransform transform;

    VerticalOrientation(OrientationTransform transform)
    {
        this.transform = transform;
    }

    public Vec3 transformVector(Vec3 vector)
    {
        return new Vec3(OrientationTransform.component(vector.x, vector.y, vector.z, transform.xAxis()),
                OrientationTransform.component(vector.x, vector.y, vector.z, transform.yAxis()),
                OrientationTransform.component(vector.x, vector.y, vector.z, transform.zAxis()));
    }

    public BlockPos transform(Vec3i pos)
    {
        return new BlockPos(component(pos, transform.xAxis()), component(pos, transform.yAxis()), component(pos, transform.zAxis()));
    }

    private static int component(Vec3i pos, int axis)
    {
        return OrientationTransform.component(pos.getX(), pos.getY(), pos.getZ(), axis);
    }

    public BlockPos inverse(Vec3i pos)
    {
        int[] result = transform.inverse(pos.getX(), pos.getY(), pos.getZ());
        return new BlockPos(result[0], result[1], result[2]);
    }

    /** Entities rotate around the centre of the origin block, like vanilla yaw transforms. */
    public Vec3 transformPoint(Vec3 point)
    {
        return transformVector(point.subtract(0.5, 0.5, 0.5)).add(0.5, 0.5, 0.5);
    }

    public Direction transform(Direction direction)
    {
        BlockPos vector = transform(new BlockPos(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
        for (Direction candidate : Direction.values())
        {
            if (candidate.getStepX() == vector.getX() && candidate.getStepY() == vector.getY() &&
                candidate.getStepZ() == vector.getZ()) return candidate;
        }
        throw new IllegalStateException("Not an axis direction: " + vector);
    }

    public VerticalOrientation cycle(boolean reverse)
    {
        return values()[Math.floorMod(ordinal() + (reverse ? -1 : 1), values().length)];
    }

    public static VerticalOrientation fromName(String name)
    {
        try { return valueOf(name); }
        catch (IllegalArgumentException | NullPointerException e) { return UP; }
    }
}
