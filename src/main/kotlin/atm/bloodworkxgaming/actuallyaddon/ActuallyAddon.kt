package atm.bloodworkxgaming.actuallyaddon

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon.MOD_ID
import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon.VERSION
import atm.bloodworkxgaming.bloodyLib.registry.AbstractDataRegistry
import atm.bloodworkxgaming.bloodyLib.util.BloodyModMain
import atm.bloodworkxgaming.actuallyaddon.handler.CommonHandler
import atm.bloodworkxgaming.actuallyaddon.proxy.SubCommonProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy

@Mod(modid = MOD_ID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", version = VERSION)
object ActuallyAddon : BloodyModMain(CommonHandler) {
    const val MOD_ID = "actuallyaddon"
    const val VERSION = "0.1"

    @SidedProxy(serverSide = "atm.bloodworkxgaming.actuallyaddon.proxy.ServerProxy", clientSide = "atm.bloodworkxgaming.actuallyaddon.proxy.ClientProxy")
    lateinit var proxy: SubCommonProxy
}

object DataRegistry : AbstractDataRegistry()