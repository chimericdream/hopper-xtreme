package com.chimericdream.hopperxtreme.registry;

import com.chimericdream.hopperxtreme.HopperXtremeMod;
import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.block.CopperHopperBlock;
import com.chimericdream.hopperxtreme.block.DiamondHopperBlock;
import com.chimericdream.hopperxtreme.block.GoldenHopperBlock;
import com.chimericdream.hopperxtreme.block.HoneyedHopperBlock;
import com.chimericdream.hopperxtreme.block.NetheriteHopperBlock;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModRegistries {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<Block>>) Registries.BLOCK.getKey());
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<Item>>) Registries.ITEM.getKey());
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<BlockEntityType<?>>>) Registries.BLOCK_ENTITY_TYPE.getKey());

/*
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(itemGroup -> {
			itemGroup.add(HONEYED_HOPPER);
			itemGroup.add(COPPER_HOPPER);
			itemGroup.add(GOLDEN_HOPPER);
			itemGroup.add(DIAMOND_HOPPER);
			itemGroup.add(NETHERITE_HOPPER);
		});
 */
    public static final RegistrySupplier<Block> HONEYED_HOPPER = registerWithItem("honeyed_hopper", HoneyedHopperBlock::new);
    public static final RegistrySupplier<Block> COPPER_HOPPER = registerWithItem("copper_hopper", CopperHopperBlock::new);
    public static final RegistrySupplier<Block> GOLDEN_HOPPER = registerWithItem("golden_hopper", GoldenHopperBlock::new);
    public static final RegistrySupplier<Block> DIAMOND_HOPPER = registerWithItem("diamond_hopper", DiamondHopperBlock::new);
    public static final RegistrySupplier<Block> NETHERITE_HOPPER = registerWithItem("netherite_hopper", NetheriteHopperBlock::new);

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

    public static void init() {
        HopperXtremeMod.LOGGER.debug("[hopperxtreme] registering blocks");
        BLOCKS.register();

        HopperXtremeMod.LOGGER.debug("[hopperxtreme] registering items");
        ITEMS.register();
    }

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String name, final Supplier<T> supplier) {
        Registrar<BlockEntityType<?>> registrar = BLOCK_ENTITY_TYPES.getRegistrar();

        return registrar.register(Identifier.of(ModInfo.MOD_ID, name), supplier);
    }

    public static RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier) {
        RegistrySupplier<Block> block = registerBlock(Identifier.of(ModInfo.MOD_ID, name), supplier);

        registerItem(Identifier.of(ModInfo.MOD_ID, name), () -> new BlockItem(block.get(), new Item.Settings().arch$tab(ItemGroups.REDSTONE)));

        return block;
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(Identifier path, Supplier<T> block) {
        Registrar<Block> registrar = BLOCKS.getRegistrar();

        if (Platform.isNeoForge()) {
            return BLOCKS.register(path.getPath(), block);
        }

        return registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(Identifier path, Supplier<T> itemSupplier) {
        Registrar<Item> registrar = ITEMS.getRegistrar();

        if (Platform.isNeoForge()) {
            return ITEMS.register(path.getPath(), itemSupplier);
        }

        return registrar.register(path, itemSupplier);
    }
}
