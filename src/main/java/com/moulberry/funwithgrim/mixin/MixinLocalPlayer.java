package com.moulberry.funwithgrim.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import com.moulberry.funwithgrim.FunWithGrim;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends Player {

    @Shadow
    @Final
    public ClientPacketListener connection;

    @Shadow
    private double xLast;
    @Shadow
    private double yLast1;
    @Shadow
    private double zLast;
    public boolean sendOnGround = false;
    public boolean justSentOnGround = false;
    public double lastPos = 0.0;
    public Vec3 startMovement = Vec3.ZERO;

    public MixinLocalPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendPosition()V"))
    public void afterSendPosition(LocalPlayer instance, Operation<Void> original) {
        if (FunWithGrim.glide) {
            if (this.justSentOnGround) {
                float jumpHeight = 0.001f;
                this.connection.send(new ServerboundMovePlayerPacket.Pos(this.getX(), this.lastPos+jumpHeight, this.getZ(), false));
                this.xLast = this.getX();
                this.yLast1 = this.lastPos+jumpHeight;
                this.zLast = this.getZ();
                this.setPos(this.getX(), this.lastPos+jumpHeight, this.getZ());
                this.setDeltaMovement(this.startMovement.multiply(0.5515, 0.0, 0.5515));
                this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, -0.078));
                this.justSentOnGround = false;
                return;
            }
            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_K) != 0) {
                if (this.sendOnGround) {
                    this.lastPos = this.getY();
                    this.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true));
                    this.startMovement = this.getDeltaMovement();
                    this.setDeltaMovement(Vec3.ZERO);
                    this.sendOnGround = false;
                    this.justSentOnGround = true;
                    return;
                }
            } else {
                this.sendOnGround = true;
            }
        }
        original.call(instance);
    }

}
