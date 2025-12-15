package cn.chahuyun.enchantment.entity.block;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.GuiSyncManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

/**
 * @author Moyuyanli
 * @Date 2024/11/10 1:22
 */
public class EntitySpiritGathering extends AbstractEntityInventory implements ITickable, IGuiHolder {

    public static String name = "spirit_gathering";

    private int progress;

    public EntitySpiritGathering() {
        super(1);
    }

    @Override
    public ModularPanel buildUI(GuiData guiData, GuiSyncManager guiSyncManager) {
        ModularPanel panel = ModularPanel.defaultPanel(getName());
        panel.bindPlayerInventory();
        return panel;
    }

    @Override
    public void update() {

    }

    /**
     * 如果允许自动化将给定的堆栈 (忽略堆栈大小) 插入到给定的插槽中，则返回true。用于
     * Gui使用Slot.isItemValid
     *
     * @param index
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    @Override
    public boolean hasCustomName() {
        return false;
    }

    /**
     * @param compound
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        this.progress = compound.getInteger("progress");
    }

    /**
     * @param compound
     * @return
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory",this.inventory.serializeNBT());
        compound.setInteger("progress", progress);
        return super.writeToNBT(compound);
    }
}
