package aut.ap.model;

import jakarta.persistence.*;
import aut.ap.Services.EmailService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "emails")
public class Email {

    public Email() {
    }

    public Email(User sender, String subject, String body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Basic(optional = false)
    private String subject;

    @Basic(optional = false)
    private String body;

    @Basic(optional = false)
    @Column(name = "created_at")
    private Timestamp sendTime;

    @PrePersist
    protected void fillSendTime() {
        sendTime = Timestamp.valueOf(LocalDateTime.now());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Timestamp getSentAt() { return sendTime; }

    @Override
    public String toString() {
        String code = EmailService.madeCode(id);

        String date = sendTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<User> recipient = EmailService.findRecipientOfEmail(this);
        String recipientList = recipient.getFirst().getEmail();

        for (int i = 1; i < recipient.size(); i ++)
            recipientList +=", " + recipient.get(i).getEmail();

        return "Code: " + code + "\n" +
                "Recipient(s): " + recipientList + "\n" +
                "Subject: " + subject + "\n" +
                "Date: " + date + "\n\n" +
                body;
    }
}