package cn.thecoldworld.thecoldworldlib;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public final class Vars {
    public static final Codec<Object> DEFAULT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Object, T>> decode(DynamicOps<T> dynamicOps, T t) {
            throw new IllegalCallerException();
        }

        @Override
        public <T> DataResult<T> encode(Object o, DynamicOps<T> dynamicOps, T t) {
            throw new IllegalCallerException();
        }
    };
    public static final String MOD_VERSION = "0.0.3";
    public static final String COMPATIBLE_VERSION = "0.0.3";
    public static final String MODID = "thecoldworldlib";
    public static final Log LOGGER = LogFactory.getLog("TheColdWorldLib");

    private Vars() {
    }
}
