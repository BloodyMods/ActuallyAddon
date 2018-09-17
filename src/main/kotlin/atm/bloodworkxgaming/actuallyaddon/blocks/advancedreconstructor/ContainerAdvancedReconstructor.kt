package atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory

class ContainerAdvancedReconstructor(inventory: IInventory, val te: TileAdvancedReconstructor) : Container() {
    override fun canInteractWith(playerIn: EntityPlayer): Boolean =
            !te.isInvalid && playerIn.getDistanceSq(te.pos.add(0.5, 0.5, 0.5)) <= 64
}