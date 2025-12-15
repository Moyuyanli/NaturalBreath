package cn.chahuyun.enchantment.container;

import cn.chahuyun.enchantment.entity.block.EntitySteelFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * @author Moyuyanli
 * @Date 2024/11/7 17:09
 */
public class ContainerSteelFurnace extends Container {


    private final EntitySteelFurnace tileEntity;


    public ContainerSteelFurnace(InventoryPlayer playerInventory, EntitySteelFurnace tileEntity) {
        this.tileEntity = tileEntity;

        // 添加熔炉的槽位
        this.addSlotToContainer(new Slot(tileEntity, 0, 56, 17));
        this.addSlotToContainer(new Slot(tileEntity, 1, 116, 35));
        this.addSlotToContainer(new Slot(tileEntity, 2, 56, 53));

        // 添加玩家的库存槽位
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // 添加玩家的热键栏槽位
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    /**
     * 确定提供的播放器是否可以使用此容器
     *
     * @param playerIn
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            listener.sendAllWindowProperties(this, tileEntity);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemStack = stackInSlot.copy();

            if (index < 3) {
                // 从熔炉槽位（0 输入，1 输出，2 燃料）shift 取出 -> 玩家背包/快捷栏
                if (!this.mergeItemStack(stackInSlot, 3, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包/快捷栏 shift 放入熔炉
                // 1) 能熔炼 -> 只塞原料槽 0
                if (!FurnaceRecipes.instance().getSmeltingResult(stackInSlot).isEmpty()) {
                    if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 2) 不可熔炼或 0 号塞满 -> 若可作为燃料，塞燃料槽 2
                else if (ForgeEventFactory.getItemBurnTime(stackInSlot) > 0) {
                    if (!this.mergeItemStack(stackInSlot, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // 3) 既不能熔炼也不能当燃料 -> 不允许快速放入
                    return ItemStack.EMPTY;
                }
            }

            // 源槽位为空则清空，否则刷新显示
            if (stackInSlot.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            // 数量未变化说明没搬走，返回空表示本次 shift 无效
            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            // 通知槽位：玩家已取走对应物品
            slot.onTake(playerIn, stackInSlot);
        }

        return itemStack;
    }
}
