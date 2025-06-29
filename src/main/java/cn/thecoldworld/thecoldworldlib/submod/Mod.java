package cn.thecoldworld.thecoldworldlib.submod;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Mod {
    public static final Codec<Mod> CODEC = Codec.of(Mod::write, Mod::decode);
    public static final Map<String, Decoder<? extends Mod>> ModTypes = new HashMap<>(10);
    public final String modid;
    public final String version;
    public final String compatibleVersion;
    public final URI downloadLink;
    protected final VersionPredicate versionPredicate;
    public final String modType;
    @Nullable
    public String modName;

    /**
     * @param modid             mod id(must be found in fabric loader )
     * @param compatibleVersion the mod Compatible With Its Own Version(fabric.mod.json's grammar)
     * @throws net.fabricmc.loader.api.VersionParsingException cannot parse version predicate
     */
    protected Mod(final String modid, final String compatibleVersion, URI downloadLink, final String MOD_TYPE) throws VersionParsingException {
        this.downloadLink = downloadLink;
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modid);
        if (modContainer.isEmpty()) throw new IllegalArgumentException("mod " + modid + " don't exist!");
        this.modid = modid;
        this.version = modContainer.get().getMetadata().getVersion().getFriendlyString();
        this.modName = modContainer.get().getMetadata().getName();
        this.compatibleVersion = compatibleVersion;
        this.versionPredicate = VersionPredicate.parse(compatibleVersion);
        this.modType = MOD_TYPE;
    }

    /**
     * @param modContainer      fabric mod container(use FabricLoader.getInstance().getModContainer(Modid).get())
     * @param compatibleVersion the mod Compatible With Its Own Version(fabric.mod.json's grammar)
     * @throws net.fabricmc.loader.api.VersionParsingException cannot parse version predicate
     */
    protected Mod(final ModContainer modContainer, final String compatibleVersion, URI downloadLink, final String MOD_TYPE) throws VersionParsingException {
        this.modid = modContainer.getMetadata().getId();
        this.version = modContainer.getMetadata().getName();
        this.compatibleVersion = compatibleVersion;
        this.versionPredicate = VersionPredicate.parse(compatibleVersion);
        this.downloadLink = downloadLink;
        this.modType = MOD_TYPE;
        this.modName = modContainer.getMetadata().getName();
    }

    /**
     * for Codec decode
     */
    protected <T> Mod(final DynamicOps<T> ops, final T input) throws VersionParsingException {
        this.modid = ops.getStringValue(ops.get(input, "modid").getOrThrow()).getOrThrow();
        this.modName = ops.getStringValue(ops.get(input, "name").getOrThrow()).getOrThrow();
        this.version = ops.getStringValue(ops.get(input, "version").getOrThrow()).getOrThrow();
        this.compatibleVersion = ops.getStringValue(ops.get(input, "compatibleVersion").getOrThrow()).getOrThrow();
        this.modType = ops.getStringValue(ops.get(input, "modType").getOrThrow()).getOrThrow();
        this.versionPredicate = VersionPredicate.parse(this.compatibleVersion);
        this.downloadLink = Codecs.URI.decode(ops, ops.get(input, "downloadLink").getOrThrow()).getOrThrow().getFirst();
    }

    /**
     * for Codec
     */
    protected static <T> DataResult<Pair<Mod, T>> decode(DynamicOps<T> ops, T input) {
        try {
            String modTypeStr = ops.getStringValue(ops.get(input, "modType").getOrThrow()).getOrThrow();
            Decoder<? extends Mod> decoder = ModTypes.get(modTypeStr);
            if (decoder == null) {
                Vars.LOGGER.error("Unexpected mod type " + modTypeStr);
                return DataResult.error(() -> "Unexpected mod type " + modTypeStr);
            }
            Pair<? extends Mod, T> dataResult = decoder.decode(ops, input).getOrThrow();
            return DataResult.success(Pair.of(dataResult.getFirst(), dataResult.getSecond()));
        } catch (Throwable e) {
            return ErrorUtil.CodecOnException(e);
        }
    }

    /**
     * for Codec
     */
    protected <T> DataResult<T> write(DynamicOps<T> ops, T ignored) {
        if (this.version == null || this.modType == null || this.modName == null || this.compatibleVersion == null || this.modid == null)
            return DataResult.error(() -> "variable is not initialized");
        try {
            T t0 = ops.set(ops.empty(), "modid", ops.createString(this.modid));
            T t1 = ops.set(t0, "modType", ops.createString(this.modType));
            T t2 = ops.set(t1, "name", ops.createString(this.modName));
            T t3 = ops.set(t2, "version", ops.createString(this.version));
            T t4 = ops.set(t3, "compatibleVersion", ops.createString(this.compatibleVersion));
            T t5 = ops.set(t4, "downloadLink", ops.createString(this.downloadLink.toString()));
            return DataResult.success(t5);
        } catch (Throwable e) {
            return ErrorUtil.CodecOnException(e);
        }
    }

    public boolean isCompatible(Mod mod) {
        if (mod == null) return false;
        try {
            return mod.modid.equals(modid) && versionPredicate.test(Version.parse(mod.version));
        } catch (Throwable ignored) {
            return false;
        }
    }

    public boolean isCompatible(ModContainer modContainer) {
        if (modContainer == null) return false;
        ModMetadata metadata = modContainer.getMetadata();
        return metadata.getId().equals(modid) && versionPredicate.test(metadata.getVersion());
    }

    /**
     * @param Modid mod id(should be found in fabric loader )
     */
    public boolean isCompatible(String Modid) {
        if (Modid == null) return false;
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Modid);
        return modContainer.isPresent() && isCompatible(modContainer.get());
    }

    /**
     * create a message about not install the mod to disconnect
     *
     * @return a message about not install the mod
     */
    public Text createNotintalledText() {
        throw new IllegalCallerException();
    }

    /**
     * create a message about not install wrong version of the mod to disconnect
     *
     * @param currentversion real version of this mod (usually from fabric mod loader instance)
     * @return a message about not install wrong version of the mod
     */
    public Text createWrongVersionText(String currentversion) {
        throw new IllegalCallerException();
    }

    @Override
    public String toString() {
        return String.format("ThColdWorldLib.Mod@%s[modid:%s;mod name:%s;mod type:%s;mod compatible version:%s]", Integer.toHexString(hashCode()), modid, modName, modType, compatibleVersion);
    }
}
