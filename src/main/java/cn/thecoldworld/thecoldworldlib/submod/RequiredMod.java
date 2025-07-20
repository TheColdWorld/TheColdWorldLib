package cn.thecoldworld.thecoldworldlib.submod;

import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.text.Text;

import java.net.URI;

public final class RequiredMod extends Mod {
    public static final Decoder<Mod> DECODER = new Decoder<>() {
        @Override
        public <T> DataResult<Pair<Mod, T>> decode(DynamicOps<T> dynamicOps, T t) {
            try {
                return DataResult.success(Pair.of(new RequiredMod(dynamicOps, t), t));
            } catch (Exception e) {
                return ErrorUtil.CodecOnException(e);
            }
        }
    };
    public static final String MOD_TYPE = "required";

    static {
        Mod.ModTypes.put(MOD_TYPE, DECODER);
    }

    public RequiredMod(String modid, String compatibleVersion, URI downloadLink) throws VersionParsingException {
        super(modid, compatibleVersion, downloadLink, MOD_TYPE);
    }

    public RequiredMod(ModContainer modContainer, String compatibleVersion, URI downloadLink) throws VersionParsingException {
        super(modContainer, compatibleVersion, downloadLink, MOD_TYPE);
    }

    private <T> RequiredMod(DynamicOps<T> ops, T t) throws VersionParsingException {
        super(ops, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text createNotintalledText() {
        return Text.translatable("disconnect.thecoldworldlib.notinstall.requiredmod", modName, version, downloadLink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text createWrongVersionText(String currentversion) {
        return Text.translatable("disconnect.thecoldworldlib.wrongversion.requiredmod", modName, version, currentversion, downloadLink);
    }
}
