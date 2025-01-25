package dev.sodiograaz.experienceCensimentiProxy.discord.jda.api.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* @author Sodiograaz
 @since 24/01/2025
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
	
	String name();
	String description() default "No description";
	long usedOnlyBy() default 0L; // This field is used to exclude all other roles and admin only the specified roleId
	
}