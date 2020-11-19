package me.nathanfallet.popolserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.popolserver.PopolServer;
import me.nathanfallet.popolserver.utils.PopolMoney;
import me.nathanfallet.popolserver.utils.PopolPlayer;
import me.nathanfallet.popolserver.utils.PopolMoney.BalanceCheckHandler;

public class MoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (sender instanceof Player) {
            // Cast player
            final Player player = (Player) sender;
            PopolPlayer pp = PopolServer.getInstance().getPlayer(player.getUniqueId());

            // Send update message
            player.sendMessage(ChatColor.YELLOW + "Vérification de votre compte...");

            // Check balance
            PopolMoney.checkBalance(pp, new BalanceCheckHandler() {
                @Override
                public void balanceChecked(Long money) {
                    // Check result
                    if (money != null) {
                        // Show balance
                        player.sendMessage(ChatColor.GREEN + PopolMoney.name + " : " + money + "₽");
                    } else {
                        // Error
                        player.sendMessage(ChatColor.RED + "Impossible de vérifier votre compte !");
                    }
                }
            });
        }
        return true;
    }

}
