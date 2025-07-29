package cn.thecoldworld.thecoldworldlib.config;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;

public final class CommonConfig extends AbstractConfig {

    public static final Identifier ID = Identifier.of(Vars.MODID, "config/common");

    static {
        if (!AbstractConfig.Register(ID, CommonConfig::new))
            throw new RuntimeException("[TheColdWorldLib]Cannot register internal config[config/exception]");
    }

    short refreshTickCount;

    public CommonConfig(File file) {
        super(file, false);
    }

    public CommonConfig() {
        super(FabricLoader.getInstance().getConfigDir().resolve("thecoldworldlib/common.json"), true);
    }

    @Override
    protected Identifier getID() {
        return ID;
    }

    @Override
    protected void createDefaultConfig() {
        refreshTickCount = 200;
    }

    @Override
    public <T> void read(DynamicOps<T> ops, T input) {
        refreshTickCount = ops.getNumberValue(ops.get(input, "RefreshTickCount").getOrThrow()).getOrThrow().shortValue();
    }

    @Override
    public <T> DataResult<T> write(DynamicOps<T> ops, T prefix) {
        try {
            prefix = super.write(ops, prefix).getOrThrow();
            prefix = ops.set(prefix, "RefreshTickCount", ops.createShort(refreshTickCount));
            return DataResult.success(prefix);
        } catch (Throwable throwable) {
            return ErrorUtil.CodecOnException(throwable);
        }
    }

    public int getRefreshTickIndex() {
        return refreshTickCount - 1;
    }

    public void setRefreshTickCount(short refreshTickCount) {
        this.refreshTickCount = refreshTickCount;
    }
}
