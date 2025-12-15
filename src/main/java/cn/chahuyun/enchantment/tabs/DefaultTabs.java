package cn.chahuyun.enchantment.tabs;

import cn.chahuyun.enchantment.Tags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * 默认Tabs
 *
 * @author Moyuyanli
 * @Date 2024/11/7 1:09
 */
public class DefaultTabs extends CreativeTabs {

    public static final DefaultTabs ENCHANTMENT_TABS = new DefaultTabs();

    public DefaultTabs() {
        super(Tags.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.DIAMOND);
    }
}
