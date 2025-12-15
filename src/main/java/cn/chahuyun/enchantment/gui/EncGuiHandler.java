package cn.chahuyun.enchantment.gui;

import cn.chahuyun.enchantment.Enchantment;
import cn.chahuyun.enchantment.entity.block.EntitySteelFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

/**
 * @author Moyuyanli
 * @Date 2024/11/7 17:11
 */
public class EncGuiHandler implements IGuiHandler {


    public static final int STEEL_FURNACE_ID = 0;

    public EncGuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Enchantment.instance, this);
    }


    /**
     * 返回要显示给用户的服务器端容器。
     *
     * @param ID     Gui ID编号
     * @param player 查看Gui的玩家
     * @param x      X位置
     * @param y      Y位置
     * @param z      Z位置
     * @param world  当前世界
     * @return 要显示给用户的GuiScreen/Container，如果没有，则为null。
     */
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == STEEL_FURNACE_ID) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if (tileEntity instanceof EntitySteelFurnace) {
                return null;
//                return new ContainerSteelFurnace(player.inventory, (EntitySteelFurnace) tileEntity);
            }
        }
        return null;
    }

    /**
     * 返回要显示给用户的容器。在客户端，这
     * 需要在服务器端返回GuiScreen的实例，这需要
     * 返回容器的实例
     *
     * @param ID     Gui ID编号
     * @param player 查看Gui的玩家
     * @param world  当前世界
     * @param x      X位置
     * @param y      Y位置
     * @param z      Z位置
     * @return 要显示给用户的GuiScreen/容器，如果没有，则为null。
     */
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == STEEL_FURNACE_ID) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if (tileEntity instanceof EntitySteelFurnace) {
                return null;
//                return new GuiSteelFurnace(new ContainerSteelFurnace(player.inventory, (EntitySteelFurnace) tileEntity));
            }
        }
        return null;
    }
}
