package dev.sodiograaz.experienceCensimentiProxy;

import com.alessiodp.lastloginapi.api.LastLogin;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.sodiograaz.experienceCensimentiProxy.configuration.GsonConfiguration;
import dev.sodiograaz.experienceCensimentiProxy.configuration.GsonConfigurationImpl;
import dev.sodiograaz.experienceCensimentiProxy.configuration.data.ExpCensProxyConfig;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.JDAClientHolderImpl;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.JDAClientHolder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "experiencecensimenti", name = "ExperienceCensimenti-Proxy",
				version = "1.0.0a", description = "Utility per i censire villaggi/regni",
				authors = {"Sodiograaz"})
public final class ExperienceCensimentiProxy {
	
	// START BOILERPLATE
	
	private final @Getter ProxyServer proxyServer;
	private final @Getter Logger logger;
	private final @Getter Path dataDirectory;
	
	// END BOILERPLATE
	
	private static @Getter ExperienceCensimentiProxy experienceCensimentiProxy;
	private static GsonConfiguration gsonConfiguration;
	private static @Getter ExpCensProxyConfig config;
	
	private static @Getter JDAClientHolder jdaClientHolder;
	private static @Getter LastLoginAPI lastLoginAPI;
	private static @Getter PartiesAPI partiesAPI;
	
	@Inject
	public ExperienceCensimentiProxy(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
		this.proxyServer = proxyServer;
		this.logger = logger;
		this.dataDirectory = dataDirectory;
	}
	
	@SneakyThrows
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		experienceCensimentiProxy = this;
		gsonConfiguration = new GsonConfigurationImpl(proxyServer, logger, dataDirectory);
		gsonConfiguration.saveFile();
		config = gsonConfiguration.getConfiguration();
		
		jdaClientHolder = new JDAClientHolderImpl(proxyServer, logger);
		jdaClientHolder.createClient();
	}
	
	private void setupLastLoginAPI() {
		if(this.proxyServer.getPluginManager().getPlugin("LastLoginAPI").isPresent()) {
			if(this.proxyServer.getPluginManager().isLoaded("LastLoginAPI")) {
				// LastLoginAPI is enabled
				lastLoginAPI = LastLogin.getApi();
			}
		}
	}
	
	private void setupPartiesAPI() {
		if (this.proxyServer.getPluginManager().getPlugin("Parties").isPresent()) {
			if(this.proxyServer.getPluginManager().isLoaded("Parties")) {
				// Parties is enabled
				partiesAPI = Parties.getApi();
			}
		}
	}
}