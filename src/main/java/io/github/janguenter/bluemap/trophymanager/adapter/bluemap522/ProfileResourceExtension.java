/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.trophymanager.activation.AddonRuntime;
import io.github.janguenter.bluemap.trophymanager.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.trophymanager.profile.TrophyManager250Profile;

import java.nio.file.Path;
import java.util.Map;
import java.util.WeakHashMap;

/** Exact-artifact admission and narrow trophy renderer installation. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private static final Key TROPHY = Key.parse("trophymanager:trophy");
    private static final Map<ResourcePack, VariantRendererCatalog> CATALOGS =
            new WeakHashMap<>();

    private final ResourcePack resourcePack;
    private final BlockRendererType renderer;
    private final AddonRuntime runtime;

    ProfileResourceExtension(
            ResourcePack resourcePack,
            BlockRendererType renderer,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.renderer = renderer;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.trophymanager.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, TrophyManager250Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        if (resourcePack.getBlockStates().get(TROPHY) == null) {
            runtime.inactive("required-resource-missing");
            return;
        }
        runtime.activate();
    }

    @Override
    public void getBlockProperties(BlockState state, BlockProperties.Builder builder) {
        if (runtime.active() && TROPHY.equals(state.getId())) {
            builder.culling(false)
                    .occluding(false)
                    .cullingIdentical(false)
                    .randomOffset(false);
        }
    }

    @Override
    public void bake() {
        if (!runtime.active()) {
            return;
        }
        VariantRendererCatalog catalog = VariantRendererCatalog.wrap(
                resourcePack, TROPHY, renderer
        );
        if (catalog.size() == 0) {
            runtime.inactive("trophy-variant-missing");
            return;
        }
        synchronized (CATALOGS) {
            CATALOGS.put(resourcePack, catalog);
        }
        System.out.println("BlueMap Trophy Manager add-on active: block-item trophy route installed.");
    }

    static VariantRendererCatalog catalog(ResourcePack resourcePack) {
        synchronized (CATALOGS) {
            return CATALOGS.get(resourcePack);
        }
    }
}
