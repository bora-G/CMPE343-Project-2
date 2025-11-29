package services;

import models.Contact;
import models.User;
import repository.ContactRepository;

import java.util.List;

public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService() {
        this.contactRepository = new ContactRepository();
    }

    public List<Contact> listAllContacts() {
        // TODO: contactRepository.findAll() çağır
        return null;
    }

    public List<Contact> searchBySingleField() {
        // TODO: kullanıcıdan field + value alınacak (Scanner),
        // contactRepository.searchByField(...) çağrılacak
        return null;
    }

    public List<Contact> searchByMultipleFields() {
        // TODO: birden fazla kriter al, repository'e pasla
        return null;
    }

    public List<Contact> sortContacts() {
        // TODO: hangi kolona göre + ASC/DESC al, repository'e pasla
        return null;
    }

    public boolean addContact(User actingUser) {
        // TODO: Senior/Manager gibi yetkili kullanıcı mı kontrol et,
        // sonra contactRepository.insert(...)
        return false;
    }

    public boolean updateContact(User actingUser) {
        // TODO: Junior üstü rollere izin ver, contactRepository.update(...)
        return false;
    }

    public boolean deleteContact(User actingUser) {
        // TODO: sadece Senior/Manager'a izin ver, contactRepository.delete(...)
        return false;
    }
}
