package Undo;

import repository.UserRepository;

public class AddUserCommand implements Command {

    private final int userId;
    private final UserRepository repo;

    public AddUserCommand(int userId, UserRepository repo) {
        this.userId = userId;
        this.repo = repo;
    }

    @Override
    public void undo() {
        repo.delete(userId);
    }
}
