package Undo;

import models.User;
import repository.UserRepository;

public class UpdateUserCommand implements Command {

    private final User oldUser;
    private final UserRepository repo;

    public UpdateUserCommand(User oldUser, UserRepository repo) {
        this.oldUser = oldUser;
        this.repo = repo;
    }

    @Override
    public void undo() {

        repo.update(oldUser);
    }
}
