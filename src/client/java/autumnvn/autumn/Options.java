package autumnvn.autumn;

import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.player.ClientInput;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Options {
    File file;
    Map<String, OptionInstance<?>> options;
    public FreeCam freeCamEntity;

    public OptionInstance<Boolean> autoAttack;
    public OptionInstance<Boolean> ignorePlayer;
    public OptionInstance<Boolean> autoHitSwap;
    public OptionInstance<Boolean> autoSprint;
    public OptionInstance<Boolean> betterChat;
    public OptionInstance<Boolean> betterNametag;
    public OptionInstance<Boolean> boat360;
    public OptionInstance<Boolean> deathCoord;
    public OptionInstance<Boolean> freeCam;
    public OptionInstance<Boolean> fullBright;
    public OptionInstance<Boolean> infoHud;
    public OptionInstance<Boolean> instantSneak;
    public OptionInstance<Boolean> keepMiningWhenSwap;
    public OptionInstance<Boolean> noFade;
    public OptionInstance<Boolean> noFishingBobber;
    public OptionInstance<Boolean> noFog;
    public OptionInstance<Boolean> noInvisible;
    public OptionInstance<Boolean> noJumpDelay;
    public OptionInstance<Boolean> noMineDelay;
    public OptionInstance<Boolean> noToast;
    public OptionInstance<Boolean> noUseDelay;
    public OptionInstance<Boolean> pingNumber;
    public OptionInstance<Boolean> rightClickHarvest;
    public OptionInstance<Boolean> thirdPersonNoClip;
    public OptionInstance<Boolean> visibleBarrier;

    public Options() {
        this.file = new File(AutumnClient.minecraft.gameDirectory, "config/autumn.txt");
        this.options = new HashMap<>();

        autoAttack = OptionInstance.createBoolean("Auto Attack", OptionInstance.cachedConstantTooltip(Component.literal("Automatically attack living entity at crosshair within reach")), false);
        options.put("autoAttack", autoAttack);
        ignorePlayer = OptionInstance.createBoolean("Ignore Player", OptionInstance.cachedConstantTooltip(Component.literal("Auto Attack will ignore player")), false);
        options.put("ignorePlayer", ignorePlayer);
        autoHitSwap = OptionInstance.createBoolean("Auto Hit Swap", OptionInstance.cachedConstantTooltip(Component.literal("Swap item in main hand to another item in hotbar when attacking, then swap back after attack so it uses base damage & cooldown of original item and enchantments & ability to disable shield of swapped item\nPriority:\nAxe if target is using shield\nBreach mace if target has more than 15 armor points\nSmite sword if target is undead\nBane of arthropods sword if target is arthropod\nImpaling trident if target is aquatic\nEnchanted sword\nNon-weapon if holding unenchanted sword/non-sharpness axe/other weapons")), true);
        options.put("autoHitSwap", autoHitSwap);
        autoSprint = OptionInstance.createBoolean("Auto Sprint", OptionInstance.cachedConstantTooltip(Component.literal("Automatically sprint when moving forward")), true);
        options.put("autoSprint", autoSprint);
        betterChat = OptionInstance.createBoolean("Better Chat", OptionInstance.cachedConstantTooltip(Component.literal("Lengthen chat history to 65k lines, keep chat/command history on switching world/server & remove chat indicator")), true);
        options.put("betterChat", betterChat);
        betterNametag = OptionInstance.createBoolean("Better Nametag", OptionInstance.cachedConstantTooltip(Component.literal("Add health & gamemode to nametag, make player nametag always visible & show recently targeted entity nametag")), true);
        options.put("betterNametag", betterNametag);
        boat360 = OptionInstance.createBoolean("Boat 360", OptionInstance.cachedConstantTooltip(Component.literal("Allow rotate 360 degrees when riding boat")), true);
        options.put("boat360", boat360);
        deathCoord = OptionInstance.createBoolean("Death Coord", OptionInstance.cachedConstantTooltip(Component.literal("Show death coordinates in chat")), true);
        options.put("deathCoord", deathCoord);
        freeCam = OptionInstance.createBoolean("Free Cam", OptionInstance.cachedConstantTooltip(Component.literal("Unbind camera from player, allow fly around & clip through blocks")), false, this::freeCamCallback);
        options.put("freeCam", freeCam);
        fullBright = OptionInstance.createBoolean("Full Bright", OptionInstance.cachedConstantTooltip(Component.literal("No more darkness")), true);
        options.put("fullBright", fullBright);
        infoHud = OptionInstance.createBoolean("Info Hud", OptionInstance.cachedConstantTooltip(Component.literal("Show fps, coordinates, direction, tps, targeted entity health, armor & horse stats on screen, show armor above hotbar, show hunger & xp bar when riding, show status effect amplifier & duration")), true);
        options.put("infoHud", infoHud);
        instantSneak = OptionInstance.createBoolean("Instant Sneak", OptionInstance.cachedConstantTooltip(Component.literal("Instantly sneak when holding shift, no animation")), true);
        options.put("instantSneak", instantSneak);
        keepMiningWhenSwap = OptionInstance.createBoolean("Keep Mining When Swap", OptionInstance.cachedConstantTooltip(Component.literal("Keep mining block when swapping item")), true);
        options.put("keepMiningWhenSwap", keepMiningWhenSwap);
        noFade = OptionInstance.createBoolean("No Fade", OptionInstance.cachedConstantTooltip(Component.literal("Remove fade animation on splash & title screen")), true);
        options.put("noFade", noFade);
        noFishingBobber = OptionInstance.createBoolean("No Fishing Bobber", OptionInstance.cachedConstantTooltip(Component.literal("Remove fishing bobber when it hooked on your face")), true);
        options.put("noFishingBobber", noFishingBobber);
        noFog = OptionInstance.createBoolean("No Fog", OptionInstance.cachedConstantTooltip(Component.literal("Remove fog (and sky), including submersions & potion effects")), true);
        options.put("noFog", noFog);
        noInvisible = OptionInstance.createBoolean("No Invisible", OptionInstance.cachedConstantTooltip(Component.literal("Force render invisible entities")), true);
        options.put("noInvisible", noInvisible);
        noJumpDelay = OptionInstance.createBoolean("No Jump Delay", OptionInstance.cachedConstantTooltip(Component.literal("Remove 10-tick delay after jumping")), true);
        options.put("noJumpDelay", noJumpDelay);
        noMineDelay = OptionInstance.createBoolean("No Mine Delay", OptionInstance.cachedConstantTooltip(Component.literal("Remove 6-tick delay when mining blocks")), true);
        options.put("noMineDelay", noMineDelay);
        noToast = OptionInstance.createBoolean("No Toast", OptionInstance.cachedConstantTooltip(Component.literal("Remove all in-game toast")), true);
        options.put("noToast", noToast);
        noUseDelay = OptionInstance.createBoolean("No Use Delay", OptionInstance.cachedConstantTooltip(Component.literal("Remove 4-tick delay when using items (has delay on first use incase you only want to click once)")), true);
        options.put("noUseDelay", noUseDelay);
        pingNumber = OptionInstance.createBoolean("Ping Number", OptionInstance.cachedConstantTooltip(Component.literal("Show ping number on tab list")), true);
        options.put("pingNumber", pingNumber);
        rightClickHarvest = OptionInstance.createBoolean("Right Click Harvest", OptionInstance.cachedConstantTooltip(Component.literal("Right click to harvest fully-grown crop & netherwart")), true);
        options.put("rightClickHarvest", rightClickHarvest);
        thirdPersonNoClip = OptionInstance.createBoolean("Third Person No Clip", OptionInstance.cachedConstantTooltip(Component.literal("Let third-person camera clip through blocks")), true);
        options.put("thirdPersonNoClip", thirdPersonNoClip);
        visibleBarrier = OptionInstance.createBoolean("Visible Barrier", OptionInstance.cachedConstantTooltip(Component.literal("Force render barrier block")), true, value -> AutumnClient.minecraft.levelRenderer.resetLevelRenderData());
        options.put("visibleBarrier", visibleBarrier);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                reader.lines().forEach(line -> {
                    String[] split = line.split(":");
                    if (split.length != 2) {
                        Autumn.LOGGER.warn("Invalid line in config file: {}", line);
                        return;
                    }
                    String key = split[0];
                    String value = split[1];
                    OptionInstance<?> option = options.get(key);
                    if (option == null || value.isEmpty()) {
                        Autumn.LOGGER.warn("Invalid option in config file: {}", line);
                    } else {
                        parseOption(option, value);
                    }
                });
            } catch (Exception e) {
                Autumn.LOGGER.error("Failed to read config file", e);
            }
        } else {
            boolean mkdirs = file.getParentFile().mkdirs();
            if (!mkdirs) {
                Autumn.LOGGER.error("Failed to create config directory");
            }
            save();
        }
    }

    <T> void parseOption(OptionInstance<T> option, String value) {
        DataResult<T> result = option.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(value));
        result.error().ifPresent(e -> Autumn.LOGGER.warn("Failed to parse option: {}", e.message()));
        result.result().ifPresent(option::set);
    }

    void save() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (Entry<String, OptionInstance<?>> option : options.entrySet()) {
                writer.println(option.getKey() + ":" + option.getValue().get());
            }
        } catch (FileNotFoundException e) {
            Autumn.LOGGER.error("Failed to create config file", e);
        }
    }

    // FreeCam
    void freeCamCallback(Boolean value) {
        AutumnClient.minecraft.smartCull = !value;
        if (value) {
            freeCamEntity = new FreeCam();
            freeCamEntity.spawn();
            AutumnClient.minecraft.setCameraEntity(freeCamEntity);
        } else {
            AutumnClient.minecraft.setCameraEntity(AutumnClient.minecraft.player);
            freeCamEntity.despawn();
            freeCamEntity.input = new ClientInput();
            freeCamEntity = null;
        }
    }
}