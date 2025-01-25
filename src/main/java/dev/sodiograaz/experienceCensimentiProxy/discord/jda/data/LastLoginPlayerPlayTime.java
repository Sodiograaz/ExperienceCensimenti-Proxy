package dev.sodiograaz.experienceCensimentiProxy.discord.jda.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* @author Sodiograaz
 @since 24/01/2025
*/
@Getter
public class LastLoginPlayerPlayTime {
	
	private final String username;
	private final Long lastLogin;
	private final Long lastLogout;
	private Long processedTimestamp;
	
	public LastLoginPlayerPlayTime(String username, Long lastLogin, Long lastLogout) {
		this.username = username;
		this.lastLogin = lastLogin;
		this.lastLogout = lastLogout;
		this.processedTimestamp = lastLogout - lastLogin;
	}
	
}