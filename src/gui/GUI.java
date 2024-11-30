package gui;

import services.ArticleParser;
import services.ChatGptQuerier;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class GUI {
    private JPanel contentPanel;

    private JPanel inputPanel;
    private JTextField urlInputField;
    private JButton submitButton;
    private JCheckBox useChatGptCheckBox;

    private JPanel outputPanel;
    private JPanel titleContainerPane;
    private JTextPane titleTextField;
    private JScrollPane summaryContainerScrollPane;
    private JTextPane generatedSummaryTextPane;
    private JPanel imageContainerPane;
    private JLabel generatedImageLabel;

    public GUI() {
        JFrame frame = new JFrame();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {

        }

        frame.setMinimumSize(new Dimension(450, 750));
        frame.add(contentPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("ChatGPT Article Summarizer");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    ArticleParser articleParser = new ArticleParser();
                    ChatGptQuerier chatGptQuerier = new ChatGptQuerier();

                    String url = urlInputField.getText();
                    String articleContent = articleParser.getArticleContentFromUrl(url);
                    String articleTitle = articleParser.getArticleTitle();
                    titleTextField.setText(articleTitle);

                    if (useChatGptCheckBox.isSelected()) {
                        String summarizedContent = chatGptQuerier.generateArticleSummary(articleContent);
                        generatedSummaryTextPane.setText(summarizedContent);
                        String generatedImageUrl = chatGptQuerier.generateDalleImage(articleTitle);
                        setImageFromUrl(generatedImageUrl);
                    } else {
                        generatedSummaryTextPane.setText(articleContent);
                    }
                } catch (RuntimeException exception) {
                    showErrorDialog(exception.getMessage());
                }
            }
        });
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
                null,
                "An error occurred:\n" + errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void setImageFromUrl(String inputUrl) {
        try {
            URL url = new URL(inputUrl);
            BufferedImage image = ImageIO.read(url);
            generatedImageLabel.setIcon(new ImageIcon(image));
            if (generatedImageLabel.getIcon() == null) {
                generatedImageLabel.setText("Generated Image Appears Here");
            } else {
                generatedImageLabel.setText("");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to set image to GUI from URL", e);
        }
    }
}
