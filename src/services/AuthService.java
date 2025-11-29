package services;

import models.User;
import repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String passwordPlainText) {
        // TODO: password'u hash'le, userRepository'den user çek,
        // role'e göre doğru User nesnesi döndür (Tester/Junior/Senior/Manager)
        return null;
    }

    public void changePassword(User user) {
        // TODO: eski şifre kontrolü, yeni şifre al, hashle, userRepository.updatePassword(...)
    }
}
