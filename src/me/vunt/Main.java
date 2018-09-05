package me.vunt;

import me.vunt.filter.FilterListener;
import me.vunt.warning.WarningLevel;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class Main extends ListenerAdapter {
    private static JDA jda;
    static Main instance;
    private ArrayList<User> mutedList =  new ArrayList<>();
    static int guildCount;

    public static void main(String[] args) throws LoginException, InterruptedException{
        String token = "dont try to get the token you shit bag";
        jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        jda.addEventListener(new Main());
        jda.addEventListener(new FilterListener());
        jda.addEventListener(new WarningLevel());
        jda.getPresence().setGame(Game.playing("Amount of Guilds: " + getOriginalGuildCount()));
        for (User u : jda.getUsers()) {
            if (!WarningLevel.getInstance().getWarningLevel().containsKey(u)) {
                WarningLevel.getInstance().getWarningLevel().put(u, 0);
            }
        }
    }
    public static int getOriginalGuildCount() {
        for (Guild g : jda.getGuilds()) {
            guildCount++;
        }
        return guildCount;
    }
    public int getGuildCount() {
        return guildCount;
    }
    public void setGuildCount(int x) {
        this.guildCount = x;
    }
    public String prefix() {
        return "!!";
    }
    public String errorMessage() {
        return "Something went wrong. Try again later?";
    }
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        final User u = event.getMember().getUser();
        int currentLevel = 0;
        if (u.isBot()) {
            return;
        }
        final String[] args = event.getMessage().getContentRaw().split(" ");
        if (WarningLevel.getInstance().getWarningLevel().get(u) != null) {
            currentLevel = (int) WarningLevel.getInstance().getWarningLevel().get(u);
        }
        final int increase = 5;
        final String reason = "Blocked Word Violation";
        //ORGANIZE CODE
        final boolean check = event.getMember().hasPermission(Permission.MANAGE_PERMISSIONS);
        if (args[0].equalsIgnoreCase(prefix() + "mute") && args.length == 2 && check) {
            User target;
            try {
                target = event.getMessage().getMentionedUsers().get(0);
            }catch (final IndexOutOfBoundsException ex) {
                event.getChannel().sendMessage(errorMessage()).queue();
                return;
            }
            if (mutedList.contains(target) || target == null) {
                event.getChannel().sendMessage(errorMessage()).queue();
                return;
            }
            mutedList.add(target);
            event.getChannel().sendMessage(target + " has been muted.").queue();
        }else if (args[0].equalsIgnoreCase(prefix() + "unmute") && args.length == 2 && check) {
            User target;
            try {
                target = event.getMessage().getMentionedUsers().get(0);
            }catch (IndexOutOfBoundsException ex) {
                event.getChannel().sendMessage(errorMessage()).queue();
                return;
            }
            if (!mutedList.contains(target)) {
                event.getChannel().sendMessage(errorMessage()).queue();
                return;
            }
            mutedList.remove(target);
            return;
        }
        if (mutedList.contains(u)) {
            event.getMessage().delete().queue();
        }
        //ORGANIZE CODE
        for (int i = 0; i < args.length; i++) {
            for (String wordBlock : FilterListener.getInstance().getBlockedWords()) {
                if (args[i].equalsIgnoreCase(wordBlock) && !event.getChannel().getName().equalsIgnoreCase("nigger-bot")) {
                    if (currentLevel + increase >= 100) {
                        event.getChannel().sendMessage(u.getAsMention() + " has reached 100 or above warning level!").queue();
                        return;
                    }
                    WarningLevel.getInstance().getWarningLevel().replace(u, currentLevel + 5);
                    event.getChannel().sendMessage(event.getMember().getUser().getAsMention() + " : Increased Level by: " + increase
                            + " Reason: " + reason).queue();
                    event.getMessage().delete().queue();
                    return;
                }
            }
        }
    }
    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        Role firstJoin = event.getGuild().getRolesByName("I am a random", true).get(0);
        if (firstJoin == null) {
            event.getGuild().getDefaultChannel().sendMessage("Something went wrong. onJoinMethod_LINE_ERROR_112 ||").queue();
            return;
        }
        event.getGuild().getController().addSingleRoleToMember(event.getMember(), firstJoin).complete();
    }
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }
}
