package services;

import models.User;
import repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public List<User> listAllUsers(User actingUser) {
        // TODO: actingUser rolü Manager mı kontrol et,
        // sonra userRepository.findAll() çağır
        return null;
    }

    public boolean addUser(User actingUser) {
        // TODO: sadece Manager'a izin ver, userRepository.insert(...)
        return false;
    }

    public boolean updateUser(User actingUser) {
        // TODO: sadece Manager'a izin ver, userRepository.update(...)
        return false;
    }

    public boolean deleteUser(User actingUser) {
        // TODO: sadece Manager'a izin ver, userRepository.delete(...)
        return false;
    }
}
