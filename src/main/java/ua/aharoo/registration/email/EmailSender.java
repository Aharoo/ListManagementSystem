package ua.aharoo.registration.email;

public interface EmailSender {

    void send(String to, String email);
}
