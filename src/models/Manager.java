package models;

public class Manager extends User {

    public Manager() {
        setRole("Manager");
    }

    @Override
    public void showUserMenu() {
        // TODO: Manager menüsü ve yetkili işlemleri
    }
}
