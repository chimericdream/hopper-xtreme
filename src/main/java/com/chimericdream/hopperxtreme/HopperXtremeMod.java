package com.chimericdream.hopperxtreme;

import com.chimericdream.hopperxtreme.block.*;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HopperXtremeMod implements ModInitializer {
	public static final Block HONEYED_HOPPER = new HoneyedHopperBlock();
	public static final Block COPPER_HOPPER = new CopperHopperBlock();
	public static final Block GOLDEN_HOPPER = new GoldenHopperBlock();
	public static final Block DIAMOND_HOPPER = new DiamondHopperBlock();
	public static final Block NETHERITE_HOPPER = new NetheriteHopperBlock();

	public static final BlockEntityType<XtremeHopperBlockEntity> XTREME_HOPPER_BLOCK_ENTITY = Registry.register(
		Registry.BLOCK_ENTITY_TYPE,
		new Identifier("hopperxtreme", "xtreme_hopper_block_entity"),
		FabricBlockEntityTypeBuilder.create(
			XtremeHopperBlockEntity::new,
			HONEYED_HOPPER,
			COPPER_HOPPER,
			GOLDEN_HOPPER,
			DIAMOND_HOPPER,
			NETHERITE_HOPPER
		).build()
	);

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("hopperxtreme", "honeyed_hopper"), HONEYED_HOPPER);
		Registry.register(Registry.ITEM, new Identifier("hopperxtreme", "honeyed_hopper"), new BlockItem(HONEYED_HOPPER, new FabricItemSettings().group(ItemGroup.REDSTONE)));

		Registry.register(Registry.BLOCK, new Identifier("hopperxtreme", "copper_hopper"), COPPER_HOPPER);
		Registry.register(Registry.ITEM, new Identifier("hopperxtreme", "copper_hopper"), new BlockItem(COPPER_HOPPER, new FabricItemSettings().group(ItemGroup.REDSTONE)));

		Registry.register(Registry.BLOCK, new Identifier("hopperxtreme", "golden_hopper"), GOLDEN_HOPPER);
		Registry.register(Registry.ITEM, new Identifier("hopperxtreme", "golden_hopper"), new BlockItem(GOLDEN_HOPPER, new FabricItemSettings().group(ItemGroup.REDSTONE)));

		Registry.register(Registry.BLOCK, new Identifier("hopperxtreme", "diamond_hopper"), DIAMOND_HOPPER);
		Registry.register(Registry.ITEM, new Identifier("hopperxtreme", "diamond_hopper"), new BlockItem(DIAMOND_HOPPER, new FabricItemSettings().group(ItemGroup.REDSTONE)));

		Registry.register(Registry.BLOCK, new Identifier("hopperxtreme", "netherite_hopper"), NETHERITE_HOPPER);
		Registry.register(Registry.ITEM, new Identifier("hopperxtreme", "netherite_hopper"), new BlockItem(NETHERITE_HOPPER, new FabricItemSettings().group(ItemGroup.REDSTONE)));
	}
}
