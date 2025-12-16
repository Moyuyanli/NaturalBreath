package cn.chahuyun.enchantment.entity.block;

import cn.chahuyun.enchantment.items.ItemExamples;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

/**
 * @author Moyuyanli
 * @Date 2024/11/10 1:22
 */
public class EntitySpiritGathering extends AbstractEntityInventory implements ITickable, IGuiHolder {

    public static String name = "spirit_gathering";

    /**
     * 完成一次“聚灵”的所需 tick 数（约 4 秒）
     */
    private static final int MAX_PROGRESS = 80;
    /**
     * 当前汇聚进度（tick 计数）
     */
    private int progress;

    public EntitySpiritGathering() {
        // 0 = 输入槽（金锭）, 1 = 输出槽（自然金锭）
        super(2);
    }


    @Override
    public ModularPanel buildUI(GuiData guiData, PanelSyncManager panelSyncManager, UISettings uiSettings) {
        ModularPanel panel = ModularPanel.defaultPanel(getName());

        // 输入槽：居中偏上（相对于整个 GUI）
        ItemSlot input = new ItemSlot()
                .slot(inventory, 0)
                .pos(80, 25);

        // 输出槽：居中偏下（相对于整个 GUI）
        ItemSlot output = new ItemSlot()
                .slot(inventory, 1)
                .pos(80, 55);

        // 中间进度/光效：居中（相对于整个 GUI）
        ProgressWidget progressWidget = new ProgressWidget()
                .size(22, 15)
                .pos(77, 40)
                .texture(GuiTextures.PROGRESS_ARROW, 22)
                .value(new DoubleSyncValue(
                        () -> this.progress / (double) MAX_PROGRESS,
                        v -> this.progress = (int) (v * MAX_PROGRESS)
                ));

        // 把所有控件直接挂到 panel 上
        panel.child(input)
                .child(output)
                .child(progressWidget);

        // 最后再把玩家背包绑定到底部
        panel.bindPlayerInventory();

        return panel;
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);

        // 只在输入是金锭且输出槽能接收结果时工作
        boolean canWork = !input.isEmpty()
                && input.getItem() == Items.GOLD_INGOT
                && (output.isEmpty()
                || (output.getItem() == ItemExamples.GOLD_NATURAL
                && output.getCount() < output.getMaxStackSize()));

        if (canWork) {
            progress++;
            if (progress >= MAX_PROGRESS) {
                // 消耗 1 个金锭
                inventory.extractItem(0, 1, false);
                // 生成 1 个自然金锭
                ItemStack result = new ItemStack(ItemExamples.GOLD_NATURAL);
                if (output.isEmpty()) {
                    inventory.setStackInSlot(1, result);
                } else {
                    output.grow(1);
                    inventory.setStackInSlot(1, output);
                }
                progress = 0;
                markDirty();
            } else {
                markDirty();
            }
        } else if (progress != 0) {
            // 条件不满足时重置进度
            progress = 0;
            markDirty();
        }
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
        // 0 号槽只接受金锭，1 号槽不允许通过自动化直接放入
        if (index == 0) {
            return stack.getItem() == Items.GOLD_INGOT;
        }
        return false;
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
        compound.setTag("inventory", this.inventory.serializeNBT());
        compound.setInteger("progress", progress);
        return super.writeToNBT(compound);
    }
}
