package autumnvn.autumn;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class FreeCam extends ClientPlayerEntity {

    static final ClientPlayNetworkHandler networkHandler = new ClientPlayNetworkHandler(
            AutumnClient.client,
            Objects.requireNonNull(AutumnClient.client.getNetworkHandler()).getConnection(),
            new ClientConnectionState(
                    new GameProfile(UUID.randomUUID(), "FreeCam"),
                    AutumnClient.client.getTelemetryManager().createWorldSession(false, null, null),
                    AutumnClient.client.getNetworkHandler().getRegistryManager(),
                    AutumnClient.client.getNetworkHandler().getEnabledFeatures(),
                    AutumnClient.client.getNetworkHandler().getBrand(),
                    AutumnClient.client.getCurrentServerEntry(),
                    AutumnClient.client.currentScreen,
                    Collections.emptyMap(),
                    AutumnClient.client.inGameHud.getChatHud().toChatState(),
                    Collections.emptyMap(),
                    ServerLinks.EMPTY
            )
    ) {

        @Override
        public void sendPacket(Packet<?> packet) {
        }
    };

    public FreeCam() {
        super(
                AutumnClient.client,
                Objects.requireNonNull(AutumnClient.client.world),
                networkHandler,
                Objects.requireNonNull(AutumnClient.client.player).getStatHandler(),
                AutumnClient.client.player.getRecipeBook(),
                PlayerInput.DEFAULT,
                false
        );
        setId(-1);
        setLoaded(true);
        getAbilities().flying = true;
        input = new KeyboardInput(AutumnClient.client.options);
        refreshPositionAndAngles(
                AutumnClient.client.player.getPos().x,
                AutumnClient.client.player.getPos().y,
                AutumnClient.client.player.getPos().z,
                AutumnClient.client.player.getYaw(),
                AutumnClient.client.player.getPitch()
        );
    }

    public void spawn() {
        if (clientWorld != null) {
            clientWorld.addEntity(this);
        }
    }

    public void despawn() {
        if (clientWorld != null) {
            clientWorld.removeEntity(getId(), RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    static final float sin45 = MathHelper.sin((float) Math.toRadians(45));

    @Override
    public void tickMovement() {
        getAbilities().setFlySpeed(0);

        double horizontalSpeed = isSprinting() ? 1.5 : 1;
        double verticalSpeed = 1;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        Vec3d forward = Vec3d.fromPolar(0, getYaw());
        Vec3d side = Vec3d.fromPolar(0, getYaw() + 90);

        input.tick();

        if (input.playerInput.forward() || input.playerInput.backward()) {
            double direction = input.playerInput.forward() ? 1 : -1;
            x += forward.x * horizontalSpeed * direction;
            z += forward.z * horizontalSpeed * direction;
        }

        if (input.playerInput.right() || input.playerInput.left()) {
            double direction = input.playerInput.right() ? 1 : -1;
            z += side.z * horizontalSpeed * direction;
            x += side.x * horizontalSpeed * direction;
        }

        if ((input.playerInput.forward() || input.playerInput.backward()) && (input.playerInput.right() || input.playerInput.left())) {
            x *= sin45;
            z *= sin45;
        }

        if (input.playerInput.jump()) y += verticalSpeed;
        if (input.playerInput.sneak()) y -= verticalSpeed;
        setVelocity(x, y, z);

        super.tickMovement();
        getAbilities().flying = true;
        setOnGround(false);
    }
}
