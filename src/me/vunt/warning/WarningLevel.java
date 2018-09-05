package me.vunt.warning;

import me.vunt.Main;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class WarningLevel extends ListenerAdapter {
    private static WarningLevel instance;
    private HashMap<User, Integer> warningLevel = new HashMap<>();
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        final String[] args = event.getMessage().getContentRaw().split(" ");
        final User u = event.getMember().getUser();
        if (!event.getAuthor().isBot()) {
            if (args[0].equalsIgnoreCase(Main.getInstance().prefix() + "warningLevel")) {
                if (!warningLevel.containsKey(u)) {
                    event.getChannel().sendMessage(Main.getInstance().errorMessage()).queue();
                    return;
                }
                event.getChannel().sendMessage(event.getMember().getAsMention() + " : Your warning level is: " + warningLevel.get(u)).queue();
            }
        }
    }
    public HashMap getWarningLevel() {
        return warningLevel;
    }
    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        if (warningLevel.containsKey(event.getUser())) {
            return;
        }
        warningLevel.put(event.getUser(), 0);
    }
    public static WarningLevel getInstance() {
        if (instance == null) {
            instance = new WarningLevel();
        }
        return instance;
    }
}
