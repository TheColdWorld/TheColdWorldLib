package cn.thecoldworld.thecoldworldlib.submod;

import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IModManagerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public final class ModManager implements Iterable<Mod> {
    private static ModManager INSTANCE;
    private final ArrayList<Mod> Mods;
    private transient boolean frozen = false;

    private ModManager() {
        Mods = new ArrayList<>(FabricLoader.getInstance().getAllMods().size());
    }

    public ModManager(int size) {
        Mods = new ArrayList<>(size);
    }

    public static ModManager getInstance() {
        return INSTANCE;
    }

    public static void initialize() {
        INSTANCE = new ModManager();
    }

    public void register(Mod mod) {
        assertFrozen();
        if (mod == null) throw new IllegalArgumentException("arg mod is null!");
        if (!FabricLoader.getInstance().isModLoaded(mod.modid))
            throw new IllegalArgumentException("mod is not loaded!");
        Mods.add(mod);
    }

    public Optional<Mod> get(String Modid) {
        if (Modid == null) throw new IllegalArgumentException("arg Modid is null!");
        for (Mod mod : Mods) {
            if (mod.modid.equals(Modid)) return Optional.of(mod);
        }
        return Optional.empty();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public int size() {
        return Mods.size();
    }

    public void assertFrozen() {
        if (frozen) try {
            throw new IllegalStateException("Mod manager is frozen");
        } catch (IllegalStateException e) {//generate stacktrace
            throw new CrashException(new CrashReport("Mod manager is frozen", e));
        }
    }

    @Override
    public @NotNull Iterator<Mod> iterator() {
        return Mods.iterator();
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + "|size:" + size() + "|frozen:" + (frozen ? "true" : "false");
    }

    public Stream<Mod> stream() {
        return this.Mods.stream();
    }

    public boolean isEmpty() {
        return this.Mods.isEmpty();
    }

    /**
     * @return {@code false} if not registered or {@code player} don't install the Optional mod,otherwise {@code true}
     */
    public boolean isHaveOptionalMod(ServerPlayerEntity player, OptionalMod mod) {
        if (!Mods.contains(mod)) return false;
        return ((IModManagerAccessor) player).getModManager().get(mod.modid).isPresent();
    }

    /**
     * @return {@code false} if not registered as an optional mod or {@code player} don't install the Optional mod,otherwise {@code true}
     */
    public boolean isHaveOptionalMod(ServerPlayerEntity player, ModContainer mod) {
        if (Mods.stream().filter(m -> m instanceof OptionalMod).noneMatch(m -> m.modid.equals(mod.getMetadata().getId())))
            return false;
        return ((IModManagerAccessor) player).getModManager().stream().anyMatch(m -> m.modid.equals(mod.getMetadata().getId()));
    }

    /**
     * @return {@code false} if not registered as an optional mod or {@code player} don't install the Optional mod,otherwise {@code true}
     */
    public boolean isHaveOptionalMod(ServerPlayerEntity player, String modid) {
        if (Mods.stream().filter(m -> m instanceof OptionalMod).noneMatch(m -> m.modid.equals(modid))) return false;
        return ((IModManagerAccessor) player).getModManager().stream().anyMatch(m -> m.modid.equals(modid));
    }
}
