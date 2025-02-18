package dev.sodiograaz.experienceCensimentiProxy.discord.jda.commands;

import com.alessiodp.lastloginapi.api.LastLogin;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI;
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.JDAUtils;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands.CommandHandler;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands.CommandInfo;
import dev.sodiograaz.experienceCensimentiProxy.discord.jda.data.LastLoginPlayerPlayTime;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/* @author Sodiograaz
 @since 24/01/2025
*/
@CommandInfo(name = "censimenti", usedOnlyBy = 805185915374796820L)
public class ControllaCensimenti implements CommandHandler {
	
	private final PartiesAPI partiesAPI = Parties.getApi();
	private final LastLoginAPI lastLoginAPI = LastLogin.getApi();

	@Override
	public List<OptionData> optionsData() {
		return List.of();
	}

	@Override
	public void executeCommand(SlashCommandInteractionEvent event, Member author, User user, User self, Guild guild) {
		String textChannelRawName = event.getChannel().getName();
		System.out.println(textChannelRawName);
		// togli primi 3 caratteri
		String withoutFirstThreeCharacters = textChannelRawName.substring(4);
		System.out.println(withoutFirstThreeCharacters);
		char firstLetterUpperCaseCharacter = withoutFirstThreeCharacters.charAt(0);
		char upperCaseCharacter = Character.toUpperCase(firstLetterUpperCaseCharacter);
		String effectiveTextChannelName = withoutFirstThreeCharacters.replaceFirst(Character.toString(firstLetterUpperCaseCharacter),
				Character.toString(upperCaseCharacter));
		System.out.println(effectiveTextChannelName);

		if(effectiveTextChannelName.contains("-")) {
			String[] strings = effectiveTextChannelName.split("-");
			char firstLetterUpperCaseCharacterX = strings[0].charAt(0);
			char upperCaseCharacterX = Character.toUpperCase(firstLetterUpperCaseCharacterX);
			String tempString = strings[0].replaceFirst(Character.toString(firstLetterUpperCaseCharacterX),
					Character.toString(upperCaseCharacterX));
			System.out.println(tempString);
			effectiveTextChannelName = tempString;
		}

		Party party = partiesAPI.getParty(effectiveTextChannelName);
		Set<UUID> members = party.getMembers();

		List<LastLoginPlayerPlayTime> players = members.stream()
				.map(uuid -> {
					LastLoginPlayer lastLoginPlayer = lastLoginAPI.getPlayer(uuid);
					return new LastLoginPlayerPlayTime(lastLoginPlayer.getName(), lastLoginPlayer.getLastLogin(),  lastLoginPlayer.getLastLogout());
				})
				.toList();

		// Da qui comincia la paginatura del messaggio
		List<MessageEmbed> messageEmbeds = new LinkedList<>();

		int pageSize = 10;
		for(int i = 0; i < players.size(); i += pageSize) {
			List<LastLoginPlayerPlayTime> pageMembers = players.subList(i, Math.min(i + pageSize, players.size()));
			StringBuilder stringBuilder = new StringBuilder();
			for(LastLoginPlayerPlayTime player : pageMembers) {
				stringBuilder.append(String.format("%s è offline da %s\n", player.getUsername(), formatDuration(player.getLastLogout())));
			}

			// Crea l'embed
			MessageEmbed embed = JDAUtils.getGuildBasedEmbedForCommand(user)
					.addField("Censimenti", String.format("```\n%s\n```", stringBuilder), false)
					.setDescription(String.format("Player totali: %s", players.size()))
					.build();

			messageEmbeds.add(embed);
		}

		List<Page> pages = new LinkedList<>();

		for (MessageEmbed messageEmbed : messageEmbeds) {
			pages.add(InteractPage.of(messageEmbed));
		}

		Page page = pages.get(0);
		MessageEmbed content = (MessageEmbed) page.getContent();
		event.replyEmbeds(content).queue(success -> {
			success.retrieveOriginal().queue(x ->
					Pages.paginate(x, pages, true));
		});
	}

	private String formatDuration(long x) {
		Instant logoutTimestamp = Instant.ofEpochSecond(x);
		Instant now = Instant.now();

		Duration duration = Duration.between(logoutTimestamp, now);

		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;

		return String.format("%d giorni, %d ore, %d minuti", days, hours, minutes);
	}
}