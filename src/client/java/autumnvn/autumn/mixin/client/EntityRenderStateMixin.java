package autumnvn.autumn.mixin.client;

import autumnvn.autumn.interfaces.IEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IEntityRenderState {

    @Unique
    private Entity entity;

    @Unique
    public Entity autumn$getEntity() {
        return entity;
    }

    @Unique
    public void autumn$setEntity(Entity entity) {
        this.entity = entity;
    }
}
