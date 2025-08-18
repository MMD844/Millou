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
            throw new IllegalArgumentException("Please fill your sender field");

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

    public static void readEmail(User reader, String code) {
        if (reader == null) throw new IllegalArgumentException("Please choose your sender");

        if (!(findByCode(code).getSender().equals(reader)) && !(findRecipientOfEmail(code).equals(reader)))
            throw new IllegalArgumentException("You cannot read this email.");

        if (!SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select read_time " +
                                "from email_recipients " +
                                "where recipient_id = :reader_id and email_id = :email_id", Timestamp.class)
                        .setParameter("reader_id", reader.getId())
                        .setParameter("email_id", findByCode(code).getId())
                        .getResultList()).isEmpty()
                && SingletonSessionFactory.get().fromTransaction(session ->
                session.createNativeQuery("select read_time " +
                                "from email_recipients " +
                                "where recipient_id = :reader_id and email_id = :email_id", Timestamp.class)
                        .setParameter("reader_id", reader.getId())
                        .setParameter("email_id", findByCode(code).getId())
                        .getSingleResult()) != null)
            return;

        SingletonSessionFactory.get().inTransaction(session ->
                session.createNativeMutationQuery("update email_recipients " +
                                "set read_at = :now " +
                                "where recipient_id = :reader_id and email_id = :email_id")
                        .setParameter("now", Timestamp.valueOf(LocalDateTime.now()))
                        .setParameter("reader_id", reader.getId())
                        .setParameter("email_id", findByCode(code).getId())
                        .executeUpdate());

    }

    public static List<User> findRecipientOfEmail(String code) {

        Email email = findByCode(code);

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
        List<User> recipients = findRecipientOfEmail(code);
        recipients.remove(findByCode(code).getSender());

        Email reply = makeEmail(sender, "[Re] " + findByCode(code).getSubject(), body);

        sendEmail(sender, reply.getSubject(), reply.getBody(), recipients);

        return reply;
    }

    public static String madeCode (int id) {
        String code = Integer.toString(id, 36);
        int len = code.length();
        for (int i = 0; i < 6 - len; i++)
            code = "0" + code;
        return code;
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

    public static void deleteEmail(User user, String code) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be empty!");
        }

        if (code == null) {
            throw new IllegalArgumentException("Code field can not be empty!");
        }

        boolean isSender = findByCode(code).getSender().getId().equals(user.getId());

        boolean isRecipient = findRecipientOfEmail(code).stream().anyMatch(recipient -> recipient.getId().equals(user.getId()));
        Integer emailId = findByCode(code).getId();

        if (!isRecipient && !isSender) {
            throw new IllegalArgumentException("You are not able to delete this email :(You don't have primission)");
        }

        if (isSender) {
            SingletonSessionFactory.get()
                    .fromTransaction(session ->
                            session.createNativeMutationQuery("delete from email_recipients " +
                                            "where email_id = : email_id")
                                    .setParameter("email_id", emailId)
                                    .executeUpdate());

            SingletonSessionFactory.get()
                    .fromTransaction(session ->
                            session.createNativeMutationQuery("delete from emails " +
                                            "where email_id = :email_id")
                                    .setParameter("email_id", emailId)
                                    .executeUpdate());
        }

        else {
            SingletonSessionFactory.get()
                    .fromTransaction(session ->
                            session.createNativeMutationQuery("delete from email_recipients " +
                                            "where email_id = : email_id and recipient_id = :recipient_id")
                                    .setParameter("email_id", emailId)
                                    .setParameter("recipient_id", user.getId())
                                    .executeUpdate());
        }
    }

    public static void editEmail(User editor, String code, String newSubject, String newBody) {

        if (editor == null)
            throw new IllegalArgumentException("Editor cannot be null!");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null!");
        if (newSubject == null || newSubject.isEmpty())
            throw new IllegalArgumentException("Subject cannot be empty!");
        if (newBody == null || newBody.isEmpty())
            throw new IllegalArgumentException("Body cannot be empty!");

        Email email = findByCode(code);

        if (!email.getSender().getId().equals(editor.getId())) {
            throw new IllegalArgumentException("Only the sender can edit the email!");
        }

        if (!email.getSubject().startsWith("[Edited] ")) {
            email.setSubject("[Edited] " + email.getSubject());
        }

        String editedBody = newBody + "\n\n[Edited at: " + LocalDateTime.now() + "]";
        email.setBody(editedBody);

        SingletonSessionFactory.get().inTransaction(session -> {
            session.merge(email);
        });
    }
}
