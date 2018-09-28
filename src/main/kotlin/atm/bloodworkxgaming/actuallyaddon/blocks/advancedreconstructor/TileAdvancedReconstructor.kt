package atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddonConfig
import atm.bloodworkxgaming.bloodyLib.energy.EnergyStorageBase
import atm.bloodworkxgaming.bloodyLib.networking.NBTSerializationState
import atm.bloodworkxgaming.bloodyLib.tile.TileEntityTickingBase
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe
import de.ellpeck.actuallyadditions.mod.misc.SoundHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.SoundCategory
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import kotlin.math.min

class TileAdvancedReconstructor : TileEntityTickingBase() {


    companion object {
        val recipes: List<LensConversionRecipe> = ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES
        const val INPUT_SIZE = 3
        const val OUTPUT_SIZE = INPUT_SIZE
        const val TOTAL_SIZE = INPUT_SIZE + OUTPUT_SIZE + 1
        const val ENERGY_CAPACITY = 10000
    }

    val stackHandlerInput = ItemStackHandlerInput()
    val stackHandlerOutput = ItemStackHandlerOutput()
    val stackHandlerBattery = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            batteryChanged.setTrue()
            markDirty()
        }
    }

    private val inputChanged = NBTSerializationState(this)
    private val outputChanged = NBTSerializationState(this)
    private val batteryChanged = NBTSerializationState(this)
    private val energyChanged = NBTSerializationState(this)

    val energyStorage = EnergyStorageBase(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, energyChanged)

    private var counter = 0

    override fun updateTickRemote() {
        val battery = stackHandlerBattery.getStackInSlot(0)
        if (energyStorage.energyStored < energyStorage.maxEnergyStored && !battery.isEmpty && battery.hasCapability(CapabilityEnergy.ENERGY, null)) {
            val eng = battery.getCapability(CapabilityEnergy.ENERGY, null)
            if (eng != null && eng.canExtract()) {
                val energy = eng.extractEnergy(energyStorage.maxEnergyStored - energyStorage.energyStored, false)
                if (energyStorage.receiveEnergyInternal(energy, false) > 0) {
                    energyChanged.scheduleUpdate()
                    batteryChanged.scheduleUpdate()
                }
            }
        }

        if (counter++ <= ActuallyAddonConfig.sleepTime)
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
            val energyCount = (energyStorage.energyStored / (recipe.energyUsed * ActuallyAddonConfig.energyModifier)).toInt()
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
            val energyCost = (effectiveCount * recipe.energyUsed * ActuallyAddonConfig.energyModifier).toInt()
            energyStorage.extractEnergyInternal(energyCost, false)

            laseredItem = true

            inputChanged.scheduleUpdate()
            outputChanged.scheduleUpdate()
            energyChanged.scheduleUpdate()

        }

        if (laseredItem) {
            world.playSound(null, pos, SoundHandler.reconstructor, SoundCategory.BLOCKS, 1.0f, 1.0f)
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

    override fun writeClientDataToNBT(tagCompound: NBTTagCompound, incremental: Boolean): NBTTagCompound {
        return tagCompound.apply {
            if (inputChanged.getAndSetFalse() || incremental)
                setTag("input", stackHandlerInput.serializeNBT())

            if (outputChanged.getAndSetFalse() || incremental)
                setTag("output", stackHandlerOutput.serializeNBT())

            if (batteryChanged.getAndSetFalse() || incremental)
                setTag("battery", stackHandlerBattery.serializeNBT())

            if (energyChanged.getAndSetFalse() || incremental)
                setTag("energy", energyStorage.serializeNBT())
        }
    }

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


}

