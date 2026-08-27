# Trophy Manager gallery

This bounded gallery compares a stock slab with four resource-driven block-item
trophies and one resource-driven creeper trophy. The row occupies
`x=164..174`, `y=100`, `z=168`:

- smooth-stone slab control;
- diamond block on smooth stone;
- gold block on cut sandstone;
- emerald block on oak;
- redstone block on deepslate;
- creeper entity trophy on brick slab.

Regenerate and validate it with:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/trophymanager-gallery.zip
```

Gallery generation is deterministic, bounded, synthetic, and free of candidate
assets or captured meshes.
