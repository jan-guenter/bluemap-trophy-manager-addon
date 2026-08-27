/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap522;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrophyRenderPlanTest {

    @Test
    void decodesBoundedBlockItemSnapshotAndFacing() {
        TrophyRenderPlan plan = TrophyRenderPlan.create(
                "item",
                "minecraft:diamond_block",
                1,
                "minecraft:cut_sandstone_slab",
                0.75D,
                0.6F,
                15F,
                "east"
        ).orElseThrow();

        assertEquals("minecraft:cut_sandstone_slab", plan.baseBlock());
        assertEquals(TrophyRenderPlan.Subject.BLOCK_ITEM, plan.subject());
        assertEquals("minecraft:diamond_block", plan.subjectId());
        assertEquals(0.75F, plan.offsetY());
        assertEquals(0.6F, plan.scale());
        assertEquals(15F, plan.rotationXDegrees());
        assertEquals(90F, plan.yawDegrees());
    }

    @Test
    void decodesBoundedCreeperSnapshotAndFacing() {
        TrophyRenderPlan plan = TrophyRenderPlan.createEntity(
                "minecraft:creeper",
                false,
                "minecraft:brick_slab",
                0.5D,
                0.5F,
                0F,
                "north"
        ).orElseThrow();

        assertEquals("minecraft:brick_slab", plan.baseBlock());
        assertEquals(TrophyRenderPlan.Subject.CREEPER, plan.subject());
        assertEquals("minecraft:creeper", plan.subjectId());
        assertEquals(180F, plan.yawDegrees());
    }

    @Test
    void rejectsUnsupportedMalformedAndUnboundedSnapshots() {
        assertTrue(TrophyRenderPlan.create(
                "entity", "minecraft:creeper", 1, null, null, null, null, "north"
        ).isEmpty());
        assertTrue(TrophyRenderPlan.createEntity(
                "minecraft:bee", null, null, null, null, null, "south"
        ).isEmpty());
        assertTrue(TrophyRenderPlan.createEntity(
                "minecraft:creeper", true, null, null, null, null, "south"
        ).isEmpty());
        assertTrue(TrophyRenderPlan.create(
                "item", "bad id", 1, null, null, null, null, "south"
        ).isEmpty());
        assertTrue(TrophyRenderPlan.create(
                "item", "minecraft:stone", 1, null, 0D, 10F, null, "south"
        ).isEmpty());
    }
}
