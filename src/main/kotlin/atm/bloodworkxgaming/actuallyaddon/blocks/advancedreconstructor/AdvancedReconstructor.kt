package atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon
import atm.bloodworkxgaming.actuallyaddon.extensions.registerMe
import atm.bloodworkxgaming.bloodyLib.registry.IHasModel
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AdvancedReconstructor : Block(Material.IRON), ITileEntityProvider, IHasModel {
    init {
        registerMe("advanced_reconstructor")
    }

    override fun createNewTileEntity(worldIn: World, meta: Int) = TileAdvancedReconstructor()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true

        worldIn.getTileEntity(pos) as? TileAdvancedReconstructor ?: return false

        playerIn.openGui(ActuallyAddon.instance, GUIAdvancedReconstructor.GUI_ID, worldIn, pos.x, pos.y, pos.z)

        return true
    }
}