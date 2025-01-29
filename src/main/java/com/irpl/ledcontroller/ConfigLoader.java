package com.irpl.ledcontroller;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class ConfigLoader {
	private final File configFile;
	private Config config;
	private final Logger logger;

	public ConfigLoader(File pluginFolder, Logger logger) {
		this.logger = logger;
		this.configFile = new File(pluginFolder, "application.conf");

		if (!configFile.exists()) {
			saveDefaultConfig();
		}

		loadConfig();
	}

	private void saveDefaultConfig() {
		try {
			Files.createDirectories(configFile.getParentFile().toPath());
			Files.copy(getClass().getClassLoader().getResourceAsStream("application.conf"), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			logger.info("Default application.conf created.");
		} catch (IOException e) {
			logger.severe("Failed to create default application.conf: " + e.getMessage());
		}
	}

	private void loadConfig() {
		config = ConfigFactory.parseFile(configFile).withFallback(ConfigFactory.load());
		logger.info("Configuration loaded successfully.");
	}

	public Config getConfig() {
		return config;
	}
}
