package com.riya.chatbot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ChatBotGUI extends JFrame {
    private JPanel messagePanel;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JPanel typingPanel = null;

    public ChatBotGUI() {
        setTitle("\uD83D\uDCAC Riya's ChatBot");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint darkBackground = new GradientPaint(0, 0, new Color(20, 20, 20), 0, getHeight(), new Color(0, 0, 0));
                g2d.setPaint(darkBackground);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);

        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(25, 25, 25));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setBackground(new Color(40, 40, 40));
        inputField.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        inputField.setForeground(new Color(220, 220, 220));
        inputField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        inputField.setCaretColor(new Color(255, 100, 180));

        Color instaPink = new Color(255, 100, 180);
        Color instaPurple = new Color(150, 50, 255);
        sendButton = new InstagramGradientButton("Enter", instaPink, instaPurple);
        sendButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Action sendAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userText = inputField.getText().trim();
                if (!userText.isEmpty()) {
                    addMessage(userText, true);
                    inputField.setText("");
                    inputField.setEnabled(false);
                    sendButton.setEnabled(false);

                    showTypingIndicator();

                    new Thread(() -> {
                        String botResponse = GeminiClient.getGeminiReply(userText);
                        SwingUtilities.invokeLater(() -> {
                            removeTypingIndicator();
                            addMessage(botResponse, false);
                            inputField.setEnabled(true);
                            sendButton.setEnabled(true);
                        });
                    }).start();
                }
            }
        };

        inputField.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        add(mainPanel);

        setVisible(true);
    }

    private void addMessage(String text, boolean isUser) {
        JLabel nameLabel = new JLabel(isUser ? "You:" : "ChatBot:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(new Color(180, 180, 180));
        nameLabel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        String htmlText = "<html><body style='width: 320px; font-family: Comic Sans MS; font-size: 14px;'>" +
                text.replaceAll("\n", "<br>") +
                "</body></html>";

        JLabel label = new JLabel();
        label.setText(isUser ? htmlText : "");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        label.setVerticalAlignment(SwingConstants.TOP);

        JPanel bubbleWrapper = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        bubbleWrapper.setOpaque(false);
        bubbleWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel bubblePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (isUser) {
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(255, 100, 180), getWidth(), getHeight(), new Color(150, 50, 255));
                    g2d.setPaint(gp);
                    g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                } else {
                    g.setColor(new Color(60, 60, 60));
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
            }
        };
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        bubblePanel.add(label);

        bubbleWrapper.add(bubblePanel);

        JPanel messageBox = new JPanel();
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setOpaque(false);
        messageBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel nameWrapper = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 0));
        nameWrapper.setOpaque(false);
        nameWrapper.add(nameLabel);

        messageBox.add(nameWrapper);
        messageBox.add(bubbleWrapper);
        messagePanel.add(messageBox);
        messagePanel.revalidate();
        scrollToBottom();

        if (!isUser) {
            simulateTypingLabel(label, htmlText);
        }
    }

    private void simulateTypingLabel(JLabel label, String fullHtmlText) {
        new Thread(() -> {
            String cleanText = fullHtmlText.replaceAll("<[^>]*>", "");
            for (int i = 0; i <= cleanText.length(); i++) {
                final String partial = cleanText.substring(0, i);
                SwingUtilities.invokeLater(() -> label.setText("<html><body style='width: 320px; font-family: Comic Sans MS; font-size: 14px;'>" +
                        partial.replaceAll("\n", "<br>") +
                        "</body></html>"));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void showTypingIndicator() {
        typingPanel = new JPanel();
        typingPanel.setLayout(new BoxLayout(typingPanel, BoxLayout.Y_AXIS));
        typingPanel.setOpaque(false);
        typingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel nameLabel = new JLabel("ChatBot:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(new Color(180, 180, 180));

        JLabel typingDots = new JLabel("Typing");
        typingDots.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        typingDots.setForeground(Color.WHITE);

        JPanel nameWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameWrapper.setOpaque(false);
        nameWrapper.add(nameLabel);

        JPanel bubbleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bubbleWrapper.setOpaque(false);
        bubbleWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        bubble.add(typingDots);

        bubbleWrapper.add(bubble);
        typingPanel.add(nameWrapper);
        typingPanel.add(bubbleWrapper);
        messagePanel.add(typingPanel);
        messagePanel.revalidate();
        scrollToBottom();

        new Thread(() -> {
            String[] dots = {"Typing", "Typing.", "Typing..", "Typing..."};
            int i = 0;
            while (typingPanel != null) {
                final String text = dots[i % dots.length];
                SwingUtilities.invokeLater(() -> typingDots.setText(text));
                i++;
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void removeTypingIndicator() {
        if (typingPanel != null) {
            messagePanel.remove(typingPanel);
            typingPanel = null;
            messagePanel.revalidate();
            messagePanel.repaint();
            scrollToBottom();
        }
    }

    public static void main(String[] args) {
        new ChatBotGUI();
    }

    static class InstagramGradientButton extends JButton {
        private final Color startColor;
        private final Color endColor;
        private final Color originalFg;
        private final Color hoverFg = Color.LIGHT_GRAY;

        public InstagramGradientButton(String text, Color startColor, Color endColor) {
            super(text);
            this.startColor = startColor;
            this.endColor = endColor;
            this.originalFg = getForeground();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(hoverFg);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(originalFg);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = getModel().isRollover()
                    ? new GradientPaint(0, 0, startColor.darker(), getWidth(), getHeight(), endColor.darker())
                    : new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }
}
