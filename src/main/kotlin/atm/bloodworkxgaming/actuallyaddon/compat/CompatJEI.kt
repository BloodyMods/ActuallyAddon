package atm.bloodworkxgaming.actuallyaddon.compat

import atm.bloodworkxgaming.actuallyaddon.ModBlocks
import net.minecraft.item.ItemStack

@JEIPlugin
class CompatJEI : IModPlugin {
    override fun register(registry: IModRegistry) {
        registry.addRecipeCatalyst(ItemStack(ModBlocks.advancedReconstructor), RECONSTRUCTOR_NAME)
    }

    companion object {
        const val RECONSTRUCTOR_NAME = "actuallyadditions.reconstructor"
    }
}