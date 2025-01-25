package dev.sodiograaz.experienceCensimentiProxy.discord.jda.api;

import net.dv8tion.jda.api.JDA;

/* @author Sodiograaz
 @since 24/01/2025
*/
public interface JDAClientHolder {
	
	JDAClientHolder createClient() throws IllegalStateException, InterruptedException;
	JDAClientHolder shutdownClient() throws IllegalStateException;
	JDA getClient() throws IllegalStateException;
	JDAClientHolder getInstance();
	
}