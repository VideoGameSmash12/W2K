package me.videogamesm12.w2k.kernel.wrapper.entity;

import com.google.gson.JsonElement;

import java.util.UUID;

/**
 * <h1>WrappedEntity</h1>
 * <p>Wrapper interface for Entity instances. Must be implemented with a Mixin.</p>
 */
public interface WrappedEntity
{
	int w2k$getId();

	UUID w2k$getUuid();

	String w2k$getType();

	JsonElement w2k$getName();

	String w2k$getNbt();

	void w2k$kill();
}
