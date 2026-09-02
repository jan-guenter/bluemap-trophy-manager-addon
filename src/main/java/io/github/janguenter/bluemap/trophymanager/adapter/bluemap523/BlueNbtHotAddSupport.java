/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.mca.MCAUtil;
import de.bluecolored.bluenbt.BlueNBT;
import de.bluecolored.bluenbt.NBTWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/** Keeps BlueMap's shared BlueNBT resolver usable after this late-loaded add-on. */
final class BlueNbtHotAddSupport {

    private static final List<String> RESOLVER_CACHES = List.of(
            "typeDeserializerMap",
            "typeResolverMap"
    );

    private BlueNbtHotAddSupport() {
    }

    /**
     * BlueNBT 3.5.1 snapshots resolver delegates on first use. Clear the exact
     * two private caches after registering this add-on's block-entity DTO.
     */
    static boolean refreshSharedDeserializerCache() {
        BlueNBT blueNbt = MCAUtil.BLUENBT;
        synchronized (blueNbt) {
            try {
                for (String name : RESOLVER_CACHES) {
                    Field field = BlueNBT.class.getDeclaredField(name);
                    if (!Map.class.isAssignableFrom(field.getType())) {
                        return false;
                    }
                    field.setAccessible(true);
                    Object value = field.get(blueNbt);
                    if (!(value instanceof Map<?, ?> cache)) {
                        return false;
                    }
                    cache.clear();
                }
                return true;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                return false;
            }
        }
    }

    /** Verifies persisted trophy data through BlueMap's shared parser. */
    static boolean retainsPersistedTrophyData() {
        try {
            BlockEntity decoded = MCAUtil.BLUENBT.read(
                    new ByteArrayInputStream(probe()), BlockEntity.class
            );
            if (!(decoded instanceof TrophyBlockEntityData data)
                    || data.trophyData() == null) {
                return false;
            }
            TrophyBlockEntityData.TrophyData trophy = data.trophyData();
            TrophyBlockEntityData.TrophyItem item = trophy.trophyItem();
            return "trophymanager:trophy".equals(data.getId().getFormatted())
                    && data.getX() == 17
                    && data.getY() == -23
                    && data.getZ() == 41
                    && "item".equals(trophy.trophyType())
                    && item != null
                    && "minecraft:diamond_block".equals(item.id())
                    && Integer.valueOf(1).equals(item.count())
                    && "minecraft:cut_sandstone_slab".equals(trophy.baseBlock());
        } catch (IOException | RuntimeException | LinkageError exception) {
            return false;
        }
    }

    private static byte[] probe() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.beginCompound();
            writer.name("id").value("trophymanager:trophy");
            writer.name("x").value(17);
            writer.name("y").value(-23);
            writer.name("z").value(41);
            writer.name("TrophyData").beginCompound();
            writer.name("TrophyType").value("item");
            writer.name("TrophyItem").beginCompound();
            writer.name("id").value("minecraft:diamond_block");
            writer.name("count").value(1);
            writer.endCompound();
            writer.name("OffsetY").value(0.5D);
            writer.name("RotX").value(0.0F);
            writer.name("Scale").value(0.5F);
            writer.name("BaseBlock").value("minecraft:cut_sandstone_slab");
            writer.endCompound();
            writer.endCompound();
        }
        return bytes.toByteArray();
    }
}
