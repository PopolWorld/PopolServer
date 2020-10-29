package me.nathanfallet.popolserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.PopolConnector;
import me.nathanfallet.popolserver.commands.MenuCommand;
import me.nathanfallet.popolserver.commands.MoneyCommand;
import me.nathanfallet.popolserver.commands.SetSpawnCommand;
import me.nathanfallet.popolserver.commands.SpawnCommand;
import me.nathanfallet.popolserver.events.InventoryClick;
import me.nathanfallet.popolserver.events.PlayerChat;
import me.nathanfallet.popolserver.events.PlayerJoin;
import me.nathanfallet.popolserver.events.PlayerQuit;
import me.nathanfallet.popolserver.events.PlayerRespawn;
import me.nathanfallet.popolserver.utils.PopolMoney;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardLinesGenerator;

public class PopolServer extends JavaPlugin {

    // Static instance
    private static PopolServer instance;

    // Retrieve instance
    public static PopolServer getInstance() {
        return instance;
    }

    // Properties
    private PopolConnector connector;
    private List<PopolPlayer> players;
    private List<ScoreboardLinesGenerator> scoreboardGenerators;

    // Enable plugin
    @Override
    public void onEnable() {
        // Store instance
        instance = this;

        // Init players
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayers().add(new PopolPlayer(player));
        }

        // Register channel to talk with BungeeCord
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register events
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChat(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);

        // Register commands
        getCommand("menu").setExecutor(new MenuCommand());
        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand());

        // Send status
        sendStatus();

        // Register tasks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Get server from cache
                APIServer cachedAPIServer = getConnector().getFromCache();

                // Create scoreboard lines
                List<String> headerLines = new ArrayList<>();
                List<String> footerLines = new ArrayList<>();
                headerLines.add(ChatColor.AQUA + "");
                headerLines.add(ChatColor.AQUA + "" + ChatColor.BOLD + "Serveur :");
                headerLines.add(ChatColor.WHITE + (cachedAPIServer != null ? cachedAPIServer.name : "Chargement..."));
                headerLines.add(ChatColor.WHITE + "" + getPlayers().size() + " joueurs");
                headerLines.add(ChatColor.GREEN + "");
                headerLines.add(ChatColor.GREEN + "" + ChatColor.BOLD + PopolMoney.name + " :");
                footerLines.add(ChatColor.YELLOW + "");
                footerLines.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "play.popolworld.fr");

                // Apply to eveyone
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Merge lines
                    List<String> lines = new ArrayList<>();
                    PopolPlayer pp = getPlayer(player.getUniqueId());

                    // Header
                    lines.addAll(headerLines);

                    // Money
                    lines.add(ChatColor.WHITE + ""
                            + (pp.getCached() != null ? pp.getCached().money + " unités" : "Chargement..."));

                    // Extra lines from other plugins
                    for (ScoreboardLinesGenerator generator : getScoreboardGenerators()) {
                        lines.addAll(generator.generateLines(player, pp));
                    }

                    // Footer
                    lines.addAll(footerLines);

                    // Apply
                    getPlayer(player.getUniqueId()).getScoreboard().update(player, lines);
                }
            }
        }, 0, 20);
    }

    // Disable plugin
    @Override
    public void onDisable() {
        // Remove players
        getPlayers().clear();

        // Clear scoreboard generators
        scoreboardGenerators.clear();

        // Send status
        sendStatus();
    }

    // Retrieve connector
    public PopolConnector getConnector() {
        // Load connector if needed
        if (connector == null) {
            try {
                // Create connector with config
                connector = new PopolConnector(getConfig().getString("id"), getConfig().getString("token"));
            } catch (Exception e) {
                // Error
                e.printStackTrace();

                // Stop server
                Bukkit.shutdown();
            }
        }

        // Return connector
        return connector;
    }

    // Retrieve players
    public List<PopolPlayer> getPlayers() {
        // Init players if needed
        if (players == null) {
            players = new ArrayList<>();
        }

        // Return players
        return players;
    }

    // Retrieve a player from its UUID
    public PopolPlayer getPlayer(UUID uuid) {
        // Iterate players
        for (PopolPlayer player : getPlayers()) {
            if (player.getUUID().equals(uuid)) {
                return player;
            }
        }

        // No player found
        return null;
    }

    // Retrieve scoreboard generators
    public List<ScoreboardLinesGenerator> getScoreboardGenerators() {
        // Init list if needed
        if (scoreboardGenerators == null) {
            scoreboardGenerators = new ArrayList<>();
        }

        // Return list
        return scoreboardGenerators;
    }

    // Send current server status
    public void sendStatus() {
        getConnector().putServer(new APIServer(null, null, null, null, null, null, "online", getPlayers().size()));
    }

    // Get spawn location
    public Location getSpawn() {
        // Get file
        File f = new File(getDataFolder(), "spawn.yml");

        // Return default spawn location if it doesn't exist
        if (!f.exists()) {
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        }

        // Else, read from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        Location l = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"),
                config.getDouble("y"), config.getDouble("z"));
        l.setYaw(config.getLong("yaw"));
        l.setPitch(config.getLong("pitch"));
        return l;
    }

    // Set spawn location
    public void setSpawn(Location l) {
        // Get file
        File f = new File(getDataFolder(), "spawn.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        // Set location
        config.set("world", l.getWorld().getName());
        config.set("x", l.getX());
        config.set("y", l.getY());
        config.set("z", l.getZ());
        config.set("yaw", l.getYaw());
        config.set("pitch", l.getPitch());

        // Save
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
