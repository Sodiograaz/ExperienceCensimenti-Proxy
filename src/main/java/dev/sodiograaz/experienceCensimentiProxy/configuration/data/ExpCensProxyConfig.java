package dev.sodiograaz.experienceCensimentiProxy.configuration.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/* @author Sodiograaz
 @since 23/01/2025
*/
@Getter
public class ExpCensProxyConfig {
	
	@SerializedName("Bot-Token")
	String token;
	@SerializedName("Guild-ID")
	Long guildId;
	
}