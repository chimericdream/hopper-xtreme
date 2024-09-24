package com.chimericdream.hopperxtreme.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CommonTags {
    public static final TagKey<Block> HOPPERS = TagKey.of(Registries.BLOCK.getKey(), Identifier.of("c", "hoppers"));
    public static final TagKey<Item> WRENCHES = TagKey.of(Registries.ITEM.getKey(), Identifier.of("c", "tools/wrenches"));
}
