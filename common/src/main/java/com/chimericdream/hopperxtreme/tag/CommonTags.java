package com.chimericdream.hopperxtreme.tag;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CommonTags {
    public static final TagKey<Block> HOPPERS = TagKey.of(Registries.BLOCK.getKey(), Identifier.of("c", "hoppers"));
}
