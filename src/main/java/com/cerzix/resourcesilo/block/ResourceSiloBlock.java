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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ResourceSiloBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 14, 14);

    public ResourceSiloBlock(BlockBehaviour.Properties props) {
        super(props);
    }

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
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(
            net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state) {
        return new com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ResourceSiloBlockEntity silo) {
                // BE is a MenuProvider now
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) silo,
                        buf -> buf.writeBlockPos(pos));
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T>
    getTicker(net.minecraft.world.level.Level level,
              net.minecraft.world.level.block.state.BlockState state,
              net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        if (level.isClientSide) return null; // tick only on server
        return (lvl, pos, st, be) -> {
            if (be instanceof com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity silo) {
                com.cerzix.resourcesilo.blockentity.ResourceSiloBlockEntity.serverTick(silo);
            }
        };
    }

}
