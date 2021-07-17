package volcanicminepreresetnotifier;
import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;


@Slf4j
@PluginDescriptor(
        name = "Volcanic Mine pre-reset notifier and Cap Counter",
        description = "Notifies on stability change 6 mins or prior for A role and B/C role and counts total number of caps",
        tags = {"vent", "mine", "volcanic", "vm", "kitsch", "cap",}
)
public class VolcanicMinePreResetNotifierPlugin extends Plugin
{
    private static final int VARBIT_STABILITY = 5938;
    private static final int VARBIT_TIME_REMAINING =5944;
    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;
    private static final int BLOCKING_ANIMATION = 832;
    private static final int MINING_ANIMATION = 8347;
    private static final int VARBIT_POINTS = 5934;
    private boolean Notification_Triggered = false;
    private int Stability_Reached_100 = 0;
    public static int capCount = 0 ;
    private int compareScore = 0;
    private int lastAnimationID = 0;
    private VolcanicMinePreResetNotifierInfoBox VMIB;

    @Provides
    VolcanicMinePreResetNotifierConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VolcanicMinePreResetNotifierConfig.class);
    }

    @Inject
    private VolcanicMinePreResetNotifierConfig config;

    @Inject
    private Notifier notifier;

    @Inject
    private Client client;

    @Inject
    private InfoBoxManager infoBoxManager;


    @Subscribe
    private void onVarbitChanged(VarbitChanged event)
    {
        if (config.preResetNotifier())
        {
            if (this.client.getVarbitValue(VARBIT_STABILITY)  == 100) {
                Stability_Reached_100 = 100 ; //activates once stability initially reaches 100 to prevent notification before stability is initially fixed
            } else if(Stability_Reached_100 - this.client.getVarbitValue(VARBIT_STABILITY) > 0) {
                if(this.client.getVarbitValue(VARBIT_TIME_REMAINING) >595 && isInVM() &&!Notification_Triggered) { //Notifies user if vent stability drops from 100 on or before 6 mins
                    notifier.notify("Fix your vent!");
                    Notification_Triggered = true;
                }
            }
        }
        if (config.capCounter())
        {
            if(this.client.getVarbitValue(VARBIT_POINTS) -compareScore ==50 && (lastAnimationID == BLOCKING_ANIMATION || lastAnimationID == MINING_ANIMATION))
            {
                if (this.VMIB != null)
                {
                    this.infoBoxManager.removeInfoBox(this.VMIB);
                    this.VMIB = null;

                }
                this.VMIB = new VolcanicMinePreResetNotifierInfoBox(this.client, this);
                capCount += 1;
                this.infoBoxManager.addInfoBox(this.VMIB);
            }
            compareScore = this.client.getVarbitValue(VARBIT_POINTS);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged change) {
        if (change.getKey().equals("capCounter"))
        {
            if (!config.capCounter())
            {
                capCount=0;
                this.infoBoxManager.removeInfoBox(this.VMIB);
                this.VMIB = null;
            }
        }
    }


    @Override
    protected void shutDown() throws Exception
    {
        reset();
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if(!isInVM())
        {
            reset();
            return;
        }

        if (client.getLocalPlayer().getAnimation()!=-1)
        {
            lastAnimationID = client.getLocalPlayer().getAnimation();
        }
    }

    private void reset()
    {
        Notification_Triggered = false;
        Stability_Reached_100 = 0;
        lastAnimationID=0;
        capCount=0;
        compareScore=0;
        this.infoBoxManager.removeInfoBox(this.VMIB);
        this.VMIB = null;
    }

    //isInVM function taken from Hipipis Plugin hub VMPlugin
    private boolean isInVM()
    {
        return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_NORTH ||
                WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_SOUTH;
    }
}