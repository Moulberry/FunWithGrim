package com.moulberry.funwithgrim.mixin;

import com.moulberry.funwithgrim.FunWithGrim;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "travel", at = @At(value = "RETURN"))
    public void travel(Vec3 vec3, CallbackInfo ci) {
        if (FunWithGrim.waterSpider && FunWithGrim.canSwimHop && (Object) this instanceof LocalPlayer player && player.input.jumping &&
                this.isInWater() && this.getDeltaMovement().y < 0.3f) {
            this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, 0.3f));
        }
//        if (FunWithGrim.glide) {
//            Vec3 movement = this.getDeltaMovement();
//            movement = new Vec3(movement.x, movement.y * 0.3f, movement.z);
//            this.setDeltaMovement(movement);
//        }
    }

}
