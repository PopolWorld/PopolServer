package me.nathanfallet.popolserver.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PlayerScoreboard {

    // Properties
    private Objective objective;
    private List<String> lastLines = new ArrayList<>();
    private String name;

    // Constructor
    public PlayerScoreboard(String name) {
        this.name = name;
    }

    // Retrieve objective instance
    public Objective getObjective() {
        return objective;
    }

    // Retrieve name
    public String getName() {
        return name;
    }

    // Check if scoreboard is active
    public boolean isActive() {
        return objective != null;
    }

    // Update scoreboard with new lines for a player
    public void update(Player player, List<String> newLines) {
        // Check is objective is null
        if (objective == null) {
            // If yes, create it
            objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(name.toLowerCase(),
                    "dummy", name);
            objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + name);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // Iterate new lines
        for (int pos = 0; pos < newLines.size(); pos++) {
            // Check if line should be reset
            if (lastLines != null && lastLines.size() > pos && lastLines.get(pos) != null
                    && !lastLines.get(pos).equals(newLines.get(pos))) {
                objective.getScoreboard().resetScores(lastLines.get(pos));
            }

            // Update current line
            objective.getScore(newLines.get(pos)).setScore(newLines.size() - pos);
        }

        // Store new lines
        lastLines = newLines;

        // And apply scoreboard to player
        player.setScoreboard(objective.getScoreboard());
    }

    // Kill scoreboard
    public void kill() {
        objective.unregister();
        objective = null;
        lastLines = null;
    }

}
