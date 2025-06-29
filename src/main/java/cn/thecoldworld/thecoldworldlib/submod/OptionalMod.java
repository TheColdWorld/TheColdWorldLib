package cn.thecoldworld.thecoldworldlib.submod;

import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;

public final class OptionalMod extends Mod {
    public static final Decoder<Mod> DECODER = new Decoder<>() {
        @Override
        public <T> DataResult<Pair<Mod, T>> decode(DynamicOps<T> dynamicOps, T t) {
            try {
                return DataResult.success(Pair.of(new OptionalMod(dynamicOps, t), t));
            } catch (Exception e) {
                return ErrorUtil.CodecOnException(e);
            }
        }
    };
    public static final String MOD_TYPE = "optional";

    static {
        Mod.ModTypes.put(MOD_TYPE, DECODER);
    }

    public OptionalMod(String modid, String compatibleVersion, URI downloadLink) throws VersionParsingException {
        super(modid, compatibleVersion, downloadLink, MOD_TYPE);
    }

    public OptionalMod(ModContainer modContainer, String compatibleVersion, URI downloadLink) throws VersionParsingException {
        super(modContainer, compatibleVersion, downloadLink, MOD_TYPE);
    }

    private <T> OptionalMod(DynamicOps<T> ops, T t) throws VersionParsingException {
        super(ops, t);
    }

    public Text createOptionalModInstallMessage() {
        return Text.translatable("chat.thecoldworldlib.optionalmod.content", modName, version,
                Text.literal(downloadLink.toString())
                        .formatted(Formatting.BLUE, Formatting.ITALIC)
                        .styled(s ->
                                s.withClickEvent(new ClickEvent.OpenUrl(downloadLink))
                                        .withUnderline(true)
                                        .withHoverEvent(new HoverEvent.ShowText(Text.translatable("hover.thecoldworld.download")))));
    }
}
