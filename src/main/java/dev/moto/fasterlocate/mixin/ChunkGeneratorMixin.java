package dev.moto.fasterlocate.mixin;

import dev.moto.fasterlocate.Fasterlocate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerLevel.class)
public abstract class ChunkGeneratorMixin {
    @Inject(at = @At("HEAD"), method = "findNearestMapStructure", cancellable = true)
    private void locateButFaster(TagKey<Structure> structure, BlockPos pos, int p_215014_, boolean p_215015_, CallbackInfoReturnable<BlockPos> cir) {
        if (structure.equals(StructureTags.ON_TREASURE_MAPS)) {
            Optional<BlockPos> foundPosition = Fasterlocate.getTreasureLocation(pos);
            foundPosition.ifPresent(cir::setReturnValue);
        }
    }
}
