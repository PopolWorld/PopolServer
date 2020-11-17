package me.nathanfallet.popolserver.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import me.nathanfallet.popolserver.PopolServer;

public class Leaderboard {

    // Properties
    private Location location;
    private String type;
    private int limit;
    private List<ArmorStand> armors;

    // Constructor
    public Leaderboard(Location location, String type, int limit) {
        this.location = location;
        this.type = type;
        this.limit = limit;
        this.armors = new ArrayList<>();
    }

    // Retrieve location
    public Location getLocation() {
        return location;
    }

    // Update location
    public void setLocation(Location location) {
        this.location = location;
    }

    // Retrieve type
    public String getType() {
        return type;
    }

    // Update type
    public void setType(String type) {
        this.type = type;
    }

    // Retrieve limit
    public int getLimit() {
        return limit;
    }

    // Update limit
    public void setLimit(int limit) {
        this.limit = limit;
    }

    // Update leaderboard
    public void update() {
        // If size is not correct, regenerate armor stands
        if (armors.size() != limit + 2) {
            // Remove all existing entities
            for (ArmorStand armor : armors) {
                armor.remove();
            }
            armors.clear();

            // Generate new entities
            Location current = location.clone().add(0, (0.27 * (limit + 2)) - 1, 0);
            for (int i = 0; i < limit + 2; i++) {
                ArmorStand armor = (ArmorStand) location.getWorld().spawnEntity(current, EntityType.ARMOR_STAND);
                armor.setVisible(false);
                armor.setGravity(false);
                armor.setBasePlate(false);
                armor.setCustomName("");
                armor.setCustomNameVisible(true);
                armors.add(armor);
                current.add(0, -0.27, 0);
            }
        }

        // Get generator
        LeaderboardGenerator generator = PopolServer.getInstance().getLeaderboardGenerators().get(type);
        if (generator == null) {
            // Set error line
            for (ArmorStand armor : armors) {
                armor.setCustomName(ChatColor.RED + "---------------------------");
            }

            // And stop here
            return;
        }

        // Iterate lines
        int i = 0;
        List<String> lines = generator.getLines(limit);
        for (ArmorStand armor : armors) {
            // Check line type
            if (i == 0) {
                // Header
                armor.setCustomName(ChatColor.YELLOW + "------- " + ChatColor.GOLD
                        + generator.getTitle() + ChatColor.YELLOW + " ---------");
            } else if (i == limit + 1) {
                // Footer
                armor.setCustomName(ChatColor.YELLOW + "---------------------------");
            } else {
                // Line
                armor.setCustomName(ChatColor.GOLD + "" + i + ". " + ChatColor.YELLOW
                        + (lines.size() >= i ? lines.get(i - 1) : ""));
            }
            i++;
        }
    }

    // Kill leaderboard
    public void kill() {
        for (ArmorStand armor : armors) {
            armor.remove();
        }
        armors.clear();
    }

}
