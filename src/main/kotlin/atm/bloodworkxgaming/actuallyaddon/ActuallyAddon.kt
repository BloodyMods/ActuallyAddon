package atm.bloodworkxgaming.actuallyaddon

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon.MOD_ID
import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon.VERSION
import atm.bloodworkxgaming.actuallyaddon.proxy.GuiProxy
import atm.bloodworkxgaming.actuallyaddon.proxy.SubCommonProxy
import atm.bloodworkxgaming.bloodyLib.registry.AbstractDataRegistry
import atm.bloodworkxgaming.bloodyLib.util.AbstractCommonHandler
import atm.bloodworkxgaming.bloodyLib.util.BloodyModMain
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry

@Mod(modid = MOD_ID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", version = VERSION, dependencies = "required-after:actuallyadditions;required:bloodylib")
object ActuallyAddon : BloodyModMain(CommonHandler) {
    const val MOD_ID = "actuallyaddon"
    const val VERSION = "@VERSION@"

    @Mod.Instance
    lateinit var instance: ActuallyAddon

    @SidedProxy(serverSide = "atm.bloodworkxgaming.actuallyaddon.proxy.ServerProxy", clientSide = "atm.bloodworkxgaming.actuallyaddon.proxy.ClientProxy")
    lateinit var proxy: SubCommonProxy

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, GuiProxy)
    }
}

object DataRegistry : AbstractDataRegistry()
object CommonHandler : AbstractCommonHandler(modItems = ModItems, modBlocks = ModBlocks)