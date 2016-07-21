package nu.nerd.swav;

// ----------------------------------------------------------------------------
/**
 * Configuration wrapper.
 */
public class Configuration {
    /**
     * If true, dyes can change the type of a mushroom block between red and
     * brown.
     */
    public boolean ALLOW_TYPE_CHANGE;

    // ------------------------------------------------------------------------
    /**
     * Reload the configuration.
     */
    public void reload() {
        ShroomWithAView.PLUGIN.reloadConfig();
        ALLOW_TYPE_CHANGE = ShroomWithAView.PLUGIN.getConfig().getBoolean("allow-type-change");
    }
} // class Configuration