package dev.sodiograaz.experienceCensimentiProxy;

import com.alessiodp.lastloginapi.api.LastLogin;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import dev.sodiograaz.experienceCensimentiProxy.configuration.TomlConfiguration;
import dev.sodiograaz.experienceCensimentiProxy.configuration.TomlConfigurationImpl;
import dev.sodiograaz.experienceCensimentiProxy.configuration.data.ExpCensProxyConfig;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.JDAClientHolderImpl;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.JDAClientHolder;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public final class ExperienceCensimentiProxy extends Plugin {
	
	private static @Getter ExperienceCensimentiProxy experienceCensimentiProxy;
	private static TomlConfiguration tomlConfiguration = new TomlConfigurationImpl();
	private static @Getter ExpCensProxyConfig config;
	
	private static @Getter JDAClientHolder jdaClientHolder;
	private static @Getter LastLoginAPI lastLoginAPI;
	private static @Getter PartiesAPI partiesAPI;
	
	@Override
	public void onEnable() {
		experienceCensimentiProxy = this;
		tomlConfiguration.saveFile();
		config = tomlConfiguration.getConfiguration();
		
		jdaClientHolder = new JDAClientHolderImpl();
	}
	
	@Override
	public void onDisable() {
	}
	
	
	private void setupLastLoginAPI() {
		if (getProxy().getPluginManager().getPlugin("LastLoginAPI") != null) {
			// LastLoginAPI is enabled
			lastLoginAPI = LastLogin.getApi();
		}
	}
	
	private void setupPartiesAPI() {
		if (getProxy().getPluginManager().getPlugin("Parties") != null) {
			// Parties is enabled
			partiesAPI = Parties.getApi();
		}
	}
}