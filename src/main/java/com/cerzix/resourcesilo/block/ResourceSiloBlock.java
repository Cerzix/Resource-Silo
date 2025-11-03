package com.cerzix.resourcesilo.block;

import com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity;
import com.cerzix.resourcesilo.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ResourceSiloBlock extends BaseEntityBlock {

    /**
     * Single-piece hollow shape (no overlapping boxes):
     * - Outer: 2..14 (X/Z), 0..15.5 (Y)
     * - Inner cavity removed: 4..12 (X/Z), 1..15.5 (Y)
     *   -> leaves 2px thick walls and a 1px floor slab (Y=0..1), open top with rim-like walls.
     */
    // ---- Two-part hitbox: pallet base + box body ----
    private static final VoxelShape SHAPE;
    static {
        VoxelShape pallet = box(1, 0, 1, 15, 2, 15);     // low wooden base
        VoxelShape boxBody = box(2, 2, 2, 14, 14, 14);   // main cardboard container

        SHAPE = Shapes.or(pallet, boxBody);
    }



    public ResourceSiloBlock(BlockBehaviour.Properties props) { super(props); }

    public ResourceSiloBlock() {
        this(BlockBehaviour.Properties.of()
                .strength(2.0F, 6.0F)
                .sound(SoundType.METAL)
                .noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResourceSiloBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ResourceSiloBlockEntity silo)) return InteractionResult.PASS;

        if (player instanceof ServerPlayer sp) {
            NetworkHooks.openScreen(sp, (MenuProvider) silo, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.RESOURCE_SILO_BE.get(),
                (lvl, p, st, be) -> ResourceSiloBlockEntity.serverTick(be));
    }
}
