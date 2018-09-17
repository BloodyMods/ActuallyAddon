package atm.bloodworkxgaming.actuallyaddon.extensions

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon
import atm.bloodworkxgaming.actuallyaddon.DataRegistry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

fun Item.registerMe(name: String) {
    this.registryName = ResourceLocation(ActuallyAddon.MOD_ID, name)
    this.unlocalizedName = name

    DataRegistry.ITEMS += this
}

fun Block.registerMe(name: String) {
    this.registryName = ResourceLocation(ActuallyAddon.MOD_ID, name)
    this.unlocalizedName = name

    DataRegistry.BLOCKS += this
}