package cn.chahuyun.enchantment.block;

import cn.chahuyun.enchantment.Tags;
import cn.chahuyun.enchantment.entity.block.EntitySpiritGathering;
import cn.chahuyun.enchantment.tabs.DefaultTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * 聚灵器
 *
 * @author Moyuyanli
 * @Date 2024/11/10 1:19
 */
@SuppressWarnings("NullableProblems")
public class BlockSpiritGathering extends Block {


    public static String getName() {
        return "spirit_gathering";
    }

    public BlockSpiritGathering() {
        super(Material.ROCK);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(DefaultTabs.ENCHANTMENT_TABS);
        this.setDefaultState(this.blockState.getBaseState());
        this.setRegistryName(new ResourceLocation(Tags.MOD_ID,  getName()));
        this.setTranslationKey(Tags.MOD_ID + '.' + getName());

        this.setLightOpacity(0);
    }


    // 1. 让这个方块走透明渲染层（Cutout 用于像玻璃那样硬边透明，Translucent 用于半透明）
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT; // 如果你的 glass 是带半透明渐变，也可以用 TRANSLUCENT
    }


    /**
     * 在整个代码中调用，作为 block的 instanceof BlockContainer 的替换
     * 将其移至 Block 基类允许希望扩展原版的 Mod
     * 块，并且还希望在该块上有一个瓦片实体，则可以。
     * <p>
     * 从此函数返回 true 以指定此块具有瓦片实体。
     *
     * @param state 当前区块的状态
     * 如果块具有图块实体，则 @return True，否则为 false
     */
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    /**
     * 在整个代码中调用，作为 ITileEntityProvider.createNewTileEntity 的替换
     * 返回与该函数相同的内容。
     * 如果此块是 ITileEntityProvider，这将回退到 ITileEntityProvider.createNewTileEntity（World）
     *
     * @param world 世界
     * @param state 当前区块的状态
     * @return 扩展 TileEntity 的类的实例
     */
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new EntitySpiritGathering();
    }
}
