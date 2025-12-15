package cn.chahuyun.enchantment.entity.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

/**
 * 抽象方块实体
 *
 * @author Moyuyanli
 * @Date 2024/11/10 1:31
 */
public abstract class AbstractEntityInventory extends TileEntity implements IInventory {


    protected final ItemStackHandler inventory;

    public AbstractEntityInventory(Integer invSize) {
        inventory = new ItemStackHandler(invSize);
    }


    /**
     * 返回清单中的插槽数。
     */
    @Override
    public int getSizeInventory() {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * 返回给定插槽中的堆栈。
     *
     * @param index
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    /**
     * 从库存槽中最多移除指定数量的项目，并将其返回到新堆栈中。
     *
     * @param index
     * @param count
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.extractItem(index, count, false);
    }

    /**
     * 从给定的槽中移除堆栈并返回它。
     *
     * @param index
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inventory.extractItem(index, Integer.MAX_VALUE, false);
    }

    /**
     * 将给定的项目堆栈设置为库存中的指定插槽 (可以是制作或装甲部分)。
     *
     * @param index
     * @param stack
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setStackInSlot(index, stack);
    }

    /**
     * 返回库存槽的最大堆栈大小。似乎总是64，可能会延长。
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * 由于与容器冲突，请勿将此方法重命名为caninteract配
     *
     * @param player
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    /**
     * 如果允许自动化将给定的堆栈 (忽略堆栈大小) 插入到给定的插槽中，则返回true。用于
     * Gui使用Slot.isItemValid
     *
     * @param index
     * @param stack
     */
    @Override
    public abstract boolean isItemValidForSlot(int index, ItemStack stack);

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }


    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void markDirty() {

    }


    @Override
    public abstract String getName() ;

    @Override
    public abstract boolean hasCustomName();

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
