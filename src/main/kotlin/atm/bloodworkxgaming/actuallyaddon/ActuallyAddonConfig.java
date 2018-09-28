package atm.bloodworkxgaming.actuallyaddon;

import net.minecraftforge.common.config.Config;

@Config(modid = ActuallyAddon.MOD_ID, name = "bloodymods/actuallyaddon")
public class ActuallyAddonConfig {
    @Config.Comment("How much more energy than a normal reconstructor it should use")
    public static double energyModifier = 1.5;

    @Config.Comment("How long to wait in between checking for new items")
    public static int sleepTime = 20;
}
