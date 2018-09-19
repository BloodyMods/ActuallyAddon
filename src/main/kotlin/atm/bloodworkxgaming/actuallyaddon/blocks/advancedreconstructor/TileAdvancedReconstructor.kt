package atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor

import atm.bloodworkxgaming.bloodyLib.energy.EnergyStorageBase
import atm.bloodworkxgaming.bloodyLib.tile.TileEntityBase
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe
import de.ellpeck.actuallyadditions.mod.misc.SoundHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.min

class TileAdvancedReconstructor : TileEntityBase(), ITickable {
    companion object {
        val recipes: List<LensConversionRecipe> = ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES
        const val energyModifier = 1.5
        const val INPUT_SIZE = 3
        const val OUTPUT_SIZE = INPUT_SIZE
        const val TOTAL_SIZE = INPUT_SIZE + OUTPUT_SIZE
        const val ENERGY_CAPACITY = 10000
    }

    val stackHandlerInput = ItemStackHandlerInput()
    val stackHandlerOutput = ItemStackHandlerOutput()
    val stackHandlerBattery = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) = markDirty()
    }
    val energyStorage = EnergyStorageBase(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, this)

    private val inputChanged = BooleanSerializationState()
    private val outputChanged = BooleanSerializationState()
    private val batteryChanged = BooleanSerializationState()
    private val energyChanged = BooleanSerializationState()

    private var counter = 0
    override fun update() {
        world ?: return
        if (world.isRemote) return

        var didStuff = false

        val battery = stackHandlerBattery.getStackInSlot(0)
        if (energyStorage.energyStored <= energyStorage.maxEnergyStored && !battery.isEmpty && battery.hasCapability(CapabilityEnergy.ENERGY, null)) {
            val eng = battery.getCapability(CapabilityEnergy.ENERGY, null)
            if (eng != null && eng.canExtract()) {
                val energy = eng.extractEnergy(energyStorage.maxEnergyStored - energyStorage.energyStored, false)
                energyStorage.receiveEnergyInternal(energy, false)
                didStuff = true
                energyChanged.setTrue()
                batteryChanged.setTrue()
            }
        }

        if (counter++ <= 10)
            return

        counter = 0

        var laseredItem = false

        for (slotIndex in 0 until stackHandlerInput.slots) {
            val stack = stackHandlerInput.getStackInSlot(slotIndex)

            if (stack.isEmpty)
                continue


            val inCount = stack.count
            val recipe = recipes.firstOrNull { it.matches(stack, ActuallyAdditionsAPI.lensDefaultConversion) }

            recipe ?: continue

            // checks how much energy there is to craft stuff
            val energyCount = (energyStorage.energyStored / (recipe.energyUsed * energyModifier)).toInt()
            if (energyCount <= 0) continue

            // checks how many fit in the output slot
            val outCount = recipe.output.count
            val outStack = recipe.output.copy()
            val extractCount = outCount * min(inCount, energyCount)
            outStack.count = extractCount

            // checks how much it can insert and then actually does the insertion
            val leftOver = stackHandlerOutput.insertItemInternal(slotIndex, outStack)
            val effectiveCount = extractCount - leftOver.count

            if (effectiveCount <= 0) continue

            stackHandlerInput.extractItemInternal(slotIndex, effectiveCount)
            val energyCost = (effectiveCount * recipe.energyUsed * energyModifier).toInt()
            energyStorage.extractEnergyInternal(energyCost, false)

            didStuff = true
            laseredItem = true

            inputChanged.setTrue()
            outputChanged.setTrue()
            energyChanged.setTrue()

        }

        if (laseredItem) {
            world.playSound(null, pos, SoundHandler.reconstructor, SoundCategory.BLOCKS, 1.0f, 1.0f)
        }

        if (didStuff) {
            markDirtyNBT()
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability === CapabilityEnergy.ENERGY
                || super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {

        return when {
            capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY -> when (facing) {
                NORTH, SOUTH, WEST, EAST -> stackHandlerInput as T
                UP, DOWN -> stackHandlerOutput as T
                else -> null
            }

            capability === CapabilityEnergy.ENERGY -> return energyStorage as T

            else -> super.getCapability(capability, facing)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("input", stackHandlerInput.serializeNBT())
        compound.setTag("output", stackHandlerOutput.serializeNBT())
        compound.setTag("battery", stackHandlerBattery.serializeNBT())
        compound.setTag("energy", energyStorage.serializeNBT())

        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        if (compound.hasKey("input"))
            stackHandlerInput.deserializeNBT(compound.getCompoundTag("input"))
        if (compound.hasKey("output"))
            stackHandlerOutput.deserializeNBT(compound.getCompoundTag("output"))
        if (compound.hasKey("battery"))
            stackHandlerBattery.deserializeNBT(compound.getCompoundTag("battery"))

        if (compound.hasKey("energy"))
            energyStorage.deserializeNBT(compound.getTag("energy") as NBTTagInt?)

        super.readFromNBT(compound)
    }

    /*override fun writeClientDataToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        return tagCompound.apply {
            if (inputChanged.getAndSetFalse())
                setTag("input", stackHandlerInput.serializeNBT())

            if (outputChanged.getAndSetFalse())
                setTag("output", stackHandlerOutput.serializeNBT())

            if (batteryChanged.getAndSetFalse())
                setTag("battery", stackHandlerBattery.serializeNBT())

            if (energyChanged.getAndSetFalse())
                setTag("energy", energyStorage.serializeNBT())
        }
    }*/

    inner class ItemStackHandlerOutput : ItemStackHandler(OUTPUT_SIZE) {
        override fun onContentsChanged(slot: Int) {
            markDirty()
            outputChanged.setTrue()
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
        internal fun insertItemInternal(slot: Int, stack: ItemStack) = super.insertItem(slot, stack, false)
    }

    inner class ItemStackHandlerInput : ItemStackHandler(INPUT_SIZE) {
        override fun onContentsChanged(slot: Int) {
            markDirty()
            inputChanged.setTrue()
        }

        // override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY!!
        internal fun extractItemInternal(slot: Int, amount: Int) = super.extractItem(slot, amount, false)
    }

    data class BooleanSerializationState(private var value: Boolean = true) {
        fun getAndInvert(): Boolean {
            value = !value
            return !value
        }

        fun getAndSetFalse(): Boolean {
            val temp = value
            value = false
            return temp
        }

        fun setTrue() {
            value = true
        }
    }
}

