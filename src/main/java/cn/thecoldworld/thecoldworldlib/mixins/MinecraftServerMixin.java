package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", shift = At.Shift.AFTER))
    private void beforeTick(CallbackInfo ci) {
        ModManager.getInstance().freeze();
    }
//    @Unique
//    ModManager TheColdWorldLib$ModManager;
//    @Inject(method = "<init>",at= @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/ReentrantThreadExecutor;<init>(Ljava/lang/String;)V",shift = At.Shift.AFTER))
//    private void onInit(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci)
//    {
//        TheColdWorldLib$ModManager=new ModManager();
//    }
}
