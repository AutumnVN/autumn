package autumnvn.autumn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import static autumnvn.autumn.Autumn.MOD_ID;

public class AutumnClient implements ClientModInitializer {
    public static Minecraft minecraft;
    public static Options options;
    public static KeyMapping autoAttackKey;
    public static KeyMapping ignorePlayerKey;
    public static KeyMapping freeCamKey;
    public static KeyMapping settingKey;
    public static KeyMapping zoomKey;
    public static double tps;

    @Override
    public void onInitializeClient() {
        minecraft = Minecraft.getInstance();
        options = new Options();

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("autumn", "autumn"));
        autoAttackKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("Auto Attack", GLFW.GLFW_KEY_R, category));
        ignorePlayerKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("Ignore Player", GLFW.GLFW_KEY_UNKNOWN, category));
        freeCamKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("Free Cam", GLFW.GLFW_KEY_H, category));
        settingKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("Open Autumn Settings", GLFW.GLFW_KEY_BACKSLASH, category));
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("Zoom", GLFW.GLFW_KEY_LEFT_ALT, category));

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> ResourceLoader.registerBuiltinPack(Identifier.fromNamespaceAndPath("autumn", "autumn"), container, Component.literal("Autumn"), PackActivationType.DEFAULT_ENABLED));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, minecraft) -> {
            options.autoAttack.set(false);
            options.freeCam.set(false);
        });

        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            if (minecraft.player == null || minecraft.gameMode == null || minecraft.level == null) return;
            while (settingKey.consumeClick()) {
                minecraft.gui.setScreen(new SettingsScreen(minecraft.gui.screen()));
            }
            handleToggleKey(autoAttackKey, options.autoAttack, "Auto Attack");
            handleToggleKey(ignorePlayerKey, options.ignorePlayer, "Ignore Player");
            handleToggleKey(freeCamKey, options.freeCam, "Free Cam");

            // AutoAttack
            if (options.autoAttack.get() && minecraft.player.getAttackStrengthScale(0.0F) >= 1.0F) {
                if (minecraft.crosshairPickEntity instanceof LivingEntity livingEntity && livingEntity.isAttackable() && livingEntity.isAlive() && livingEntity.hurtTime == 0 && !(options.ignorePlayer.get() && livingEntity instanceof Player)) {
                    minecraft.gameMode.attack(minecraft.player, livingEntity);
                    minecraft.player.swing(InteractionHand.MAIN_HAND);
                }
            }

            // FreeCam
            if (options.freeCam.get() && minecraft.player.input instanceof KeyboardInput) {
                ClientInput input = new ClientInput();
                input.keyPresses = new Input(false, false, false, false, false, minecraft.player.input.keyPresses.shift(), false);
                minecraft.player.input = input;
            } else if (!options.freeCam.get() && !(minecraft.player.input instanceof KeyboardInput)) {
                minecraft.player.input = new KeyboardInput(minecraft.options);
            }

            // RightClickHarvest
            if (options.rightClickHarvest.get() && minecraft.options.keyUse.isDown() && minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hitResult = (BlockHitResult) minecraft.hitResult;
                BlockPos pos = hitResult.getBlockPos();
                Block block = minecraft.level.getBlockState(pos).getBlock();
                if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(minecraft.level.getBlockState(pos)) || block instanceof NetherWartBlock && minecraft.level.getBlockState(pos).getValue(NetherWartBlock.AGE) == 3) {
                    minecraft.gameMode.startDestroyBlock(pos, hitResult.getDirection());
                    minecraft.player.swing(InteractionHand.MAIN_HAND);
                }
            }
        });
    }

    static void handleToggleKey(KeyMapping key, OptionInstance<Boolean> option, String name) {
        if (minecraft.player == null) return;
        while (key.consumeClick()) {
            option.set(!option.get());
            minecraft.player.sendOverlayMessage(Component.literal(name + " is now " + (option.get() ? "§aON" : "§cOFF")));
        }
    }
}