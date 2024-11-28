package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import com.google.common.collect.Lists;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import static ahud.adaptivehud.AdaptiveHUD.complexVARS;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class Misc {
    private final MinecraftClient client = MinecraftClient.getInstance();

//    public String stat(@LocalFlagName("type") List<String> type, @LocalFlagName("stat") List<String> stat) {
////        String statTypeStr = type.getFirst();
////        StatType<?> statType;
////        if (statTypeStr.equals("broken")) statType = Stats.BROKEN;
//        try {
//            LOGGER.info("MADE IT HERRE!!!");
//            String statisticKey = "stone";
//
//            String a = String.valueOf(Stats.MINED.getOrCreateStat(Stats.MINED.getRegistry().get(Identifier.of(statisticKey))).getValue());
//            return a;
//            // Parse the key to an Identifier
////            Identifier statId = Identifier.of(statisticKey);
////            LOGGER.info("1");
////            Identifier test = Registries.CUSTOM_STAT.get(statId);
////            // Resolve the stat type and value (e.g., entity killed)
////            LOGGER.info("2");
////
////            Stat<?> statistic = Stats.CUSTOM.getOrCreateStat(test);
////            LOGGER.info("aaa");
////            return String.valueOf(statistic.getValue());
//////        Identifier statIdentifier = Registries.CUSTOM_STAT.get(Identifier.of(stat.getFirst()));
//////        if (Stats.CUSTOM.hasStat(statIdentifier)) {
//////
//////            Stat<Identifier> statistic = Stats.CUSTOM.getOrCreateStat(statIdentifier);
//////            return String.valueOf(client.player.getStatHandler().getStat(statistic));
//////        };
////
//////        client.player.getStatHandler().getStat(Stat.getName(new StatType())
//////        Lists.newArrayList(new StatType[]{Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED});
//////        return null;
//        } catch (Exception e) {
//            LOGGER.info(e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }

    public String key_pressed(@LocalFlagName("key") List<String> scancode) {
        // All scancodes can be found at "https://www.glfw.org/docs/3.3/group__keys.html".
        // For example, "R" is 82.
        return String.valueOf(GLFW.glfwGetKey(client.getWindow().getHandle(), Integer.parseInt(scancode.getFirst())) == GLFW.GLFW_PRESS);
    }

    public String cps(@LocalFlagName("key") List<String> scancode) {
        return String.valueOf(complexVARS.getCPSClicks(Integer.parseInt(scancode.getFirst())));
    }

    public String client_mod_name() {
        return ClientBrandRetriever.getClientModName(); // "fabric"
    }

    public String game_version() {
        return client.getGameVersion(); // "Fabric"
    }

    public String client_version_type() {
        return "release".equalsIgnoreCase(this.client.getVersionType()) ? "" : this.client.getVersionType(); // "Fabric"
    }

    public String version() {
        return SharedConstants.getGameVersion().getName();
    }

    public String mods() {
        return String.valueOf(FabricLoader.getInstance().getAllMods().size());
    }

    public String moon_phase() {
        return String.valueOf(client.world.getMoonPhase() + 1);
    }

    public String chat_open() {
        return String.valueOf(client.currentScreen instanceof ChatScreen);
    }

    public String screen_open() {
        return String.valueOf(client.currentScreen != null);
    }

    public String screen_name() {
        if (client.currentScreen != null) {
            return String.valueOf(!client.currentScreen.getTitle().getString().isBlank() ? client.currentScreen.getTitle().getString() : "null");
        }
        return null;
    }

    public Iterable<Entity> entities() {
        return client.world.getEntities();
    }
}
