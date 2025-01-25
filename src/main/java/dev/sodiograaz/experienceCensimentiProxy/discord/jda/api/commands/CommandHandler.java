package dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/* @author Sodiograaz
 @since 24/01/2025
*/
public interface CommandHandler {
	
	void executeCommand(SlashCommandInteractionEvent event, Member author, User user, User self, Guild guild);

	List<OptionData> optionsData();
	
	default boolean checkIfCommandInfoIsDefined() {
		return CommandHandler.class
				.getAnnotation(CommandInfo.class) == null;
	}
	
	default @Nullable CommandInfo getCommandInfo() throws IllegalAccessException {
		if(checkIfCommandInfoIsDefined())
			throw new IllegalAccessException("Un comando non è stato possibile registrarlo. mancano le istruzioni necessarie.");
		return this.getClass()
				.getAnnotation(CommandInfo.class);
	}
	
}