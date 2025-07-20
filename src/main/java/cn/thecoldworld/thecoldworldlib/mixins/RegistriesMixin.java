package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.debug.DebugStickItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registries.class)
public class RegistriesMixin {

    @Inject(method = "bootstrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registries;freezeRegistries()V"))
    private static void register(CallbackInfo ci) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            var key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("thecoldworldlib:debug_stick"));
            Items.register(key, DebugStickItem::new, new Item.Settings()
                    .registryKey(key)
                    .translationKey("item.minecraft.debug_stick")
                    .rarity(Rarity.EPIC)
                    .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .maxCount(1)
            );
        }
    }
}
