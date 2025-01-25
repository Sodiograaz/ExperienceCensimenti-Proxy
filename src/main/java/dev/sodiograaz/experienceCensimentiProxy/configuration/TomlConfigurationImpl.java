package dev.sodiograaz.experienceCensimentiProxy.configuration;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import dev.sodiograaz.experienceCensimentiProxy.ExperienceCensimentiProxy;
import dev.sodiograaz.experienceCensimentiProxy.configuration.data.ExpCensProxyConfig;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/* @author Sodiograaz
 @since 24/01/2025
*/
public class TomlConfigurationImpl implements TomlConfiguration {
	
	private File file;
	private ExpCensProxyConfig config;
	private final Path configPath = Paths.get(ExperienceCensimentiProxy.getExperienceCensimentiProxy().getDataFolder().getAbsolutePath(), "config.toml");
	
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
		if(file != null && file.exists()) {
			ExperienceCensimentiProxy.getExperienceCensimentiProxy()
					.getLogger()
					.info("File di configurazione già esistente.");
			copyFile();
			return this;
		}
		file = new File(configPath.toUri());
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
		try(InputStream isfr = Files.newInputStream(configPath)) {
			this.config = new TomlMapper().readValue(isfr, ExpCensProxyConfig.class);
		}
		return this;
	}

}