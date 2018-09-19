package atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IPlantable
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import kotlin.math.min


class ContainerAdvancedReconstructor(inventory: IInventory, private val te: TileAdvancedReconstructor) : Container() {
    override fun canInteractWith(playerIn: EntityPlayer): Boolean =
            !te.isInvalid && playerIn.getDistanceSq(te.pos.add(0.5, 0.5, 0.5)) <= 64

    init {
        addOwnSlots()
        addPlayerSlots(inventory)
    }


    private fun addPlayerSlots(playerInventory: IInventory) {
        // main inv
        for (row in 0..2) {
            for (col in 0..8) {
                val x = 10 + col * 18
                val y = 96 + row * 18
                this.addSlotToContainer(Slot(playerInventory, col + row * 9 + 9, x, y))
            }
        }

        // hot bar
        for (row in 0..8) {
            val x = 10 + row * 18
            val y = 154
            this.addSlotToContainer(Slot(playerInventory, row, x, y))
        }
    }

    private fun addOwnSlots() {
        val input = this.te.stackHandlerInput
        val output = this.te.stackHandlerOutput
        var x = 64
        val yInput = 32
        val yOutput = 68

        addSlotToContainer(SlotItemHandler(te.stackHandlerBattery, 0, 10, 68))


        // Add our own slots
        for (i in 0 until min(input.slots, output.slots)) {
            addSlotToContainer(SlotItemHandler(input, i, x, yInput).apply { this.slotNumber = i + 3 })
            addSlotToContainer(SlotOutput(output, i, x, yOutput))

            x += 18
        }
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? {
        var previous = ItemStack.EMPTY

        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val current = slot.stack
            previous = current.copy()

            // If item is in our custom Inventory or armor slot
            if (index < INV_START) {

                // try to place in player inventory / action bar
                if (!this.mergeItemStack(current, INV_START, HOTBAR_END + 1, true)) {

                    return ItemStack.EMPTY
                }

                slot.onSlotChanged()

            } else {

                if (current.item is IPlantable) {
                    if (!this.mergeItemStack(current, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                }


                if (!this.mergeItemStack(current, 0, TileAdvancedReconstructor.TOTAL_SIZE, false)) {
                    return ItemStack.EMPTY
                }
            }// Item is in inventory / hotbar, try to place in custom inventory or armor slots

            if (current.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (current.count == previous.count) {
                return ItemStack.EMPTY
            }

            // slot.onTake(playerIn, current);
            slot.onSlotChanged()
        }

        return previous
    }

    companion object {
        private val INV_START = TileAdvancedReconstructor.TOTAL_SIZE
        private val INV_END = INV_START + 26
        private val HOTBAR_START = INV_END + 1
        private val HOTBAR_END = HOTBAR_START + 8
    }
}

class SlotOutput(itemHandler: IItemHandler, index: Int, xPosition: Int, yPosition: Int) : SlotItemHandler(itemHandler, index, xPosition, yPosition)