/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.trophymanager.activation.AddonRuntime;

/** BlueMap 5.22 registration boundary. Family renderer registrations go here. */
public final class BlueMap522Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final BlockRendererType TROPHY_RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_trophymanager:trophy"),
            BlueMap522Adapter::createRenderer
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ProfileResourceExtensionType(TROPHY_RENDERER, RUNTIME);
    private static final BlockEntityType TROPHY_BLOCK_ENTITY = new BlockEntityType.Impl(
            Key.parse("trophymanager:trophy"),
            TrophyBlockEntityData.class
    );

    private BlueMap522Adapter() {
    }

    /** Registers the exact trophy route, retained NBT projection, and resource probe. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, TROPHY_RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || !RegistryGuard.canRegister(BlockEntityType.REGISTRY, TROPHY_BLOCK_ENTITY)) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, TROPHY_RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)
                || !RegistryGuard.register(BlockEntityType.REGISTRY, TROPHY_BLOCK_ENTITY)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        return true;
    }

    private static BlockRenderer createRenderer(
            ResourcePack pack,
            TextureGallery gallery,
            RenderSettings settings
    ) {
        try {
            return new TrophyRenderer(
                    pack,
                    gallery,
                    settings,
                    RUNTIME,
                    ProfileResourceExtension.catalog(pack)
            );
        } catch (RuntimeException exception) {
            RUNTIME.inactive("renderer-construction-"
                    + exception.getClass().getSimpleName());
            return BlockRendererType.DEFAULT.create(pack, gallery, settings);
        }
    }
}
