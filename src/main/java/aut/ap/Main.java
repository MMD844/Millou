package aut.ap;
import aut.ap.model.*;
import aut.ap.Services.EmailService;
import aut.ap.Services.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);

        while (true) {
            System.out.println("[L]ogin, [S]ign up, [Q]uit:");
            String command = scn.nextLine().trim().toLowerCase();

            if ("l".equals(command) || "login".equalsIgnoreCase(command)) {
                System.out.print("Email: ");
                String email = scn.nextLine().trim();

                System.out.print("Password: ");
                String password = scn.nextLine().trim();

                try {
                    User user = UserService.loginUser(email, password);
                    System.out.println("Welcome back, " + user.getName() + "!");

                    showEmailsToString("Unread Emails", EmailService.readUnreadEmails(user));

                    while (true) {
                        System.out.println("[S]end, [V]iew, [R]eply, [F]orward, Read by [C]ode, [Q]uit: ");
                        String cmd = scn.nextLine().trim().toLowerCase();

                        if ("s".equals(cmd) || "send".equalsIgnoreCase(cmd)) {
                            System.out.print("Recipient(s) (comma-separated): ");
                            String[] recipientsEmail = scn.nextLine().split(", ");

                            ArrayList<String> wrongEmails = new ArrayList<>();
                            ArrayList<User> existEmails = new ArrayList<>();

                            for (String recipient: recipientsEmail)
                                if (UserService.findByEmail(recipient) == null)
                                    wrongEmails.add(recipient);
                                else
                                    existEmails.add(UserService.findByEmail(recipient));

                            System.out.print("Subject: ");
                            String subject = scn.nextLine();

                            System.out.print("Body: ");
                            String body = scn.nextLine();
                            try {
                                Email sentEmail = EmailService.sendEmail(user, subject, body, existEmails);

                                System.out.println("Successfully sent your email.\n" +
                                        "BUT NOT TO " + wrongEmails + "; THEY DOES NOT EXIST\n" +
                                        "Code: " + Integer.toString(sentEmail.getId(), 36));
                            }catch (Exception e) {
                                System.out.println("Error" + e.getMessage());
                            }

                        } else if ("v".equals(cmd) || "view".equalsIgnoreCase(cmd)) {
                            System.out.print("[A]ll, [U]nread, [S]ent: ");
                            String choice = scn.nextLine().trim().toLowerCase();

                            try {
                                if ("a".equals(choice) || "all".equalsIgnoreCase(choice))
                                    showEmailsToString("All Emails", EmailService.showAllEmails(user));
                                else if ("u".equals(choice) || "unread".equalsIgnoreCase(choice))
                                    showEmailsToString("Unread Emails", EmailService.readUnreadEmails(user));
                                else if ("s".equals(choice) || "sent".equalsIgnoreCase(choice))
                                    showEmailsToString("Sent Emails", EmailService.readSentEmails(user));
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }

                        } else if ("r".equals(cmd) || "reply".equalsIgnoreCase(cmd)) {
                            System.out.println("Code :");
                            String code = scn.nextLine().trim();
                            System.out.println("Write your emali's body :");
                            String body = scn.nextLine();

                            try {
                                Email email1 = EmailService.replyEmail(user, code, body);
                                int id = email1.getId();

                                System.out.println("Successfully sent your reply to email" + code + "\n");
                                System.out.println("Code :" + Integer.toString(id, 36));
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                        else if ("f".equals(cmd) || "forward".equalsIgnoreCase(cmd)) {
                            System.out.println("Code:");
                            String code = scn.nextLine().trim();

                            System.out.println("Recipient(s) :");
                            String input = scn.nextLine().trim();
                            List<String> emails = Arrays.asList(input.split(","));

                            ArrayList<User> wrongEmails = new ArrayList<>();
                            ArrayList<User> existEmails = new ArrayList<>();

                            for (String e : emails) {
                                if (UserService.findByEmail(e) == null) {
                                    wrongEmails.add(UserService.findByEmail(e));
                                }
                                else {
                                    existEmails.add(UserService.findByEmail(e));
                                }

                            }

                            try {
                                Email email1 = EmailService.forwardEmail(user, code, existEmails);
                                int id = email1.getId();

                                System.out.println("Successfully forwarded your email.\n");
                                System.out.println("Code :" + Integer.toString(id, 36));

                                if (!wrongEmails.isEmpty()) {
                                    System.out.println("Your email not forwarded to:" + wrongEmails + "because thats not exists!");
                                }
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                        else if ("c".equals(cmd) || "read by code".equalsIgnoreCase(cmd)) {
                            System.out.println("Code :");

                            String code = scn.nextLine().trim();

                            try {
                                Email findedEmail = EmailService.findByCode(code);

                                EmailService.readEmail(user, findedEmail);

                                System.out.println(findedEmail);
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                        else if ("q".equals(cmd) || "quit".equalsIgnoreCase(cmd))
                            break;
                        else {
                            System.out.println("Invalid command. Please try again.");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

            else if ("s".equals(command) || "sign up".equalsIgnoreCase(command)) {
                System.out.print("Name: ");
                String name = scn.nextLine().trim();

                System.out.print("Email: ");
                String email = scn.nextLine().trim();

                System.out.print("Password: ");
                String password = scn.nextLine().trim();

                try {
                    UserService.registerUser(name, email, password);
                    System.out.println("Your new account is created.");
                    System.out.println("Go ahead and login!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if ("q".equals(command) || "quit".equalsIgnoreCase(command))
                break;
            else {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }

    public static void showEmailsToString(String title, List<Email> emails) {
        System.out.println(title + ": (" + emails.size() + ")\n");
        for (Email email: emails)
            System.out.println("+ " + email.getSender().getEmail() + " - " + email.getSubject() + "(" + Integer.toString(email.getId(), 36) + ")");
    }
}