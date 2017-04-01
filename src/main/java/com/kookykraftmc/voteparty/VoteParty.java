package com.kookykraftmc.voteparty;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.title.Title;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Plugin(id = "voteparty", name = "Voteparty", description = "A drop party for voting", url = "http://kookykraftmc.net", authors = {"TimeTheCat"}
)
public class VoteParty {
    public static VoteParty instance;

    @Inject
    private Logger logger;

    //config stuff
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultCfg;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> cfgMgr;
    private ConfigurationNode cfg;

    @Inject
    private Game game;
    private List<String> voteRewards;
    private int untilParty;

    Random random = new Random();

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        setupConfig();
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        instance = this;
        registerCommands();
    }

    private void registerCommands() {
        CommandSpec addCmd = CommandSpec.builder()
                .executor(new VPAddCommand())
                .permission("voteparty.addvote")
                .description(Text.of("Add a vote to the voteparty, console only."))
                .build();
        CommandSpec startPartyCmd = CommandSpec.builder()
                .executor(new VPStartPartyCommand())
                .permission("voteparty.startparty")
                .description(Text.of("Start the vote party."))
                .build();
        CommandSpec base = CommandSpec.builder()
                .executor(new VPCommand())
                .permission("voteparty.base")
                .description(Text.of("See how many votes until a vote party."))
                .child(addCmd, "addvote")
                .child(startPartyCmd, "startparty")
                .build();

        game.getCommandManager().register(this, base, "vp");

    }

    private void setupConfig() {
        logger.info("Setting up config...");
        try {
            if (!defaultCfg.exists()) {
                defaultCfg.createNewFile();

                this.cfg = cfgMgr.load();
                this.cfg.getNode("voting", "rewards").setValue(new ArrayList(){{
                    add("econ add @p 200");
                    add("econ add @p 400");
                    add("econ add @p 600");
                    add("econ add @p 800");
                    add("econ add @p 1000");
                    add("econ add @p 1200");
                    add("econ add @p 1400");
                    add("econ add @p 1600");
                    add("econ add @p 1800");
                    add("econ add @p 2000");
                    add("econ add @p 4000");
                    add("econ add @p 10000");
                    add("econ add @p 20000");
                }});
                this.cfg.getNode("voting", "needed-top-party").setValue(50);


                this.cfg.getNode("messages", "prefix").setValue("&5[Voting]");
                this.cfg.getNode("messages", "broadcast").setValue(TypeToken.of(TextTemplate.class), Texts.broadcastMessage);
                this.cfg.getNode("messages", "party").setValue(TypeToken.of(TextTemplate.class), Texts.partyMessage);

                this.cfgMgr.save(cfg);
            }

            this.cfg = cfgMgr.load();

            this.voteRewards = cfg.getNode("voting", "rewards").getList(TypeToken.of(String.class));

            if (cfg.getNode("save").getValue() == null) {
                this.untilParty = cfg.getNode("voting", "needed-top-party").getInt();
            } else {
                this.untilParty = cfg.getNode("save").getInt();
            }

            Texts.setBroadcastMessage(cfg.getNode("messages", "broadcast").getValue(TypeToken.of(TextTemplate.class)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleVote() {
        untilParty--;
        if (untilParty == 0) {
            party();
            untilParty = cfg.getNode("voting", "needed-top-party").getInt();
        }
        Map<String, TextElement> m = new HashMap<>();
        m.put("votes", Text.of(untilParty));
        game.getServer().getBroadcastChannel().send(Texts.broadcastMessage.apply(m).build(), ChatTypes.ACTION_BAR);
        game.getServer().getBroadcastChannel().send(Texts.broadcastMessage.apply(m).build());
        save();
    }

    void party() {
        game.getServer().getBroadcastChannel().send(Texts.partyMessage.apply().build(), ChatTypes.ACTION_BAR);
        game.getServer().getBroadcastChannel().send(Texts.partyMessage.apply().build());
        for (Player player : game.getServer().getOnlinePlayers()) {
            String reward = voteRewards.get(random.nextInt(voteRewards.size())).replace("@p", player.getName());
            game.getCommandManager().process(game.getServer().getConsole(), reward);
            player.playSound(SoundTypes.BLOCK_ANVIL_LAND, player.getLocation().getPosition(), 2);
            player.sendTitle(Title.of(Texts.partyMessage.apply().build()));
        }
    }

    Text getVotesUntilText(int b) {
        Map<String, TextElement> m = new HashMap<>();
        m.put("votes", Text.of(b));
        return Texts.broadcastMessage.apply(m).build();
    }

    private void save() {
        try {
            cfg.getNode("save").setValue(untilParty);
            cfgMgr.save(cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Game getGame() {
        return game;
    }

    public File getDefaultCfg() {
        return defaultCfg;
    }

    public Logger getLogger() {
        return logger;
    }

    static VoteParty getInstance() {
        return instance;
    }

    public ConfigurationNode getCfg() {
        return cfg;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getCfgMgr() {
        return cfgMgr;
    }

    public int getUntilParty() {
        return untilParty;
    }
}
