//package cn.chahuyun.enchantment.gui;
//
//import net.minecraft.client.gui.inventory.GuiContainer;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.inventory.Container;
//import net.minecraft.util.ResourceLocation;
//
///**
// * @author Moyuyanli
// * @Date 2024/11/7 17:10
// */
//public class GuiSteelFurnace extends GuiContainer {
//
//    private static final ResourceLocation TEXTURE = new ResourceLocation("enchantment:textures/gui/steel_furnace.png");
//
//    public GuiSteelFurnace(Container inventorySlotsIn) {
//        super(inventorySlotsIn);
//    }
//
//    /**
//     * 绘制此容器的背景层 (在项目后面)。
//     *
//     * @param partialTicks
//     * @param mouseX
//     * @param mouseY
//     */
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.getTextureManager().bindTexture(TEXTURE);
//        int x = (this.width - this.xSize) / 2;
//        int y = (this.height - this.ySize) / 2;
//        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        String title = I18n.format("tile.enchantment.block_steel_furnace.name");
//        this.fontRenderer.drawString(title, (this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2), 6, 4210752);
//        this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
//    }
//}
