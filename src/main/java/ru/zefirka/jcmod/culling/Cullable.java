package ru.zefirka.jcmod.culling;

public interface Cullable {
	boolean isForcedVisible();
	void addForcedVisible(long timeout);

	boolean isCheckTimeout();
	void addCheckTimeout(long timeout);
	
	void setCulled(boolean value);
	boolean isCulled();
}
