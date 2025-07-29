package cn.thecoldworld.thecoldworldlib.debug;

import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.CommonPlayC2SPacket;
import cn.thecoldworld.thecoldworldlib.networking.packet.s2c.CommonPlayS2CPacket;
import cn.thecoldworld.thecoldworldlib.utils.ErrorUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugStickItem extends Item {
    public DebugStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new CommonPlayS2CPacket<>(DebugVars.S2CMetadata, new DebugMessage("server", "Server debug hello")));
        } else {
            world.sendPacket(new CommonPlayC2SPacket<>(DebugVars.C2SMetadata, new DebugMessage("client", "Client debug hello")));
        }

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            try {
                NbtCompound testNbt = new NbtCompound();
                testNbt.putString("n", "test.Exception");
                testNbt.putString("m", "Test message");
                testNbt.put("s", new NbtList());
                SerializedException.SubException.CODEC.parse(NbtOps.INSTANCE, testNbt).getOrThrow();
            } catch (Exception e) {
                player.sendMessage(Text.literal(ErrorUtil.createFailString(e)));
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return false;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return 0;
    }
}
