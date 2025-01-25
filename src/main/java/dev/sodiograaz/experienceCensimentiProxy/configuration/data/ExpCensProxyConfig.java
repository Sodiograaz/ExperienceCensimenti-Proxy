package dev.sodiograaz.experienceCensimentiProxy.configuration.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/* @author Sodiograaz
 @since 23/01/2025
*/
@Getter
public class ExpCensProxyConfig {
	
	@JsonProperty("Bot-Token")
	String token;
	@JsonProperty("Guild-ID")
	Long guildId;
	
}