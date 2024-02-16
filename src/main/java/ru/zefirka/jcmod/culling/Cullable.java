package ru.zefirka.jcmod.culling;

public interface Cullable {

	void setTimeout();
	boolean isForcedVisible();
	
	void setCulled(boolean value);
	boolean isCulled();
}
