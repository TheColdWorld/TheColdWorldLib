package cn.thecoldworld.thecoldworldlib.entrypoints;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import cn.thecoldworld.thecoldworldlib.submod.OptionalMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.net.URI;
import java.net.URISyntaxException;

public class Universal implements ModInitializer {

    @Override
    public void onInitialize() {
        ModManager.initialize();
        try {
            ModManager.getInstance().register(new OptionalMod(Vars.MODID, "=0.0.1-Alpha", new URI("https://modrinth.com/project/thecoldworldlib")));
        } catch (VersionParsingException | URISyntaxException e) {
            throw new CrashException(new CrashReport("", e));
        }
    }
}
