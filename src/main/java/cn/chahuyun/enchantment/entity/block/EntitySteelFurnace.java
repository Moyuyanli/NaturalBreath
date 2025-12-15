package cn.chahuyun.enchantment.entity.block;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.GuiSyncManager;
import com.cleanroommc.modularui.widgets.ItemSlot;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

import static net.minecraft.launchwrapper.LogWrapper.log;

/**
 * @author Moyuyanli
 * @Date 2024/11/6 15:59
 */
@SuppressWarnings("NullableProblems")
public final class EntitySteelFurnace extends TileEntity implements IInventory, ITickable, IGuiHolder {
    private int progress;
    private int fuel;

    // 2 代表“我们需要两个槽位”——一个放输入，一个放输出。
    // 我们指定 0 号槽为输入，1 号槽为输出,2号燃料槽。
    public final ItemStackHandler inventory = new ItemStackHandler(3);

    @Override
    public void update() {
        // 所有的逻辑全部在这里发生…… 至于这里会发生什么完全要看
        // 想象力。
        // 这也是 Minecraft 的魅力之一——由原版熔炉所产生的“操纵
        // 物品”的概念，产生了无数机器，为科技主题 Mod 提供了
        // 理论可能。

        // 如上文所述，只要这个 TileEntity 还存在于这个世界上，
        // 这个 TileEntity 的 update 方法每一个 tick 就都会
        // 被调用一次。我们不应也没有必要在这里写死循环。
        if (!world.isRemote) {
            if (fuel > 0) {
                // IItemHandler 的下标从 0 开始。
                ItemStack input = inventory.getStackInSlot(0);
                if (input.isEmpty()) {
//                    for (EntityItem entity : this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.pos.up()))) {
//                        final ItemStack result = FurnaceRecipes.instance().getSmeltingResult(entity.getItem());
//                        if (!result.isEmpty()) {
//                            // insertItem 的第一个参数是要放入的物品
//                            // 第二个参数是目标槽位的编号
//                            // 第三个参数指定“该操作是不是仅模拟”（simulate/dry-run）。
//                            // 若第三个参数为 true，则该操作不会真的把物品放入物品栏中。
//                            // 常用于测试目标物品栏。
//                            entity.setItem(inventory.insertItem(0, entity.getItem(), false));
//                            progress = 0;
//                            break;
//                        }
//                    }
                } else {
                    if (!inventory.extractItem(0, 1, true).isEmpty() && !FurnaceRecipes.instance().getSmeltingResult(input).isEmpty()) {
                        --fuel;
                        ++progress;
                        if (progress > 40) {
                            ItemStack inSlot = inventory.extractItem(0, 1, false);
                            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(inSlot).copy();
//                            InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), result.copy());
                            inventory.insertItem(1, result, false);
                            progress = 0;
                        }
                    }
                }
            } else {
                ItemStack fuelItem = inventory.extractItem(2, 1, true);
                if (!fuelItem.isEmpty() && ForgeEventFactory.getItemBurnTime(fuelItem) > 0) {
                    int time = ForgeEventFactory.getItemBurnTime(fuelItem);

                    inventory.extractItem(2, 1, false);
                    fuel += (time * 4);
                }
            }
        }
    }


    @Override
    public ModularPanel buildUI(GuiData guiData, GuiSyncManager guiSyncManager) {
        ModularPanel panel = ModularPanel.defaultPanel("附魔台");
        ItemSlot input = new ItemSlot().slot(inventory, 0)
                .pos(50, 32);
        panel.bindPlayerInventory()
                .child(input)
                .child(new ItemSlot().slot(inventory, 1)
                        .pos(112, 32))
                .child(new ItemSlot().slot(inventory, 2)
                        .alignX(0.5f)
                        .alignY(0.40f)
                )
                .child(new ProgressWidget()
                        .size(20)
                        .leftRel(0.5f).topRelAnchor(0.25f, 0.5f)
                        .texture(GuiTextures.PROGRESS_ARROW, 20)
                        .value(new DoubleSyncValue(() -> this.progress / 40.0, val -> this.progress = (int) (val * 40.0))));
        return panel;
    }

    /**
     * @param fuel 玩家当前手持的物品（见 MyLavaFurnace.onBlockActivated）
     * @return 尝试消耗燃料后的剩余物品，有可能仍然是未经修改的 fuel 实例
     */
    public ItemStack tryAcceptFuel(ItemStack fuel) {
        if (fuel.getItem() == Items.LAVA_BUCKET) {
            this.fuel += 10000;
            return new ItemStack(Items.BUCKET);
        } else if (fuel.getItem() == Items.COAL) {
            this.fuel += 1600;
            return ItemStack.EMPTY;
        } else {
            return fuel;
        }
    }


    // 注意，虽然 TileEntity 本身是抽象类，但它并没有任何抽象方法。
    // 有鉴于 Minecraft 反序列化 TileEntity 的方式，TileEntity 必须要有 public 的
    // 零参构造器。这里我们省略构造器不写，即使用隐式声明的默认构造器。


    public int getFuel() {
        return fuel;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new IItemHandler() {
                @Override
                public int getSlots() {
                    return inventory.getSlots();
                }

                @Override
                public ItemStack getStackInSlot(int slot) {
                    return inventory.getStackInSlot(slot);
                }

                @Override
                public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                    // 仅当对 0 号槽位尝试输入物品时允许通过，否则拒绝输入
                    LogWrapper.info("insertItem: " + slot + " " + stack);
                    if (slot == 0 && ! FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty()) {
                        return inventory.insertItem(0, stack, simulate);
                    } else if (slot == 2 && ForgeEventFactory.getItemBurnTime(stack) > 0) {
                        return inventory.insertItem(2, stack, simulate);
                    }
                    return stack;
                }

                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    // 仅当对 1 号槽位尝试提取物品时允许听过，否则拒绝提取
                    if (slot == 1) {
                        return inventory.extractItem(1, amount, simulate);
                    }
                    return ItemStack.EMPTY;
                }

                @Override
                public int getSlotLimit(int slot) {
                    return inventory.getSlotLimit(slot);
                }

                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    // 我们只允许 0 号槽位输入物品。
                    // 1 号槽位中的物品不能通过自动化手段输入，必须由我们自己控制。
                    return slot == 0 || slot == 2;
                }
            });
        } else {
            return super.getCapability(cap, facing);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        // 从 NBT 标签中读数据，以反序列化 TileEntity
        // 一定要调用 super.readFromNBT
        super.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        this.progress = tag.getInteger("Progress");
        this.fuel = tag.getInteger("Fuel");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound nbt = inventory.serializeNBT();
        // 向 NBT 标签中写数据，以序列化 TileEntity
        // 在返回前务必写入必须的数据
        // 一定要调用 super.writeToNBT(tag)
        tag.setTag("inventory",nbt);
        tag.setInteger("Progress", this.progress);
        tag.setInteger("Fuel", this.fuel);
        return super.writeToNBT(tag);
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
        return player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) {
            // 只允许有熔炼结果的物品
            return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
        } else if (index == 2) {
            // 只允许可作为燃料的物品
            return ForgeEventFactory.getItemBurnTime(stack) > 0;
        }
        // 输出槽 1 禁止外部放东西
        return false;
    }

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

    @Override
    public String getName() {
        return "Steel Furnace";
    }


    @Override
    public boolean hasCustomName() {
        return false;
    }



}
