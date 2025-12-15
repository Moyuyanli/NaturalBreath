package cn.chahuyun.enchantment.register;

import cn.chahuyun.enchantment.items.ItemExamples;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SuppressWarnings({"MethodMayBeStatic", "UnusedReturnValue"})
public class RegistryItems {
    public static final List<Item> ITEMS_TO_REGISTER = new LinkedList<>();
    public static final List<Item> ITEMS_TO_REGISTER_CT = new LinkedList<>();
    public static final Map<String, Item> CUSTOM_MODEL_ITEMS_TO_REGISTER_CT = new LinkedHashMap<>();

    public static final List<Item> ITEM_MODELS_TO_REGISTER = new LinkedList<>();
    public static final Map<String, Item> ITEM_CUSTOM_MODELS_TO_REGISTER = new LinkedHashMap<>();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        GenericRegistryPrimer.INSTANCE.wipe(event.getGenericType());

        ITEMS_TO_REGISTER.add(ItemExamples.GOLD_NATURAL);
        ITEMS_TO_REGISTER.add(ItemExamples.GOLD_ROD);
        ITEMS_TO_REGISTER.add(ItemExamples.ENCHANTMENT);

        registerItems();

        GenericRegistryPrimer.INSTANCE.fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());


        GameRegistry.addSmelting(Items.GOLD_INGOT, new ItemStack(ItemExamples.ENCHANTMENT), 15f);
    }

    public static void registerItems() {
        ITEMS_TO_REGISTER.forEach(RegistryItems::registerItem);
        ITEMS_TO_REGISTER.clear();
        ITEMS_TO_REGISTER_CT.forEach(RegistryItems::registerItem);
        ITEMS_TO_REGISTER_CT.clear();
        CUSTOM_MODEL_ITEMS_TO_REGISTER_CT.forEach((path, item) -> registerItem(item, path));
        CUSTOM_MODEL_ITEMS_TO_REGISTER_CT.clear();
    }

    public static void registerItemModels() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            ITEM_MODELS_TO_REGISTER.clear();
            ITEM_CUSTOM_MODELS_TO_REGISTER.clear();
            return;
        }
        ITEM_MODELS_TO_REGISTER.forEach(RegistryItems::registerItemModel);
        ITEM_MODELS_TO_REGISTER.clear();
        ITEM_CUSTOM_MODELS_TO_REGISTER.forEach((path, item) -> registerItemModel(item, path));
        ITEM_CUSTOM_MODELS_TO_REGISTER.clear();
        setMeshDef();
    }

    @SideOnly(Side.CLIENT)
    private static void setMeshDef() {
//        ModelLoader.setCustomMeshDefinition(ItemBlockEStorageEnergyCell.L4, new EStorageEnergyCellItemRenderer(ItemBlockEStorageEnergyCell.L4, "l4"));
//        ModelLoader.setCustomMeshDefinition(ItemBlockEStorageEnergyCell.L6, new EStorageEnergyCellItemRenderer(ItemBlockEStorageEnergyCell.L6, "l6"));
//        ModelLoader.setCustomMeshDefinition(ItemBlockEStorageEnergyCell.L9, new EStorageEnergyCellItemRenderer(ItemBlockEStorageEnergyCell.L9, "l9"));
    }

    public static <T extends Item> T registerItem(T item) {
        ITEM_MODELS_TO_REGISTER.add(item);
        GenericRegistryPrimer.INSTANCE.register(item);
        return item;
    }

    public static <T extends Item> T registerItem(T item, String modelPath) {
        ITEM_CUSTOM_MODELS_TO_REGISTER.put(modelPath, item);
        GenericRegistryPrimer.INSTANCE.register(item);
        return item;
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        registerItemModels();
    }

    public static void registerItemModel(final Item item) {
        NonNullList<ItemStack> list = NonNullList.create();
        ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());

        item.getSubItems(Objects.requireNonNull(item.getCreativeTab()), list);
        if (list.isEmpty()) {
            ModelLoader.setCustomModelResourceLocation(
                    item, 0, new ModelResourceLocation(registryName, "inventory"));
        } else {
            list.forEach(stack -> ModelLoader.setCustomModelResourceLocation(
                    item, stack.getItemDamage(), new ModelResourceLocation(registryName, "inventory")));
        }
    }

    public static void registerItemModel(final Item item, final String modelPath) {
        NonNullList<ItemStack> list = NonNullList.create();
        ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
        ResourceLocation modelLocation = new ResourceLocation(registryName.getNamespace(), modelPath);

        item.getSubItems(Objects.requireNonNull(item.getCreativeTab()), list);
        if (list.isEmpty()) {
            ModelLoader.setCustomModelResourceLocation(
                    item, 0, new ModelResourceLocation(modelLocation, "inventory"));
        } else {
            list.forEach(stack -> ModelLoader.setCustomModelResourceLocation(
                    item, stack.getItemDamage(), new ModelResourceLocation(modelLocation, "inventory")));
        }
    }
}
