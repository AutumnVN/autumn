package autumnvn.autumn;

import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class AutumnClient implements ClientModInitializer {
    public static MinecraftClient client;
    public static Options options;
    public static KeyBinding settingKey;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        options = new Options();
        settingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Autumn Settings", GLFW.GLFW_KEY_BACKSLASH, "Autumn"));

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            while (settingKey.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen));
            }

            // RightClickHarvest
            if (options.rightClickHarvest.getValue() && client.options.useKey.isPressed() && client.crosshairTarget != null && client.crosshairTarget.getType() == Type.BLOCK) {
                BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
                BlockPos pos = hitResult.getBlockPos();
                Block block = world.getBlockState(pos).getBlock();
                if (block instanceof CropBlock cropBlock && cropBlock.isMature(world.getBlockState(pos)) || block instanceof NetherWartBlock && world.getBlockState(pos).get(NetherWartBlock.AGE) == 3) {
                    client.interactionManager.attackBlock(pos, hitResult.getSide());
                    client.player.swingHand(client.player.getActiveHand());
                }
            }
        });
    }
}
