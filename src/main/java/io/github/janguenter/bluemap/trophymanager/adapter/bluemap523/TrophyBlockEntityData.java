/* SPDX-License-Identifier: MIT */

package io.github.janguenter.bluemap.trophymanager.adapter.bluemap523;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

/** Narrow projection of the stable TrophyData compound used by the client renderer. */
public final class TrophyBlockEntityData extends MCABlockEntity {

    @NBTName("TrophyData")
    private TrophyData trophyData;

    public TrophyBlockEntityData() {
    }

    TrophyData trophyData() {
        return trophyData;
    }

    /** Persisted visual fields; unsupported entity and component payloads stay opaque. */
    public static final class TrophyData {

        @NBTName("TrophyType")
        private String trophyType;

        @NBTName("TrophyItem")
        private TrophyItem trophyItem;

        @NBTName("TrophyEntity")
        private TrophyEntity trophyEntity;

        @NBTName("OffsetY")
        private Double offsetY;

        @NBTName("RotX")
        private Float rotationX;

        @NBTName("Scale")
        private Float scale;

        @NBTName("BaseBlock")
        private String baseBlock;

        public TrophyData() {
        }

        String trophyType() {
            return trophyType;
        }

        TrophyItem trophyItem() {
            return trophyItem;
        }

        TrophyEntity trophyEntity() {
            return trophyEntity;
        }

        Double offsetY() {
            return offsetY;
        }

        Float rotationX() {
            return rotationX;
        }

        Float scale() {
            return scale;
        }

        String baseBlock() {
            return baseBlock;
        }
    }

    /** Persisted 1.21 ItemStack identity; item components are intentionally ignored. */
    public static final class TrophyItem {

        private String id;
        private Integer count;

        public TrophyItem() {
        }

        String id() {
            return id;
        }

        Integer count() {
            return count;
        }
    }

    /** Persisted entity identity; variant-specific payload stays opaque. */
    public static final class TrophyEntity {

        @NBTName("entityType")
        private String entityType;

        @NBTName("powered")
        private Boolean powered;

        public TrophyEntity() {
        }

        String entityType() {
            return entityType;
        }

        Boolean powered() {
            return powered;
        }
    }
}
