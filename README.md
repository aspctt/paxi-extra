# <p align=center> Paxi Extra </p>

<div align="center">

![Version](https://img.shields.io/badge/Available_for-1.21.1_|_26.1-blue)
![Requires](https://img.shields.io/badge/Requires-Paxi-blueviolet)
![License](https://img.shields.io/badge/License-LGPL--3.0--only-red)

![Fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)
![NeoForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg)
![Forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg)

[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/aspctt/paxi-extra)
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg)](https://modrinth.com/mod/paxi-extra)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/paxi-extra)

</div>

## Description

This is an addon for [Paxi](https://modrinth.com/mod/paxi), serving five purposes:

- Making it possible to use Paxi for built-in resource packs and data packs added by mods <sub>(See [#27](https://github.com/YUNG-GANG/Paxi/issues/27))</sub>
- Restricting Paxi from auto-loading packs in `config -> paxi -> resourcepacks` and `datapacks` if they're not listed in `resourcepack_load_order.json` or `datapack_load_order.json`
- Letting Paxi generate the datapack folder and JSON file on start-up instead of after world generation
- Adding a `--user--` flag which lets you decide where your own packs go, handy if you want the packs you picked yourself to sit above the Paxi ones, or anywhere else you like
- Removing the reorder arrows from packs Paxi loads, since the load order decides where those sit and a pack dragged up or down just snaps back

This is a port of [Paxi Plus](https://modrinth.com/mod/paxiplus) by Lancet_, renamed to keep the two projects apart.

## How to use?

Write the packs you want, in the order you want them, and nothing else gets force-loaded:

```json
{
  "loadOrder": [
    "resourcepacks/my_base_pack.zip",
    "mod/somemod",
    "--user--",
    "resourcepacks/my_overrides.zip"
  ]
}
```

Later in the list wins. Anything above `--user--` sits under the packs you picked yourself, anything below it sits on top of them. Leave `--user--` out and everything stays on top, the way Paxi normally does it.

For the built-in pack support you need the ID of the pack. You can check the mod's source, guess it, or use [Resource Pack Overrides](https://modrinth.com/mod/resource-pack-overrides) and hold **D** on the resource pack screen to see the IDs of every pack. You can order `fabric` and `mod_resources` too, if you want all the mod assets to sit at a particular spot in the stack.

**It is NOT required to load `fabric` and other built-in packs with Paxi, it's there as an optional feature.**

Loading packs from anywhere in your instance rather than only Paxi's own folder already works, since Paxi itself has done that since 1.21. Keeping your packs in the normal `resourcepacks` folder also means Modrinth recognises them, so a modpack that includes them gets credited properly.

## Installation

Place the JAR in your `mods` folder, along with its dependencies below. Load order files live in `config/paxi/`.

## Dependencies

* Minecraft 1.21.1, or 26.1 through 26.1.2
* NeoForge 21.1.0 or newer (1.21.1), 26.1.2.75 or newer (26.1), **or** Fabric Loader 0.16.0 or newer
* [Paxi](https://modrinth.com/mod/paxi)
* [YUNG's API](https://modrinth.com/mod/yungs-api), which Paxi already requires

The jar name says which loader it is built for: `PaxiExtra-<version>+<minecraft version>-<loader>.jar`.

## Licensing

Paxi Extra is licensed under the **GNU Lesser General Public License v3.0**, the same licence as the mod it is ported from. The full terms are in [LICENSE](./LICENSE).

## Credits

### Core

* Lancet_ - original mod
* Fyoncle - contributor to the original
* aspctt - this port

### Built on

* [Paxi](https://github.com/YUNG-GANG/Paxi) by YUNGNICKYOUNG - LGPL-3.0
* [NeoForge](https://neoforged.net/) and [Fabric](https://fabricmc.net/) - mod loaders
* [Stonecutter](https://github.com/kikugie/stonecutter) - multi-loader, multi-version build
