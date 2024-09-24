package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.HopperXtremeMod;
import com.chimericdream.hopperxtreme.ModInfo;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static com.chimericdream.hopperxtreme.registry.ModRegistries.registerItem;

public class ModItems {
    public static RegistrySupplier<Item> WRENCH = null;

    public static void init() {
        HopperXtremeMod.LOGGER.info("Checking if Minekea is loaded...");
        if (Platform.isModLoaded("minekea")) {
            HopperXtremeMod.LOGGER.info("Minekea is loaded! Skipping registration of Hopper X-Treme's wrench.");
        } else {
            HopperXtremeMod.LOGGER.info("Minekea not loaded. Registering Hopper X-Treme's wrench.");
            WRENCH = registerItem(Identifier.of(ModInfo.MOD_ID, "tools/wrench"), WrenchItem::new);
        }
    }
}
