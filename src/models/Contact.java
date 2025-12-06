package models;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a contact entity within the Role-Based Contact Management System.
 * <p>
 * This class maps directly to the {@code contacts} table in the MySQL database.
 * It encapsulates all personal and professional details of a contact, including
 * mandatory fields (like First Name, Last Name, Primary Phone) and optional fields
 * (like Middle Name, Secondary Phone, LinkedIn URL) as specified in the project description.
 * </p>
 *
 * @author [Group Members Names Here]
 * @version 1.0
 * @see repository.ContactRepository
 */
public class Contact {

    private int contactId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String linkedinUrl;
    private Date birthDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;


    /**
     * Gets the unique identifier for this contact.
     * @author Can
     * @return The contact ID (Primary Key).
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets the unique identifier for this contact.
     * @author Can
     * @param contactId The contact ID to set.
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Gets the first name of the contact.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the contact.
     * <p>This is a mandatory field.</p>
     * @author Can
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name of the contact.
     *@author Can
     * @return The middle name, or {@code null} if not set.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name of the contact.
     * <p>This is an optional field.</p>
     *@author Can
     * @param middleName The middle name to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name of the contact.
     *@author Can
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the contact.
     * <p>This is a mandatory field.</p>
     *@author Can
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the nickname of the contact.
     *@author Can
     * @return The nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname of the contact.
     *@author Can
     * @param nickname The nickname to set.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets the primary phone number.
     *@author Can
     * @return The primary phone number string.
     */
    public String getPhonePrimary() {
        return phonePrimary;
    }

    /**
     * Sets the primary phone number.
     * <p>This is a mandatory field.</p>
     *@author Can
     * @param phonePrimary The 10-digit phone number to set.
     */
    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    /**
     * Gets the secondary phone number.
     *@author Can
     * @return The secondary phone number, or {@code null} if not set.
     */
    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    /**
     * Sets the secondary phone number.
     * <p>This is an optional field.</p>
     *@author Can
     * @param phoneSecondary The secondary phone number to set.
     */
    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    /**
     * Gets the email address.
     *@author Can
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *@author Can
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the LinkedIn profile URL.
     *@author Can
     * @return The LinkedIn URL, or {@code null} if not set.
     */
    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    /**
     * Sets the LinkedIn profile URL.
     * <p>This is an optional field.</p>
     *@author Can
     * @param linkedinUrl The URL string to set.
     */
    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    /**
     * Gets the birthdate of the contact.
     *@author Can
     * @return The birthdate.
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birthdate of the contact.
     *@author Can
     * @param birthDate The SQL Date object representing the birth date.
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the timestamp of when the record was created.
     *@author Can
     * @return The creation timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp of when the record was created.
     *@author Can
     * @param createdAt The timestamp to set.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp of when the record was last updated.
     *@author Can
     * @return The update timestamp.
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp of when the record was last updated.
     *@author Can
     * @param updatedAt The timestamp to set.
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}