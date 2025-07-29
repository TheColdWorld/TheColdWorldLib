package cn.thecoldworld.thecoldworldlib.config;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;

public final class ExceptionConfig extends AbstractConfig {

    public static final Identifier ID = Identifier.of(Vars.MODID, "config/exception");

    static {
        if (!AbstractConfig.Register(ID, ExceptionConfig::new))
            throw new RuntimeException("[TheColdWorldLib]Cannot register internal config[config/exception]");
    }

    private boolean _writePacketExceptionToFile;
    private String _exceptionFileStoreDir;

    /**
     * create TheColdWorldLib's config of Exceptions from current file
     *
     * @param file config file ,encoding must be {@code StandardCharsets.UTF_8}
     */
    public ExceptionConfig(File file) {
        super(file, false);
    }

    /**
     * create ExceptionConfig with default config
     */
    public ExceptionConfig() {
        super(FabricLoader.getInstance().getConfigDir().resolve("thecoldworldlib/exception.json"), true);
    }

    @Override
    protected Identifier getID() {
        return ID;
    }

    public String get_exceptionFileStoreDir() {
        if (_exceptionFileStoreDir == null) _exceptionFileStoreDir = "./TheColdWorldLib/exceptions";
        return _exceptionFileStoreDir;
    }

    public void set_exceptionFileStoreDir(String _exceptionFileStoreDir) {
        this._exceptionFileStoreDir = _exceptionFileStoreDir;
    }

    @Override
    public <T> DataResult<T> write(DynamicOps<T> ops, T prefix) {
        try {
            prefix = super.write(ops, prefix).getOrThrow();
            prefix = ops.set(prefix, "WritePacketExceptionToFile", ops.createBoolean(_writePacketExceptionToFile));
            prefix = ops.set(prefix, "ExceptionFileStoreDir", ops.createString(_exceptionFileStoreDir));
        } catch (Throwable e) {
            return ErrorUtil.CodecOnException(e);
        }
        return DataResult.success(prefix);
    }

    @Override
    protected void createDefaultConfig() {
        _writePacketExceptionToFile = false;
        _exceptionFileStoreDir = "./TheColdWorldLib/exceptions";
    }

    @Override
    public <T> void read(DynamicOps<T> ops, T input) {
        _writePacketExceptionToFile = ops.getBooleanValue(ops.get(input, "WritePacketExceptionToFile").getOrThrow()).getOrThrow();
        _exceptionFileStoreDir = ops.getStringValue(ops.get(input, "ExceptionFileStoreDir").getOrThrow()).getOrThrow();
    }

    public boolean is_WritePacketExceptionToFile() {
        return _writePacketExceptionToFile;
    }

    public void WritePacketExceptionToFile(boolean value) {
        _writePacketExceptionToFile = value;
    }
}
