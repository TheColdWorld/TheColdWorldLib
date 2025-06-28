package cn.thecoldworld.thecoldworldlib.submod;

import cn.thecoldworld.thecoldworldlib.Utils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.text.Text;

import java.net.URI;

public final class ResourcePackRequiredMod extends Mod {
    public static final Decoder<Mod> DECODER = new Decoder<>() {
        @Override
        public <T> DataResult<Pair<Mod, T>> decode(DynamicOps<T> dynamicOps, T t) {
            try {
                return DataResult.success(Pair.of(new ResourcePackRequiredMod(dynamicOps, t), t));
            } catch (Exception e) {
                return Utils.CodecOnException(e);
            }
        }
    };
    public static final String MOD_TYPE = "resourcePackRequired";

    static {
        Mod.ModTypes.put(MOD_TYPE, DECODER);
    }

    public final String translateKey;
    public final String requiredTranslateResult;

    public ResourcePackRequiredMod(String modid, String compatibleVersion, String translateKey, String requiredTranslateResult, URI downloadLink) throws VersionParsingException {
        super(modid, compatibleVersion, downloadLink, MOD_TYPE);
        this.translateKey = translateKey;
        this.requiredTranslateResult = requiredTranslateResult;
    }

    public ResourcePackRequiredMod(ModContainer modContainer, String compatibleVersion, String translateKey, String requiredTranslateResult, URI downloadLink) throws VersionParsingException {
        super(modContainer, compatibleVersion, downloadLink, MOD_TYPE);
        this.translateKey = translateKey;
        this.requiredTranslateResult = requiredTranslateResult;
    }

    private <T> ResourcePackRequiredMod(DynamicOps<T> ops, T t) throws VersionParsingException {
        super(ops, t);
        this.translateKey = ops.getStringValue(ops.get(t, "translateKey").getOrThrow()).getOrThrow();
        this.requiredTranslateResult = ops.getStringValue(ops.get(t, "requiredTranslateResult").getOrThrow()).getOrThrow();
    }

    @Override
    protected <T> DataResult<T> write(DynamicOps<T> ops, T ignored) {
        DataResult<T> result = super.write(ops, ignored);
        if (result.isError()) return result;
        else try {
            T r = result.getOrThrow();
            ops.set(r, "translateKey", ops.createString(translateKey));
            ops.set(r, "requiredTranslateResult", ops.createString(requiredTranslateResult));
            return DataResult.success(r);
        } catch (Throwable e) {
            return Utils.CodecOnException(e);
        }
    }

    @Override
    public Text createNotintalledText() {
        return Text.translatable("disconnect.thecoldworldlib.notinstall.respackrequiredmod", modName, version, downloadLink);
    }
}
