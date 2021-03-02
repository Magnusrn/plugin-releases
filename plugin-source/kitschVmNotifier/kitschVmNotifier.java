package net.runelite.client.plugins.kitschVmNotifier;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;


@Slf4j
@PluginDescriptor(
        name = "kitschVmNotifier",
        description = "Notifies on stability change 6 mins or prior for A role and B/C role",
        tags = {"Vent", "Mine", "Volcanic", "VM", "kitsch"}
)
public class kitschVmNotifier extends Plugin
{
    private static final int VARBIT_STABILITY = 5938;
    private static final int VARBIT_TIME_REMAINING =5944;
    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;
    boolean Notification_Triggered = false;
    int Stability_Reached_100 = 0;

    @Inject
    private Notifier notifier;

    @Inject
    private Client client;

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if (this.client.getVarbitValue(VARBIT_STABILITY)  == 100) {
            Stability_Reached_100 = 100 ; //activates once stability initially reaches 100 to prevent notification before stability is initially fixed
        } else if(Stability_Reached_100 - this.client.getVarbitValue(VARBIT_STABILITY) > 0) {
            if(this.client.getVarbitValue(VARBIT_TIME_REMAINING) >595 && isInVM() &&!Notification_Triggered) { //Notifies user if vent stability drops from 100 on or before 6 mins
                notifier.notify("Fix your vent!");
                Notification_Triggered = true;
            }
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        reset();
    }

    private void reset()
    {
        Notification_Triggered = false;
        Stability_Reached_100 = 0;
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (!isInVM())
        {
            reset();
        }
    }

    //isInVM function taken from Hipipis Plugin hub VMPlugin
    private boolean isInVM()
    {
        return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_NORTH ||
                WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_SOUTH;
    }
}



