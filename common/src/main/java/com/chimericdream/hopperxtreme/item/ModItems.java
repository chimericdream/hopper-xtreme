package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static com.chimericdream.hopperxtreme.registry.ModRegistries.registerItem;

public class ModItems {
    public static final RegistrySupplier<Item> WRENCH = registerItem(Identifier.of(ModInfo.MOD_ID, "tools/wrench"), WrenchItem::new);

    public static void init() {}
}
