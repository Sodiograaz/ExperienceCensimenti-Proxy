package dev.sodiograaz.experienceCensimentiProxy.discord.jda.events;

import dev.sodiograaz.experienceCensimentiProxy.discord.jda.JDAUtils;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.JDAClientHolder;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands.CommandHandler;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands.CommandInfo;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.TimeUnit;

/* @author Sodiograaz
 @since 24/01/2025
*/
public class CommandManager extends ListenerAdapter {
	
	private final @Getter Set<CommandHandler> commandHandlerSet;
	
	@SneakyThrows
	public CommandManager(String classPath) {
		List<CommandHandler> tmpCommandHandlersList = new ArrayList<>();
		for(Class<? extends CommandHandler> extendedClass : new Reflections(classPath).getSubTypesOf(CommandHandler.class)) {
			CommandHandler commandHandler = extendedClass.getConstructor().newInstance();
			tmpCommandHandlersList.add(commandHandler);
		}
		this.commandHandlerSet = new LinkedHashSet<>(tmpCommandHandlersList);
		tmpCommandHandlersList = null;
	}
	
	@Override
	@SneakyThrows
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		String name = event.getName();
		
		if(!event.isFromGuild()) return;
		
		Member author = event.getMember();
		User user = event.getUser();
		SelfUser self = event.getJDA().getSelfUser();
		Guild guild = event.getGuild();
		
		for(CommandHandler commandHandler : this.commandHandlerSet) {
			CommandInfo commandInfo = commandHandler.getCommandInfo();
			if(commandInfo.name().equals(name)) {
				// Check only one role access
				if(commandInfo.usedOnlyBy() != 0L) {
					if(author.getRoles().stream()
							.noneMatch(x -> x.getIdLong() == commandInfo.usedOnlyBy())) {
						event.replyEmbeds(JDAUtils.getGuildBasedEmbedForCommand(user)
								.setDescription(String.format("Non puoi eseguire questo comando!\nTi serve il ruolo %s",
										guild.getRoleById(commandInfo.usedOnlyBy()).getName()))
								.build())
								.queue(x -> x.deleteOriginal()
										.queueAfter(1, TimeUnit.MINUTES));
						return;
					}
				}
				commandHandler.executeCommand(event, author, user, self, guild);
			}
		}
		
		super.onSlashCommandInteraction(event);
	}
}