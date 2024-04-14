package be.florens.expandability.test.fabric.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {

    @Accessor
    void setSpawnInvulnerableTime(int spawnInvulnerableTime);
}
