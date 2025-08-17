package aut.ap;
import aut.ap.model.*;
import aut.ap.Services.EmailService;
import aut.ap.Services.UserService;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    // Sizes
    static Dimension mainMenuSize = new Dimension(500, 300);
    static Dimension accountSize = new Dimension(500, 400);
    static Dimension buttonSize = new Dimension(150, 50);
    static Dimension imageSize = new Dimension(190, 190);

    static HashMap<Integer, ArrayList<JList<String>>> accountUnreadList = new HashMap<>();
    static HashMap<Integer, ArrayList<JList<String>>> accountAllList = new HashMap<>();
    static HashMap<Integer, ArrayList<JList<String>>> accountSentList = new HashMap<>();

    public static void main(String[] args) {
        final int[] accountCount = {0};

        // MainMenu Frame
        JFrame mainFrame = new JFrame("MILLOU");
        mainFrame.setLayout(null);
        mainFrame.setSize(mainMenuSize);
        mainFrame.setLocationRelativeTo(null);

        // mainMenu Panel
        JPanel publicPanel = new JPanel();
        publicPanel.setLayout(null);
        publicPanel.setSize(mainMenuSize);
        publicPanel.setLocation(0, 0);
        publicPanel.setBackground(new Color(0xFFFFFF));

        ImageIcon millouIcon = new ImageIcon("src\\assets\\img.png");
        JLabel millouLabel = new JLabel(new ImageIcon(millouIcon.getImage().getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_SMOOTH)));
        millouLabel.setBounds(50, 35, imageSize.width, imageSize.height);
        publicPanel.add(millouLabel);

        JButton logInButton = new JButton("Log In");
        logInButton.setLocation(300, 35);
        logInButton.setSize(buttonSize);
        publicPanel.add(logInButton);
        logInButton.setMargin(new Insets(2,5,2,5));

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setLocation(300, 105);
        signUpButton.setSize(buttonSize);
        publicPanel.add(signUpButton);
        signUpButton.setMargin(new Insets(2,5,2,5));

        JButton quitButton = new JButton("Quit");
        quitButton.setLocation(300, 175);
        quitButton.setSize(buttonSize);
        publicPanel.add(quitButton);
        quitButton.setMargin(new Insets(2,5,2,5));

        mainFrame.add(publicPanel);

        // LogInMain Panel
        JPanel logInPanel = new JPanel();
        logInPanel.setLayout(null);
        logInPanel.setSize(mainMenuSize);
        logInPanel.setLocation(0, 0);
        logInPanel.setBackground(new Color(0xFFFFFF));

        JLabel logInEmailLabel = new JLabel("Email:");
        logInEmailLabel.setBounds(100, 45, 80, 25);
        JTextField logInEmailField = new JTextField();
        logInEmailField.setBounds(190, 45, 200, 25);
        logInPanel.add(logInEmailLabel);
        logInPanel.add(logInEmailField);

        JLabel logInPasswordLabel = new JLabel("Password:");
        logInPasswordLabel.setBounds(100, 75, 80, 25);
        JPasswordField logInPasswordField = new JPasswordField();
        logInPasswordField.setBounds(190, 75, 200, 25);
        logInPanel.add(logInPasswordLabel);
        logInPanel.add(logInPasswordField);

        JButton logInSubmitButton = new JButton("Submit");
        logInSubmitButton.setSize(buttonSize);
        logInSubmitButton.setLocation(190, 125);
        logInPanel.add(logInSubmitButton);
        logInSubmitButton.setMargin(new Insets(2,5,2,5));

        JButton logInBackButton = new JButton("Back");
        logInBackButton.setSize(buttonSize);
        logInBackButton.setLocation(190, 175);
        logInPanel.add(logInBackButton);
        logInBackButton.setMargin(new Insets(2,5,2,5));

        logInSubmitButton.addActionListener(e -> {
            String email = completeEmail(logInEmailField.getText());
            String password = new String(logInPasswordField.getPassword());

            try {
                User user = UserService.loginUser(email, password);
                JOptionPane.showMessageDialog(mainFrame,"Welcome back :) , " + user.getName() + "!\n");
                if (!accountUnreadList.containsKey(user.getId()))
                    accountUnreadList.put(user.getId(), new ArrayList<>());
                if (!accountAllList.containsKey(user.getId()))
                    accountAllList.put(user.getId(), new ArrayList<>());
                if (!accountSentList.containsKey(user.getId()))
                    accountSentList.put(user.getId(), new ArrayList<>());

                // Account Frame
                JFrame newFrame = new JFrame(user.getName());
                newFrame.setSize(accountSize);
                int accountLocType = (accountCount[0] + 1) % 4;
                newFrame.setLocation(((accountLocType + 1) % 2) * 1040, ((accountLocType / 2) % 2) * 400);

                // Account Main Panel
                JPanel accMainPanel = new JPanel(null);
                accMainPanel.setBounds(0, 0, accountSize.width, accountSize.height);
                accMainPanel.setBackground(new Color(0xFFFFFF));

                showEmails(accMainPanel, "Unread Emails", EmailService.readUnreadEmails(user), 0, 0, accountSize.width - (buttonSize.width + 20), accountSize.height, user.getId());

                JButton sendButton = new JButton("Send");
                sendButton.setBounds(332, 10, buttonSize.width, buttonSize.height);
                ImageIcon sendIcon = new ImageIcon("src\\assets\\img_3.png");
                sendButton.setIcon(sendIcon);
                accMainPanel.add(sendButton);
                sendButton.setMargin(new Insets(2,5,2,5));


                JButton viewButton = new JButton("View");
                viewButton.setBounds(332, 70, buttonSize.width, buttonSize.height);
                accMainPanel.add(viewButton);
                viewButton.setMargin(new Insets(2,5,2,5));


                JButton replyButton = new JButton("Reply");
                replyButton.setBounds(332, 130, buttonSize.width, buttonSize.height);
                accMainPanel.add(replyButton);
                replyButton.setMargin(new Insets(2,5,2,5));

                JButton forwardButton = new JButton("Forward");
                forwardButton.setBounds(332, 190, buttonSize.width, buttonSize.height);
                accMainPanel.add(forwardButton);
                forwardButton.setMargin(new Insets(2,5,2,5));


                JButton deleteButton = new JButton();
                ImageIcon deleteIcon = new ImageIcon("src\\assets\\img_2.png");
                deleteButton.setBounds(332, 250, buttonSize.width, buttonSize.height);
                deleteButton.setIcon(deleteIcon);
                deleteButton.setMargin(new Insets(2,5,2,5));
                accMainPanel.add(deleteButton);

                JButton accQuitButton = new JButton("Log out");
                accQuitButton.setBounds(332, 310, buttonSize.width, buttonSize.height);
                accMainPanel.add(accQuitButton);

                // Send Panel
                JPanel sendPanel = new JPanel();
                sendPanel.setLayout(null);
                sendPanel.setSize(mainMenuSize);
                sendPanel.setLocation(0, 0);
                sendPanel.setBackground(new Color(0xFFFFFF));

                JLabel sendRecipients = new JLabel("Recipient(s):");
                sendRecipients.setBounds(100, 45, 80, 25);
                JTextField sendRecipientsField = new JTextField();
                sendRecipientsField.setBounds(190, 45, 200, 25);
                sendPanel.add(sendRecipients);
                sendPanel.add(sendRecipientsField);

                JLabel sendSubject = new JLabel("Subject:");
                sendSubject.setBounds(100, 70, 80, 25);
                JTextField sendSubjectField = new JTextField();
                sendSubjectField.setBounds(190, 70, 200, 25);
                sendPanel.add(sendSubject);
                sendPanel.add(sendSubjectField);

                JLabel sendBody = new JLabel("Body:");
                sendBody.setBounds(100, 95, 80, 25);
                JTextField sendBodyField = new JTextField();
                sendBodyField.setBounds(190, 95, 200, 25);
                sendPanel.add(sendBody);
                sendPanel.add(sendBodyField);

                JButton sendSendButton = new JButton("Send");
                sendSendButton.setSize(buttonSize);
                sendSendButton.setLocation(190, 125);
                sendPanel.add(sendSendButton);

                JButton sendBackButton = new JButton("Back");
                sendBackButton.setSize(buttonSize);
                sendBackButton.setLocation(190, 175);
                sendPanel.add(sendBackButton);

                sendSendButton.addActionListener(e1 -> {
                    String subject = sendSubjectField.getText();
                    String body = sendBodyField.getText();
                    String recipients = sendRecipientsField.getText();
                    String[] recipientsList = recipients.split(", ");

                    ArrayList<String> wrongEmails = new ArrayList<>();
                    ArrayList<User> existEmails = new ArrayList<>();

                    for (String recipient: recipientsList)
                        if (UserService.findByEmail(completeEmail(recipient)) == null)
                            wrongEmails.add(recipient);
                        else
                            existEmails.add(UserService.findByEmail(completeEmail(recipient)));

                    try {
                        Email sentEmail = EmailService.sendEmail(user, subject, body, existEmails);
                        refreshUsers(existEmails);
                        refreshUsers(Arrays.asList(user));

                        String statusMessage = "Successfully sent your email.";
                        if (!wrongEmails.isEmpty()) {
                            statusMessage += "\nBUT NOT TO " ;
                            for (String wrongEmail: wrongEmails)
                                statusMessage += wrongEmail;
                            statusMessage += "; THEY DOES NOT EXIST";
                        }
                        statusMessage += "\nCode: " + EmailService.madeCode(sentEmail.getId());

                        JOptionPane.showMessageDialog(newFrame, statusMessage);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newFrame,"Error: " + ex.getMessage());
                    }
                });
                sendBackButton.addActionListener(e1 -> {
                    newFrame.remove(sendPanel);
                    newFrame.add(accMainPanel);
                    newFrame.repaint();
                    newFrame.revalidate();
                });

                sendButton.addActionListener(e1 -> {
                    newFrame.remove(accMainPanel);
                    newFrame.add(sendPanel);
                    newFrame.repaint();
                    newFrame.revalidate();
                });

                // View Panel
                JPanel viewPanel = new JPanel(null);
                viewPanel.setSize(accountSize);
                viewPanel.setBackground(new Color(0xA6FFFFFF, true));

                ImageIcon icon = new ImageIcon("src\\assets\\img_1.png");
                JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_SMOOTH)));
                imageLabel.setBounds(50, 85, imageSize.width, imageSize.height);
                viewPanel.add(imageLabel);

                JButton allEmailsButton = new JButton("All Emails");
                allEmailsButton.setLocation(280, 20);
                allEmailsButton.setSize(buttonSize);
                viewPanel.add(allEmailsButton);

                JButton unreadEmailsButton = new JButton("Unread Emails");
                unreadEmailsButton.setLocation(280, 20 + (buttonSize.height + 20));
                unreadEmailsButton.setSize(buttonSize);
                viewPanel.add(unreadEmailsButton);

                JButton sentEmailsButton = new JButton("Sent Emails");
                sentEmailsButton.setLocation(280, 20 + 2 * (buttonSize.height + 20));
                sentEmailsButton.setSize(buttonSize);
                viewPanel.add(sentEmailsButton);

                JButton readByCodeButton = new JButton("Read by Code");
                readByCodeButton.setLocation(280, 20 + 3 * (buttonSize.height + 20));
                readByCodeButton.setSize(buttonSize);
                viewPanel.add(readByCodeButton);

                JButton viewBackButton = new JButton("Back");
                viewBackButton.setLocation(280, 20 + 4 * (buttonSize.height + 20));
                viewBackButton.setSize(buttonSize);
                viewPanel.add(viewBackButton);

                allEmailsButton.addActionListener(e2 -> {
                    try {
                        view(newFrame, viewPanel, "All Emails", EmailService.showAllEmails(user), user.getId());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newFrame,"Error: " + ex.getMessage());
                    }
                });
                unreadEmailsButton.addActionListener(e2 -> {
                    try {
                        view(newFrame, viewPanel, "Unread Emails", EmailService.readUnreadEmails(user), user.getId());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newFrame,"Error: " + ex.getMessage());
                    }
                });
                sentEmailsButton.addActionListener(e2 -> {
                    try {
                        view(newFrame, viewPanel, "Sent Emails", EmailService.readSentEmails(user), user.getId());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newFrame,"Error: " + ex.getMessage());
                    }
                });
                readByCodeButton.addActionListener(e2 -> {
                    String code = JOptionPane.showInputDialog(newFrame, "Enter email code:");

                    if (code != null) {
                        try {
                            Email foundEmail = EmailService.findByCode(code);
                            EmailService.readEmail(user, EmailService.findByCode(code));
                            refreshUsers(Arrays.asList(user));

                            JPanel readPanel = new JPanel(null);
                            readPanel.setBounds(0, 0, accountSize.width, accountSize.height);

                            JTextArea messageArea = new JTextArea(foundEmail.toString());
                            messageArea.setLineWrap(true);
                            messageArea.setWrapStyleWord(true);
                            messageArea.setEditable(false);
                            JScrollPane messageScrollPane = new JScrollPane(messageArea);
                            messageScrollPane.setBounds(0, 0, accountSize.width, accountSize.height - (buttonSize.height + 80));
                            readPanel.add(messageScrollPane);

                            JButton messageBackButton = new JButton("Back");
                            messageBackButton.setBounds((accountSize.width - buttonSize.width) / 2, 300, buttonSize.width, buttonSize.height);
                            readPanel.add(messageBackButton);

                            messageBackButton.addActionListener(e3 -> {
                                newFrame.remove(readPanel);
                                newFrame.add(viewPanel);
                                newFrame.revalidate();
                                newFrame.repaint();
                            });

                            newFrame.remove(viewPanel);
                            newFrame.add(readPanel);
                            newFrame.revalidate();
                            newFrame.repaint();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(newFrame,"Error: " + ex.getMessage());
                        }
                    }
                });
                viewBackButton.addActionListener(e2 -> {
                    newFrame.remove(viewPanel);
                    newFrame.add(accMainPanel);
                    newFrame.revalidate();
                    newFrame.repaint();
                });

                viewButton.addActionListener(e1 -> {
                    newFrame.remove(accMainPanel);
                    newFrame.add(viewPanel);
                    newFrame.revalidate();
                    newFrame.repaint();
                });

                // Reply Panel
                JPanel replyPanel = new JPanel();
                replyPanel.setLayout(null);
                replyPanel.setSize(mainMenuSize);
                replyPanel.setLocation(0, 0);
                replyPanel.setBackground(new Color(0xFFFFFF));

                JLabel replyCode = new JLabel("Code:");
                replyCode.setBounds(100, 45, 80, 25);
                JTextField replyCodeField = new JTextField();
                replyCodeField.setBounds(190, 45, 200, 25);
                replyPanel.add(replyCode);
                replyPanel.add(replyCodeField);

                JLabel replyBody = new JLabel("Body:");
                replyBody.setBounds(100, 75, 80, 25);
                JTextField replyBodyField = new JTextField();
                replyBodyField.setBounds(190, 75, 200, 25);
                replyPanel.add(replyBody);
                replyPanel.add(replyBodyField);

                JButton sendReplyButton = new JButton("Reply");
                sendReplyButton.setSize(buttonSize);
                sendReplyButton.setLocation(190, 125);
                replyPanel.add(sendReplyButton);

                JButton replyBackButton = new JButton("Back");
                replyBackButton.setSize(buttonSize);
                replyBackButton.setLocation(190, 175);
                replyPanel.add(replyBackButton);

                sendReplyButton.addActionListener(e1 -> {
                    try {
                        String code = replyCodeField.getText();
                        String body = replyBodyField.getText();
                        Email repliedEmail = EmailService.replyEmail(user, code, body);
                        refreshUsers(EmailService.findRecipientOfEmail(repliedEmail));
                        refreshUsers(Arrays.asList(user));

                        JOptionPane.showMessageDialog(newFrame, "Successfully sent your reply to email" + code + "\nCode: " + EmailService.madeCode(repliedEmail.getId()));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newFrame, "Error: " + ex.getMessage());
                    }
                });
                replyBackButton.addActionListener(e1 -> {
                    newFrame.remove(replyPanel);
                    newFrame.add(accMainPanel);
                    newFrame.revalidate();
                    newFrame.repaint();
                });

                replyButton.addActionListener(e1 -> {
                    newFrame.remove(accMainPanel);
                    newFrame.add(replyPanel);

                    newFrame.revalidate();
                    newFrame.repaint();
                });

                // Forward Panel
                JPanel forwardPanel = new JPanel();
                forwardPanel.setLayout(null);
                forwardPanel.setSize(mainMenuSize);
                forwardPanel.setLocation(0, 0);
                forwardPanel.setBackground(new Color(0xFFFFFF));

                JLabel forwardCode = new JLabel("Code:");
                forwardCode.setBounds(100, 45, 80, 25);
                JTextField forwardCodeField = new JTextField();
                forwardCodeField.setBounds(190, 45, 200, 25);
                forwardPanel.add(forwardCode);
                forwardPanel.add(forwardCodeField);

                JLabel forwardRecipient = new JLabel("Recipient(s):");
                forwardRecipient.setBounds(100, 75, 80, 25);
                JTextField forwardRecipientField  = new JTextField();
                forwardRecipientField.setBounds(190, 75, 200, 25);
                forwardPanel.add(forwardRecipient);
                forwardPanel.add(forwardRecipientField);

                JButton sendForwardButton = new JButton("Forward");
                sendForwardButton.setSize(buttonSize);
                sendForwardButton.setLocation(190, 125);
                forwardPanel.add(sendForwardButton);

                JButton forwardBackButton = new JButton("Back");
                forwardBackButton.setSize(buttonSize);
                forwardBackButton.setLocation(190, 175);
                forwardPanel.add(forwardBackButton);

                sendForwardButton.addActionListener(e2 -> {
                    String code = forwardCodeField.getText();
                    String recipients = forwardRecipientField.getText();
                    String[] recipientsList = recipients.split(", ");

                    ArrayList<String> wrongEmails = new ArrayList<>();
                    ArrayList<User> existEmails = new ArrayList<>();

                    for (String recipient: recipientsList)
                        if (UserService.findByEmail(completeEmail(recipient)) == null)
                            wrongEmails.add(recipient);
                        else
                            existEmails.add(UserService.findByEmail(completeEmail(recipient)));

                    try {
                        Email forwardedEmail = EmailService.forwardEmail(user, code, existEmails);
                        refreshUsers(existEmails);
                        refreshUsers(Arrays.asList(user));

                        String statusMessage = "Successfully forwarded your email.";
                        if (!wrongEmails.isEmpty()) {
                            statusMessage += "\nBUT NOT TO " ;
                            for (String wrongEmail: wrongEmails)
                                statusMessage += wrongEmail;
                            statusMessage += "; THEY DOES NOT EXIST";
                        }
                        statusMessage += "\nCode: " + EmailService.madeCode(forwardedEmail.getId());

                        JOptionPane.showMessageDialog(newFrame, statusMessage);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(newFrame,"Error: " + ex.getMessage());
                    }
                });
                forwardBackButton.addActionListener(e2 -> {
                    newFrame.remove(forwardPanel);
                    newFrame.add(accMainPanel);
                    newFrame.revalidate();
                    newFrame.repaint();
                });

                forwardButton.addActionListener(e1 -> {
                    newFrame.remove(accMainPanel);
                    newFrame.add(forwardPanel);

                    newFrame.revalidate();
                    newFrame.repaint();
                });

                // Delete Button
                deleteButton.addActionListener(e1 -> {
                    while (true) {
                        String code = JOptionPane.showInputDialog(newFrame, "Enter email code:");

                        if (code != null) {
                            try {
                                Email deletedEmail = EmailService.findByCode(code);
                                List<User> recipients = EmailService.findRecipientOfEmail(EmailService.findByCode(code));
                                EmailService.deleteEmail(user, EmailService.findByCode(code));
                                refreshUsers(recipients);
                                refreshUsers(Arrays.asList(user));

                                JOptionPane.showMessageDialog(newFrame, "Successfully deleted email\nCode: " + EmailService.madeCode(deletedEmail.getId()));
                                break;
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(newFrame, "Error: " + ex.getMessage());
                            }
                        }
                        else
                            break;
                    }
                });

                // LogOut Button
                accQuitButton.addActionListener(e1 -> newFrame.dispose());

                newFrame.add(accMainPanel);

                newFrame.setVisible(true);

                accountCount[0]++;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame,"Error: " + ex.getMessage());
            }
        });
        logInBackButton.addActionListener(e -> {
            mainFrame.remove(logInPanel);
            mainFrame.add(publicPanel);
            mainFrame.revalidate();
            mainFrame.repaint();
        });

        logInButton.addActionListener(e -> {
            mainFrame.remove(publicPanel);
            mainFrame.add(logInPanel);
            mainFrame.revalidate();
            mainFrame.repaint();
        });

        // SignUp Panel
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(null);
        signUpPanel.setSize(mainMenuSize);
        signUpPanel.setLocation(0, 0);
        signUpPanel.setBackground(new Color(0xFFFFFF));

        JLabel signUpNameLabel = new JLabel("Name:");
        signUpNameLabel.setBounds(100, 15, 80, 25);
        JTextField signUpNameField = new JTextField();
        signUpNameField.setBounds(190, 15, 200, 25);
        signUpPanel.add(signUpNameLabel);
        signUpPanel.add(signUpNameField);

        JLabel signUpEmailLabel = new JLabel("Email:");
        signUpEmailLabel.setBounds(100, 45, 80, 25);
        JTextField signUpEmailField = new JTextField();
        signUpEmailField.setBounds(190, 45, 200, 25);
        signUpPanel.add(signUpEmailLabel);
        signUpPanel.add(signUpEmailField);

        JLabel signUpPasswordLabel = new JLabel("Password:");
        signUpPasswordLabel.setBounds(100, 75, 80, 25);
        JPasswordField signUpPasswordField = new JPasswordField();
        signUpPasswordField.setBounds(190, 75, 200, 25);
        signUpPanel.add(signUpPasswordLabel);
        signUpPanel.add(signUpPasswordField);

        JButton signUpSubmitButton = new JButton("Submit");
        signUpSubmitButton.setSize(buttonSize);
        signUpSubmitButton.setLocation(190, 125);
        signUpPanel.add(signUpSubmitButton);

        JButton signUpBackButton = new JButton("Back");
        signUpBackButton.setSize(buttonSize);
        signUpBackButton.setLocation(190, 175);
        signUpPanel.add(signUpBackButton);

        signUpSubmitButton.addActionListener(e -> {
            String name = signUpNameField.getText();
            String email = completeEmail(signUpEmailField.getText());
            String password = new String(signUpPasswordField.getPassword());

            try {
                UserService.registerUser(name, email, password);
                JOptionPane.showMessageDialog(mainFrame,"Your new account is created.\nGo ahead and login!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(mainFrame,"Error: " + ex.getMessage());
            }
        });
        signUpBackButton.addActionListener(e -> {
            mainFrame.remove(signUpPanel);
            mainFrame.add(publicPanel);
            mainFrame.revalidate();
            mainFrame.repaint();
        });

        signUpButton.addActionListener(e -> {
            mainFrame.remove(publicPanel);
            mainFrame.add(signUpPanel);
            mainFrame.revalidate();
            mainFrame.repaint();
        });

        // Quit Button
        quitButton.addActionListener(e -> {
            try {
                PrintWriter writer = new PrintWriter("src\\main\\logs\\hibernate.log");
                writer.print("");
                writer.close();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(mainFrame,"Error: " + ex.getMessage());
            }
            System.exit(0);
        });

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public static DefaultListModel<String> defaultListModelMaker(String title, List<Email> emails) {
        DefaultListModel<String> emailListModel = new DefaultListModel<>();
        emailListModel.addElement(title + ": (" + emails.size() + ")");
        for (Email email : emails) {
            emailListModel.addElement("From: " + email.getSender().getEmail() + " | Subject: " + email.getSubject() + "(" + EmailService.madeCode(email.getId()) + ")");
        }
        return emailListModel;
    }

    public static void showEmails(JPanel panel, String title, List<Email> emails, int x, int y, int width, int height, int userId) {
        DefaultListModel<String> emailListModel = defaultListModelMaker(title, emails);

        JList<String> emailList = new JList<>(emailListModel);
        if (title.equalsIgnoreCase("Unread Emails"))
            accountUnreadList.get(userId).add(emailList);
        if (title.equalsIgnoreCase("All Emails"))
            accountAllList.get(userId).add(emailList);
        if (title.equalsIgnoreCase("Sent Emails"))
            accountSentList.get(userId).add(emailList);
        JScrollPane scrollPane = new JScrollPane(emailList);
        scrollPane.setBounds(x, y, width, height);
        panel.add(scrollPane);
    }

    public static void view(JFrame frame, JPanel viewPanel, String title, List<Email> emails, int userId) {
        JPanel panel = new JPanel(null);
        panel.setBounds(0, 0, accountSize.width, accountSize.height);

        showEmails(panel, title, emails, 0, 0, accountSize.width, accountSize.height - (buttonSize.width + 20), userId);

        JButton unreadEmailBackButton = new JButton("Back");
        unreadEmailBackButton.setBounds((accountSize.width - buttonSize.width) / 2, 300, buttonSize.width, buttonSize.height);
        panel.add(unreadEmailBackButton);

        unreadEmailBackButton.addActionListener(e3 -> {
            frame.remove(panel);
            frame.add(viewPanel);
            frame.revalidate();
            frame.repaint();
        });

        frame.remove(viewPanel);
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public static void refreshUnreadMassage(List<User> users) {
        for (User user: users)
            if (accountUnreadList.containsKey(user.getId()))
                for (JList<String> list: accountUnreadList.get(user.getId()))
                    list.setModel(defaultListModelMaker("Unread Emails: ", EmailService.readUnreadEmails(user)));
    }

    public static void refreshAllMassages(List<User> users) {
        for (User user: users)
            if (accountAllList.containsKey(user.getId()))
                for (JList<String> list: accountAllList.get(user.getId()))
                    list.setModel(defaultListModelMaker("All Emails: ", EmailService.showAllEmails(user)));
    }

    public static void refreshSentMassage(List<User> users) {
        for (User user: users)
            if (accountSentList.containsKey(user.getId()))
                for (JList<String> list: accountSentList.get(user.getId()))
                    list.setModel(defaultListModelMaker("Sent Emails: ", EmailService.readSentEmails(user)));
    }

    public static void refreshUsers(List<User> users) {
        refreshUnreadMassage(users);
        refreshAllMassages(users);
        refreshSentMassage(users);
    }

    public static String completeEmail(String email) {
        if (!email.endsWith("@milou.com"))
            email += "@milou.com";
        return email;
    }
}