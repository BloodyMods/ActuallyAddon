package atm.bloodworkxgaming.actuallyaddon.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

abstract class GuiBase<T : TileEntity>(val tile: T, container: Container, val background: ResourceLocation) : GuiContainer(container) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.textureManager.bindTexture(background)

        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize)


        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2

        drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY, x, y)
    }


    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)

        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2

        drawGuiContainerForegroundLayer(mouseX, mouseY, x, y)
    }

    abstract fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int, offsetX: Int, offsetY: Int)
    abstract fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int, offsetX: Int, offsetY: Int)

    protected fun getBarScaled(pixels: Int, count: Int, max: Int): Int {
        return if (count > 0 && max > 0) count * pixels / max else 0
    }
}
