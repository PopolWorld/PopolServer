package me.nathanfallet.popolserver.utils;

import java.util.List;

import org.bukkit.entity.Player;

public interface ScoreboardLinesGenerator {

    List<String> generateLines(Player player, PopolPlayer pp);
    
}
