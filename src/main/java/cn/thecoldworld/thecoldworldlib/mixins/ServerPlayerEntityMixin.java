package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IModManagerAccessor;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IModManagerAccessor {
    @Unique
    ModManager modManager = null;

    @Override
    public @Nullable ModManager getModManager() {
        return modManager;
    }

    @Override
    public void setModManager(@NotNull ModManager modManager) {
        this.modManager = modManager;
    }
}
