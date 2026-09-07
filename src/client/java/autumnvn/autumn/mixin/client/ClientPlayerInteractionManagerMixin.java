package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    private int destroyDelay;

    @Shadow
    private BlockPos destroyBlockPos;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private int slot;

    // KeepMiningWhenSwap
    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    private void sameDestroyTarget(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (AutumnClient.options.keepMiningWhenSwap.get() && pos.equals(destroyBlockPos)) {
            cir.setReturnValue(true);
        }
    }

    // NoMineDelay
    @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
    private void continueDestroyBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (AutumnClient.options.noMineDelay.get()) {
            this.destroyDelay = 0;
        }
    }

    // FreeCam
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attack(Player player, Entity target, CallbackInfo ci) {
        if (target == minecraft.player) {
            ci.cancel();
        }
    }

    // AutoHitSwap
    @Inject(method = "attack", at = @At("HEAD"))
    private void attack2(Player player, Entity target, CallbackInfo ci) {
        if (AutumnClient.options.autoHitSwap.get()) {
            slot = player.getInventory().getSelectedSlot();
            ItemStack stack = player.getMainHandItem();
            Item item = stack.getItem();

            if (getAxeHotbarSlot(player) != -1 && target instanceof Player playerEntity && playerEntity.getUseItem().is(Items.SHIELD)) {
                player.getInventory().setSelectedSlot(getAxeHotbarSlot(player));
                return;
            }

            if (getBreachMaceHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && livingEntity.getArmorValue() > 15) {
                player.getInventory().setSelectedSlot(getBreachMaceHotbarSlot(player));
                return;
            }

            if (getSmiteSwordHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && isInTag(livingEntity.getType(), EntityTypeTags.UNDEAD)) {
                player.getInventory().setSelectedSlot(getSmiteSwordHotbarSlot(player));
                return;
            }

            if (getBaneOfArthropodsSwordHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && isInTag(livingEntity.getType(), EntityTypeTags.ARTHROPOD)) {
                player.getInventory().setSelectedSlot(getBaneOfArthropodsSwordHotbarSlot(player));
                return;
            }

            if (getImpalingTridentHotbarSlot(player) != -1 && target instanceof LivingEntity livingEntity && isInTag(livingEntity.getType(), EntityTypeTags.AQUATIC)) {
                player.getInventory().setSelectedSlot(getImpalingTridentHotbarSlot(player));
                return;
            }

            if (getEnchantedSwordHotbarSlot(player) != -1) {
                player.getInventory().setSelectedSlot(getEnchantedSwordHotbarSlot(player));
                return;
            }

            if (getNonWeaponHotbarSlot(player) != -1 && ((stack.is(ItemTags.SWORDS) && stack.getEnchantments().isEmpty()) || (item instanceof AxeItem && !hasEnchantment(stack, Enchantments.SHARPNESS)) || stack.is(ItemTags.PICKAXES) || item instanceof ShovelItem || item instanceof HoeItem || item instanceof TridentItem || item instanceof MaceItem)) {
                player.getInventory().setSelectedSlot(getNonWeaponHotbarSlot(player));
            }
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void attack3(Player player, Entity target, CallbackInfo ci) {
        if (AutumnClient.options.autoHitSwap.get()) {
            player.getInventory().setSelectedSlot(slot);
        }
    }

    @Unique
    private static int getAxeHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static int getBreachMaceHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == Items.MACE && hasEnchantment(stack, Enchantments.BREACH)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static int getSmiteSwordHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ItemTags.SWORDS) && hasEnchantment(stack, Enchantments.SMITE)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static int getBaneOfArthropodsSwordHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ItemTags.SWORDS) && hasEnchantment(stack, Enchantments.BANE_OF_ARTHROPODS)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static int getImpalingTridentHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof TridentItem && hasEnchantment(stack, Enchantments.IMPALING)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static int getEnchantedSwordHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ItemTags.SWORDS) && !stack.getEnchantments().isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static int getNonWeaponHotbarSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            Item item = stack.getItem();
            if (!stack.is(ItemTags.SWORDS) && !(item instanceof AxeItem) && !stack.is(ItemTags.PICKAXES) && !(item instanceof ShovelItem) && !(item instanceof HoeItem) && !(item instanceof TridentItem) && !(item instanceof MaceItem)) {
                return i;
            }
        }
        return -1;
    }

    @Unique
    private static boolean hasEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        for (Holder<Enchantment> holder : stack.getEnchantments().keySet()) {
            if (holder.is(enchantment)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean isInTag(EntityType<?> type, TagKey<EntityType<?>> tag) {
        for (Holder<EntityType<?>> holder : BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(tag)) {
            if (holder.value() == type) {
                return true;
            }
        }
        return false;
    }
}