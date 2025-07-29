package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.config.CommonConfig;
import cn.thecoldworld.thecoldworldlib.config.ConfigManager;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Unique
    private short TheColdWorldLib$ticker = 0;

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", shift = At.Shift.AFTER))
    private void beforeTick(CallbackInfo ci) {
        ModManager.getInstance().freeze();
        ConfigManager.getInstance().freeze();
        ConfigManager.getInstance().write();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CommonConfig config = ConfigManager.getInstance().getOrElse(Identifier.of(Vars.MODID, "config/common"), CommonConfig.class, CommonConfig::new);
        if (TheColdWorldLib$ticker == config.getRefreshTickIndex()) {
            TheColdWorldLib$ticker = 0;
            ConfigManager.getInstance().write();
        } else TheColdWorldLib$ticker++;
    }
}
