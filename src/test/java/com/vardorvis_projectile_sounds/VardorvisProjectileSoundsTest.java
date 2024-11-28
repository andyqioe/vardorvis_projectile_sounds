package com.vardorvis_projectile_sounds;

import com.vardorvis_projectile_sounds.VardorvisProjectileSoundsPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VardorvisProjectileSoundsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VardorvisProjectileSoundsPlugin.class);
		RuneLite.main(args);
	}
}