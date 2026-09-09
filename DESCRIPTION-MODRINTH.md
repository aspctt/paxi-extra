# <p align=center> Paxi Extra </p>

<div align="center">

![Version](https://img.shields.io/badge/Available_for-1.21.1_|_26.1-blue)
![Requires](https://img.shields.io/badge/Requires-Paxi-blueviolet)
![License](https://img.shields.io/badge/License-GPL--3.0--only-red)

![NeoForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg)
![Fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/fabric_vector.svg)

[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/aspctt/paxiextra-neoforge)
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg)](https://modrinth.com/mod/paxiextra-neoforged)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/paxi-extra-neoforged)

</div>

Paxi Extra is an addon for [Paxi](https://modrinth.com/mod/paxi) that makes the load order file the whole story.

Paxi force-loads resource packs and data packs for a modpack and orders them with a JSON file. Paxi Extra extends that file to reach packs Paxi cannot normally see, stops it loading packs it was never told about, and adds a marker for where the player's own packs belong in the stack.

This is a NeoForge port of [Paxi Plus](https://modrinth.com/mod/paxiplus) by Lancet_, a Fabric mod for 1.20.1, renamed to keep the two projects apart.

### Built-in packs from mods can be ordered

A load order entry that matches no file on disk is looked up among the packs mods have already contributed, and the matching one is re-created under Paxi's pack source, forced on, and placed where the file says. That covers a mod's own resource or data pack, whether or not the mod asks for it to be shown separately.

Write the pack's id rather than a file name:

```json
{
  "loadOrder": [
    "mod/somemod",
    "my_overrides.zip"
  ]
}
```

Pack ids are not always guessable. [Resource Pack Overrides](https://modrinth.com/mod/resource-pack-overrides) shows them all: hold **D** on the resource pack screen.

### Nothing loads unless it is listed

Paxi force-loads every pack sitting in `config/paxi/resourcepacks` and `config/paxi/datapacks`, ordered or not. Paxi Extra loads only what `resourcepack_load_order.json` and `datapack_load_order.json` name, so a pack can be left in the folder without being active and a modpack ships exactly the stack it declares.

### The data pack folder exists before the first world

Paxi creates `config/paxi/datapacks` and its load order file the first time a world loads. Paxi Extra creates both while the game is still starting, so a fresh instance has them to edit right away.

### `--user--` marks where the player's packs sit

Paxi packs are always on top of anything the player selected. Adding `--user--` to a load order splits it: everything listed before the marker is placed underneath the player's own packs, everything after stays above them. Put it last and every Paxi pack sits below the player's, which is what you want when the modpack's packs are a base rather than an override.

```json
{
  "loadOrder": [
    "modpack_base.zip",
    "--user--",
    "modpack_overrides.zip"
  ]
}
```

Loading packs from any folder in the instance, rather than only from Paxi's own, is a feature of the Fabric addon that Paxi itself has carried since 1.21. Nothing here changes it.

### Requirements

Minecraft 1.21.1 or 26.1 through 26.1.2, on NeoForge, with [Paxi](https://modrinth.com/mod/paxi) and [YUNG's API](https://modrinth.com/mod/yungs-api) installed. Paxi already requires YUNG's API.

### License

Paxi Extra is licensed under the GNU Lesser General Public License v3.0, the same licence as the Fabric mod it is ported from. The full terms are in [LICENSE](https://github.com/aspctt/paxiextra-neoforge/blob/main/LICENSE).

### Credits

Lancet_ wrote the original Fabric mod, with contributions from Fyoncle. Paxi is by YUNGNICKYOUNG. This port is by aspctt.
