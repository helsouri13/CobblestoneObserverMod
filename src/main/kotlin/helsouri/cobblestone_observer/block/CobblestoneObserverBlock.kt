package helsouri.cobblestone_observer.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.redstone.Orientation

class CobblestoneObserverBlock(settings: Properties) : Block(settings) {

    companion object {
        val POWERED: BooleanProperty = BlockStateProperties.POWERED
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(POWERED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(POWERED)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        orientation: Orientation?,
        movedByPiston: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston)

        if (level.isClientSide) return

        // "Armed" = a redstone signal is being supplied to this block from an external source.
        val armed = level.hasNeighborSignal(pos)

        if (!armed) {
            // If we lose our arming signal mid-pulse, turn off cleanly.
            if (state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_ALL)
            }
            return
        }

        // Observer is armed. Check the block above.
        val aboveIsCobble = level.getBlockState(pos.above()).`is`(Blocks.COBBLESTONE)

        if (aboveIsCobble && !state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_ALL)
            level.scheduleTick(pos, this, 2)
        }
    }

    override fun tick(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        random: RandomSource
    ) {
        if (state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_ALL)
        }
    }

    override fun isSignalSource(state: BlockState): Boolean = true

    override fun getSignal(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Int = if (state.getValue(POWERED)) 15 else 0

    override fun getDirectSignal(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Int = if (state.getValue(POWERED)) 15 else 0
}