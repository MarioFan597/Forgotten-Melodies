package com.melodyjam.plugin.Systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.melodyjam.plugin.ForgottenMelodiesPlugin;
import com.melodyjam.plugin.component.AuraBlock;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AuraBlockInitializer extends RefSystem<ChunkStore> {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void onEntityAdded(@NonNullDecl Ref ref, @NonNullDecl AddReason addReason, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
        if (info == null) return;

        AuraBlock auraBlock = (AuraBlock)commandBuffer.getComponent(ref, ForgottenMelodiesPlugin.instance().getAuraBlockComponentType());
        if (auraBlock == null) return;

        int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
        int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
        int z = ChunkUtil.zFromBlockInColumn(info.getIndex());

        WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
        if (worldChunk != null) {
            worldChunk.setTicking(x, y, z, true);
        }
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(BlockModule.BlockStateInfo.getComponentType(), ForgottenMelodiesPlugin.instance().getAuraBlockComponentType());
    }
}
