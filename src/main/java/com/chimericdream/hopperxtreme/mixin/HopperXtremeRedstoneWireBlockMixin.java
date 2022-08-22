package com.chimericdream.hopperxtreme.mixin;

import com.chimericdream.hopperxtreme.HopperXtremeMod;
import com.chimericdream.hopperxtreme.tag.CommonBlockTags;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneWireBlock.class)
public class HopperXtremeRedstoneWireBlockMixin {
	@Redirect(
		method = "canRunOnTop",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
	)
	private boolean hopperXtremeCanRunOnTop(BlockState state, Block block) {
		return state.isOf(block) || state.isIn(CommonBlockTags.HOPPERS);
	}
}
