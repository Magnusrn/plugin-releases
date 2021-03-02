package net.runelite.client.plugins.KitschVMPlugin;
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
import net.runelite.api.widgets.Widget;


@Slf4j
@PluginDescriptor(
        name = "KitschVMPlugin",
        description = "Calculates Unknown vent at VM given 2 known vents",
        tags = {"Vent", "Mine", "Volcanic", "VM"}
)
public class KitschVMPlugin extends Plugin
{
    private static final int VARBIT_STABILITY = 5938;
    private static final int VARBIT_TIME_REMAINING =5944;
    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;
    private static final int VARBIT_VENT_A = 5939;
    private static final int VARBIT_VENT_B = 5940;
    private static final int VARBIT_VENT_C = 5942;
    private static final int HUD_WIDGET_ID = 611;
    private static final int HUD_VARBIT_VENT_A = 19;
    private static final int VARBIT_VENT_A_STATUS = 5936; //0 if unblocked, 1 if blocked

    int Stability_Change;
    int compareStability = 50; //idk what to do for this shit. used to compare the stability to determine the difference at the beginning of the unknown vent calculations
    int Known_Vent_1 ;
    int Known_Vent_2 ;
    int Unknown_Vent_Value ;
    int firstVentCalculation = 0;
    int secondVentCalculation = 0 ;



    @Inject
    private Notifier notifier;

    @Inject
    private Client client;

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if (compareStability != this.client.getVarbitValue((VARBIT_STABILITY)) && this.client.getVarbitValue(VARBIT_TIME_REMAINING ) >50) { //runs function only if stability is different by comparing y values and not in lobby
            Stability_Change = this.client.getVarbitValue(VARBIT_STABILITY) - compareStability;
            compareStability = this.client.getVarbitValue(VARBIT_STABILITY); //sets to = stability after stability change has been determined

            if (secondVentCalculation != calcUnknownVent(Known_Vent_1,Known_Vent_2,Stability_Change) || secondVentCalculation !=0) {
                secondVentCalculation = calcUnknownVent(Known_Vent_1,Known_Vent_2,Stability_Change);
            }

            if (firstVentCalculation != 0 && secondVentCalculation != 0 ) {
                if (firstVentCalculation>=secondVentCalculation) {
                    if (this.client.getVarbitValue(VARBIT_VENT_A_STATUS) == 1){
                        Unknown_Vent_Value = secondVentCalculation;
                    } else {
                        Unknown_Vent_Value =100-secondVentCalculation;
                    }
                } else {
                    Unknown_Vent_Value = 100-secondVentCalculation;
                }
            }
            firstVentCalculation = secondVentCalculation ;
        }
    }

    public int calcUnknownVent(int vent1, int vent2, int stabilityChange) {
        int vent3 = ((25 - stabilityChange) * 3) - (Math.abs(vent1 - 50) + Math.abs(vent2 - 50)) + 50;
        return vent3;
    }

    @Override
    protected void shutDown() throws Exception
    {
        reset();
    }

    private void reset()
    {
        compareStability = 50;
        Unknown_Vent_Value  = 0;
        firstVentCalculation = 0;
        secondVentCalculation = 0 ;
    }

    @Subscribe
    public void onGameTick(GameTick tick) {

        if (this.client.getVarbitValue(VARBIT_VENT_A) != 127) { //sets values for vents if value is known
            Known_Vent_1 = this.client.getVarbitValue(VARBIT_VENT_A);
        }
        if (this.client.getVarbitValue(VARBIT_VENT_B) != 127) {
            if (this.client.getVarbitValue(VARBIT_VENT_A) != 127) {
                Known_Vent_2 = this.client.getVarbitValue(VARBIT_VENT_B);
            } else {
                Known_Vent_1 = this.client.getVarbitValue(VARBIT_VENT_B);
                Known_Vent_2 = this.client.getVarbitValue(VARBIT_VENT_C);
            }
        } else if (this.client.getVarbitValue(VARBIT_VENT_C) != 127) {
            Known_Vent_2 = this.client.getVarbitValue(VARBIT_VENT_C);
        }



        Widget widget = client.getWidget(HUD_WIDGET_ID, HUD_VARBIT_VENT_A);
        String text = "Vent A: " + Unknown_Vent_Value + "%";
        widget.setText(text);

        if (!isInVM())
        {
            reset();
        }
        if (this.client.getVarbitValue(VARBIT_TIME_REMAINING) == 498) //time when mine resets at 5 mins
        {
            reset();
           log.info("Shits resetting");
        }
    }

    //isInVM function taken from Hipipis Plugin hub VMPlugin
    private boolean isInVM()
    {
        return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_NORTH ||
                WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_SOUTH;
    }
}

//problems
//gets confused if the estimated first value is wrong and block is wrong
//calculations are happening before 2 vents are checked
//if vent2>vent and vent wrong direction, swap
//fix nullpoint exception
//change vents to be just vent 1/vent2/vent 3, knowing a/b/c unnecessary(if all 3 known then hide value)



