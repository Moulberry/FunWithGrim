package com.moulberry.funwithgrim;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunWithGrim implements ModInitializer {
    public static boolean waterSpider = false;
    public static boolean glide = false;
    public static boolean canSwimHop = false;

    public static boolean cancelNextMotion = false;

	@Override
	public void onInitialize() {
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext) -> {
            var command = ClientCommandManager.literal("waterspider");
            command.executes(ctx -> {
                waterSpider = !waterSpider;
                ctx.getSource().sendFeedback(Component.literal("waterspider: " + waterSpider));
                return 0;
            });
            commandDispatcher.register(command);
        });
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext) -> {
            var command = ClientCommandManager.literal("glide");
            command.executes(ctx -> {
                glide = !glide;
                ctx.getSource().sendFeedback(Component.literal("glide: " + glide));
                return 0;
            });
            commandDispatcher.register(command);
        });
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            if (minecraft.level == null || minecraft.player == null) {
                return;
            }

            if (waterSpider && minecraft.player.horizontalCollision && minecraft.player.input.jumping && !minecraft.player.minorHorizontalCollision) {
                ItemStack held = minecraft.player.getMainHandItem();
                if (held.getItem() == Items.WATER_BUCKET) {

                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            if (minecraft.level == null || minecraft.player == null) {
                return;
            }

            canSwimHop = !minecraft.player.onGround() && minecraft.level.containsAnyLiquid(minecraft.player.getBoundingBox().inflate(0.09)) &&
                !minecraft.level.noCollision(minecraft.player.getBoundingBox().inflate(minecraft.player.getDeltaMovement().x+0.5, 0.0, minecraft.player.getDeltaMovement().z+0.5));
        });
	}

    private static boolean isInWaterLenient(Minecraft minecraft) {
        AABB playerAabb = minecraft.player.getBoundingBox().inflate(0.09);
        int i = Mth.floor(playerAabb.minX);
        int j = Mth.ceil(playerAabb.maxX);
        int k = Mth.floor(playerAabb.minY);
        int l = Mth.ceil(playerAabb.maxY);
        int m = Mth.floor(playerAabb.minZ);
        int n = Mth.ceil(playerAabb.maxZ);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for(int o = i; o < j; ++o) {
            for(int p = k; p < l; ++p) {
                for(int q = m; q < n; ++q) {
                    BlockState blockState = minecraft.level.getBlockState(mutableBlockPos.set(o, p, q));
                    if (blockState.getFluidState().is(Fluids.WATER)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
