package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeHupperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHopperBlockEntity;
import com.chimericdream.hopperxtreme.entity.XtremeMultiHupperBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.chimericdream.hopperxtreme.registry.ModRegistries.registerBlockEntity;
import static com.chimericdream.hopperxtreme.registry.ModRegistries.registerWithItem;

public class Hoppers {
    private static final Item.Settings DEFAULT_SETTINGS = new Item.Settings().arch$tab(ItemGroups.REDSTONE);

    public static final RegistrySupplier<Block> HONEYED_HOPPER = registerWithItem("honeyed_hopper", () -> new XtremeHopperBlock(20, "honeyed_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> COPPER_HOPPER = registerWithItem("copper_hopper", () -> new XtremeHopperBlock(8, "copper_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_HOPPER = registerWithItem("golden_hopper", () -> new XtremeHopperBlock(4, "golden_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_HOPPER = registerWithItem("diamond_hopper", () -> new XtremeHopperBlock(2, "diamond_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_HOPPER = registerWithItem("netherite_hopper", () -> new XtremeHopperBlock(1, "netherite_hopper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> MULTI_HOPPER = registerWithItem("multi_hopper", () -> new XtremeMultiHopperBlock(8, "multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_MULTI_HOPPER = registerWithItem("golden_multi_hopper", () -> new XtremeMultiHopperBlock(4, "golden_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_MULTI_HOPPER = registerWithItem("diamond_multi_hopper", () -> new XtremeMultiHopperBlock(2, "diamond_multi_hopper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_MULTI_HOPPER = registerWithItem("netherite_multi_hopper", () -> new XtremeMultiHopperBlock(1, "netherite_multi_hopper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> HUPPER = registerWithItem("hupper", () -> new XtremeHupperBlock(8, "hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> HONEYED_HUPPER = registerWithItem("honeyed_hupper", () -> new XtremeHupperBlock(20, "honeyed_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> COPPER_HUPPER = registerWithItem("copper_hupper", () -> new XtremeHupperBlock(8, "copper_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_HUPPER = registerWithItem("golden_hupper", () -> new XtremeHupperBlock(4, "golden_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_HUPPER = registerWithItem("diamond_hupper", () -> new XtremeHupperBlock(2, "diamond_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_HUPPER = registerWithItem("netherite_hupper", () -> new XtremeHupperBlock(1, "netherite_hupper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<Block> MULTI_HUPPER = registerWithItem("multi_hupper", () -> new XtremeMultiHupperBlock(8, "multi_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> GOLDEN_MULTI_HUPPER = registerWithItem("golden_multi_hupper", () -> new XtremeMultiHupperBlock(4, "golden_multi_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> DIAMOND_MULTI_HUPPER = registerWithItem("diamond_multi_hupper", () -> new XtremeMultiHupperBlock(2, "diamond_multi_hupper"), DEFAULT_SETTINGS);
    public static final RegistrySupplier<Block> NETHERITE_MULTI_HUPPER = registerWithItem("netherite_multi_hupper", () -> new XtremeMultiHupperBlock(1, "netherite_multi_hupper"), DEFAULT_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<XtremeHopperBlockEntity>> XTREME_HOPPER_BLOCK_ENTITY = registerBlockEntity(
        "xtreme_hopper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeHopperBlockEntity::new,
            HONEYED_HOPPER.get(),
            COPPER_HOPPER.get(),
            GOLDEN_HOPPER.get(),
            DIAMOND_HOPPER.get(),
            NETHERITE_HOPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeHupperBlockEntity>> XTREME_HUPPER_BLOCK_ENTITY = registerBlockEntity(
        "xtreme_hupper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeHupperBlockEntity::new,
            HUPPER.get(),
            HONEYED_HUPPER.get(),
            COPPER_HUPPER.get(),
            GOLDEN_HUPPER.get(),
            DIAMOND_HUPPER.get(),
            NETHERITE_HUPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeMultiHopperBlockEntity>> XTREME_MULTI_HOPPER_BLOCK_ENTITY = registerBlockEntity(
        "xtreme_multi_hopper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeMultiHopperBlockEntity::new,
            MULTI_HOPPER.get(),
            GOLDEN_MULTI_HOPPER.get(),
            DIAMOND_MULTI_HOPPER.get(),
            NETHERITE_MULTI_HOPPER.get()
        ).build(null)
    );

    public static final RegistrySupplier<BlockEntityType<XtremeMultiHupperBlockEntity>> XTREME_MULTI_HUPPER_BLOCK_ENTITY = registerBlockEntity(
        "xtreme_multi_hupper_block_entity",
        () -> BlockEntityType.Builder.create(
            XtremeMultiHupperBlockEntity::new,
            MULTI_HUPPER.get(),
            GOLDEN_MULTI_HUPPER.get(),
            DIAMOND_MULTI_HUPPER.get(),
            NETHERITE_MULTI_HUPPER.get()
        ).build(null)
    );

    public static void init() {}
}
