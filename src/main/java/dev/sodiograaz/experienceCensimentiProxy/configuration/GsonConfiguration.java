package dev.sodiograaz.experienceCensimentiProxy.configuration;

import dev.sodiograaz.experienceCensimentiProxy.configuration.data.ExpCensProxyConfig;

/* @author Sodiograaz
 @since 23/01/2025
*/
// Read-only class
public interface GsonConfiguration {
	
	ExpCensProxyConfig getConfiguration();
	GsonConfiguration saveFile();
	GsonConfiguration copyFile();;
	
}