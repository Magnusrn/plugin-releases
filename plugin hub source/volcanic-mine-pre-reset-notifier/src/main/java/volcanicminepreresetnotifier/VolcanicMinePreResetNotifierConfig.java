package volcanicminepreresetnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("vmGroup")
public interface VolcanicMinePreResetNotifierConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "preResetNotifier",
            name = "Pre-reset notifier",
            description = "Notifies on stability change 6 mins or prior for A role and B/C role"
    )
    default boolean preResetNotifier()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "capCounter",
            name = "Cap counter",
            description = "Displays an infobox with the total vents capped"
    )
    default boolean capCounter()
    {
        return true;
    }

}