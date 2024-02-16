package ru.zefirka.jcmod.culling;

public interface Cullable {
	boolean isCheckTimeout();
	void addCheckTimeout(long timeout);
	
	void setCulled(boolean value);
	boolean isCulled();
}
