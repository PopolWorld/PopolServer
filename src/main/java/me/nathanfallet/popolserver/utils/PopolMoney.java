package me.nathanfallet.popolserver.utils;

import me.nathanfallet.popolserver.api.APIPlayer;
import me.nathanfallet.popolserver.api.APIRequest.CompletionHandler;
import me.nathanfallet.popolserver.api.APIResponseStatus;

public class PopolMoney {

    // Money name (constant)
    public static final String name = "PopolMoney";

    // Check player balance
    public static void checkBalance(PopolPlayer player, final BalanceCheckHandler handler) {
        // Fetch API
        player.get(new CompletionHandler<APIPlayer>() {
            @Override
            public void completionHandler(APIPlayer object, APIResponseStatus status) {
                // Check response and status
                if (object != null && status == APIResponseStatus.ok) {
                    // Return money
                    handler.balanceChecked(object.money);
                } else {
                    // Error, return null
                    handler.balanceChecked(null);
                }
            }
        });
    }

    // Update player balance
    public static void updateBalance(PopolPlayer player, Long newBalance, final BalanceUpdatedHandler handler) {
        // Put API
        player.put(new APIPlayer(player.getUUID().toString(), null, newBalance, null, null),
                new CompletionHandler<APIPlayer>() {
                    @Override
                    public void completionHandler(APIPlayer object, APIResponseStatus status) {
                        // Check response and status
                        if (object != null && status == APIResponseStatus.ok) {
                            // Return new balance
                            handler.balanceUpdated(object.money);
                        } else {
                            // Error, return null
                            handler.balanceUpdated(null);
                        }
                    }
                });
    }

    // Interfaces for balance check
    public interface BalanceCheckHandler {

        void balanceChecked(Long money);

    }

    // Interface for money update
    public interface BalanceUpdatedHandler {

        void balanceUpdated(Long money);

    }

}
