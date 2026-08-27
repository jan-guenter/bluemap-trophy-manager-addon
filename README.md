# BlueMap Trophy Manager Add-on

A Java 21 BlueMap add-on for the exact `trophymanager-1.21.1-2.5.0` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: staging prototype. The exact artifact gate and BlueMap 5.22 adapter
render resource-driven block-item trophies, neutral vanilla creeper trophies,
and their configured slab bases. Unsupported targets fall back atomically to
BlueMap's stock rendering.

## Build

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
