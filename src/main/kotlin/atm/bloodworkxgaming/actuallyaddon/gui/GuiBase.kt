package atm.bloodworkxgaming.actuallyaddon.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

/**
 * Created by Jonas on 06.05.2017.
 */
abstract class GuiBase<T : TileEntity>(val tile: T, container: Container, val background: ResourceLocation) : GuiContainer(container) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.textureManager.bindTexture(background)

        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize)
    }


    protected fun getBarScaled(pixels: Int, count: Int, max: Int): Int {
        return if (count > 0 && max > 0) count * pixels / max else 0
    }
}
