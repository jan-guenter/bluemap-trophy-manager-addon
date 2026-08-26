/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.BlockState;

import java.util.Optional;

/** Validated deterministic item-trophy snapshot derived from persisted NBT. */
record TrophyRenderPlan(
        String baseBlock,
        String itemBlock,
        float offsetY,
        float scale,
        float rotationXDegrees,
        float yawDegrees
) {

    private static final String DEFAULT_BASE = "minecraft:smooth_stone_slab";
    private static final float DEFAULT_OFFSET_Y = 0.5F;
    private static final float DEFAULT_SCALE = 0.5F;

    static Optional<TrophyRenderPlan> decode(
            TrophyBlockEntityData data,
            BlockState host
    ) {
        if (data == null || data.trophyData() == null) {
            return Optional.empty();
        }
        TrophyBlockEntityData.TrophyData trophy = data.trophyData();
        TrophyBlockEntityData.TrophyItem item = trophy.trophyItem();
        return create(
                trophy.trophyType(),
                item == null ? null : item.id(),
                item == null ? null : item.count(),
                trophy.baseBlock(),
                trophy.offsetY(),
                trophy.scale(),
                trophy.rotationX(),
                host.getProperties().get("facing")
        );
    }

    static Optional<TrophyRenderPlan> create(
            String type,
            String itemId,
            Integer count,
            String baseBlock,
            Double offsetY,
            Float scale,
            Float rotationX,
            String facing
    ) {
        if (!"item".equals(type)
                || !validId(itemId)
                || (count != null && count <= 0)) {
            return Optional.empty();
        }
        String base = validId(baseBlock) ? baseBlock : DEFAULT_BASE;
        float y = offsetY == null ? DEFAULT_OFFSET_Y : offsetY.floatValue();
        float size = scale == null ? DEFAULT_SCALE : scale;
        float pitch = rotationX == null ? 0F : rotationX;
        if (!Float.isFinite(y) || y < -4F || y > 4F
                || !Float.isFinite(size) || size < 0.05F || size > 4F
                || !Float.isFinite(pitch) || pitch < -3600F || pitch > 3600F) {
            return Optional.empty();
        }
        float yaw = switch (facing) {
            case "east" -> 90F;
            case "north" -> 180F;
            case "west" -> 270F;
            default -> 0F;
        };
        return Optional.of(new TrophyRenderPlan(base, itemId, y, size, pitch, yaw));
    }

    private static boolean validId(String value) {
        return value != null
                && value.length() <= 256
                && value.matches("[a-z0-9_.-]+:[a-z0-9_./-]+");
    }
}
