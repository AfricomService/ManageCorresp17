package com.africom.correspmanage.client;

import java.io.Serializable;

public class UserFeignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;

    public UserFeignDTO() {
        // nécessaire pour Jackson
    }

    public UserFeignDTO(String id, String login, String firstName, String lastName, String email) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "UserFeignDTO{" +
            "id='" + id +
            ", login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
