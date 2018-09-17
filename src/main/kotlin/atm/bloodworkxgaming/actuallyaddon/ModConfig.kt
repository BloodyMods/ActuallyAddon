package atm.bloodworkxgaming.actuallyaddon

import net.minecraftforge.common.config.Config

@Config(modid = ActuallyAddon.MOD_ID)
object ModConfig {
    @JvmField
    var toolDurability: Int = 49

    @JvmField
    var disableWoodenTools: Boolean = true

    @JvmField
    var forceAxe: Boolean = true
    @JvmField
    var hurtPlayer: Boolean = true
    @JvmField
    var whitelistedNoneTools: Array<String> = emptyArray()
    @JvmField
    var blacklistedTools: Array<String> = arrayOf("minecraft:wooden_axe")
    @JvmField
    var stickDropChange: Double = 0.1
    @JvmField
    var disableFlintDrops: Boolean = true

    @JvmField
    var flintToGravel: Int = 3
}