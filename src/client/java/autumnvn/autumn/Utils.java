package autumnvn.autumn;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Utils {
    private static class TimedEntity {
        long time;
        Entity entity;

        TimedEntity(Entity entity) {
            this.entity = entity;
            this.time = System.currentTimeMillis();
        }
    }

    private static TimedEntity recentTargetedEntity;

    public static Entity getTargetedEntity(float tickDelta) {
        Entity entity2 = AutumnClient.client.getCameraEntity();
        Vec3d vec3d = entity2.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = entity2.getRotationVec(1);
        Box box = entity2.getBoundingBox().expand(32767, 32767, 32767);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity2, vec3d, vec3d.add(vec3d2.multiply(32767)), box, entity -> !entity.isSpectator() && entity.canHit(), 32767);

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
}
