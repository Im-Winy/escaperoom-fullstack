package com.escaperoom.escaperoom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final String FROM_EMAIL = "williamnyacka14@gmail.com";
    private static final String SIGNATURE = "\n\nCordialement,\nL'équipe EscapeRoom";

    @Async
    public void sendEmail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom(FROM_EMAIL);
        javaMailSender.send(mailMessage);
    }

    public void sendConfirmRegister(String email, String username, String password) {
        String subject = "Confirmation de votre inscription";
        String message = "Bonjour " + username + ",\n\n"
                + "Merci de vous être inscrit sur EscapeRoom.\n"
                + "Voici vos informations de connexion :\n"
                + "🔐 Mot de passe : " + password + "\n\n"
                + "Nous vous recommandons de le modifier dès votre première connexion." + SIGNATURE;

        sendEmail(email, subject, message);
    }

    public void sendReservationConfirmation(String email, String username, String eventName, String eventDate, String eventTime) {
        String subject = "Confirmation de votre réservation";
        String message = "Bonjour " + username + ",\n\n"
                + "Nous vous confirmons la réservation suivante :\n\n"
                + "📍 Événement : " + eventName + "\n"
                + "📅 Date : " + eventDate + "\n"
                + "🕒 Heure : " + eventTime + "\n\n"
                + "Nous avons hâte de vous accueillir !"
                + SIGNATURE;

        sendEmail(email, subject, message);
    }

    public void sendReservationCancellation(String email, String username, String eventName, String date, String time) {
        String subject = "Annulation de votre réservation";
        String message = "Bonjour " + username + ",\n\n"
                + "Votre réservation pour l'événement suivant a bien été annulée :\n\n"
                + "📍 Événement : " + eventName + "\n"
                + "📅 Date : " + date + "\n"
                + "🕒 Heure : " + time + "\n\n"
                + "Nous espérons vous revoir prochainement." + SIGNATURE;

        sendEmail(email, subject, message);
    }
}
