package Undo;

import models.User;
import repository.UserRepository;

public class DeleteUserCommand implements Command {

    private final User deletedUser;
    private final UserRepository repo;

    public DeleteUserCommand(User deletedUser, UserRepository repo) {
        this.deletedUser = deletedUser;
        this.repo = repo;
    }

    @Override
    public void undo() {
        // delete'in tersi = silinen user'ı yeniden eklemek
        repo.insert(deletedUser);
    }
}
