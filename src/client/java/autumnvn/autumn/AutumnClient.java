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
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class AutumnClient implements ClientModInitializer {
    public static MinecraftClient client;
    public static Options options;
    public static KeyBinding autoAttackKey;
    public static KeyBinding ignorePlayerKey;
    public static KeyBinding settingKey;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        options = new Options();
        options.autoAttack.setValue(false);
        autoAttackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Attack", GLFW.GLFW_KEY_R, "Autumn"));
        ignorePlayerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Ignore Player", GLFW.GLFW_KEY_UNKNOWN, "Autumn"));
        settingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Autumn Settings", GLFW.GLFW_KEY_BACKSLASH, "Autumn"));

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            while (settingKey.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen));
            }
            handleToggleKey(autoAttackKey, options.autoAttack, "Auto Attack");
            handleToggleKey(ignorePlayerKey, options.ignorePlayer, "Ignore Player");

            // AutoAttack
            if (options.autoAttack.getValue() && client.player.getAttackCooldownProgress(0) >= 1 && client.targetedEntity instanceof LivingEntity livingEntity && livingEntity.isAttackable() && livingEntity.isAlive() && livingEntity.hurtTime == 0 && !(options.ignorePlayer.getValue() && livingEntity instanceof PlayerEntity)) {
                client.interactionManager.attackEntity(client.player, livingEntity);
                client.player.swingHand(client.player.getActiveHand());
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

    private void handleToggleKey(KeyBinding key, SimpleOption<Boolean> option, String name) {
        while (key.wasPressed()) {
            option.setValue(!option.getValue());
            client.player.sendMessage(Text.of(name + " is now " + (option.getValue() ? "§aON" : "§cOFF")), true);
        }
    }
}
