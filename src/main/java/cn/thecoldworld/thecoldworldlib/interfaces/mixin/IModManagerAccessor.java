package cn.thecoldworld.thecoldworldlib.interfaces.mixin;

import cn.thecoldworld.thecoldworldlib.submod.ModManager;

public interface IModManagerAccessor {
    default ModManager getModManager() {
        throw new AssertionError();
    }

    default void setModManager(ModManager manager) {
        throw new AssertionError();
    }
}
