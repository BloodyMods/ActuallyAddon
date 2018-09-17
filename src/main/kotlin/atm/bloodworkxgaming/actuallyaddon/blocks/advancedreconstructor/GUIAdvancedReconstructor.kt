package atm.bloodworkxgaming.actuallyaddon.blocks.advancedreconstructor

import atm.bloodworkxgaming.actuallyaddon.ActuallyAddon
import atm.bloodworkxgaming.actuallyaddon.gui.GuiBase
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation

class GUIAdvancedReconstructor(tileEntity: TileAdvancedReconstructor, container: Container)
    : GuiBase<TileAdvancedReconstructor>(tileEntity, container, BACKGROUND) {

    init {
        xSize = WIDTH
        ySize = HEIGHT
    }

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 178

        val BACKGROUND = ResourceLocation(ActuallyAddon.MOD_ID, "textures/gui/advanced_reconstructor_gui.png")
        val GUI_ID = 1
    }
}