package models;

import java.util.Random;

public class RandomPassword {

    public static final String ALPHA_NUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int lENGTH = 15;

    public RandomPassword() {
    }

    public String createPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder(lENGTH);
        for (int i = 0; i < lENGTH; i++)
            password.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC
                    .length())));
        return password.toString();
    }
}