package atm.bloodworkxgaming.actuallyaddon

import atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor.AdvancedReconstructor
import atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor.TileAdvancedReconstructor
import atm.bloodworkxgaming.bloodyLib.registry.AbstractModBlocks
import atm.bloodworkxgaming.bloodyLib.util.RegistryUtils
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistry

object ModBlocks : AbstractModBlocks(DataRegistry) {
    val advancedReconstructor = AdvancedReconstructor()


    override fun registerBlocks(registry: IForgeRegistry<Block>) {
        super.registerBlocks(registry)

        RegistryUtils.registerTileEntity<TileAdvancedReconstructor>(ResourceLocation(ActuallyAddon.MOD_ID, "advanced_reconstructor"))
    }
}