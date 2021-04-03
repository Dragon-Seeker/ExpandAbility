package be.florens.effectful.mixin.swimming;

import be.florens.effectful.EventDispatcher;
import be.florens.effectful.Util;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tags/Tag;)D"))
	private double setFluidHeight(LivingEntity entity, Tag<Fluid> tag) {
		if (entity instanceof Player) {
			InteractionResult shouldSwim = EventDispatcher.onPlayerSwim((Player) entity);
			return Util.processEventResult(shouldSwim, 1D, 0D, () -> entity.getFluidHeight(tag));
		}

		return entity.getFluidHeight(tag); // Vanilla behaviour
	}

	@Redirect(method = {"aiStep", "travel", "checkFallDamage"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"))
	private boolean setInWater(LivingEntity entity) {
		if (entity instanceof Player) {
			return Util.processEventResult(EventDispatcher.onPlayerSwim((Player) entity), entity::isInWater);
		}

		return entity.isInWater(); // Vanilla behaviour
	}

	/**
	 * Reset the fall distance every tick when swimming is enabled
	 */
	@Inject(method = "checkFallDamage", at = @At("HEAD"))
	private void resetFallHeight(CallbackInfo info) {
		//noinspection ConstantConditions
		if ((Object) this instanceof Player && EventDispatcher.onPlayerSwim((Player) (Object) this).consumesAction()) {
			this.fallDistance = 0;
		}
	}
}