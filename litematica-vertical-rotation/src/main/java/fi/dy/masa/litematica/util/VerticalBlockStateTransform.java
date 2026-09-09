package fi.dy.masa.litematica.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** Rotate representable direction/axis properties without inventing invalid Minecraft states. */
public final class VerticalBlockStateTransform
{
    private VerticalBlockStateTransform() {}

    public static BlockState transform(BlockState state, VerticalOrientation orientation)
    {
        if (orientation == VerticalOrientation.UP) return state;
        BlockState result = state;
        for (Property<?> property : state.getProperties())
        {
            result = transformProperty(state, result, property, orientation);
        }
        return result;
    }

    private static <T extends Comparable<T>> BlockState transformProperty(
            BlockState source, BlockState result, Property<T> property, VerticalOrientation orientation)
    {
        T value = source.getValue(property);
        Object rotated = value;
        if (value instanceof Direction direction)
        {
            rotated = orientation.transform(direction);
        }
        else if (value instanceof Direction.Axis axis)
        {
            Direction direction = switch (axis) { case X -> Direction.EAST; case Y -> Direction.UP; case Z -> Direction.SOUTH; };
            rotated = orientation.transform(direction).getAxis();
        }
        for (T candidate : property.getPossibleValues())
        {
            if (orientation == VerticalOrientation.DOWN &&
                (property.getName().equals("half") || property.getName().equals("type")))
            {
                String oldName = property.getName(value);
                String newName = property.getName(candidate);
                if ((oldName.equals("top") && newName.equals("bottom")) ||
                    (oldName.equals("bottom") && newName.equals("top"))) return result.setValue(property, candidate);
            }
        }
        for (T candidate : property.getPossibleValues())
        {
            if (candidate.equals(rotated)) return result.setValue(property, candidate);
        }
        return result;
    }
}
