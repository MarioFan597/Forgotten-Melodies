package com.melodyjam.plugin.Systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.melodyjam.plugin.component.AuraBlock;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AuraBlockSystem extends EntityTickingSystem<ChunkStore> {
    private static final Query<ChunkStore> QUERY = Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType() );
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void tick(float dt, int index, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        BlockSection blocks = (BlockSection)archetypeChunk.getComponent(index, BlockSection.getComponentType());
        assert blocks != null;

        if (blocks.getTickingBlocksCountCopy() == 0) {
            return;
        }

        ChunkSection section = (ChunkSection)archetypeChunk.getComponent(index, ChunkSection.getComponentType());
        assert section != null;

        BlockComponentChunk blockComponentChunk = (BlockComponentChunk)commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());
        assert blockComponentChunk != null;

        blocks.forEachTicking(blockComponentChunk, commandBuffer, section.getY(),
            (chunkComponent, tickingCommandBuffer, localX, localY, localZ, blockId) -> {
                Ref<ChunkStore> blockRef = chunkComponent.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                if (blockRef == null) {
                    return BlockTickStrategy.IGNORED;
                }

                AuraBlock aura = (AuraBlock)tickingCommandBuffer.getComponent(blockRef, AuraBlock.getComponentType());
                if (aura == null) {
                    return BlockTickStrategy.IGNORED;
                }

                WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(section.getChunkColumnReference(), WorldChunk.getComponentType());

                int globalX = localX + (worldChunk.getX() * 32);
                int globalZ = localZ + (worldChunk.getZ() * 32);

                aura.runBlockAction(dt, globalX, localY, globalZ, worldChunk.getWorld().getEntityStore().getStore());

                return BlockTickStrategy.CONTINUE;
            });
    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return QUERY;
    }
}
