package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
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

    public static void init() {}
}
