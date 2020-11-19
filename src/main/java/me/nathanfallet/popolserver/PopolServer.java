package me.nathanfallet.popolserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.popolserver.api.APIConfiguration;
import me.nathanfallet.popolserver.api.APIServer;
import me.nathanfallet.popolserver.api.PopolConnector;
import me.nathanfallet.popolserver.commands.LeaderboardCommand;
import me.nathanfallet.popolserver.commands.MenuCommand;
import me.nathanfallet.popolserver.commands.MoneyCommand;
import me.nathanfallet.popolserver.commands.SetSpawnCommand;
import me.nathanfallet.popolserver.commands.SpawnCommand;
import me.nathanfallet.popolserver.events.InventoryClick;
import me.nathanfallet.popolserver.events.PlayerChat;
import me.nathanfallet.popolserver.events.PlayerJoin;
import me.nathanfallet.popolserver.events.PlayerQuit;
import me.nathanfallet.popolserver.events.PlayerRespawn;
import me.nathanfallet.popolserver.utils.Leaderboard;
import me.nathanfallet.popolserver.utils.LeaderboardGenerator;
import me.nathanfallet.popolserver.utils.PopolMoney;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.ScoreboardGenerator;

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
    private List<ScoreboardGenerator> scoreboardGenerators;
    private Map<String, LeaderboardGenerator> leaderboardGenerators;
    private Map<String, Leaderboard> leaderboards;

    // Enable plugin
    @Override
    public void onEnable() {
        // Store instance
        instance = this;

        // Init players
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayers().add(new PopolPlayer(player));
        }

        // Clear custom entities
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getCustomName() != null && entity.getCustomName().startsWith(ChatColor.COLOR_CHAR + "")) {
                    entity.remove();
                }
            }
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
        getCommand("leaderboard").setExecutor(new LeaderboardCommand());
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
                            + (pp.getCached() != null ? pp.getCached().money + "₽" : "Chargement..."));

                    // Extra lines from other plugins
                    for (ScoreboardGenerator generator : getScoreboardGenerators()) {
                        lines.addAll(generator.generateLines(player, pp));
                    }

                    // Footer
                    lines.addAll(footerLines);

                    // Apply
                    getPlayer(player.getUniqueId()).getScoreboard().update(player, lines);
                }

                // Refresh leaderboards
                for (Leaderboard leaderboard : getLeaderboards().values()) {
                    leaderboard.update();
                }
            }
        }, 0, 20);

        // Saving task
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Save leaderboards
                saveLeaderboards();
            }
        }, 300 * 20, 300 * 20);
    }

    // Disable plugin
    @Override
    public void onDisable() {
        // Remove players
        for (PopolPlayer player : getPlayers()) {
            player.getScoreboard().kill();
        }
        players = null;

        // Clear scoreboard generators
        scoreboardGenerators = null;

        // Save leaderboards
        saveLeaderboards();

        // Clear leaderboards and generators
        for (Leaderboard leaderboard : getLeaderboards().values()) {
            leaderboard.kill();
        }
        leaderboards = null;
        leaderboardGenerators = null;

        // Send status
        sendStatus();
    }

    // Retrieve connector
    public PopolConnector getConnector() {
        // Load connector if needed
        if (connector == null) {
            try {
                // Create connector with config
                connector = new PopolConnector(getConfig().getString("id"), getConfig().getString("token"),
                        new APIConfiguration(getConfig().getString("scheme"), getConfig().getString("host")));
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
    public List<ScoreboardGenerator> getScoreboardGenerators() {
        // Init list if needed
        if (scoreboardGenerators == null) {
            scoreboardGenerators = new ArrayList<>();
        }

        // Return list
        return scoreboardGenerators;
    }

    // Retrieve leaderboard generators
    public Map<String, LeaderboardGenerator> getLeaderboardGenerators() {
        // Init list if needed
        if (leaderboardGenerators == null) {
            leaderboardGenerators = new HashMap<>();
        }

        // Return list
        return leaderboardGenerators;
    }

    // Retrieve leaderboards
    public Map<String, Leaderboard> getLeaderboards() {
        // Init list if needed
        if (leaderboards == null) {
            leaderboards = new HashMap<>();

            // And read from file
            FileConfiguration file = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "leaderboards.yml"));
            for (String key : file.getKeys(false)) {
                // Extract variables
                Location location = new Location(Bukkit.getWorld(file.getString(key + ".location.world")),
                        file.getDouble(key + ".location.x"), file.getDouble(key + ".location.y"),
                        file.getDouble(key + ".location.z"));
                String type = file.getString(key + ".type");
                int limit = file.getInt(key + ".limit");

                // Put in list
                leaderboards.put(key, new Leaderboard(location, type, limit));
            }
        }

        // Return list
        return leaderboards;
    }

    // Save leaderboards
    public void saveLeaderboards() {
        // Get file
        File empty = new File(getDataFolder(), ".empty");
        File source = new File(getDataFolder(), "leaderboards.yml");
        FileConfiguration file = YamlConfiguration.loadConfiguration(empty);

        // Set keys
        for (String key : getLeaderboards().keySet()) {
            Leaderboard leaderboard = leaderboards.get(key);
            file.set(key + ".location.world", leaderboard.getLocation().getWorld().getName());
            file.set(key + ".location.x", leaderboard.getLocation().getX());
            file.set(key + ".location.y", leaderboard.getLocation().getY());
            file.set(key + ".location.z", leaderboard.getLocation().getZ());
            file.set(key + ".type", leaderboard.getType());
            file.set(key + ".limit", leaderboard.getLimit());
        }

        // Save file
        try {
            file.save(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
