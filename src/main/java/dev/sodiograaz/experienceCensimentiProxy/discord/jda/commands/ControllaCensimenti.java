package dev.sodiograaz.experienceCensimentiProxy.discord.jda.commands;

import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import dev.sodiograaz.experienceCensimentiProxy.ExperienceCensimentiProxy;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.JDAUtils;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands.CommandHandler;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.data.LastLoginPlayerPlayTime;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.OffsetDateTime;
import java.util.*;

/* @author Sodiograaz
 @since 24/01/2025
*/
public class ControllaCensimenti implements CommandHandler {
	
	private final PartiesAPI partiesAPI = ExperienceCensimentiProxy.getPartiesAPI();
	private final LastLoginAPI lastLoginAPI = ExperienceCensimentiProxy.getLastLoginAPI();
	
	@Override
	public List<OptionData> optionsData() {
		return List.of(new OptionData(OptionType.STRING, "gilda", "Scegli la gilda da censire", true));
	}
	
	@Override
	public void executeCommand(SlashCommandInteractionEvent event, Member author, User user, User self, Guild guild) {
		String gilda = event.getOption("gilda").getAsString();
		
		Party party = partiesAPI.getParty(gilda);
		Set<UUID> members = party.getMembers();

// Prepara la lista dei dati dei membri
		List<LastLoginPlayerPlayTime> lastLoginPlayerPlayTimeList = members.stream()
				.map(uuid -> {
					LastLoginPlayer player = lastLoginAPI.getPlayer(uuid);
					return new LastLoginPlayerPlayTime(player.getName(), player.getLastLogin(), player.getLastLogout());
				})
				.toList();
		
		List<MessageEmbed> embeds = new LinkedList<>();
		
		// Raggruppa i membri in pagine di massimo 10 elementi per pagina
		int pageSize = 10; // Numero massimo di membri per pagina
		for (int i = 0; i < lastLoginPlayerPlayTimeList.size(); i += pageSize) {
			// Crea un sottogruppo di massimo 10 membri
			List<LastLoginPlayerPlayTime> pageMembers = lastLoginPlayerPlayTimeList.subList(
					i, Math.min(i + pageSize, lastLoginPlayerPlayTimeList.size())
			);
			
			// Genera il contenuto della pagina
			StringBuilder stringBuilder = new StringBuilder();
			for (LastLoginPlayerPlayTime member : pageMembers) {
				stringBuilder.append(String.format("%s è stato offline per %s\n",
						member.getUsername(),
						convertDifference(member.getProcessedTimestamp())
				));
			}
			
			// Crea l'embed per la pagina
			MessageEmbed embed = JDAUtils.getGuildBasedEmbedForCommand(user)
					.addField("Censimenti", String.format("```\n%s\n```", stringBuilder.toString()), false)
					.build();
			embeds.add(embed);
		}
		
		List<Page> pages = new LinkedList<>();
		
		for (MessageEmbed embed : embeds) {
			pages.add(InteractPage.of(embed));
		}
		
		event.replyEmbeds((MessageEmbed) pages.get(0).getContent()).queue(success -> {
			Pages.paginate(success.retrieveOriginal().complete(), pages, true);
		});
	}
	
	private String convertDifference(long differenceInMillis) {
		// Converte millisecondi in secondi
		long seconds = differenceInMillis / 1000;
		
		// Calcolo giorni, ore, minuti e secondi
		long days = seconds / 86400; // 1 giorno = 86400 secondi
		seconds %= 86400;
		
		long hours = seconds / 3600; // 1 ora = 3600 secondi
		seconds %= 3600;
		
		long minutes = seconds / 60; // 1 minuto = 60 secondi
		seconds %= 60;
		
		return String.format("%s giorni e %s ore e %s secondi", days, hours, minutes);
	}
	
}