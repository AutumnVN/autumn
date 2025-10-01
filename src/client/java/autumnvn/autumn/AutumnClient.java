package autumnvn.autumn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import static autumnvn.autumn.Autumn.MOD_ID;

public class AutumnClient implements ClientModInitializer {
    public static MinecraftClient client;
    public static Options options;
    public static KeyBinding autoAttackKey;
    public static KeyBinding ignorePlayerKey;
    public static KeyBinding freeCamKey;
    public static KeyBinding settingKey;
    public static KeyBinding zoomKey;
    public static double tps;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        options = new Options();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("autumn:autumn"));
        autoAttackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Attack", GLFW.GLFW_KEY_R, category));
        ignorePlayerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Ignore Player", GLFW.GLFW_KEY_UNKNOWN, category));
        freeCamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Free Cam", GLFW.GLFW_KEY_H, category));
        settingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Autumn Settings", GLFW.GLFW_KEY_BACKSLASH, category));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Zoom", GLFW.GLFW_KEY_LEFT_ALT, category));

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("autumn", "autumn"), container, Text.literal("Autumn"), ResourcePackActivationType.DEFAULT_ENABLED));
        BlockRenderLayerMap.putBlock(Blocks.BARRIER, BlockRenderLayer.TRANSLUCENT);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            options.autoAttack.setValue(false);
            options.freeCam.setValue(false);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null || client.world == null) return;
            while (settingKey.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen));
            }
            handleToggleKey(autoAttackKey, options.autoAttack, "Auto Attack");
            handleToggleKey(ignorePlayerKey, options.ignorePlayer, "Ignore Player");
            handleToggleKey(freeCamKey, options.freeCam, "Free Cam");

            // AutoAttack
            if (options.autoAttack.getValue() && client.player.getAttackCooldownProgress(0) >= 1) {
                if (client.targetedEntity instanceof LivingEntity livingEntity && livingEntity.isAttackable() && livingEntity.isAlive() && livingEntity.hurtTime == 0 && !(options.ignorePlayer.getValue() && livingEntity instanceof PlayerEntity)) {
                    client.interactionManager.attackEntity(client.player, livingEntity);
                    client.player.swingHand(Hand.MAIN_HAND);
                }
            }

            // FreeCam
            if (options.freeCam.getValue() && client.player.input instanceof KeyboardInput) {
                Input input = new Input();
                input.playerInput = new PlayerInput(false, false, false, false, false, client.player.input.playerInput.sneak(), false);
                client.player.input = input;
            } else if (!options.freeCam.getValue() && !(client.player.input instanceof KeyboardInput)) {
                client.player.input = new KeyboardInput(client.options);
            }

            // RightClickHarvest
            if (options.rightClickHarvest.getValue() && client.options.useKey.isPressed() && client.crosshairTarget != null && client.crosshairTarget.getType() == Type.BLOCK) {
                BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;
                BlockPos pos = hitResult.getBlockPos();
                Block block = client.world.getBlockState(pos).getBlock();
                if (block instanceof CropBlock cropBlock && cropBlock.isMature(client.world.getBlockState(pos)) || block instanceof NetherWartBlock && client.world.getBlockState(pos).get(NetherWartBlock.AGE) == 3) {
                    client.interactionManager.attackBlock(pos, hitResult.getSide());
                    client.player.swingHand(Hand.MAIN_HAND);
                }
            }
        });
    }

    static void handleToggleKey(KeyBinding key, SimpleOption<Boolean> option, String name) {
        if (client.player == null) return;
        while (key.wasPressed()) {
            option.setValue(!option.getValue());
            client.player.sendMessage(Text.of(name + " is now " + (option.getValue() ? "§aON" : "§cOFF")), true);
        }
    }
}
