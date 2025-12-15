package cn.chahuyun.enchantment.register;

import cn.chahuyun.enchantment.Enchantment;
import cn.chahuyun.enchantment.Tags;
import cn.chahuyun.enchantment.block.BlockSpiritGathering;
import cn.chahuyun.enchantment.block.BlockSteelFurnace;
import cn.chahuyun.enchantment.entity.block.EntitySpiritGathering;
import cn.chahuyun.enchantment.entity.block.EntitySteelFurnace;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"MethodMayBeStatic", "UnusedReturnValue"})
public class RegistryBlocks {
    public static final List<Block> BLOCK_MODEL_TO_REGISTER = new ArrayList<>();

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        GenericRegistryPrimer.INSTANCE.wipe(event.getGenericType());

        registerBlocks();
        registerTileEntities();

        Enchantment.LOGGER.info("block register over !");

        GenericRegistryPrimer.INSTANCE.fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    /**
     * 注册方块
     */
    public static void registerBlocks() {
        prepareItemBlockRegister(registerBlock(new BlockSteelFurnace()));
        prepareItemBlockRegister(registerBlock(new BlockSpiritGathering()));
    }

    /**
     * 注册方块实体
     */
    public static void registerTileEntities() {
        registerTileEntity(EntitySteelFurnace.class, BlockSteelFurnace.registryName);
        registerTileEntity(EntitySpiritGathering.class,EntitySpiritGathering.name);
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        registerBlockModels();
    }

    public static void registerBlockModels() {
        BLOCK_MODEL_TO_REGISTER.forEach(RegistryBlocks::registerBlockModel);
        BLOCK_MODEL_TO_REGISTER.clear();
    }

    public static void registerTileEntity(Class<? extends TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, new ResourceLocation(Tags.MOD_ID, name));
    }

    public static <T extends Block> T registerBlock(T block) {
        BLOCK_MODEL_TO_REGISTER.add(block);
        GenericRegistryPrimer.INSTANCE.register(block);

        return block;
    }

    public static ItemBlock prepareItemBlockRegister(Block block) {
        return prepareItemBlockRegister(new ItemBlock(block));
    }

    public static <T extends ItemBlock> T prepareItemBlockRegister(T item) {
        Block block = item.getBlock();
        ResourceLocation registryName = Objects.requireNonNull(block.getRegistryName());
        String translationKey = block.getTranslationKey();

        item.setRegistryName(registryName).setTranslationKey(translationKey);
        RegistryItems.ITEMS_TO_REGISTER.add(item);
        return item;
    }

    public static void registerBlockModel(final Block block) {
        Item item = Item.getItemFromBlock(block);
        ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
        ModelBakery.registerItemVariants(item, registryName);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(registryName, "inventory"));
    }
}
