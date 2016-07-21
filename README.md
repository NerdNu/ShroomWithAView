ShroomWithAView
===============
A Bukkit plugin that allows players to edit huge mushroom block face textures
with dyes.


Usage
-----
To change the colour of a mushroom block, players must right click on it with
bonemeal or red, brown or yellow dye.  To change a mushroom's texture, the
player must have permission to build in the current region (according to
`WorldGuard`).  Changes are automatically logged using `LogBlock`.

`ShroomWithAView` will *at least* change the colour of the face of the
mushroom block that was clicked.  The plugin may have to change the colour of
other faces due to restrictions on the allowable combinations of textures
that can be applied to mushroom block faces.  The plugin will attempt to change
the minimum set of other faces in order to meet the restrictions.  The allowed
combinations of cap, pore and stem textures for mushroom blocks are documented
on the [Minecraft wiki page for Mushrooms](http://minecraft.gamepedia.com/Mushroom_(block)#Block_data).

The effect of dyes on mushroom blocks is as follows:

 * **bonemeal** on the side of the block will set all vertical faces to the
   stem texture; on the top or bottom face, it sets *all* faces to the **stem**
   texture.
 * **red dye** on a face will set that face to the **red cap** texture if the
   block is a red mushroom block, or if the configuration option to allow
   the mushroom type to be changed is set.
 * **brown dye** on a face will set that face to the **brown cap** texture if
   the block is a brown mushroom block, or if the configuration option to allow
   the mushroom type to be changed is set.
 * **yellow dye** on a face will set that face to the **pore** texture.
 * *If the player is crouched* when they apply the dye, then all faces of the
   block will be set to the corresponding texture.


Configuration
-------------
 * `allow-type-change` - If true, dyes can change the type of a mushroom
   block between red and brown.
 * `dye.sound` - Name of [Sound](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html)
   to play when dye is placed, or NONE for silence.
 * `dye.volume` - Sound volume. Sound range is this value multiplied by 15 blocks.
 * `dye.pitch` - Sound pitch, in the range [0.5, 2.0].


Commands
--------
 * `/shroomwithaview reload` - Reload the configuration.


Permissions
-----------
 * `shroomwithaview.admin` - Permission to use `/shroomwithaview reload`.
