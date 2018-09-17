package atm.bloodworkxgaming.actuallyaddon.blocks

import atm.bloodworkxgaming.actuallyaddon.extensions.registerMe
import atm.bloodworkxgaming.bloodyLib.registry.IHasModel
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.world.World

class AdvancedReconstructor : Block(Material.IRON), ITileEntityProvider, IHasModel {
    init {
        registerMe("advanced_reconstructor")
    }

    override fun createNewTileEntity(worldIn: World, meta: Int) = TileAdvancedReconstructor()
}