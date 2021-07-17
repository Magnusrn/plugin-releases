package volcanicminepreresetnotifier;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VolcanicMinePreResetNotifierPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VolcanicMinePreResetNotifierPlugin.class);
		RuneLite.main(args);
	}
}