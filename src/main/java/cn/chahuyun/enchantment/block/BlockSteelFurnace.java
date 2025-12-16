package cn.chahuyun.enchantment.block;

import cn.chahuyun.enchantment.Tags;
import cn.chahuyun.enchantment.entity.block.EntitySteelFurnace;
import cn.chahuyun.enchantment.tabs.DefaultTabs;
import com.cleanroommc.modularui.factory.TileEntityGuiFactory;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * 钢炉
 */
public final class BlockSteelFurnace extends Block {

    public static String registryName = "block_steel_furnace";

    public BlockSteelFurnace() {
        super(Material.ROCK);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(DefaultTabs.ENCHANTMENT_TABS);
        this.setDefaultState(this.blockState.getBaseState());
        this.setRegistryName(new ResourceLocation(Tags.MOD_ID, registryName));
        this.setTranslationKey(Tags.MOD_ID + '.' + registryName);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new EntitySteelFurnace();
    }

    /**
     * 方块被激活(右键)
     *
     * @param world
     * @param pos
     * @param state
     * @param player
     * @param hand
     * @param facing
     * @param hitX
     * @param hitY
     * @param hitZ
     * @return
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof EntitySteelFurnace && !world.isRemote) {
            TileEntityGuiFactory.INSTANCE.open(player, pos);
//            player.openGui(Enchantment.instance, EncGuiHandler.STEEL_FURNACE_ID, world, pos.getX(), pos.getY(), pos.getZ());
//            player.setHeldItem(hand, ((EntitySteelFurnace) tile).tryAcceptFuel(player.getHeldItem(hand)));
//            player.sendStatusMessage(new TextComponentString("Fuel: " + ((EntitySteelFurnace) tile).getFuel()), true);
            return true;
        }
        return false;
    }


    /**
     * 方块被破坏
     *
     * @param world 世界
     * @param pos   位置
     * @param state
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof EntitySteelFurnace) {
            final ItemStackHandler inv = ((EntitySteelFurnace) tile).inventory;
            for (int i = 0; i < inv.getSlots(); i++) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inv.getStackInSlot(i));
            }
        }
        super.breakBlock(world, pos, state);
    }

}