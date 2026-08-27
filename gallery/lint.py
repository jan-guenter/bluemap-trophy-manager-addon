#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated Trophy Manager gallery without starting Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        raise ValueError("load tag differs from the exact namespace")
    if len(cases.PLACEMENTS) != 6:
        raise ValueError("gallery must contain exactly six bounded placements")
    if cases.PLACEMENTS[0].nbt is not None or cases.PLACEMENTS[0].expected != (
        "stock-visible"
    ):
        raise ValueError("first placement must remain the stock slab control")
    minimum_x, minimum_y, minimum_z, maximum_x, maximum_y, maximum_z = (
        cases.ENVELOPE
    )
    for placement in cases.PLACEMENTS:
        if not (
            minimum_x <= placement.x <= maximum_x
            and minimum_y <= placement.y <= maximum_y
            and minimum_z <= placement.z <= maximum_z
        ):
            raise ValueError("placement escaped its bounded envelope")

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    if len(re.findall(r"^setblock ", functions, re.MULTILINE)) != 6:
        raise ValueError("gallery must place exactly six blocks")
    if len(re.findall(r"^data merge block ", functions, re.MULTILINE)) != 5:
        raise ValueError("gallery must write exactly five trophy snapshots")
    if len(re.findall(r"^execute unless data block ", functions, re.MULTILINE)) != 5:
        raise ValueError("gallery must verify exactly five trophy snapshots")
    lowered = functions.lower()
    for forbidden in ("summon ", " op ", "deop ", "stop "):
        if forbidden in lowered:
            raise ValueError(f"forbidden gallery command: {forbidden}")
    print("review gallery lint passed: six bounded placements")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
