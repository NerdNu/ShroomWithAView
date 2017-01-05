package nu.nerd.swav;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

// ----------------------------------------------------------------------------
/**
 * {@link IProtector} implementation in terms of the WorldGuard plugin.
 */
public class WorldGuardProtector implements IProtector {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param plugin a reference to the WorldGuard plugin.
     */
    public WorldGuardProtector(Plugin plugin) {
        _worldGuard = (WorldGuardPlugin) plugin;
    }

    // --------------------------------------------------------------------------
    /**
     * @see nu.nerd.swav.IProtector#canBuild(org.bukkit.entity.Player,
     *      org.bukkit.Location)
     */
    @Override
    public boolean canBuild(Player player, Location loc) {
        return _worldGuard.canBuild(player, loc);
    }

    // ------------------------------------------------------------------------
    /**
     * Reference to WorldGuard.
     */
    protected WorldGuardPlugin _worldGuard;
} // class WorldGuardProtector