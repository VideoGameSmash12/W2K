package me.videogamesm12.w2k.kernel.data;

import com.google.gson.JsonElement;

import java.util.UUID;

public interface AbstractW2KEntity
{
	JsonElement w2kGetEntityName();

	String w2kGetEntityType();

	String w2kGetEntityLocation();

	int w2kGetEntityId();

	UUID w2kGetEntityUuid();

	String w2kGetEntityNBT();
}
