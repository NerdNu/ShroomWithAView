package nu.nerd.swav;

import org.bukkit.Sound;

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

    /**
     * Sound of dye being applied.
     */
    public Sound DYE_SOUND;

    /**
     * Volume of the dye sound.
     */
    public float DYE_VOLUME;

    /**
     * Pitch of the dye sound.
     */
    public float DYE_PITCH;

    // ------------------------------------------------------------------------
    /**
     * Reload the configuration.
     */
    public void reload() {
        ShroomWithAView.PLUGIN.reloadConfig();
        ALLOW_TYPE_CHANGE = ShroomWithAView.PLUGIN.getConfig().getBoolean("allow-type-change");
        DYE_SOUND = loadSound("dye.sound", "dye sound");
        DYE_VOLUME = (float) ShroomWithAView.PLUGIN.getConfig().getDouble("dye.volume");
        DYE_PITCH = (float) ShroomWithAView.PLUGIN.getConfig().getDouble("dye.pitch");
    }

    // ------------------------------------------------------------------------
    /**
     * Load a sound from the String configuration value at the given path,
     * interpreting NONE as null.
     *
     * Log a warning if the sound name in the configuration is invalid.
     *
     * @param path the path to the configuration string.
     * @param description how to describe the sound in any logged warning.
     * @return the Sound, or null for NONE or invalid names.
     */
    protected Sound loadSound(String path, String description) {
        String soundName = ShroomWithAView.PLUGIN.getConfig().getString(path);
        try {
            return soundName.equalsIgnoreCase("NONE") ? null : Sound.valueOf(soundName);
        } catch (IllegalArgumentException ex) {
            ShroomWithAView.PLUGIN.getLogger().warning("Invalid " + description + " name: \"" + soundName + "\"");
            return null;
        }
    }
} // class Configuration