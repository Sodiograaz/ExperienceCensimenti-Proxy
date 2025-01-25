package dev.sodiograaz.experienceCensimentiProxy.discord.jda;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Instant;

/* @author Sodiograaz
 @since 24/01/2025
*/
public class JDAUtils {
	
	public static EmbedBuilder getGuildBasedEmbed() {
		final Guild guild = JDAClientHolderImpl.getGuild();
		return new EmbedBuilder()
				.setAuthor(guild.getName(), guild.getIconUrl(), guild.getIconUrl())
				.setColor(Color.RED)
				.setTimestamp(Instant.now());
	}
	
	public static EmbedBuilder getGuildBasedEmbedForCommand(User user) {
		return getGuildBasedEmbed()
				.setFooter("Executed by " + user.getAsTag(), user.getAvatarUrl())
				.setUrl("https://mcexp.it");
	}
	
	
}