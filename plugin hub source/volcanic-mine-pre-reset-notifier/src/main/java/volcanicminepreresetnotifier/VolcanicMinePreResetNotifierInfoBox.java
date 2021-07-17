package volcanicminepreresetnotifier;

import java.awt.Color;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

public class VolcanicMinePreResetNotifierInfoBox extends InfoBox {
    private VolcanicMinePreResetNotifierPlugin plugin;
    private Client client;

    public VolcanicMinePreResetNotifierInfoBox(Client client, VolcanicMinePreResetNotifierPlugin plugin) {
        super(ImageUtil.getResourceStreamFromClass(VolcanicMinePreResetNotifierPlugin.class, "/rock.png"), plugin);
        this.plugin = plugin;
        this.client = client;
    }

    public String getText() {
        return String.valueOf(VolcanicMinePreResetNotifierPlugin.capCount);
    }

    public Color getTextColor() {
        return Color.WHITE;
    }

    public String getTooltip() {
        return "You have capped  " + VolcanicMinePreResetNotifierPlugin.capCount + " times.";
    }
}