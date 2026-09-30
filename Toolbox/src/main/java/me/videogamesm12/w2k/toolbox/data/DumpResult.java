package me.videogamesm12.w2k.toolbox.data;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class DumpResult
{
	private List<String> successful;

	private List<String> failed;

	@Builder.Default
	private List<String> ignored = Collections.emptyList();

	private File outputDirectory;
}