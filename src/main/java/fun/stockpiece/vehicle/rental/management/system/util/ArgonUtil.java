package fun.stockpiece.vehicle.rental.management.system.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class ArgonUtil {
    private static final Argon2PasswordEncoder arg2springSecurity = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public static String hashPassword(String password) {
        return arg2springSecurity.encode(password);
    }

    public static Boolean checkPassword(String rawPassword, String hashedPassword) {
        return arg2springSecurity.matches(rawPassword, hashedPassword);
    }
}
