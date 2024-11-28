package com.vardorvis_projectile_sounds;

import com.google.inject.Provides;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.sound.sampled.*;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Vardorvis Projectile Sounds",
	description = "Map vardorvis projectiles to a custom sound",
	tags = {"dt2", "boss", "pvm"}
)
public class VardorvisProjectileSoundsPlugin extends Plugin
{
	// Vardorvis' Head Projectile IDs
	private static final int MAGIC_PROJECTILE = 2520;
	private static final int RANGE_PROJECTILE = 2521;

	// Sound ids to map to
	private static final int MAGIC_PROJECTILE_SOUND_ID = 123;
	private static final int RANGE_PROJECTILE_SOUND_ID = 7120;

	public HashMap<Integer, Sound> projectileSounds = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private VardorvisProjectileSoundConfig config;

	private static final File SOUND_DIR = new File(RuneLite.RUNELITE_DIR, "SoundSwapper");

	@Provides
	VardorvisProjectileSoundConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VardorvisProjectileSoundConfig.class);
	}

	@Override
    protected void startUp() throws Exception
    {
        try
        {
            if (!SOUND_DIR.exists())
            {
                SOUND_DIR.mkdir();
            }
        }
        catch (SecurityException securityException)
        {
            log.error("Attempted to create SoundSwapper directory and a security exception prompted a fault");
        }
		// Magic projectile
		tryLoadSound(projectileSounds, "123", MAGIC_PROJECTILE_SOUND_ID);

		// Range projectile
		tryLoadSound(projectileSounds, "7120", RANGE_PROJECTILE_SOUND_ID);
        
    }

	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileMoved)
	{
		Projectile projectile = projectileMoved.getProjectile();
		if (projectile.getId() == RANGE_PROJECTILE)
		{
			playCustomSound(projectileSounds.get(RANGE_PROJECTILE_SOUND_ID), 100);
		}
		else if (projectile.getId() == MAGIC_PROJECTILE)
		{
			playCustomSound(projectileSounds.get(MAGIC_PROJECTILE_SOUND_ID), 100);
		}
	}

	private void playCustomSound(Sound sound, int volume)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(sound.getFormat(), sound.getBytes(), 0, sound.getNumBytes());

            if (volume != -1)
            {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                if (control != null)
                {
                    control.setValue((float)(volume / 2 - 45));
                }
            }

            clip.setFramePosition(0);
            clip.start();
        } catch (LineUnavailableException e)
        {
            log.warn("Failed to play custom sound");
        }
    }

	private boolean tryLoadSound(HashMap<Integer, Sound> sounds, String sound_name, Integer sound_id)
    {
        File sound_file = new File(SOUND_DIR, sound_name + ".wav");

        if (sound_file.exists())
        {
            try
            {
                InputStream fileStream = new BufferedInputStream(new FileInputStream(sound_file));
                AudioInputStream stream = AudioSystem.getAudioInputStream(fileStream);

                int streamLen = (int)stream.getFrameLength() * stream.getFormat().getFrameSize();
                byte[] bytes = new byte[streamLen];
                stream.read(bytes);

                Sound sound = new Sound(bytes, stream.getFormat(), streamLen);
                sounds.put(sound_id, sound);

                return true;
            }
            catch (UnsupportedAudioFileException | IOException e)
            {
                log.warn("Unable to load custom sound " + sound_name, e);
            }
        }

        return false;
    }
}