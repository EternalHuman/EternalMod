package ru.zefirka.jcmod.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import ru.zefirka.jcmod.culling.EternalOptimizer;
import ru.zefirka.jcmod.culling.Cullable;

@Mixin(value = { Entity.class, TileEntity.class })
public class CullableMixin implements Cullable {

	private long lasttime = 0;
	private long timeout = 0;
	private boolean culled = false;
	private boolean outOfCamera = false;
	private boolean preOutOfCamera = false;

	@Override
	public void addForcedVisible(long timeout) {
		lasttime = System.currentTimeMillis() + timeout;
	}

	@Override
	public boolean isForcedVisible() {
		return lasttime > System.currentTimeMillis();
	}

	@Override
	public void addCheckTimeout(long timeout) {
		timeout = System.currentTimeMillis() + timeout;
	}

	@Override
	public boolean isCheckTimeout() {
		return timeout > System.currentTimeMillis();
	}

	@Override
	public void setCulled(boolean value) {
		this.culled = value;
		if (!value) {
			addForcedVisible(1250);
		}
	}

	@Override
	public boolean isCulled() {
		if (!EternalOptimizer.enabled) return false;
		return culled;
	}
}