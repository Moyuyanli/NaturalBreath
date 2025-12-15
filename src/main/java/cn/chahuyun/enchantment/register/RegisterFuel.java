package cn.chahuyun.enchantment.register;

import cn.chahuyun.enchantment.Tags;
import net.minecraft.init.Items;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Moyuyanli
 * @Date 2024/11/8 0:00
 */
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class RegisterFuel {

    @SubscribeEvent
    public static void getVanillaFurnaceFuelValue(FurnaceFuelBurnTimeEvent event) {
        if (!event.getItemStack().isEmpty()) {
            if (event.getItemStack().getItem() == Items.COAL) {
                event.setBurnTime(100);
            }
            // 可以设定为 0。0 代表“这个物品不是燃料”，更准确地说是“这个物品燃烧时间是 0”。
            // 可以设定为 -1。-1 代表“由原版逻辑来决定”。
            // 可通过 event.getBurnTime() 获得当前决定的燃烧时间。
            // 这个事件可以取消。取消意味着后续的 Event listener 将不会收到这个事件，进而
            // 无法修改燃烧时间。
        }
    }

}
