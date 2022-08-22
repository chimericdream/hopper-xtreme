package com.chimericdream.hopperxtreme.tag;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CommonBlockTags {
    public static final TagKey<Block> HOPPERS = TagKey.of(Registry.BLOCK_KEY, new Identifier("c", "hoppers"));

    public void init() {
    }
}
