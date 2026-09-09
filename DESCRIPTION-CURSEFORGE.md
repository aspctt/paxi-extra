<h1 style="text-align: center;"> Paxi Extra </h1>

<p style="text-align: center;">
	<img src="https://img.shields.io/badge/Available_for-1.21.1_|_26.1-blue" alt="Version">
	<img src="https://img.shields.io/badge/Requires-Paxi-blueviolet" alt="Requires">
	<img src="https://img.shields.io/badge/License-LGPL--3.0--only-red" alt="License">
</p>

<p style="text-align: center;">
	<img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg" alt="NeoForge">
	<img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg" alt="Fabric">
</p>

<p style="text-align: center;">
	<a href="https://github.com/aspctt/paxi-extra"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg" alt="Available on GitHub"></a>
	<a href="https://modrinth.com/mod/paxi-extra"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg" alt="Available on Modrinth"></a>
	<a href="https://www.curseforge.com/minecraft/mc-mods/paxi-extra"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg" alt="Available on CurseForge"></a>
</p>

<p>This is an addon for Paxi, serving four purposes:</p>

<ul>
  <li>Making it possible to use Paxi for built-in resource packs and data packs added by mods (See <a href="https://github.com/YUNG-GANG/Paxi/issues/27">#27</a>)</li>
  <li>Restricting Paxi from auto-loading packs in <code>config -&gt; paxi -&gt; resourcepacks</code> and <code>datapacks</code> if they're not listed in <code>resourcepack_load_order.json</code> or <code>datapack_load_order.json</code></li>
  <li>Letting Paxi generate the datapack folder and JSON file on start-up instead of after world generation</li>
  <li>Adding a <code>--user--</code> flag which lets you decide where your own packs go, handy if you want the packs you picked yourself to sit above the Paxi ones, or anywhere else you like</li>
</ul>

<h3 style="text-align: center;">How to use?</h3>

<p>Write the packs you want, in the order you want them, and nothing else gets force-loaded:</p>

<pre><code>{
  "loadOrder": [
    "resourcepacks/my_base_pack.zip",
    "mod/somemod",
    "--user--",
    "resourcepacks/my_overrides.zip"
  ]
}</code></pre>

<p>Later in the list wins. Anything above <code>--user--</code> sits under the packs you picked yourself, anything below it sits on top of them. Leave <code>--user--</code> out and everything stays on top, the way Paxi normally does it.</p>

<p>For the built-in pack support you need the ID of the pack. You can check the mod's source, guess it, or use <a href="https://www.curseforge.com/minecraft/mc-mods/resource-pack-overrides">Resource Pack Overrides</a> and hold <strong>D</strong> on the resource pack screen to see the IDs of every pack. You can order <code>fabric</code> and <code>mod_resources</code> too, if you want all the mod assets to sit at a particular spot in the stack.</p>

<p><strong>It is NOT required to load <code>fabric</code> and other built-in packs with Paxi, it's there as an optional feature.</strong></p>

<p>Loading packs from anywhere in your instance rather than only Paxi's own folder already works, since Paxi itself has done that since 1.21. Keeping your packs in the normal <code>resourcepacks</code> folder also means Modrinth recognises them, so a modpack that includes them gets credited properly.</p>

<h3>Requirements</h3>

<p>Minecraft 1.21.1 or 26.1 through 26.1.2, on either NeoForge or Fabric, with Paxi and YUNG's API installed. Paxi already requires YUNG's API. Grab the file matching your loader: the jar name ends in <code>-neoforge</code> or <code>-fabric</code>.</p>

<h3>License</h3>

<p>Paxi Extra is licensed under the GNU Lesser General Public License v3.0, the same licence as the mod it is ported from. The full terms are in <a href="https://github.com/aspctt/paxi-extra/blob/main/LICENSE">LICENSE</a>.</p>

<h3>Credits</h3>

<p>Paxi Extra is a port of Paxi Plus by Lancet_, with contributions from Fyoncle, renamed to keep the two projects apart. Paxi itself is by YUNGNICKYOUNG.</p>
