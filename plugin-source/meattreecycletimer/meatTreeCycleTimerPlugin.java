package net.runelite.client.plugins.meattreecycletimer;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "Muttadiles Cycle Timer",
        description = "adds an overlay timer when cutting the Meat Tree",
        tags = {"woodcutting", "cycle","cox","meat","tree"}
)

public class meatTreeCycleTimerPlugin extends Plugin
{
    private boolean startedChopping = false;
    public static int ticksToChop = 4;
    private GameObject meatTreeObject = null;
    private boolean treeChoppedDown = false;

    @Inject
    private Client client;

    @Inject
    private meatTreeOverlay meatTreeOverlay;

    @Inject
    private OverlayManager overlayManager;

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        String CHOPPING_STRING = "You swing your axe...";

        if (event.getMessage().equals(CHOPPING_STRING) && !startedChopping && meatTreeObject!=null && client.getLocalPlayer().getLocalLocation().distanceTo(meatTreeObject.getLocalLocation())<1000)
        {
            startedChopping = true;
        }

        String CHOP_STRING = "You hack away some of the meat.";

        if (event.getMessage().equals(CHOP_STRING)){
            ticksToChop = 5;
        }

        if (Text.removeTags(event.getMessage()).equals("The raid has begun!"))
        {
            reset();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (startedChopping)
        {
            if (ticksToChop>0)
            {
                ticksToChop--;
            }
            else
            {
                ticksToChop =4;
            }

            if (!treeChoppedDown && meatTreeObject!=null && client.getLocalPlayer().getLocalLocation().distanceTo(meatTreeObject.getLocalLocation())<1000)
            {
                this.overlayManager.add(this.meatTreeOverlay);
            }

            else
            {
                this.overlayManager.remove(this.meatTreeOverlay);
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (event.getGameObject().getId() == 30012)
        {
            if (startedChopping)
            {
                treeChoppedDown = true;
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (event.getGameObject().getId() == 30012)
        {
            meatTreeObject = event.getGameObject();
        }
    }

    private void reset()
    {
        startedChopping = false;
        ticksToChop = 4;
        meatTreeObject = null;
        treeChoppedDown = false;
        this.overlayManager.remove(this.meatTreeOverlay);
    }
}