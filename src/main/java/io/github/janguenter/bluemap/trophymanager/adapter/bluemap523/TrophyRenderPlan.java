/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.BlockState;

import java.util.Optional;

/** Validated deterministic trophy snapshot derived from persisted NBT. */
record TrophyRenderPlan(
        String baseBlock,
        Subject subject,
        String subjectId,
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
        String facing = host.getProperties().get("facing");
        if ("item".equals(trophy.trophyType())) {
            TrophyBlockEntityData.TrophyItem item = trophy.trophyItem();
            return create(
                    trophy.trophyType(),
                    item == null ? null : item.id(),
                    item == null ? null : item.count(),
                    trophy.baseBlock(),
                    trophy.offsetY(),
                    trophy.scale(),
                    trophy.rotationX(),
                    facing
            );
        }
        if ("entity".equals(trophy.trophyType())) {
            TrophyBlockEntityData.TrophyEntity entity = trophy.trophyEntity();
            return createEntity(
                    entity == null ? null : entity.entityType(),
                    entity == null ? null : entity.powered(),
                    trophy.baseBlock(),
                    trophy.offsetY(),
                    trophy.scale(),
                    trophy.rotationX(),
                    facing
            );
        }
        return Optional.empty();
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
        return common(
                Subject.BLOCK_ITEM, itemId, baseBlock,
                offsetY, scale, rotationX, facing
        );
    }

    static Optional<TrophyRenderPlan> createEntity(
            String entityType,
            Boolean powered,
            String baseBlock,
            Double offsetY,
            Float scale,
            Float rotationX,
            String facing
    ) {
        if (!"minecraft:creeper".equals(entityType) || Boolean.TRUE.equals(powered)) {
            return Optional.empty();
        }
        return common(
                Subject.CREEPER, entityType, baseBlock,
                offsetY, scale, rotationX, facing
        );
    }

    private static Optional<TrophyRenderPlan> common(
            Subject subject,
            String subjectId,
            String baseBlock,
            Double offsetY,
            Float scale,
            Float rotationX,
            String facing
    ) {
        String base = validId(baseBlock) ? baseBlock : DEFAULT_BASE;
        float y = offsetY == null ? DEFAULT_OFFSET_Y : offsetY.floatValue();
        float size = scale == null ? DEFAULT_SCALE : scale;
        float pitch = rotationX == null ? 0F : rotationX;
        if (!Float.isFinite(y) || y < -4F || y > 4F
                || !Float.isFinite(size) || size < 0.05F || size > 4F
                || !Float.isFinite(pitch) || pitch < -3600F || pitch > 3600F) {
            return Optional.empty();
        }
        float yaw = yaw(facing);
        return Optional.of(new TrophyRenderPlan(
                base, subject, subjectId, y, size, pitch, yaw
        ));
    }

    private static float yaw(String facing) {
        return switch (facing) {
            case "east" -> 90F;
            case "north" -> 180F;
            case "west" -> 270F;
            default -> 0F;
        };
    }

    enum Subject {
        BLOCK_ITEM,
        CREEPER
    }

    private static boolean validId(String value) {
        return value != null
                && value.length() <= 256
                && value.matches("[a-z0-9_.-]+:[a-z0-9_./-]+");
    }
}
