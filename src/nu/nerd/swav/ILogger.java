package nu.nerd.swav;

import org.bukkit.Location;
import org.bukkit.entity.Player;

// ----------------------------------------------------------------------------
/**
 * Interface abstracting the operations of the block edit logging plugin.
 * 
 * The main purpose of this is to allow classes to be dynamically compiled even
 * if the LogBlock plugin is unavailable.
 */
public interface ILogger {
    /**
     * Log the replacement of one block type/data with another.
     * 
     * @param player the player doing the edit.
     * @param loc the Location of the block.
     * @param oldTypeId the old Material id.
     * @param oldData the old data value of the block.
     * @param newTypeId the new Material id.
     * @param newData the new data value of the block.
     */
    public void logBlockReplace(Player player, Location loc,
                                int oldTypeId, byte oldData,
                                int newTypeId, byte newData);
} // class ILogger