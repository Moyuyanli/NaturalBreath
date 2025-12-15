package cn.chahuyun.enchantment.items;

import cn.chahuyun.enchantment.Tags;
import cn.chahuyun.enchantment.tabs.DefaultTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * 附魔器
 *
 * @author Moyuyanli
 * @Date 2024/10/25 23:10
 */
public class EnchantmentItem extends Item {

    public static String registryName = "item_enchantment";

    public EnchantmentItem() {
        this.setRegistryName(Tags.MOD_ID + ":" + registryName);
        this.setTranslationKey(Tags.MOD_ID + "." + registryName);
        this.setMaxStackSize(64);
        this.setCreativeTab(DefaultTabs.ENCHANTMENT_TABS);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }


}
