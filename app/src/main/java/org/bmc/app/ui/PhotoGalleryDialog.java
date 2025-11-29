package org.bmc.app.ui;

import org.bmc.app.dao.PhotoDAO;
import org.bmc.app.model.Photo;
import org.bmc.app.util.PhotoStorageUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Read-only dialog for viewing all photos associated with a job.
 */
public class PhotoGalleryDialog extends JDialog {
    private PhotoDAO photoDAO;
    private int jobId;
    private List<Photo> photos;
    private JPanel thumbnailPanel;
    
    public PhotoGalleryDialog(Frame parent, int jobId) {
        super(parent, "Photos for Job #" + jobId, true);
        this.photoDAO = new PhotoDAO();
        this.jobId = jobId;
        
        initializeDialog();
        loadPhotos();
        refreshThumbnails();
    }
    
    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(900, 700);
        setLocationRelativeTo(getParent());
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel titleLabel = new JLabel("Job #" + jobId + " Photo Gallery");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Thumbnail scroll pane
        thumbnailPanel = new JPanel();
        thumbnailPanel.setLayout(new GridLayout(0, 3, 15, 15)); // 3 columns
        thumbnailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        thumbnailPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(thumbnailPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadPhotos() {
        try {
            photos = photoDAO.findByJobId(jobId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading photos: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            photos = List.of();
        }
    }
    
    private void refreshThumbnails() {
        thumbnailPanel.removeAll();
        
        if (photos.isEmpty()) {
            JLabel emptyLabel = new JLabel("No photos available for this job.");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            thumbnailPanel.setLayout(new BorderLayout());
            thumbnailPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            thumbnailPanel.setLayout(new GridLayout(0, 3, 15, 15));
            for (Photo photo : photos) {
                JPanel photoCard = createPhotoCard(photo);
                thumbnailPanel.add(photoCard);
            }
        }
        
        thumbnailPanel.revalidate();
        thumbnailPanel.repaint();
    }
    
    private JPanel createPhotoCard(Photo photo) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.setBackground(Color.WHITE);
        
        // Thumbnail image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(250, 200));
        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Load thumbnail
        try {
            File photoFile = PhotoStorageUtil.getPhotoFile(photo.getFilePath());
            if (photoFile.exists()) {
                BufferedImage img = ImageIO.read(photoFile);
                if (img != null) {
                    // Scale to thumbnail size
                    Image scaledImage = img.getScaledInstance(250, 200, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    imageLabel.setText("Cannot load image");
                }
            } else {
                imageLabel.setText("Image not found");
            }
        } catch (Exception e) {
            imageLabel.setText("Error loading image");
        }
        
        // Click to view full size
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                viewFullSize(photo);
            }
        });
        
        card.add(imageLabel, BorderLayout.CENTER);
        
        // Photo info at bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(photo.getDisplayName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(nameLabel);
        
        if (photo.getDescription() != null && !photo.getDescription().trim().isEmpty()) {
            JLabel descLabel = new JLabel(photo.getDescription());
            descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(descLabel);
        }
        
        card.add(infoPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void viewFullSize(Photo photo) {
        PhotoViewerDialog viewer = new PhotoViewerDialog(this, photo);
        viewer.setVisible(true);
    }
}
