#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Bounded block-item and creeper trophy comparison cases."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "trophymanager_gallery"
ENVELOPE = (162, 99, 166, 176, 103, 170)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    nbt: str | None
    expected: str


PLACEMENTS = (
    Placement(
        "slab-control",
        "smooth-stone slab stock control",
        164,
        100,
        168,
        "minecraft:smooth_stone_slab[type=bottom,waterlogged=false]",
        None,
        "stock-visible",
    ),
    Placement(
        "diamond-smooth",
        "diamond block trophy on smooth stone",
        166,
        100,
        168,
        "trophymanager:trophy[facing=south,waterlogged=false]",
        '{TrophyData:{TrophyType:"item",TrophyItem:{id:"minecraft:diamond_block",count:1},OffsetY:0.5d,RotX:0.0f,Scale:0.5f,BaseBlock:"minecraft:smooth_stone_slab",Name:"Diamond"}}',
        "dynamic-base-and-block-item",
    ),
    Placement(
        "gold-sandstone",
        "gold block trophy on cut sandstone",
        168,
        100,
        168,
        "trophymanager:trophy[facing=east,waterlogged=false]",
        '{TrophyData:{TrophyType:"item",TrophyItem:{id:"minecraft:gold_block",count:1},OffsetY:0.5d,RotX:0.0f,Scale:0.5f,BaseBlock:"minecraft:cut_sandstone_slab",Name:"Gold"}}',
        "dynamic-base-and-block-item",
    ),
    Placement(
        "emerald-oak",
        "scaled emerald block trophy on oak",
        170,
        100,
        168,
        "trophymanager:trophy[facing=north,waterlogged=false]",
        '{TrophyData:{TrophyType:"item",TrophyItem:{id:"minecraft:emerald_block",count:1},OffsetY:0.55d,RotX:0.0f,Scale:0.65f,BaseBlock:"minecraft:oak_slab",Name:"Emerald"}}',
        "dynamic-base-scale-and-facing",
    ),
    Placement(
        "redstone-deepslate",
        "small redstone block trophy on deepslate tile",
        172,
        100,
        168,
        "trophymanager:trophy[facing=west,waterlogged=false]",
        '{TrophyData:{TrophyType:"item",TrophyItem:{id:"minecraft:redstone_block",count:1},OffsetY:0.6d,RotX:0.0f,Scale:0.4f,BaseBlock:"minecraft:deepslate_tile_slab",Name:"Redstone"}}',
        "dynamic-base-scale-and-facing",
    ),
    Placement(
        "creeper-entity",
        "creeper entity trophy on brick slab",
        174,
        100,
        168,
        "trophymanager:trophy[facing=north,waterlogged=false]",
        '{TrophyData:{TrophyType:"entity",TrophyEntity:{entityType:"minecraft:creeper"},OffsetY:0.5d,RotX:0.0f,Scale:0.5f,BaseBlock:"minecraft:brick_slab",Name:"Creeper"}}',
        "dynamic-base-and-creeper-entity",
    ),
)
