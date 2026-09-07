package autumnvn.autumn;

import java.util.Objects;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class FreeCam extends AbstractClientPlayer {

    static final float sin45 = Mth.sin((float) Math.toRadians(45));

    public ClientInput input;

    public FreeCam() {
        super(
                Objects.requireNonNull(AutumnClient.minecraft.level),
                new GameProfile(UUID.randomUUID(), "FreeCam"));
        setId(-1);
        getAbilities().flying = true;
        input = new KeyboardInput(AutumnClient.minecraft.options);
        setPos(
                Objects.requireNonNull(AutumnClient.minecraft.player).getX(),
                AutumnClient.minecraft.player.getY(),
                AutumnClient.minecraft.player.getZ());
        setYRot(AutumnClient.minecraft.player.getYRot());
        setXRot(AutumnClient.minecraft.player.getXRot());
    }

    public void spawn() {
        Objects.requireNonNull(AutumnClient.minecraft.level).addEntity(this);
    }

    public void despawn() {
        Objects.requireNonNull(AutumnClient.minecraft.level).removeEntity(getId(), Entity.RemovalReason.DISCARDED);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith(Entity other) {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    @Override
    public boolean canSimulateMovement() {
        return true;
    }

    @Override
    public void tick() {
        getAbilities().setFlyingSpeed(0);

        double horizontalSpeed = isSprinting() ? 1.5 : 1;
        double verticalSpeed = 1;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        Vec3 forward = Vec3.directionFromRotation(0, getYRot());
        Vec3 side = Vec3.directionFromRotation(0, getYRot() + 90);

        input.tick();

        if (input.keyPresses.forward() || input.keyPresses.backward()) {
            double direction = input.keyPresses.forward() ? 1 : -1;
            x += forward.x * horizontalSpeed * direction;
            z += forward.z * horizontalSpeed * direction;
        }

        if (input.keyPresses.right() || input.keyPresses.left()) {
            double direction = input.keyPresses.right() ? 1 : -1;
            z += side.z * horizontalSpeed * direction;
            x += side.x * horizontalSpeed * direction;
        }

        if ((input.keyPresses.forward() || input.keyPresses.backward()) && (input.keyPresses.right() || input.keyPresses.left())) {
            x *= sin45;
            z *= sin45;
        }

        if (input.keyPresses.jump())
            y += verticalSpeed;
        if (input.keyPresses.shift())
            y -= verticalSpeed;
        setDeltaMovement(x, y, z);

        super.tick();
        getAbilities().flying = true;
        setOnGround(false);
    }

    @Override
    protected void applyInput() {
        Vec2 moveVector = input.getMoveVector();
        if (moveVector.lengthSquared() != 0.0F) {
            moveVector = moveVector.scale(0.98F);
        }
        applyInputHelper(moveVector, input.keyPresses.jump());
    }

    private void applyInputHelper(Vec2 moveVector, boolean jumping) {
        this.xxa = moveVector.x;
        this.zza = moveVector.y;
        this.jumping = jumping;
        this.setSprinting((AutumnClient.minecraft.options.keySprint.isDown() && input.keyPresses.forward()) || (input.keyPresses.forward() && this.isSprinting()));
    }
}
