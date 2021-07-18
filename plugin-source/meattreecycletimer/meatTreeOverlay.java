package net.runelite.client.plugins.meattreecycletimer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class meatTreeOverlay extends OverlayPanel {
    private final Client client;
    private final meatTreeCycleTimerPlugin plugin;

    @Inject
    public meatTreeOverlay(Client client, meatTreeCycleTimerPlugin plugin) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        this.panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Tick:   ") + 15, 0));
        this.panelComponent.getChildren().add(LineComponent.builder().left("Tick: ").right(String.valueOf(meatTreeCycleTimerPlugin.ticksToChop)).build());
        return super.render(graphics);
    }
}