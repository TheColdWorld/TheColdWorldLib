package cn.thecoldworld.thecoldworldlib.debug;

import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;

public final class DebugVars {
    public static CommonPacketMetadata.ClientBind<DebugMessage> S2CMetadata = null;
    public static CommonPacketMetadata.ServerBind<DebugMessage> C2SMetadata = null;

    private DebugVars() {
    }
}
