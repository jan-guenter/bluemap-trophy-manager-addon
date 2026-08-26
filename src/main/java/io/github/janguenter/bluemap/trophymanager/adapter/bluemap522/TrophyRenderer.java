/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.trophymanager.activation.AddonRuntime;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/** Restores persisted slab bases and deterministic block-item trophy subjects. */
final class TrophyRenderer implements BlockRenderer {

    private static final String TROPHY = "trophymanager:trophy";
    private static final ThreadLocal<Boolean> STOCK_FALLBACK =
            ThreadLocal.withInitial(() -> Boolean.FALSE);

    private final ResourcePack resourcePack;
    private final TextureGallery textures;
    private final RenderSettings settings;
    private final AddonRuntime runtime;
    private final VariantRendererCatalog catalog;
    private final ResourceModelRenderer models;
    private final Map<BlockRendererType, BlockRenderer> hosts = new IdentityHashMap<>();

    TrophyRenderer(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            AddonRuntime runtime,
            VariantRendererCatalog catalog
    ) {
        this.resourcePack = resourcePack;
        this.textures = textures;
        this.settings = settings;
        this.runtime = runtime;
        this.catalog = catalog;
        this.models = new ResourceModelRenderer(resourcePack, textures, settings);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        int safeStart = target.getTileModel().size();
        try {
            if (runtime.active() && renderTrophy(block, target, mapColor, safeStart)) {
                return;
            }
            stock(block, variant, target, mapColor);
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            target.getTileModel().reset(safeStart);
            stockSafely(block, variant, target, mapColor, safeStart);
        }
    }

    private boolean renderTrophy(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor,
            int safeStart
    ) {
        if (!TROPHY.equals(block.getBlockState().getId().getFormatted())
                || "true".equals(block.getBlockState().getProperties().get("waterlogged"))
                || !(block.getBlockEntity() instanceof TrophyBlockEntityData data)
                || data.getId() == null
                || !TROPHY.equals(data.getId().getFormatted())) {
            return false;
        }
        Optional<TrophyRenderPlan> decoded = TrophyRenderPlan.decode(
                data, block.getBlockState()
        );
        if (decoded.isEmpty()) {
            return false;
        }
        TrophyRenderPlan plan = decoded.orElseThrow();
        if (!plan.baseBlock().endsWith("_slab")
                || TROPHY.equals(plan.itemBlock())) {
            return false;
        }

        BlockState base = state(
                plan.baseBlock(), Map.of("type", "bottom", "waterlogged", "false")
        );
        BlockState subject = state(plan.itemBlock(), Map.of());
        if (base == null || subject == null
                || !renderState(base, block, target, mapColor)) {
            target.getTileModel().reset(safeStart);
            target.initialize(safeStart);
            return false;
        }

        int subjectStart = target.getTileModel().size();
        target.initialize(subjectStart);
        if (!renderState(subject, block, target, mapColor)) {
            target.getTileModel().reset(safeStart);
            target.initialize(safeStart);
            return false;
        }
        transformSubject(target, plan);
        target.initialize(safeStart);
        return true;
    }

    private boolean renderState(
            BlockState state,
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        var resource = resourcePack.getBlockStates().get(state.getId());
        if (resource == null) {
            return false;
        }
        int start = target.getTileModel().size();
        resource.forEach(
                state,
                block.getX(),
                block.getY(),
                block.getZ(),
                selected -> {
                    if (selected.getRenderer() == BlockRendererType.DEFAULT) {
                        models.render(block, selected, target, mapColor);
                    }
                }
        );
        return target.getTileModel().size() > start;
    }

    private static void transformSubject(TileModelView subject, TrophyRenderPlan plan) {
        float size = 1.5F * plan.scale();
        subject.translate(-0.5F, -0.5F, -0.5F)
                .scale(size, size, size)
                .rotate(plan.rotationXDegrees(), 1F, 0F, 0F)
                .rotate(plan.yawDegrees(), 0F, 1F, 0F)
                .translate(
                        0.5F,
                        plan.offsetY() + 0.5F - 0.25F * plan.scale(),
                        0.5F
                );
    }

    private static BlockState state(String id, Map<String, String> properties) {
        try {
            return new BlockState(Key.parse(id), properties);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void stock(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor
    ) {
        if (STOCK_FALLBACK.get()) {
            return;
        }
        STOCK_FALLBACK.set(Boolean.TRUE);
        try {
            BlockRendererType type = catalog == null
                    ? BlockRendererType.DEFAULT : catalog.original(variant);
            hosts.computeIfAbsent(
                    type, found -> found.create(resourcePack, textures, settings)
            ).render(block, variant, target, mapColor);
        } finally {
            STOCK_FALLBACK.set(Boolean.FALSE);
        }
    }

    private void stockSafely(
            BlockNeighborhood block,
            Variant variant,
            TileModelView target,
            Color mapColor,
            int safeStart
    ) {
        try {
            stock(block, variant, target, mapColor);
        } catch (RuntimeException exception) {
            target.getTileModel().reset(safeStart);
        }
    }
}
