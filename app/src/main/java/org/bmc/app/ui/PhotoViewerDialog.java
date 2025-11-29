package org.bmc.app.ui;

import org.bmc.app.model.Photo;
import org.bmc.app.util.PhotoStorageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Dialog for viewing photos in full size.
 * Displays the full resolution image with zoom and pan capabilities.
 */
public class PhotoViewerDialog extends JDialog {
    
    private Photo photo;
    private JLabel imageLabel;
    
    public PhotoViewerDialog(Dialog parent, Photo photo) {
        super(parent, "Photo Viewer", true);
        this.photo = photo;
        
        initializeDialog();
        loadAndDisplayPhoto();
        
        setLocationRelativeTo(parent);
    }
    
    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        
        // Image display area
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setPreferredSize(new Dimension(780, 550));
        add(scrollPane, BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel nameLabel = new JLabel(photo.getDisplayName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(nameLabel, BorderLayout.WEST);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        infoPanel.add(closeButton, BorderLayout.EAST);
        
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void loadAndDisplayPhoto() {
        try {
            File imageFile = PhotoStorageUtil.getPhotoFile(photo.getFilePath());
            if (imageFile != null && imageFile.exists()) {
                BufferedImage img = ImageIO.read(imageFile);
                if (img != null) {
                    // Scale image to fit window while maintaining aspect ratio
                    int maxWidth = 780;
                    int maxHeight = 530;
                    
                    int imgWidth = img.getWidth();
                    int imgHeight = img.getHeight();
                    
                    double scale = Math.min(
                        (double) maxWidth / imgWidth,
                        (double) maxHeight / imgHeight
                    );
                    
                    // Only scale down if image is larger than display area
                    if (scale < 1.0) {
                        int scaledWidth = (int) (imgWidth * scale);
                        int scaledHeight = (int) (imgHeight * scale);
                        Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(scaledImg));
                    } else {
                        // Display at original size
                        imageLabel.setIcon(new ImageIcon(img));
                    }
                } else {
                    showError("Unable to load image");
                }
            } else {
                showError("Image file not found");
            }
        } catch (Exception e) {
            showError("Error loading image: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        imageLabel.setText(message);
        imageLabel.setForeground(Color.RED);
    }
}
