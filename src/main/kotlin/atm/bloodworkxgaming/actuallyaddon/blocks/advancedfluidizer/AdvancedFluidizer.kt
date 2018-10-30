package atm.bloodworkxgaming.actuallyaddon.blocks.advancedfluidizer

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon
import atm.bloodworkxgaming.actuallyaddon.extensions.registerMe
import atm.bloodworkxgaming.bloodyLib.registry.IHasModel
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryHelper
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AdvancedFluidizer : Block(Material.IRON), ITileEntityProvider, IHasModel {
    init {
        registerMe("advanced_fluidizer")
        setHardness(1f)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int) = TileAdvancedFluidizer()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (worldIn.isRemote) return true

        if (worldIn.getTileEntity(pos) !is TileAdvancedFluidizer) return false

        playerIn.openGui(ActuallyAddon.instance, GUIAdvancedFluidizer.GUI_ID, worldIn, pos.x, pos.y, pos.z)

        return true
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTileEntity(pos) as? TileAdvancedFluidizer ?: return

        arrayOf(tile.stackHandlerBattery, tile.stackHandlerSeed).forEach {
            for (i in 0 until it.slots) {
                InventoryHelper.spawnItemStack(worldIn, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), it.getStackInSlot(i))
            }
        }

        super.breakBlock(worldIn, pos, state)
    }
}