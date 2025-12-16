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

    /**
     * 构造一个具有指定大小的物品栏处理器。
     *
     * @param invSize 物品栏的槽位数量
     */
    public AbstractEntityInventory(Integer invSize) {
        inventory = new ItemStackHandler(invSize);
    }


    /**
     * 返回清单中的插槽数。
     *
     * @return 槽位的数量
     */
    @Override
    public int getSizeInventory() {
        return inventory.getSlots();
    }

    /**
     * 判断该物品栏是否为空（未实现具体逻辑）。
     *
     * @return 总是返回 false
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * 返回给定插槽中的堆栈。
     *
     * @param index 插槽索引
     * @return 对应插槽中的物品堆栈
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    /**
     * 从库存槽中最多移除指定数量的项目，并将其返回到新堆栈中。
     *
     * @param index 插槽索引
     * @param count 要减少的数量
     * @return 减少后的物品堆栈
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.extractItem(index, count, false);
    }

    /**
     * 从给定的槽中移除堆栈并返回它。
     *
     * @param index 插槽索引
     * @return 移除的物品堆栈
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inventory.extractItem(index, Integer.MAX_VALUE, false);
    }

    /**
     * 将给定的项目堆栈设置为库存中的指定插槽 (可以是制作或装甲部分)。
     *
     * @param index 插槽索引
     * @param stack 要放入的物品堆栈
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setStackInSlot(index, stack);
    }

    /**
     * 返回库存槽的最大堆栈大小。似乎总是64，可能会延长。
     *
     * @return 最大堆叠数，默认为64
     */
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * 检查玩家是否能够与此方块实体交互。
     * 注意：由于与容器冲突，请勿将此方法重命名为canInteractWith。
     *
     * @param player 当前操作的玩家对象
     * @return 若距离小于等于8格则返回 true，否则返回 false
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
    }

    /**
     * 打开物品栏时调用的方法（空实现）。
     *
     * @param player 打开物品栏的玩家
     */
    @Override
    public void openInventory(EntityPlayer player) {

    }

    /**
     * 关闭物品栏时调用的方法（空实现）。
     *
     * @param player 关闭物品栏的玩家
     */
    @Override
    public void closeInventory(EntityPlayer player) {

    }

    /**
     * 如果允许自动化将给定的堆栈 (忽略堆栈大小) 插入到给定的插槽中，则返回true。用于
     * Gui使用Slot.isItemValid
     * Slot判断后,会调用一次isItemValidForSlot
     *
     * @param index 插槽索引
     * @param stack 待验证的物品堆栈
     * @return 是否允许插入该物品至对应槽位
     */
    @Override
    public abstract boolean isItemValidForSlot(int index, ItemStack stack);

    /**
     * 获取字段值（空实现）。
     *
     * @param id 字段ID
     * @return 始终返回0
     */
    @Override
    public int getField(int id) {
        return 0;
    }

    /**
     * 设置字段值（空实现）。
     *
     * @param id    字段ID
     * @param value 新的字段值
     */
    @Override
    public void setField(int id, int value) {

    }

    /**
     * 获取字段总数（空实现）。
     *
     * @return 始终返回0
     */
    @Override
    public int getFieldCount() {
        return 0;
    }

    /**
     * 清空所有槽位的内容。
     */
    @Override
    public void clear() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }


    /**
     * 标记当前TileEntity已修改，确保其所在的区块稍后会被保存到磁盘上。
     * 防止游戏认为它没有变化而跳过保存。
     */
    @Override
    public void markDirty() {

    }


    /**
     * 获取物品栏名称。
     *
     * @return 物品栏名称字符串
     */
    @Override
    public abstract String getName() ;

    /**
     * 判断是否有自定义名称。
     *
     * @return 是否有自定义名称
     */
    @Override
    public abstract boolean hasCustomName();

    /**
     * 获取显示名称组件（空实现）。
     *
     * @return 显示名称文本组件，始终返回null
     */
    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}
