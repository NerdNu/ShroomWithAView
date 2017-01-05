package nu.nerd.swav;

import org.bukkit.Location;
import org.bukkit.entity.Player;

// ----------------------------------------------------------------------------
/**
 * Interface abstracting the operations of the block protection plugin.
 * 
 * The main purpose of this is to allow classes to be dynamically compiled even
 * if the WorldGuard plugin is unavailable.
 */
public interface IProtector {
    // ------------------------------------------------------------------------
    /**
     * Return true if the specified player can build at the specified location.
     * 
     * @param player the player.
     * @param loc the location.
     * @return true if the specified player can build at the specified location.
     */
    public boolean canBuild(Player player, Location loc);
} // class IProtector