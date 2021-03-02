package net.runelite.client.plugins.kitschcoxlogger;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import java.io.*;
import java.util.Arrays;
import java.util.List;


@Slf4j
@PluginDescriptor(
        name = "Cox Timers Logger",
        description = "Saves raid timers to a text file in .runelite/CoxTimersLogs for each raid, requires de0 timers",
        tags = {"Cox", "Timers", "de0", "kitsch"}
)
public class kitschCoxLoggerPlugin extends Plugin{

    @Inject
    private Client client;

    List<String> roomCompletionStringList = Arrays.asList("Combat room","Puzzle","Olm phase","Olm head duration:","<col=ef20ff>Upper level complete!","<col=ef20ff>Middle level complete!","<col=ef20ff>Lower level complete!","<col=ef20ff>Congratulations - your raid is complete!</col>","Your completed Chambers of Xeric","Ice Demon pop duration:","Muttadile tree cut duration:");
    List<String> removeFromGameMessage = Arrays.asList("Combat room ","Puzzle ","`","<col=ff0000>","</col>","<col=ef20ff>","<br>","Congratulations - your raid is complete!","complete! ");
    String roomTimesString ;

    @Subscribe
    private void onChatMessage(ChatMessage event){
        if (event.getMessage().startsWith("<col=ef20ff>The raid has begun!")) {
            reset();
        }

        for (String roomPrefix :roomCompletionStringList){
            if (event.getMessage().startsWith(roomPrefix)){
                String removeFromString = event.getMessage();
                for (String removestring : removeFromGameMessage)
                {
                    removeFromString = removeFromString.replace(removestring,"");
                }
                roomTimesString += removeFromString +"\n";
        }

        }
        if (event.getMessage().startsWith("Your completed Chambers of Xeric Challenge Mode count is:")){
            String killcount = event.getMessage().replaceAll("\\D+","");
            textfilecreator(killcount,true);
        }
        else if (event.getMessage().startsWith("Your completed Chambers of Xeric count is:")){
            String killcount = event.getMessage().replaceAll("\\D+","");
            textfilecreator(killcount,false);
        }
    }


    private void textfilecreator(String killcount,boolean isInCm) {
        File cmDir = new File(RUNELITE_DIR + "\\CoxTimerLogs\\" + client.getLocalPlayer().getName() + "\\CM");
        File coxDir = new File(RUNELITE_DIR + "\\CoxTimerLogs\\" + client.getLocalPlayer().getName() + "\\COX");

        coxDir.mkdirs();
        cmDir.mkdirs();

        FileWriter fileWriter = null;
        try {
            if (isInCm) {
                fileWriter = new FileWriter( RUNELITE_DIR + "\\CoxTimerLogs\\" + client.getLocalPlayer().getName() + "\\CM\\" + killcount.substring(4) + ".txt", true);
            } else {
                fileWriter = new FileWriter(RUNELITE_DIR + "\\CoxTimerLogs\\" + client.getLocalPlayer().getName() + "\\COX\\" + killcount.substring(4) + ".txt", true);
            }
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(roomTimesString);
            printWriter.close();
        } catch (IOException e) {
        }
        reset();
    }


    @Override
    protected void shutDown() throws Exception {
        reset();
    }

    private void reset() {
        roomTimesString = "";
    }

}
//if logged out, reset?
//add olm duration and floor splits hmm



