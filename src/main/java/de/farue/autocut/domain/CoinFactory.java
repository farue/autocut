package de.farue.autocut.domain;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Component
public class CoinFactory {

    private static final List<String> WORD_LIST = new ArrayList<>();

    static {
        try {
            WORD_LIST.addAll(Files.readAllLines(Paths.get("src/main/resources/wordlist.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final SecureRandom random;

    public CoinFactory() {
        this.random = new SecureRandom();
    }

    public Coin createNewCoin() {
        int randomIndex = generateNumberBetween(0, WORD_LIST.size());
        String randomWord = WORD_LIST.get(randomIndex);
        int randomNumber = generateNumberBetween(100, 999);
        String token = randomWord + randomNumber;

        Coin coin = new Coin();
        coin.setToken(token);
        return coin;
    }

    private int generateNumberBetween(int min, int max) {
        int bound = max - min + 1;
        return min + random.nextInt(bound);
    }
}
