package dev.sodiograaz.experienceCensimentiProxy.discord.jda;

import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.PaginatorBuilder;
import dev.sodiograaz.experienceCensimentiProxy.ExperienceCensimentiProxy;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.JDAClientHolder;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands.CommandInfo;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.events.CommandManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.stream.Collectors;

/* @author Sodiograaz
 @since 24/01/2025
*/
public class JDAClientHolderImpl implements JDAClientHolder {
	
	private JDA client;
	private final String token = ExperienceCensimentiProxy.getExperienceCensimentiProxy()
			.getConfig()
			.getToken();
	
	private static @Getter Guild guild;
	private static final CommandManager commandManager = new CommandManager("dev.sodiograaz.experienceCensimenti.discord.jda.commands");
	
	@SneakyThrows
	@Override
	public JDAClientHolder createClient() throws IllegalStateException, InterruptedException {
		if(this.client != null) throw new IllegalStateException("Il client è stato già creato e non può essere sostituito!");
		
		this.client = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
				.enableCache(CacheFlag.MEMBER_OVERRIDES)
				.setMemberCachePolicy(member -> !member.getUser().isBot())
				.setCompression(Compression.NONE)
				.addEventListeners(commandManager)
				.build()
				.awaitReady();
		
		Pages.activate(PaginatorBuilder.createPaginator(client)
				.shouldRemoveOnReact(false)
				.shouldEventLock(true).build());
		
		guild = this.client.getGuildById(ExperienceCensimentiProxy.getConfig()
				.getGuildId());
		
		if (guild != null) {
			guild.loadMembers()
					.onSuccess(x -> ExperienceCensimentiProxy.getExperienceCensimentiProxy()
							.getLogger()
							.info("Caricato tutti i membri discord: " + x.size() + "."))
					.onError(x -> ExperienceCensimentiProxy.getExperienceCensimentiProxy()
							.getLogger()
							.info("Errore nel caricare tutti i membri discord.\n" + x.getLocalizedMessage()));
		}
		
		guild.updateCommands()
				.addCommands(commandManager.getCommandHandlerSet().stream()
						.map(x -> {
							CommandInfo commandInfo = null;
							try {
								commandInfo = x.getCommandInfo();
							} catch (IllegalAccessException e) {}
							return new CommandDataImpl(commandInfo.name(), commandInfo.description())
									.addOptions(x.optionsData());
						})
						.collect(Collectors.toList()))
				.queue();
		
		return this;
	}
	
	@Override
	public JDAClientHolder shutdownClient() throws IllegalStateException {
		if(this.client == null) throw new IllegalStateException("Il client non esiste e per tanto non può essere spento.");
		this.client.shutdown();
		this.client = null;
		return this;
	}
	
	@Override
	public JDA getClient() throws IllegalStateException {
		return this.client;
	}
	
	@Override
	public JDAClientHolder getInstance() {
		return this;
	}
}