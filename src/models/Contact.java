package models;

import java.sql.Date;
import java.sql.Timestamp;

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

    private String safe(Object o) {
        if (o == null) {
            return "No data available";
        }
        return o.toString();
    }

    public String toString() {
        return "Contact " + contactId + "\n" +
                "----------------------------\n" +
                "First Name     : " + safe(firstName) + "\n" +
                "Middle Name    : " + safe(middleName) + "\n" +
                "Last Name      : " + safe(lastName) + "\n" +
                "Nickname       : " + safe(nickname) + "\n" +
                "\n" +
                "Primary Phone  : " + safe(phonePrimary) + "\n" +
                "Secondary Phone: " + safe(phoneSecondary) + "\n" +
                "\n" +
                "E-mail         : " + safe(email) + "\n" +
                "LinkedIn       : " + safe(linkedinUrl) + "\n" +
                "\n" +
                "Birth Date     : " + safe(birthDate) + "\n" +
                "Created At     : " + safe(createdAt) + "\n" +
                "Updated At     : " + safe(updatedAt) + "\n";
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhonePrimary() {
        return phonePrimary;
    }

    public void setPhonePrimary(String phonePrimary) {
        this.phonePrimary = phonePrimary;
    }

    public String getPhoneSecondary() {
        return phoneSecondary;
    }

    public void setPhoneSecondary(String phoneSecondary) {
        this.phoneSecondary = phoneSecondary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
