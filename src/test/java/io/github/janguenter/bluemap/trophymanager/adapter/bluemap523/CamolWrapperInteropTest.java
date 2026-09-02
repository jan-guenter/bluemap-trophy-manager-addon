/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.ArrayTileModel;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.map.mask.Mask;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.PackVersion;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.DimensionType;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.biome.Biome;
import de.bluecolored.bluemap.core.world.block.BlockAccess;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import io.github.janguenter.bluemap.trophymanager.activation.AddonRuntime;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CamolWrapperInteropTest {

    private static final Key TROPHY = Key.parse("trophymanager:trophy");
    private static final BlockState HOST = new BlockState(TROPHY, Map.of(
            "facing", "south", "waterlogged", "false"
    ));

    @Test
    void lateGlobalWrapperStillDispatchesBaseAndSubject() throws Exception {
        ResourcePack pack = new ResourcePack(new PackVersion(34, 0));
        TextureGallery textures = new TextureGallery();
        TestSettings settings = new TestSettings();
        AtomicInteger stockCalls = new AtomicInteger();
        AtomicInteger lateWrapperCalls = new AtomicInteger();

        BlockRendererType stock = emittingType(
                "test:trophy-stock", 12, stockCalls, 0.2F, 0.2F, 0.2F, 1F
        );
        BlockRendererType base = emittingType(
                "test:base", 2, null, 1F, 0F, 0F, 0.5F
        );
        BlockRendererType subject = emittingType(
                "test:subject", 3, null, 0F, 0F, 1F, 0.75F
        );

        Variant trophyVariant = variant(pack, TROPHY, "test:block/trophy", stock);
        Variant baseVariant = variant(
                pack, Key.parse("minecraft:smooth_stone_slab"),
                "test:block/base", base
        );
        Variant subjectVariant = variant(
                pack, Key.parse("minecraft:diamond_block"),
                "test:block/subject", subject
        );

        BlockRendererType trophyWrapper = new BlockRendererType.Impl(
                Key.parse("test:trophy-wrapper"),
                (ignoredPack, ignoredTextures, ignoredSettings) ->
                        (block, variant, target, color) -> target.add(99)
        );
        VariantRendererCatalog trophyCatalog = VariantRendererCatalog.wrap(
                pack, TROPHY, trophyWrapper
        );

        List<Variant> all = List.of(trophyVariant, baseVariant, subjectVariant);
        IdentityHashMap<Variant, BlockRendererType> originals = new IdentityHashMap<>();
        all.forEach(variant -> originals.put(variant, variant.getRenderer()));
        BlockRendererType lateWrapper = new BlockRendererType.Impl(
                Key.parse("test:camol-like-wrapper"),
                (currentPack, currentTextures, currentSettings) ->
                        (block, variant, target, color) -> {
                            lateWrapperCalls.incrementAndGet();
                            originals.get(variant).create(
                                    currentPack, currentTextures, currentSettings
                            ).render(block, variant, target, color);
                        }
        );
        all.forEach(variant -> variant.setRenderer(lateWrapper));

        AddonRuntime runtime = newRuntime();
        runtime.activate();
        TrophyRenderer renderer = new TrophyRenderer(
                pack, textures, settings, runtime, trophyCatalog
        );
        BlockNeighborhood block = new BlockNeighborhood(
                new FixedBlockAccess(trophyData()), pack, settings,
                DimensionType.OVERWORLD
        );
        ArrayTileModel model = new ArrayTileModel(32);
        TileModelView target = new TileModelView(model);
        Color mapColor = new Color();

        renderer.render(block, trophyVariant, target, mapColor);

        assertEquals(5, model.size());
        assertEquals(0, target.getStart());
        assertEquals(5, target.getSize());
        assertEquals(2, lateWrapperCalls.get());
        assertEquals(0, stockCalls.get());
        assertEquals(0.4F, mapColor.r, 0.0001F);
        assertEquals(0F, mapColor.g, 0.0001F);
        assertEquals(0.6F, mapColor.b, 0.0001F);
        assertEquals(0.75F, mapColor.a, 0.0001F);
    }

    private static Variant variant(
            ResourcePack pack,
            Key id,
            String model,
            BlockRendererType renderer
    ) {
        Variant variant = new Variant(new ResourcePath<Model>(model));
        variant.setRenderer(renderer);
        pack.getBlockStates().put(
                id, new de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate
                        .BlockState(new Variants(
                                new VariantSet[0], new VariantSet(variant)
                        ))
        );
        return variant;
    }

    private static BlockRendererType emittingType(
            String id,
            int triangles,
            AtomicInteger calls,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        return new BlockRendererType.Impl(
                Key.parse(id),
                (pack, textures, settings) -> (block, variant, target, color) -> {
                    if (calls != null) {
                        calls.incrementAndGet();
                    }
                    target.add(triangles);
                    color.set(red, green, blue, alpha, false);
                }
        );
    }

    private static AddonRuntime newRuntime() throws ReflectiveOperationException {
        Constructor<AddonRuntime> constructor = AddonRuntime.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static TrophyBlockEntityData trophyData() throws ReflectiveOperationException {
        TrophyBlockEntityData data = new TrophyBlockEntityData();
        TrophyBlockEntityData.TrophyData trophy = new TrophyBlockEntityData.TrophyData();
        TrophyBlockEntityData.TrophyItem item = new TrophyBlockEntityData.TrophyItem();
        setField(MCABlockEntity.class, data, "id", TROPHY);
        setField(TrophyBlockEntityData.TrophyItem.class, item,
                "id", "minecraft:diamond_block");
        setField(TrophyBlockEntityData.TrophyItem.class, item, "count", 1);
        setField(TrophyBlockEntityData.TrophyData.class, trophy, "trophyType", "item");
        setField(TrophyBlockEntityData.TrophyData.class, trophy, "trophyItem", item);
        setField(TrophyBlockEntityData.TrophyData.class, trophy,
                "baseBlock", "minecraft:smooth_stone_slab");
        setField(TrophyBlockEntityData.TrophyData.class, trophy, "offsetY", 0.5D);
        setField(TrophyBlockEntityData.TrophyData.class, trophy, "rotationX", 0F);
        setField(TrophyBlockEntityData.TrophyData.class, trophy, "scale", 0.5F);
        setField(TrophyBlockEntityData.class, data, "trophyData", trophy);
        return data;
    }

    private static void setField(Class<?> owner, Object target, String name, Object value)
            throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static final class FixedBlockAccess implements BlockAccess {

        private final TrophyBlockEntityData blockEntity;
        private int x;
        private int y;
        private int z;

        private FixedBlockAccess(TrophyBlockEntityData blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public void set(int newX, int newY, int newZ) {
            x = newX;
            y = newY;
            z = newZ;
        }

        @Override
        public BlockAccess copy() {
            FixedBlockAccess copy = new FixedBlockAccess(blockEntity);
            copy.set(x, y, z);
            return copy;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
            return z;
        }

        @Override
        public BlockState getBlockState() {
            return x == 0 && y == 0 && z == 0 ? HOST : BlockState.AIR;
        }

        @Override
        public LightData getLightData() {
            return new LightData(15, 0);
        }

        @Override
        public Biome getBiome() {
            return Biome.DEFAULT;
        }

        @Override
        public BlockEntity getBlockEntity() {
            return x == 0 && y == 0 && z == 0 ? blockEntity : null;
        }

        @Override
        public boolean hasOceanFloorY() {
            return false;
        }

        @Override
        public int getOceanFloorY() {
            return 0;
        }
    }

    private record TestSettings() implements RenderSettings {

        @Override
        public int getRemoveCavesBelowY() {
            return Integer.MIN_VALUE;
        }

        @Override
        public int getCaveDetectionOceanFloor() {
            return 0;
        }

        @Override
        public boolean isCaveDetectionUsesBlockLight() {
            return false;
        }

        @Override
        public float getAmbientLight() {
            return 0F;
        }

        @Override
        public Mask getRenderMask() {
            return Mask.ALL;
        }

        @Override
        public boolean isSaveHiresLayer() {
            return false;
        }

        @Override
        public boolean isRenderTopOnly() {
            return false;
        }
    }
}
