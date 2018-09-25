package atm.bloodworkxgaming.actuallyaddon.proxy

import atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor.ContainerAdvancedReconstructor
import atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor.GUIAdvancedReconstructor
import atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor.TileAdvancedReconstructor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

object GuiProxy : IGuiHandler {
    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val te = world.getTileEntity(pos)


        return when (te) {
            is TileAdvancedReconstructor -> GUIAdvancedReconstructor(te, ContainerAdvancedReconstructor(player.inventory, te))
            else -> null
        }


    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val te = world.getTileEntity(pos)


        return when (te) {
            is TileAdvancedReconstructor -> ContainerAdvancedReconstructor(player.inventory, te)
            else -> null
        }
    }
}