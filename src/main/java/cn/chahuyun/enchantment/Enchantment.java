package cn.chahuyun.enchantment;


import cn.chahuyun.enchantment.gui.EncGuiHandler;
import cn.chahuyun.enchantment.register.RegistryBlocks;
import cn.chahuyun.enchantment.register.RegistryItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// 此处的值应与meta-inf/mods.toml文件中的条目匹配
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Enchantment {

    @Mod.Instance(Tags.MOD_ID)
    public static Enchantment instance;

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SuppressWarnings("unused")
    public Enchantment() {
        MinecraftForge.EVENT_BUS.register(new RegistryBlocks());
        MinecraftForge.EVENT_BUS.register(new RegistryItems());
    }

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     * 看看有多少FMLStateEvents你可以通过 @ Mod.EventHandler注释听这里
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new EncGuiHandler();
    }


}
