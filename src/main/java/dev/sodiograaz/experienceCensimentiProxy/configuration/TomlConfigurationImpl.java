package dev.sodiograaz.experienceCensimentiProxy.configuration;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.sodiograaz.experienceCensimentiProxy.ExperienceCensimentiProxy;
import dev.sodiograaz.experienceCensimentiProxy.configuration.data.ExpCensProxyConfig;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/* @author Sodiograaz
 @since 24/01/2025
*/
public class TomlConfigurationImpl implements TomlConfiguration {
	
	// START BOILERPLATE
	
	private final ProxyServer proxyServer;
	private final Logger logger;
	private final Path configPath;
	
	public TomlConfigurationImpl(ProxyServer proxyServer, Logger logger, Path configPath) {
		this.proxyServer = proxyServer;
		this.logger = logger;
		this.configPath = configPath;
		this.file = new File(configPath.toFile(), "config.toml");
	}
	
	// END BOILERPLATE
	
	private final File file;
	private ExpCensProxyConfig config;
	
	@Override
	public ExpCensProxyConfig getConfiguration() {
		if(this.config == null) {
			saveFile();
		}
		return this.config;
	}
	
	@SneakyThrows
	@Override
	public TomlConfiguration saveFile() {
		if(configPath.toFile() != null && configPath.toFile().exists() && file != null && file.exists()) {
			ExperienceCensimentiProxy.getExperienceCensimentiProxy()
					.getLogger()
					.info("File di configurazione già esistente.");
			copyFile();
			return this;
		}
		configPath.toFile().mkdir();
		file.createNewFile();
		
		try (InputStream isfr = this.getClass()
				.getClassLoader()
				.getResourceAsStream("config.toml")) {
			ExperienceCensimentiProxy.getExperienceCensimentiProxy()
					.getLogger()
					.info("Copiando il default nella nuova directory.");
			FileUtils.copyInputStreamToFile(isfr, file);
			copyFile();
			
		}
		return this;
	}
	
	@SneakyThrows
	@Override
	public TomlConfiguration copyFile() {
		try(InputStream isfr = new FileInputStream(this.file)) {
			this.config = new TomlMapper().readValue(isfr, ExpCensProxyConfig.class);
		}
		return this;
	}

}