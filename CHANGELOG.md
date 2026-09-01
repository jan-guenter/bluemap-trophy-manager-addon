# Changelog

## 0.1.0-alpha.2 - 2026-09-02

- Migrated the accepted renderer to the exact BlueMap 5.23 feature backport.
- Replaced local runtime, registry, and resource-extension helpers with the
  pinned shared Adapter API source module.
- Preserved persisted trophy NBT after late BlueNBT type registration.
- Kept synthetic slab and block-item rendering functional when Camol installs
  its outer global variant wrapper after Trophy Manager.
- Kept the alpha.1 gallery, profile, trophy poses, and stock fallback scope.

## 0.1.0-alpha.1 - 2026-08-27

- Generated a fail-closed Java 21 BlueMap add-on seed for
  `trophymanager-1.21.1-2.5.0`.
- Added resource-driven rendering for block-item trophies, neutral vanilla
  creeper trophies, and configured slab bases, including the stable client
  transform controls.
- Added atomic stock fallback for unsupported targets and malformed data.
- Added a deterministic six-case visual gallery and render-plan tests.
