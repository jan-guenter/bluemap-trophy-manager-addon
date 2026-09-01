# BlueMap Trophy Manager Add-on

A Java 21 BlueMap add-on for the exact `trophymanager-1.21.1-2.5.0` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: unpublished `0.1.0-alpha.2` BlueMap 5.23 migration candidate. It
preserves the owner-accepted alpha.1 block-item and creeper trophy renderers,
profile, gallery, and fallback behavior while moving shared compatibility
helpers into the pinned Adapter API. The exact production JAR is 102,452 bytes
with SHA-256
`ab0aa103f50966dfc33c184e02a3ea948a2204a14d402a45718238c9dbf5a0b2`.

## Build

Clone with `--recurse-submodules`, or initialize the toolkit and Adapter API
submodules in an existing checkout. The settings preflight accepts only the
committed pins and rejects uninitialized, changed, or dirty submodule
checkouts.

```bash
gradle --no-daemon \
  -PbluemapSourcePath=../bluemap-backport \
  -PtrophyManagerJar=/path/to/trophymanager-1.21.1-2.5.0.jar \
  clean prototypeCheck build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the real gallery.
See `provenance/upstreams.json` for immutable artifact identities and the
[execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

The exact BlueMap checkout is commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` with API commit
`285c9a60eff3ac2b0cab308ce1058d1565be0971`. Exactly four Adapter API helpers
are compiled from commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`,
source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb`; no module JAR is installed,
bundled, or nested.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior; the add-on
creates no custom world state.

Set `-Dbluemap.trophymanager.disabled=true` to leave the exact profile inactive.

## Scope boundary

The initial routes support installed, propertyless block-item models, neutral
vanilla creepers using the installed entity texture, and installed slab
blockstates selected by Trophy Manager's `BaseBlock` data. They reproduce the
stable client pose from `OffsetY`, `RotX`, `Scale`, and facing. Other entity
trophies, charged creepers, generated/flat item models, component-sensitive
item models, waterlogged trophies, malformed data, and unsupported blockstates
stay stock.

No Trophy Manager binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
