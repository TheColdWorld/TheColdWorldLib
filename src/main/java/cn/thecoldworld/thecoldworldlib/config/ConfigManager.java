package cn.thecoldworld.thecoldworldlib.config;

import cn.thecoldworld.thecoldworldlib.utils.FileUtil;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class ConfigManager {
    private static final ConfigManager _instance = new ConfigManager();
    private final Map<String, AbstractConfig> configs = new LinkedHashMap<>();
    private boolean frozen = false;

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        return _instance;
    }

    public void register(AbstractConfig config) {
        configs.put(config.getID().toString(), config);
    }

    public void freeze() {
        frozen = true;
    }

    public boolean register(File file) {
        if (frozen) return false;
        Optional<AbstractConfig> config = AbstractConfig.fromFile(file);
        if (config.isPresent()) {
            register(config.get());
            return true;
        } else return false;
    }

    public void write() {
        for (Map.Entry<String, AbstractConfig> config : configs.entrySet()) {
            JsonObject obj = config.getValue().write(JsonOps.INSTANCE, new JsonObject()).getOrThrow().getAsJsonObject();
            if (!FileUtil.RecreateFile(config.getValue().configFile)) continue;
            else try (FileOutputStream stream = new FileOutputStream(config.getValue().configFile, false)) {
                stream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception ignored) {
            }
        }
    }

    public <T extends AbstractConfig> Optional<T> get(Identifier id, Class<T> tClass) {
        AbstractConfig config = configs.get(id.toString());
        if (config == null) return Optional.empty();
        else return config.as(tClass);
    }

    public <T extends AbstractConfig> T getOrElse(Identifier id, Class<T> tClass, Supplier<T> elseGet) {
        AbstractConfig config = configs.get(id.toString());
        if (config == null) {
            T t = elseGet.get();
            configs.put(t.getID().toString(), t);
            return t;
        } else {
            Optional<T> t = config.as(tClass);
            return t.orElseGet(() -> {
                T t1 = elseGet.get();
                return (T) configs.put(t1.getID().toString(), t1);//checked
            });
        }
    }
}
