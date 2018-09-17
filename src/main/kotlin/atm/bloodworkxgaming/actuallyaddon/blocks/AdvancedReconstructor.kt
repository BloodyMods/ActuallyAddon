package atm.bloodworkxgaming.actuallyaddon.blocks

import atm.bloodworkxgaming.actuallyaddon.extensions.registerMe
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.world.World

class AdvancedReconstructor : Block(Material.IRON), ITileEntityProvider {
    init {
        registerMe("advanced_reconstructor")
    }

    override fun createNewTileEntity(worldIn: World, meta: Int) = TileAdvancedReconstructor()
}