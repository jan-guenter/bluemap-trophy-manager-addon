/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;

/** Emits the vanilla creeper's neutral six-cuboid pose from its installed atlas. */
final class CreeperTrophyEmitter {

    static final Key TEXTURE = Key.parse("minecraft:entity/creeper/creeper");

    private static final float TEXTURE_WIDTH = 64F;
    private static final float TEXTURE_HEIGHT = 32F;

    private final ResourcePack resourcePack;
    private final TextureGallery textureGallery;

    CreeperTrophyEmitter(ResourcePack resourcePack, TextureGallery textureGallery) {
        this.resourcePack = resourcePack;
        this.textureGallery = textureGallery;
    }

    boolean emit(BlockNeighborhood block, TileModelView target, Color mapColor) {
        Texture texture = resourcePack.getTextures().get(TEXTURE);
        if (texture == null) {
            return false;
        }
        int start = target.getTileModel().size();

        box(block, target, -4F, -8F, -4F, 4F, 0F, 4F,
                0F, 6F, 0F, 0F, 0F, 8F, 8F, 8F);
        box(block, target, -4F, 0F, -2F, 4F, 12F, 2F,
                0F, 6F, 0F, 16F, 16F, 8F, 12F, 4F);
        box(block, target, -2F, 0F, -2F, 2F, 6F, 2F,
                -2F, 18F, 4F, 0F, 16F, 4F, 6F, 4F);
        box(block, target, -2F, 0F, -2F, 2F, 6F, 2F,
                2F, 18F, 4F, 0F, 16F, 4F, 6F, 4F);
        box(block, target, -2F, 0F, -2F, 2F, 6F, 2F,
                -2F, 18F, -4F, 0F, 16F, 4F, 6F, 4F);
        box(block, target, -2F, 0F, -2F, 2F, 6F, 2F,
                2F, 18F, -4F, 0F, 16F, 4F, 6F, 4F);

        if (target.getTileModel().size() == start) {
            return false;
        }
        mapColor.add(new Color().set(texture.getColorPremultiplied()));
        return true;
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private void box(
            BlockNeighborhood block,
            TileModelView target,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float pivotX,
            float pivotY,
            float pivotZ,
            float textureU,
            float textureV,
            float width,
            float height,
            float depth
    ) {
        x0 += pivotX;
        x1 += pivotX;
        y0 += pivotY;
        y1 += pivotY;
        z0 += pivotZ;
        z1 += pivotZ;

        face(block, target, Direction.UP,
                x1, y0, z1, x0, y0, z1, x0, y0, z0, x1, y0, z0,
                textureU + depth, textureV,
                textureU + depth + width, textureV + depth);
        face(block, target, Direction.DOWN,
                x1, y1, z0, x0, y1, z0, x0, y1, z1, x1, y1, z1,
                textureU + depth + width, textureV + depth,
                textureU + depth + 2F * width, textureV);
        face(block, target, Direction.WEST,
                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0,
                textureU, textureV + depth,
                textureU + depth, textureV + depth + height);
        face(block, target, Direction.SOUTH,
                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0,
                textureU + depth, textureV + depth,
                textureU + depth + width, textureV + depth + height);
        face(block, target, Direction.EAST,
                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1,
                textureU + depth + width, textureV + depth,
                textureU + 2F * depth + width, textureV + depth + height);
        face(block, target, Direction.NORTH,
                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1,
                textureU + 2F * depth + width, textureV + depth,
                textureU + 2F * depth + 2F * width, textureV + depth + height);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private void face(
            BlockNeighborhood block,
            TileModelView target,
            Direction direction,
            float ax,
            float ay,
            float az,
            float bx,
            float by,
            float bz,
            float cx,
            float cy,
            float cz,
            float dx,
            float dy,
            float dz,
            float u0,
            float v0,
            float u1,
            float v1
    ) {
        int start = target.add(2);
        TileModel model = target.getTileModel();
        model.setPositions(start,
                vanillaX(ax), vanillaY(ay), vanillaZ(az),
                vanillaX(bx), vanillaY(by), vanillaZ(bz),
                vanillaX(cx), vanillaY(cy), vanillaZ(cz));
        model.setPositions(start + 1,
                vanillaX(ax), vanillaY(ay), vanillaZ(az),
                vanillaX(cx), vanillaY(cy), vanillaZ(cz),
                vanillaX(dx), vanillaY(dy), vanillaZ(dz));
        model.setUvs(start,
                u1 / TEXTURE_WIDTH, v0 / TEXTURE_HEIGHT,
                u0 / TEXTURE_WIDTH, v0 / TEXTURE_HEIGHT,
                u0 / TEXTURE_WIDTH, v1 / TEXTURE_HEIGHT);
        model.setUvs(start + 1,
                u1 / TEXTURE_WIDTH, v0 / TEXTURE_HEIGHT,
                u0 / TEXTURE_WIDTH, v1 / TEXTURE_HEIGHT,
                u1 / TEXTURE_WIDTH, v1 / TEXTURE_HEIGHT);
        int material = textureGallery.get(TEXTURE);
        model.setMaterialIndex(start, material);
        model.setMaterialIndex(start + 1, material);
        model.setColor(start, 1F, 1F, 1F);
        model.setColor(start + 1, 1F, 1F, 1F);
        model.setAOs(start, 1F, 1F, 1F);
        model.setAOs(start + 1, 1F, 1F, 1F);
        Light light = light(block, direction);
        model.setSunlight(start, light.sunlight());
        model.setSunlight(start + 1, light.sunlight());
        model.setBlocklight(start, light.blocklight());
        model.setBlocklight(start + 1, light.blocklight());
    }

    private static float vanillaX(float modelX) {
        return modelX / 16F;
    }

    private static float vanillaY(float modelY) {
        return 1.501F - modelY / 16F;
    }

    private static float vanillaZ(float modelZ) {
        return -modelZ / 16F;
    }

    private static Light light(BlockNeighborhood block, Direction direction) {
        int x = switch (direction) {
            case EAST -> 1;
            case WEST -> -1;
            default -> 0;
        };
        int y = switch (direction) {
            case UP -> 1;
            case DOWN -> -1;
            default -> 0;
        };
        int z = switch (direction) {
            case SOUTH -> 1;
            case NORTH -> -1;
            default -> 0;
        };
        LightData own = block.getLightData();
        LightData neighbor = block.getNeighborBlock(x, y, z).getLightData();
        return new Light(
                Math.max(own.getSkyLight(), neighbor.getSkyLight()),
                Math.max(own.getBlockLight(), neighbor.getBlockLight())
        );
    }

    private record Light(int sunlight, int blocklight) {
    }
}
