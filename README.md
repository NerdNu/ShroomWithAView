ShroomWithAView
===============
A Bukkit plugin that allows players to edit huge mushroom block face textures
with dyes.

`ShroomWithAView` also lets players use bonemeal to:

 * set all sides of a log to its bark texture, or back to a 
   regular log,
 * switch stone and sandstone double slabs between their stacked and seamless
   textures.
 * rotate the base (spiral texture) of shulker boxes to the face that was 
   clicked, or the opposite face if the clicked face is already the base.

In order to edit block textures, players must have permission to build in the
`WorldGuard` region containing the block.  Changes are logged using
`LogBlock`.


Usage With Mushroom Blocks
--------------------------
To change the colour of a mushroom block, players must right click on it with
bonemeal or red, brown or yellow dye.

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


Usage With Logs
---------------
Right clicking any face of a log while holding **bonemeal** will turn it into
an **all-bark** log, if it is not one already.

Clicking any face of an all-bark log with **bonemeal** will turn the clicked
face, and the face on the opposite side of the log, back to the **growth rings**
texture.


Usage With Stone and Sandstone Double Slabs
-------------------------------------------
Right clicking any face of a smooth stone or sandstone double slab block with
**bonemeal** will toggle it's texture between the divided and seamless textures.


Usage With Shulker Boxes
------------------------
Right clicking any face of a shulker box with **bonemeal** will rotate the box
so that the face is the base (bottom) of the box. If the clicked face is
_already_ the base, the box will rotate so that the _opposite_ face is the base.

To avoid opening the shulker box when rotating it, crouch while clicking.

NOTE: Occasionally, shulker boxes can get stuck in the open position. This is
probably just a client-side visual glitch. The problem goes away on server
restart, or possibly on relog.


Item Consumption
----------------
In SURVIVAL mode, one dye item will be consumed every time the clicked block's
texture changes, or if the player is crouching when they click with the dye.

In CREATIVE mode, the dye item is not consumed.


Configuration
-------------
 * `allow-type-change` - If true, dyes can change the type of a mushroom
   block between red and brown.
 * `allow-rotate-shulker-box` - If true, shulker boxes can be rotated by 
   right clicking on them with bonemeal.
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
