package dev.sodiograaz.experienceCensimentiProxy.configuration;

import dev.sodiograaz.experienceCensimentiProxy.configuration.data.ExpCensProxyConfig;

import java.io.File;

/* @author Sodiograaz
 @since 23/01/2025
*/
// Read-only class
public interface TomlConfiguration {
	
	ExpCensProxyConfig getConfiguration();
	TomlConfiguration saveFile();
	TomlConfiguration copyFile();;
	
}