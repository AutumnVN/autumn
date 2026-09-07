package autumnvn.autumn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class Utils {

    static class TimedEntity {
        long time;
        Entity entity;

        TimedEntity(Entity entity) {
            this.entity = entity;
            this.time = System.currentTimeMillis();
        }
    }

    static TimedEntity recentTargetedEntity;

    public static Entity getTargetedEntity() {
        Entity cameraEntity = AutumnClient.minecraft.getCameraEntity();
        if (cameraEntity == null) return null;

        double maxDistance = 128;
        float tickDelta = AutumnClient.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        Vec3 vec3d = cameraEntity.getEyePosition();
        Vec3 vec3d2 = cameraEntity.getViewVector(tickDelta).scale(maxDistance);
        AABB box = cameraEntity.getBoundingBox().expandTowards(vec3d2).inflate(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(cameraEntity, vec3d, vec3d.add(vec3d2), box, entity -> !entity.isSpectator() && entity.isPickable(), maxDistance * maxDistance);

        if (entityHitResult != null) {
            recentTargetedEntity = new TimedEntity(entityHitResult.getEntity());
            return recentTargetedEntity.entity;
        }

        if (recentTargetedEntity != null && (System.currentTimeMillis() - recentTargetedEntity.time) > 3000) {
            recentTargetedEntity = null;
        }

        return recentTargetedEntity == null ? null : recentTargetedEntity.entity;
    }

    public static String color(double value, double min, double max) {
        double third = (max - min) / 3;
        return value < min + third ? "§c" : value < min + third * 2 ? "§e" : "§a";
    }

    public static String getOwnerName(Entity entity) {
        if (!(entity instanceof TamableAnimal tameableEntity)) return null;

        if (AutumnClient.minecraft.level == null) return null;
        LivingEntity owner = EntityReference.get(tameableEntity.getOwnerReference(), AutumnClient.minecraft.level, LivingEntity.class);
        return owner == null ? null : Objects.requireNonNull(owner.getDisplayName()).getString();
    }
}