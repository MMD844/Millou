package aut.ap.Services;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class EmailService {
    public static Email sendEmail(User sender, String subject, String body, List<User> recipients) {
        if (recipients.isEmpty())
            throw new IllegalArgumentException("Recipients can't be null!");

        Email email = makeEmail(sender, subject, body);


        for (User recip : recipients) {
            SingletonSessionFactory.get()
                    .inTransaction(session ->
                            session.createNativeMutationQuery("insert into email_recipients(email_id, recipient_id) " +
                                            "values (:email_id, :recipient_id)")
                                    .setParameter("email_id", email.getId())
                                    .setParameter("recipient_id", recip.getId())
                                    .executeUpdate());
        }
        return email;
    }

    public static List<Email> showAllEmails(User viewer) {
        if (viewer == null)
            throw new IllegalArgumentException("Please choose your sender");

        return SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createNativeQuery("select  e.id,  e.sender_id, e.subject, e.body, e.created_at \n " +
                                                "from emails e " +
                                                "join email_recipients er on er.email_id = e.id " +
                                                "where recipient_id = :recipient_id"
                                        , Email.class)
                                .setParameter("recipient_id", viewer.getId())
                                .getResultList());

    }

    public static List<Email> readUnreadEmails(User user) {
        if (user == null)
            throw new IllegalArgumentException("Please choose your sender");

        return SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createNativeQuery("select  e.id,  e.sender_id, e.subject, e.body, e.created_at \n " +
                                                "from emails e " +
                                                "join email_recipients er on er.email_id = e.id " +
                                                "where recipient_id = :recipient_id and er.read_at is null "
                                                , Email.class)
                                .setParameter("recipient_id", user.getId())
                                .getResultList());
    }

    public static List<Email> readSentEmails(User sender) {
        if (sender == null) throw new IllegalArgumentException("Please choose your sender");

        return SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createNativeQuery("select * from emails e " +
                                        "where sender_id = :sender_id", Email.class)
                                .setParameter("sender_id", sender.getId())
                                .getResultList());
    }

    public static void readEmail(User reader, Email email) {
        if (reader == null) throw new IllegalArgumentException("Please choose your sender");

        if (!(email.getSender().equals(reader)) && !(findRecipientOfEmail(email).equals(reader)))
            throw new IllegalArgumentException("You cannot read this email.");

        if (!SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select read_time " +
                                "from email_recipients " +
                                "where recipient_id = :reader_id and email_id = :email_id", Timestamp.class)
                        .setParameter("reader_id", reader.getId())
                        .setParameter("email_id", email.getId())
                        .getResultList()).isEmpty()
                && SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select read_time " +
                                "from email_recipients " +
                                "where recipient_id = :reader_id and email_id = :email_id", Timestamp.class)
                        .setParameter("reader_id", reader.getId())
                        .setParameter("email_id", email.getId())
                        .getSingleResult()) != null)
            return;

        SingletonSessionFactory.get().inTransaction(session ->
                session.createNativeMutationQuery("update email_recipients " +
                                "set read_at = :now " +
                                "where recipient_id = :reader_id and email_id = :email_id")
                        .setParameter("now", Timestamp.valueOf(LocalDateTime.now()))
                        .setParameter("reader_id", reader.getId())
                        .setParameter("email_id", email.getId())
                        .executeUpdate());

    }

    public static List<User> findRecipientOfEmail(Email email) {
        return SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createNativeQuery("select u.id, u.name, u.email, u.password, u.created_at " +
                                                "from users u " +
                                                "join email_recipients er on er.recipient_id = u.id " +
                                                "where er.email_id = :email_id"
                                        , User.class)
                                .setParameter("email_id", email.getId())
                                .getResultList());
    }

    private static Email makeEmail(User sender, String subject, String body) {
        if (sender == null) throw new IllegalArgumentException("Please choose your sender");

        if (subject == null || subject.isEmpty())
            throw new IllegalArgumentException("Subject can't be empty");

        if (body == null || body.isEmpty())
            throw new IllegalArgumentException("Body can't be empty");

        Email email = new Email(sender, subject, body);

        SingletonSessionFactory.get()
                .inTransaction(session ->
                        session.persist(email));

        return email;
    }

    public static Email replyEmail(User sender, String code, String body) {
        Email email = findByCode(code);
        List<User> recipients = findRecipientOfEmail(email);
        recipients.remove(email.getSender());

        Email reply = makeEmail(sender, "[Re] " + email.getSubject(), body);

        sendEmail(sender, reply.getSubject(), reply.getBody(), recipients);

        return reply;
    }

    public static Email forwardEmail(User sender, String code, List<User> recipients) {
        Email email = findByCode(code);

        Email forwardedEmail = makeEmail(sender, "[Fw] " + email.getSubject(), email.getBody());

        sendEmail(sender, forwardedEmail.getSubject(), forwardedEmail.getBody(), recipients);

        return forwardedEmail;
    }

    public static Email findByCode(String code) {
        if (code == null || code.isEmpty())
            throw new IllegalArgumentException("code cannot be empty");

        Integer emailId = Integer.parseInt(code);

        return SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createNativeQuery("select * from emails e " +
                                                "where e.id = :email_id"
                                        , Email.class)
                                .setParameter("email_id", emailId)
                                .getSingleResult());
    }
}
