package me.nathanfallet.popolserver.utils;

import java.util.List;

public interface LeaderboardGenerator {

    String getTitle();

    List<String> getLines(int limit);

}
