package cn.chahuyun.enchantment.items;

import cn.chahuyun.enchantment.Tags;
import cn.chahuyun.enchantment.tabs.DefaultTabs;
import net.minecraft.item.Item;

/**
 * 金棒
 *
 * @author Moyuyanli
 * @Date 2024/11/10 0:19
 */
public class ItemGoldRod extends Item {

    public static String getName() {
        return "gold_rod";
    }


    public ItemGoldRod() {
        this.setRegistryName(Tags.MOD_ID + ":" + getName());
        this.setTranslationKey(Tags.MOD_ID + "." + getName());
        this.setMaxStackSize(64);
        this.setCreativeTab(DefaultTabs.ENCHANTMENT_TABS);
    }
}
