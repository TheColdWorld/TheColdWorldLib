package cn.thecoldworld.thecoldworldlib.config;

import cn.thecoldworld.thecoldworldlib.exceptions.InvalidFileException;
import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import cn.thecoldworld.thecoldworldlib.utils.FileUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractConfig {
    private static final Map<String, Function<File, ? extends AbstractConfig>> REGISTRIES = new LinkedHashMap<>();
    public transient final File configFile;

    /**
     * internal use
     */
    protected AbstractConfig(@NotNull Path filePath) {
        this(filePath.toFile(), false);
    }

    protected AbstractConfig(String filePath) {
        this(new File(filePath), false);
    }

    protected AbstractConfig(@NotNull Path filePath, boolean create) {
        this(filePath.toFile(), create);
    }

    protected AbstractConfig(String filePath, boolean create) {
        this(new File(filePath), create);
    }

    protected AbstractConfig(File file) {
        this(file, false);
    }

    protected AbstractConfig(File file, boolean create) {
        try {
            if (!file.exists()) {
                if (!create)
                    throw new InvalidFileException(file, new FileNotFoundException("file " + file.getAbsolutePath() + " not found"));
                else if (!FileUtil.CreateFile(file))
                    throw new InvalidFileException(file, "cannot create file '" + file.getAbsolutePath() + "'");
                this.configFile = file;
                createDefaultConfig();
                return;
            }
            if (!file.isFile())
                throw new IllegalArgumentException("path '" + file.getAbsolutePath() + "' is not a file");
            this.configFile = file;
            read(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)));
        } catch (IOException | JsonParseException e) {
            throw new InvalidFileException(file, e);
        }
    }

    public static <T extends AbstractConfig> boolean Register(Identifier id, Function<File, T> factory) {
        if (REGISTRIES.containsKey(id.toString())) return false;
        else {
            REGISTRIES.put(id.toString(), factory);
            return true;
        }
    }

    public static Optional<AbstractConfig> fromFile(File file) {
        try {
            if (!file.exists() || !file.isFile()) return Optional.empty();
            JsonObject object = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (!REGISTRIES.containsKey(object.get("id").getAsString())) return Optional.empty();
            return Optional.of(REGISTRIES.get(object.get("id").getAsString()).apply(file));
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }

    public static <T extends AbstractConfig> Optional<T> fromFile(File file, Class<T> tClass) {
        try {
            if (!file.exists() || !file.isFile()) return Optional.empty();
            JsonObject object = JsonParser.parseString(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (!REGISTRIES.containsKey(object.get("id").getAsString())) return Optional.empty();
            return REGISTRIES.get(object.get("id").getAsString()).apply(file).as(tClass);
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }

    protected abstract Identifier getID();

    public <T> DataResult<T> write(DynamicOps<T> ops, T prefix) {
        try {
            prefix = ops.set(prefix, "id", Identifier.CODEC.encode(getID(), ops, ops.empty()).getOrThrow());
            return DataResult.success(prefix);
        } catch (Throwable throwable) {
            return ErrorUtil.CodecOnException(throwable);
        }
    }

    public <T extends AbstractConfig> Optional<T> as(Class<T> tClass) {
        if (tClass.isInstance(this)) {
            try {
                return Optional.of(tClass.cast(this));
            } catch (Throwable ignored) {
                return Optional.empty();
            }
        } else return Optional.empty();
    }

    protected abstract void createDefaultConfig();

    public abstract <T> void read(DynamicOps<T> ops, T input);
}
