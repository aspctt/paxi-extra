<h1 style="text-align: center;"> Paxi Extra </h1>

<p style="text-align: center;">
	<img src="https://img.shields.io/badge/Available_for-1.21.1_|_26.1-blue" alt="Version">
	<img src="https://img.shields.io/badge/Requires-Paxi-blueviolet" alt="Requires">
	<img src="https://img.shields.io/badge/License-LGPL--3.0-red" alt="License">
</p>

<p style="text-align: center;">
	<img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg" alt="NeoForge">
	<img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/fabric_vector.svg" alt="Fabric">
</p>

<p style="text-align: center;">
	<a href="https://github.com/aspctt/paxiextra-neoforge"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg" alt="Available on GitHub"></a>
	<a href="https://modrinth.com/mod/paxiextra-neoforged"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg" alt="Available on Modrinth"></a>
	<a href="https://www.curseforge.com/minecraft/mc-mods/paxi-extra-neoforged"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg" alt="Available on CurseForge"></a>
</p>

<p>Paxi Extra is an addon for <a href="https://www.curseforge.com/minecraft/mc-mods/paxi-neoforge">Paxi</a> that makes the load order file the whole story.</p>

<p>Paxi force-loads resource packs and data packs for a modpack and orders them with a JSON file. Paxi Extra extends that file to reach packs Paxi cannot normally see, stops it loading packs it was never told about, and adds a marker for where the player's own packs belong in the stack.</p>

<p>This is a NeoForge port of Paxi Plus by Lancet_, a Fabric mod for 1.20.1, renamed to keep the two projects apart.</p>

<h3>Built-in packs from mods can be ordered</h3>

<p>A load order entry that matches no file on disk is looked up among the packs mods have already contributed, and the matching one is re-created under Paxi's pack source, forced on, and placed where the file says. That covers a mod's own resource or data pack, whether or not the mod asks for it to be shown separately.</p>

<p>Write the pack's id rather than a file name:</p>

<pre><code>{
  "loadOrder": [
    "mod/somemod",
    "my_overrides.zip"
  ]
}</code></pre>

<p>Pack ids are not always guessable. <a href="https://www.curseforge.com/minecraft/mc-mods/resource-pack-overrides">Resource Pack Overrides</a> shows them all: hold <strong>D</strong> on the resource pack screen.</p>

<h3>Nothing loads unless it is listed</h3>

<p>Paxi force-loads every pack sitting in <code>config/paxi/resourcepacks</code> and <code>config/paxi/datapacks</code>, ordered or not. Paxi Extra loads only what <code>resourcepack_load_order.json</code> and <code>datapack_load_order.json</code> name, so a pack can be left in the folder without being active and a modpack ships exactly the stack it declares.</p>

<h3>The data pack folder exists before the first world</h3>

<p>Paxi creates <code>config/paxi/datapacks</code> and its load order file the first time a world loads. Paxi Extra creates both while the game is still starting, so a fresh instance has them to edit right away.</p>

<h3>--user-- marks where the player's packs sit</h3>

<p>Paxi packs are always on top of anything the player selected. Adding <code>--user--</code> to a load order splits it: everything listed before the marker is placed underneath the player's own packs, everything after stays above them. Put it last and every Paxi pack sits below the player's, which is what you want when the modpack's packs are a base rather than an override.</p>

<pre><code>{
  "loadOrder": [
    "modpack_base.zip",
    "--user--",
    "modpack_overrides.zip"
  ]
}</code></pre>

<p>Loading packs from any folder in the instance, rather than only from Paxi's own, is a feature of the Fabric addon that Paxi itself has carried since 1.21. Nothing here changes it.</p>

<h3>Requirements</h3>

<p>Minecraft 1.21.1 or 26.1 through 26.1.2, on NeoForge, with <a href="https://www.curseforge.com/minecraft/mc-mods/paxi-neoforge">Paxi</a> and <a href="https://www.curseforge.com/minecraft/mc-mods/yungs-api">YUNG's API</a> installed. Paxi already requires YUNG's API.</p>

<h3>License</h3>

<p>Paxi Extra is licensed under the GNU Lesser General Public License v3.0, the same licence as the Fabric mod it is ported from. The full terms are in <a href="https://github.com/aspctt/paxiextra-neoforge/blob/main/LICENSE">LICENSE</a>.</p>

<h3>Credits</h3>

<p>Lancet_ wrote the original Fabric mod, with contributions from Fyoncle. Paxi is by YUNGNICKYOUNG. This port is by aspctt.</p>
