package me.vunt.filter;

import me.vunt.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;

public class FilterListener extends ListenerAdapter {
    static EmbedBuilder eb;
    static FilterListener instance;
    static ArrayList<String> blockedWords = new ArrayList<>();

    public enum type { ADD, REMOVE, LIST, ERROR }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        String blockedWord = "";
        final String[] args = event.getMessage().getContentRaw().split(" ");
        if (!event.getAuthor().isBot()) {
            if (event.getChannel().getName().equalsIgnoreCase("nigger-bot")) {
                if (args[0].equalsIgnoreCase(Main.getInstance().prefix() + "addBlockedWord")) {
                    if (args.length == 2) {
                        try {
                            blockedWord = args[1];
                            blockedWords.add(blockedWord);
                            buildEmbed(event.getMember(), blockedWord, type.ADD);
                            event.getChannel().sendMessage(eb.build()).queue();
                        } catch (final ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }else {
                        buildEmbed(event.getMember(), blockedWord, type.ERROR);
                        event.getChannel().sendMessage(eb.build()).queue();
                    }
                }else if (args[0].equalsIgnoreCase(Main.getInstance().prefix() + "getBlockedWords")) {
                    buildEmbed(event.getMember(), blockedWord, type.LIST);
                    event.getChannel().sendMessage(eb.build()).queue();
                }else if (args[0].equalsIgnoreCase(Main.getInstance().prefix() + "removeBlockedWord")) {
                    blockedWord = args[1];
                    if (blockedWords.contains(blockedWord)) {
                        blockedWords.remove(blockedWord);
                        buildEmbed(event.getMember(), blockedWord, type.REMOVE);
                        event.getChannel().sendMessage(eb.build()).queue();
                    }else {
                        buildEmbed(event.getMember(), blockedWord, type.ERROR);
                        event.getChannel().sendMessage(eb.build()).queue();
                    }
                }
            }
        }
    }
    public void buildEmbed(Member member, String word, type val) {
        String name = member.getUser().getName() + "#" + member.getUser().getDiscriminator();
        eb = new EmbedBuilder();
        eb.setAuthor(name, member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
        switch (val) {
            case ADD:
                eb.setTitle("Added Blocked Word:");
                break;
            case LIST:
                eb.setTitle("Current Blocked Words:");
                break;
            case REMOVE:
                eb.setTitle("Removed Blocked Word:");
                break;
            case ERROR:
                eb.setTitle("ERROR");
                break;
            default:
                eb.setTitle("ERROR");
                break;
        }
        eb.setColor(Color.red);
        eb.setColor(new Color(0xF40C0C));
        eb.setColor(new Color(255, 0, 54));
        eb.setThumbnail(member.getGuild().getIconUrl());
        if (val.equals(type.ERROR)) {
            return;
        }
        if (!val.equals(type.LIST)) {
            eb.setDescription(word);
        }else {
            StringBuilder sb = new StringBuilder();
            for (String words : blockedWords) {
                sb.append(words + "\n");
            }
            eb.setDescription(sb);
        }
    }
    public ArrayList<String> getBlockedWords() {
        return blockedWords;
    }
    public static FilterListener getInstance() {
        if (instance == null) {
            instance = new FilterListener();
        }
        return instance;
    }
}
