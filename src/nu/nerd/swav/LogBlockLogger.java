package nu.nerd.swav;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.diddiz.LogBlock.Actor;
import de.diddiz.LogBlock.LogBlock;

// ----------------------------------------------------------------------------
/**
 * {@link ILogger} implementation in terms of the LogBlock plugin.
 */
public class LogBlockLogger implements ILogger {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param plugin a reference to the LogBlock plugin.
     */
    public LogBlockLogger(Plugin plugin) {
        _logBlock = (LogBlock) plugin;
    }

    // ------------------------------------------------------------------------
    /**
     * @see nu.nerd.swav.ILogger#logBlockReplace(org.bukkit.entity.Player,
     *      org.bukkit.Location, int, byte, int, byte)
     */
    @Override
    public void logBlockReplace(Player player, Location loc, int oldTypeId, byte oldData, int newTypeId, byte newData) {
        Actor actor = Actor.actorFromEntity(player);
        _logBlock.getConsumer().queueBlockReplace(actor, loc,
                                                  oldTypeId, oldData,
                                                  newTypeId, newData);
    }

    // ------------------------------------------------------------------------
    /**
     * Reference to LogBlock.
     */
    protected LogBlock _logBlock;
} // class LogBlockLogger