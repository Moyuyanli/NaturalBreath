package cn.chahuyun.enchantment.block;

import cn.chahuyun.enchantment.Tags;
import cn.chahuyun.enchantment.tabs.DefaultTabs;
import com.cleanroommc.modularui.factory.TileEntityGuiFactory;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 天使方块
 */
public class BlockAngel extends Block {

    public static final BlockAngel INSTANCE = new BlockAngel();

    public BlockAngel() {
        super(Material.ROCK);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(DefaultTabs.ENCHANTMENT_TABS);
        this.setDefaultState(this.blockState.getBaseState());
        this.setRegistryName(new ResourceLocation(Tags.MOD_ID, "angel_block"));
        this.setTranslationKey(Tags.MOD_ID + '.' + "angel_block");
    }

    @Nonnull
    @Override
    public String getLocalizedName() {
        return I18n.format("tile.novaeng_core.angel_block.name");
    }

    @Override
    public boolean canHarvestBlock(@Nonnull final IBlockAccess world,
                                   @Nonnull final BlockPos pos,
                                   @Nonnull final EntityPlayer player) {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(final World worldIn,
                                          @Nonnull final BlockPos pos,
                                          @Nonnull final IBlockState state,
                                          float chance,
                                          final int fortune) {
        if (worldIn.isRemote || worldIn.restoringBlockSnapshots) {
            return;
        }

        List<ItemStack> drops = getDrops(worldIn, pos, state, fortune);
        chance = ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, fortune, chance, false, harvesters.get());

        for (ItemStack drop : drops) {
            if (worldIn.rand.nextFloat() <= chance) {
                spawnItemToPlayer(worldIn, pos, drop);
            }
        }
    }

    protected void spawnItemToPlayer(World worldIn, BlockPos pos, ItemStack stack) {
        if (worldIn.isRemote || stack.isEmpty() || !worldIn.getGameRules().getBoolean("doTileDrops") || worldIn.restoringBlockSnapshots) {
            return;
        }
        if (captureDrops.get()) {
            capturedDrops.get().add(stack);
            return;
        }

        double x;
        double y;
        double z;

        EntityPlayer harvester = harvesters.get();
        if (harvester != null) {
            x = harvester.posX;
            y = harvester.posY;
            z = harvester.posZ;
        } else {
            x = (double) (worldIn.rand.nextFloat() * 0.5F) + 0.25D + pos.getX();
            y = (double) (worldIn.rand.nextFloat() * 0.5F) + 0.25D + pos.getY();
            z = (double) (worldIn.rand.nextFloat() * 0.5F) + 0.25D + pos.getZ();
        }

        EntityItem entityitem = new EntityItem(worldIn, x, y, z, stack);
        entityitem.setNoPickupDelay();
        worldIn.spawnEntity(entityitem);
    }

    /**
     * Called when the block is right clicked by a player.
     *
     * @param worldIn
     * @param pos
     * @param state
     * @param playerIn
     * @param hand
     * @param facing
     * @param hitX
     * @param hitY
     * @param hitZ
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
//            Gui.TILE_ENTITY.open(playerIn, worldIn, pos);
        }
        return true;
    }
}
