package nu.nerd.swav;

import static org.bukkit.material.types.MushroomBlockTexture.ALL_CAP;
import static org.bukkit.material.types.MushroomBlockTexture.ALL_PORES;
import static org.bukkit.material.types.MushroomBlockTexture.ALL_STEM;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_EAST;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_NORTH;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_NORTH_EAST;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_NORTH_WEST;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_SOUTH;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_SOUTH_EAST;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_SOUTH_WEST;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_TOP;
import static org.bukkit.material.types.MushroomBlockTexture.CAP_WEST;
import static org.bukkit.material.types.MushroomBlockTexture.STEM_SIDES;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.material.types.MushroomBlockTexture;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

// ----------------------------------------------------------------------------
/**
 * Allow players to edit huge mushroom blocks, logs and double slabs with dyes.
 */
public class ShroomWithAView extends JavaPlugin implements Listener {
    /**
     * Singleton-like reference to this plugin.
     */
    public static ShroomWithAView PLUGIN;

    /**
     * Configuration instance.
     */
    public static Configuration CONFIG = new Configuration();

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        PLUGIN = this;

        saveDefaultConfig();
        CONFIG.reload();

        Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null) {
            _protector = new WorldGuardProtector(worldGuard);
        } else {
            getLogger().warning("WorldGuard is not available. Players can edit textures anywhere!");
        }

        Plugin logBlock = getServer().getPluginManager().getPlugin("LogBlock");
        if (logBlock != null) {
            _logger = new LogBlockLogger(logBlock);
        } else {
            getLogger().warning("LogBlock is not available. Textures editing will not be logged!");
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(getName())) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                CONFIG.reload();
                sender.sendMessage(ChatColor.GOLD + getName() + " configuration reloaded.");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Invalid command syntax.");
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * When a player right clicks on a huge mushroom block with specific dye
     * colours in a region where they can build, change the colour of the
     * clicked block face.
     */
    @SuppressWarnings("deprecation")
    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        Material oldBlockType = block.getType();
        boolean editingMushroom = oldBlockType == Material.HUGE_MUSHROOM_1 ||
                                  oldBlockType == Material.HUGE_MUSHROOM_2;
        boolean editingLog = oldBlockType == Material.LOG ||
                             oldBlockType == Material.LOG_2;
        boolean editingDoubleStep = oldBlockType == Material.DOUBLE_STEP ||
                                    oldBlockType == Material.DOUBLE_STONE_SLAB2;
        boolean editingShulkerBox = CONFIG.ALLOW_ROTATE_SHULKER_BOX &&
                                    SHULKER_BOX_MATERIALS.contains(oldBlockType);
        if (!editingMushroom && !editingLog && !editingDoubleStep && !editingShulkerBox) {
            return;
        }

        Player player = event.getPlayer();
        EquipmentSlot slot = event.getHand();
        ItemStack item = (slot == EquipmentSlot.HAND) ? player.getEquipment().getItemInMainHand()
                                                      : player.getEquipment().getItemInOffHand();
        if (item.getType() != Material.INK_SACK || (_protector != null && !_protector.canBuild(player, block.getLocation()))) {
            return;
        }

        DyeColor colour = ((Dye) item.getData()).getColor();
        AxisFace axisFace = TO_AXIS_FACE.get(event.getBlockFace());
        Material newBlockType = oldBlockType;
        byte oldData = block.getData();
        byte newData = oldData;

        if (editingMushroom) {
            MushroomBlockTexture currentTexture = MushroomBlockTexture.getByData(oldData);
            if (colour == DyeColor.WHITE) {
                if ((axisFace == AxisFace.UP || axisFace == AxisFace.DOWN || player.isSneaking()) &&
                    currentTexture != ALL_STEM) {
                    newData = MushroomBlockTexture.ALL_STEM.getData();
                } else if (currentTexture != MushroomBlockTexture.ALL_STEM &&
                           currentTexture != MushroomBlockTexture.STEM_SIDES) {
                    newData = MushroomBlockTexture.STEM_SIDES.getData();
                }
            } else if (colour == DyeColor.YELLOW) {
                MushroomBlockTexture newTexture = player.isSneaking() ? MushroomBlockTexture.ALL_PORES
                                                                      : ADD_PORES.get(currentTexture).get(axisFace);
                if (newTexture != null) {
                    newData = newTexture.getData();
                }
            } else if ((colour == DyeColor.BROWN && (CONFIG.ALLOW_TYPE_CHANGE || block.getType() == Material.HUGE_MUSHROOM_1)) ||
                       (colour == DyeColor.RED && (CONFIG.ALLOW_TYPE_CHANGE || block.getType() == Material.HUGE_MUSHROOM_2))) {
                if (CONFIG.ALLOW_TYPE_CHANGE) {
                    newBlockType = (colour == DyeColor.BROWN) ? Material.HUGE_MUSHROOM_1 : Material.HUGE_MUSHROOM_2;
                }

                MushroomBlockTexture newTexture = player.isSneaking() ? MushroomBlockTexture.ALL_CAP
                                                                      : ADD_CAP.get(currentTexture).get(axisFace);
                if (newTexture != null) {
                    newData = newTexture.getData();
                }
            }
        } else if (colour == DyeColor.WHITE) {
            if (editingLog) {
                if ((oldData & 0xC) != 0xC) {
                    newData = (byte) (oldData | 0xC);
                } else if ((oldData & 0xC) == 0xC) {
                    newData = (byte) ((oldData & 0x3) | TO_LOG_BITS.get(axisFace));
                }
            } else if (editingDoubleStep) {
                if ((oldData & 3) < 2) {
                    newData ^= 8;
                }
            } else if (editingShulkerBox) {
                byte clickedFaceData = AXIS_FACE_TO_SHULKER_DATA.get(axisFace);
                // Shulker box data values cycle modulo 6.
                byte normalisedOldData = (byte) (oldData % 6);
                if (clickedFaceData == normalisedOldData) {
                    // Opposite face simply toggles bit 0.
                    newData = (byte) (normalisedOldData ^ 1);
                } else {
                    newData = clickedFaceData;
                }
            }
        }

        // Only log and edit and consume the item if either the data value or
        // block Material changed.
        if (oldData != newData || oldBlockType != newBlockType) {
            if (_logger != null) {
                _logger.logBlockReplace(player, block.getLocation(),
                                        block.getTypeId(), oldData,
                                        newBlockType.getId(), newData);
            }

            block.setType(newBlockType);
            block.setData(newData);
            useOneItemInSurvival(player, slot, item);

            if (CONFIG.DYE_SOUND != null) {
                Location loc = block.getLocation();
                loc.getWorld().playSound(loc, CONFIG.DYE_SOUND, CONFIG.DYE_VOLUME, CONFIG.DYE_PITCH);
            }
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Consume one item from the specified ItemStack in the specified hand slot
     * if the Player is in SURVIVAL mode.
     *
     * @param player the Player.
     * @param slot the slot, which will be the main hand or off hand.
     * @param item the ItemStack.
     */
    protected void useOneItemInSurvival(Player player, EquipmentSlot slot, ItemStack item) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            ItemStack newItem;
            if (item.getAmount() == 1) {
                newItem = null;
            } else {
                newItem = item.clone();
                newItem.setAmount(item.getAmount() - 1);
            }

            if (slot == EquipmentSlot.HAND) {
                player.getEquipment().setItemInMainHand(newItem);
            } else {
                player.getEquipment().setItemInOffHand(newItem);
            }
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Create a Map<> from the clicked AxisFace of a block to the corresponding
     * MushroomBlockTexture to set the block to.
     *
     * @param up the texture to set when the upward facing face is clicked.
     * @param down the texture to set when the downward facing face is clicked.
     * @param east the texture to set when the east facing face is clicked.
     * @param north the texture to set when the north facing face is clicked.
     * @param west the texture to set when the west facing face is clicked.
     * @param south the texture to set when the south facing face is clicked.
     */
    protected static EnumMap<AxisFace, MushroomBlockTexture> axisFaceTextures(MushroomBlockTexture up, MushroomBlockTexture down,
                                                                              MushroomBlockTexture east, MushroomBlockTexture north,
                                                                              MushroomBlockTexture west, MushroomBlockTexture south) {
        EnumMap<AxisFace, MushroomBlockTexture> result = new EnumMap<AxisFace, MushroomBlockTexture>(AxisFace.class);
        result.put(AxisFace.UP, up);
        result.put(AxisFace.DOWN, down);
        result.put(AxisFace.EAST, east);
        result.put(AxisFace.NORTH, north);
        result.put(AxisFace.WEST, west);
        result.put(AxisFace.SOUTH, south);
        return result;
    }

    // ------------------------------------------------------------------------
    /**
     * The subset of BlockFaces that are axis-aligned.
     */
    enum AxisFace {
        UP, DOWN, EAST, NORTH, WEST, SOUTH
    };

    /**
     * Look up table mapping BlockFace constants to AxisFaces.
     */
    private static final EnumMap<BlockFace, AxisFace> TO_AXIS_FACE = new EnumMap<>(BlockFace.class);
    static {
        TO_AXIS_FACE.put(BlockFace.UP, AxisFace.UP);
        TO_AXIS_FACE.put(BlockFace.DOWN, AxisFace.DOWN);
        TO_AXIS_FACE.put(BlockFace.EAST, AxisFace.EAST);
        TO_AXIS_FACE.put(BlockFace.NORTH, AxisFace.NORTH);
        TO_AXIS_FACE.put(BlockFace.WEST, AxisFace.WEST);
        TO_AXIS_FACE.put(BlockFace.SOUTH, AxisFace.SOUTH);
    }

    /**
     * Look up table mapping clicked AxisFace constants to the value of bits 2
     * and 3 of the data nybble that encodes the orientation of a log.
     */
    private static final EnumMap<AxisFace, Integer> TO_LOG_BITS = new EnumMap<>(AxisFace.class);
    static {
        TO_LOG_BITS.put(AxisFace.UP, 0x0);
        TO_LOG_BITS.put(AxisFace.DOWN, 0x0);
        TO_LOG_BITS.put(AxisFace.EAST, 0x4);
        TO_LOG_BITS.put(AxisFace.NORTH, 0x8);
        TO_LOG_BITS.put(AxisFace.WEST, 0x4);
        TO_LOG_BITS.put(AxisFace.SOUTH, 0x8);
    }

    /**
     * Look up table of the new texture of a mushroom block after a cap face is
     * added.
     *
     * The first index is the current texture, and the second index is the
     * AxisFace index. If there is no change, the entry is null.
     */
    private static final EnumMap<MushroomBlockTexture, EnumMap<AxisFace, MushroomBlockTexture>> ADD_CAP = new EnumMap<>(MushroomBlockTexture.class);
    static {
        // -------- Current ---- UP -- DOWN -- EAST -- NORTH -- WEST -- SOUTH
        ADD_CAP.put(ALL_CAP, axisFaceTextures(null, null, null, null, null, null));
        ADD_CAP.put(ALL_PORES, axisFaceTextures(CAP_TOP, ALL_CAP, CAP_EAST, CAP_NORTH, CAP_WEST, CAP_SOUTH));
        ADD_CAP.put(ALL_STEM, axisFaceTextures(CAP_TOP, ALL_CAP, CAP_EAST, CAP_NORTH, CAP_WEST, CAP_SOUTH));
        ADD_CAP.put(CAP_EAST, axisFaceTextures(null, ALL_CAP, null, CAP_NORTH_EAST, CAP_WEST, CAP_SOUTH_EAST));
        ADD_CAP.put(CAP_NORTH, axisFaceTextures(null, ALL_CAP, CAP_NORTH_EAST, null, CAP_NORTH_WEST, CAP_SOUTH));
        ADD_CAP.put(CAP_NORTH_EAST, axisFaceTextures(null, ALL_CAP, null, null, CAP_NORTH_WEST, CAP_SOUTH_EAST));
        ADD_CAP.put(CAP_NORTH_WEST, axisFaceTextures(null, ALL_CAP, CAP_NORTH_EAST, null, null, CAP_SOUTH_WEST));
        ADD_CAP.put(CAP_SOUTH, axisFaceTextures(null, ALL_CAP, CAP_SOUTH_EAST, CAP_NORTH, CAP_SOUTH_WEST, null));
        ADD_CAP.put(CAP_SOUTH_EAST, axisFaceTextures(null, ALL_CAP, null, CAP_NORTH_EAST, CAP_SOUTH_WEST, null));
        ADD_CAP.put(CAP_SOUTH_WEST, axisFaceTextures(null, ALL_CAP, CAP_SOUTH_EAST, CAP_NORTH_WEST, null, null));
        ADD_CAP.put(CAP_TOP, axisFaceTextures(null, ALL_CAP, CAP_EAST, CAP_NORTH, CAP_WEST, CAP_SOUTH));
        ADD_CAP.put(CAP_WEST, axisFaceTextures(null, ALL_CAP, CAP_EAST, CAP_NORTH_WEST, null, CAP_SOUTH_WEST));
        ADD_CAP.put(STEM_SIDES, axisFaceTextures(CAP_TOP, ALL_CAP, CAP_EAST, CAP_NORTH, CAP_WEST, CAP_SOUTH));
    }

    /**
     * Look up table of the new texture of a mushroom block after a pores face
     * is added.
     *
     * The first index is the current texture, and the second index is the
     * AxisFace index. If there is no change, the entry is null.
     *
     * The mapping to tries to change the face while preserving as many cap
     * faces as possible, favouring north and west faces when several solutions
     * are possible.
     */
    private static final EnumMap<MushroomBlockTexture, EnumMap<AxisFace, MushroomBlockTexture>> ADD_PORES = new EnumMap<>(MushroomBlockTexture.class);
    static {
        // -------- Current ---- UP -- DOWN -- EAST -- NORTH -- WEST -- SOUTH
        ADD_PORES.put(ALL_CAP, axisFaceTextures(ALL_PORES, CAP_NORTH_WEST, CAP_NORTH_WEST, CAP_SOUTH_WEST, CAP_NORTH_EAST, CAP_NORTH_WEST));
        ADD_PORES.put(ALL_PORES, axisFaceTextures(null, null, null, null, null, null));
        ADD_PORES.put(ALL_STEM, axisFaceTextures(STEM_SIDES, STEM_SIDES, ALL_PORES, ALL_PORES, ALL_PORES, ALL_PORES));
        ADD_PORES.put(CAP_EAST, axisFaceTextures(ALL_PORES, null, CAP_TOP, null, null, null));
        ADD_PORES.put(CAP_NORTH, axisFaceTextures(ALL_PORES, null, null, CAP_TOP, null, null));
        ADD_PORES.put(CAP_NORTH_EAST, axisFaceTextures(ALL_PORES, null, CAP_NORTH, CAP_EAST, null, null));
        ADD_PORES.put(CAP_NORTH_WEST, axisFaceTextures(ALL_PORES, null, null, CAP_WEST, CAP_NORTH, null));
        ADD_PORES.put(CAP_SOUTH, axisFaceTextures(ALL_PORES, null, null, null, null, CAP_TOP));
        ADD_PORES.put(CAP_SOUTH_EAST, axisFaceTextures(ALL_PORES, null, CAP_SOUTH, null, null, CAP_EAST));
        ADD_PORES.put(CAP_SOUTH_WEST, axisFaceTextures(ALL_PORES, null, null, null, CAP_SOUTH, CAP_WEST));
        ADD_PORES.put(CAP_TOP, axisFaceTextures(ALL_PORES, null, null, null, null, null));
        ADD_PORES.put(CAP_WEST, axisFaceTextures(ALL_PORES, null, null, null, CAP_TOP, null));
        ADD_PORES.put(STEM_SIDES, axisFaceTextures(null, null, ALL_PORES, ALL_PORES, ALL_PORES, ALL_PORES));
    }

    /**
     * The set of all Materials that are shulker boxes.
     */
    private static final HashSet<Material> SHULKER_BOX_MATERIALS = new HashSet<>(
        Arrays.asList(Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
                      Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIME_SHULKER_BOX,
                      Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX,
                      Material.RED_SHULKER_BOX, Material.SILVER_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX));

    /**
     * Map from AxisFace to data value of shulker box with base spiral texture
     * on that face.
     */
    private static final EnumMap<AxisFace, Byte> AXIS_FACE_TO_SHULKER_DATA = new EnumMap<>(AxisFace.class);
    static {
        AXIS_FACE_TO_SHULKER_DATA.put(AxisFace.UP, (byte) 0);
        AXIS_FACE_TO_SHULKER_DATA.put(AxisFace.DOWN, (byte) 1);
        AXIS_FACE_TO_SHULKER_DATA.put(AxisFace.SOUTH, (byte) 2);
        AXIS_FACE_TO_SHULKER_DATA.put(AxisFace.NORTH, (byte) 3);
        AXIS_FACE_TO_SHULKER_DATA.put(AxisFace.EAST, (byte) 4);
        AXIS_FACE_TO_SHULKER_DATA.put(AxisFace.WEST, (byte) 5);
    }

    /**
     * Block protection implementation.
     */
    protected IProtector _protector;

    /**
     * Block edit logging implementation.
     */
    protected ILogger _logger;
} // class ShroomWithAView