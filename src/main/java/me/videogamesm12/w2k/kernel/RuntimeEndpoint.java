package me.videogamesm12.w2k.kernel;

import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.util.SysUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.ZipFile;

public class RuntimeEndpoint
{
	// 2024-11-12T03\:03\:05-0700
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':'mm':'ssZZ");

	public static void main(String[] args)
	{
		int prompt = JOptionPane.showConfirmDialog(null, "This cannot be run as a standalone application and must be"
				+ "installed as a mod for Minecraft using the Fabric Mod Loader.\nWould you like to install it?",
				"HUH", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (prompt == JOptionPane.YES_OPTION)
		{
			File gameFolder;

			switch (SysUtils.getOperatingSystem())
			{
				case LINUX:
				{
					gameFolder = new File(new File(System.getProperty("user.home")), ".minecraft");
					break;
				}
				case WINDOWS:
				{
					gameFolder = Paths.get(System.getProperty("user.home"), "/AppData/Roaming/.minecraft").toFile();
					break;
				}
				default:
				{
					JOptionPane.showMessageDialog(null, "Your operating system isn't supported by this. Please let Video know so that he can implement support for it.", "Unsupported operating system", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if (!gameFolder.exists())
			{
				int prompt2 = JOptionPane.showConfirmDialog(null, ".minecraft was not found. Do you have the "
						+ "game installed using a third party launcher and if so, do you want to install it in a"
						+ " specific instance?", "HUH", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

				if (prompt2 == JOptionPane.YES_OPTION)
				{
					try
					{
						gameFolder = promptInstanceFolder(System.getProperty("user.home"));
					}
					// At this point they're just fucking with us.
					catch (StackOverflowError ignored)
					{
						try
						{
							SysUtils.execute("xdg-open", "https://www.youtube.com/watch?v=xvFZjo5PgG0");
							return;
						}
						catch (IOException ex)
						{
							System.err.println("Rickroll failed :(");
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Then I have no idea what to tell you.", "Welp", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if (gameFolder == null)
			{
				return;
			}

			final File modsFolder = new File(gameFolder, "mods");
			if (!modsFolder.exists())
			{
				modsFolder.mkdirs();
			}

			try
			{
				final URL url = RuntimeEndpoint.class.getProtectionDomain().getCodeSource().getLocation();
				final File sourceFile = Paths.get(url.toURI()).toFile();
				final File destinationFile = new File(modsFolder, sourceFile.getName());

				if (destinationFile.exists())
				{
					try (ZipFile existingFile = new ZipFile(destinationFile))
					{
						BuildMetadata thisBuild = BuildMetadata.getMetadataFromClassJar(RuntimeEndpoint.class);
						BuildMetadata existingBuild = BuildMetadata.getMetadataFromZipFile(existingFile);

						if (thisBuild == null) return;

						long existingBuildCommitDate = LocalDateTime.ofInstant(Instant.from(dateFormat.parse(
								Objects.requireNonNull(existingBuild).getCommitTime())), ZoneId.of("America/Denver"))
								.toEpochSecond(ZoneOffset.UTC);

						long ourBuildCommitDate = LocalDateTime.ofInstant(Instant.from(dateFormat.parse(
								Objects.requireNonNull(BuildMetadata.getMetadataFromClassJar(RuntimeEndpoint.class)).getCommitTime())), ZoneId.of("America/Denver"))
								.toEpochSecond(ZoneOffset.UTC);

						// Existing build has the same commit time as this one's
						if (existingBuildCommitDate == ourBuildCommitDate)
						{
							// Older build
							if (!existingBuild.isDirty() && thisBuild.isDirty())
							{
								int confirmation = JOptionPane.showConfirmDialog(null, "You already have W2K installed, but it appears to be an older build. Would you like to update it?", "Older development build detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
								if (confirmation == JOptionPane.NO_OPTION)
								{
									return;
								}
							}
							// Same build.
							else if (existingBuild.isDirty() == thisBuild.isDirty())
							{
								JOptionPane.showMessageDialog(null, "You already have this version of W2K installed.", "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							// Newer build
							else
							{
								JOptionPane.showMessageDialog(null, "You have a newer version of W2K installed already.", "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						else if (existingBuildCommitDate > ourBuildCommitDate)
						{
							JOptionPane.showMessageDialog(null, "This instance has a newer version of W2K installed already.", "Error", JOptionPane.QUESTION_MESSAGE);
							return;
						}
						else
						{
							int confirmUpgrade = JOptionPane.showConfirmDialog(null, "You already have W2K installed, but it appears to be an older version. Would you like to upgrade?", "Older version detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if (confirmUpgrade == JOptionPane.NO_OPTION)
							{
								return;
							}
						}

					}
					catch (IOException ex)
					{
						System.err.println("Error!");
						ex.printStackTrace();
					}

					// Nuke the old and bring in the new.
					destinationFile.delete();
				}

				Files.copy(sourceFile.toPath(), destinationFile.toPath());
			}
			catch (IOException | URISyntaxException ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "We weren't able to copy the mod jar to your mods folder.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static File promptInstanceFolder(String directory)
	{
		final JFileChooser folderChooser = new JFileChooser(directory);
		folderChooser.setDialogTitle("Choose an instance to install to");
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			final File selectedFile = folderChooser.getSelectedFile();
			if (!selectedFile.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "That's not a folder. Try again.");
				return promptInstanceFolder(selectedFile.getParentFile().getAbsolutePath());
			}

			File instanceFolder = new File(selectedFile, ".minecraft");
			if (instanceFolder.exists())
			{
				return instanceFolder;
			}
			else if (selectedFile.getName().equalsIgnoreCase(".minecraft"))
			{
				return selectedFile;
			}

			JOptionPane.showMessageDialog(null, "This isn't a .minecraft folder.");
			return promptInstanceFolder(selectedFile.getParentFile().getAbsolutePath());
		}

		return null;
	}
}
