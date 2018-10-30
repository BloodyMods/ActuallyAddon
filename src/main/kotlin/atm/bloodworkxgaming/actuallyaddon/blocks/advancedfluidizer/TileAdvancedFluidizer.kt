package atm.bloodworkxgaming.actuallyaddon.blocks.advancedfluidizer

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddonConfig
import atm.bloodworkxgaming.bloodyLib.energy.EnergyStorageBase
import atm.bloodworkxgaming.bloodyLib.fluid.FluidTankBase
import atm.bloodworkxgaming.bloodyLib.networking.NBTSerializationState
import atm.bloodworkxgaming.bloodyLib.tile.TileEntityTickingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class TileAdvancedFluidizer : TileEntityTickingBase() {
    companion object {
        const val INPUT_SIZE = 1
    }

    val stackHandlerSeed = ItemStackHandlerSeed()
    val stackHandlerBattery = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            batteryChanged.scheduleUpdate()
        }
    }

    private val seedChanged = NBTSerializationState(this)
    private val batteryChanged = NBTSerializationState(this)
    private val energyChanged = NBTSerializationState(this)
    private val fluidInputChanged = NBTSerializationState(this)
    private val fluidOutputChanged = NBTSerializationState(this)

    val energyStorage = EnergyStorageBase(ActuallyAddonConfig.advancedFluidizer.maxPowerCapacity, ActuallyAddonConfig.advancedFluidizer.maxPowerIn, 0, energyChanged)
    val fluidTankInput = FluidTankBase(fluidInputChanged, ActuallyAddonConfig.advancedFluidizer.fluidCapacity)
    val fluidTankOutput = FluidTankBase(fluidOutputChanged, ActuallyAddonConfig.advancedFluidizer.fluidCapacity)

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

        if (fluidTankInput.fluidAmount >= 1000 && !stackHandlerSeed.getStackInSlot(0).isEmpty) {
            val stack = fluidTankInput.drainInternal(1000, true)
            val fluidOut = FluidStack(FluidRegistry.LAVA, 1000)
            val outSim = fluidTankOutput.fillInternal(fluidOut, false)

            if (stack != null && stack.amount >= 1000 && outSim == 1000) {
                fluidTankOutput.fillInternal(fluidOut, true)
                stackHandlerSeed.extractItemInternal(0, 1)

                fluidInputChanged.scheduleUpdate()
                fluidOutputChanged.scheduleUpdate()
                energyChanged.scheduleUpdate()
                seedChanged.scheduleUpdate()
            }
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability === CapabilityEnergy.ENERGY
                || capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {

        return when {
            capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY -> when (facing) {
                UP, DOWN -> stackHandlerSeed as T
                else -> null
            }

            capability === CapabilityEnergy.ENERGY -> return energyStorage as T
            capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY -> when (facing) {
                NORTH, WEST -> fluidTankInput as T
                SOUTH, EAST -> fluidTankOutput as T
                else -> null
            }

            else -> super.getCapability(capability, facing)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.apply {
            setTag("seed", stackHandlerSeed.serializeNBT())
            setTag("battery", stackHandlerBattery.serializeNBT())
            setTag("energy", energyStorage.serializeNBT())
            setTag("input", fluidTankInput.writeToNBT(NBTTagCompound()))
            setTag("output", fluidTankOutput.writeToNBT(NBTTagCompound()))
        }
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        if (compound.hasKey("seed"))
            stackHandlerSeed.deserializeNBT(compound.getCompoundTag("seed"))

        if (compound.hasKey("battery"))
            stackHandlerBattery.deserializeNBT(compound.getCompoundTag("battery"))

        if (compound.hasKey("energy"))
            energyStorage.deserializeNBT(compound.getTag("energy") as NBTTagInt?)

        if (compound.hasKey("input"))
            fluidTankInput.readFromNBT(compound.getCompoundTag("input"))

        if (compound.hasKey("output"))
            fluidTankOutput.readFromNBT(compound.getCompoundTag("output"))

        super.readFromNBT(compound)
    }

    override fun writeClientDataToNBT(tagCompound: NBTTagCompound, incremental: Boolean): NBTTagCompound {
        return tagCompound.apply {
            if (seedChanged.getAndSetFalse() || incremental)
                setTag("seed", stackHandlerSeed.serializeNBT())

            if (batteryChanged.getAndSetFalse() || incremental)
                setTag("battery", stackHandlerBattery.serializeNBT())

            if (energyChanged.getAndSetFalse() || incremental)
                setTag("energy", energyStorage.serializeNBT())

            if (fluidInputChanged.getAndSetFalse() || incremental)
                setTag("input", fluidTankInput.writeToNBT(NBTTagCompound()))

            if (fluidOutputChanged.getAndSetFalse() || incremental)
                setTag("output", fluidTankOutput.writeToNBT(NBTTagCompound()))
        }
    }

    inner class ItemStackHandlerSeed : ItemStackHandler(INPUT_SIZE) {
        override fun onContentsChanged(slot: Int) {
            seedChanged.scheduleUpdate()
        }

        // override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY!!
        internal fun extractItemInternal(slot: Int, amount: Int) = super.extractItem(slot, amount, false)
    }


}

