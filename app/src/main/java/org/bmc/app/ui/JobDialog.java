package org.bmc.app.ui;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.dao.EmployeeDAO;
import org.bmc.app.dao.JobDAO;
import org.bmc.app.dao.PhotoDAO;
import org.bmc.app.model.Customer;
import org.bmc.app.model.Employee;
import org.bmc.app.model.Job;
import org.bmc.app.model.Photo;
import org.bmc.app.util.PhotoStorageUtil;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal dialog for creating new jobs or editing existing job records.
 * Provides form validation, customer selection, and database persistence through JobDAO.
 * 
 * @author BMC Systems Team
 */
public class JobDialog extends JDialog {
    
    private static final int DESC_ROWS = 4;
    private static final int DESC_COLS = 30;
    private static final int DATE_FIELD_COLS = 15;
    private static final String DATE_FORMAT_HINT = " (YYYY-MM-DD)";
    
    private final JobDAO jobDAO;
    private final CustomerDAO customerDAO;
    private final EmployeeDAO employeeDAO;
    private final PhotoDAO photoDAO;
    private final Job job;
    private boolean saved;
    
    private JComboBox<CustomerItem> customerCombo;
    private JComboBox<EmployeeItem> employeeCombo;
    private JTextArea descriptionArea;
    private JComboBox<Job.Status> statusCombo;
    private JTextField startDateField;
    private JTextField dueDateField;
    private JTextField estimatedLaborCostField;
    private JTextField estimatedMaterialCostField;
    private JPanel photoThumbnailPanel;
    private List<Photo> jobPhotos;
    
    /**
     * Wrapper class for displaying customer information in combo box.
     */
    private static class CustomerItem {
        final Integer id;
        final String name;
        
        CustomerItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name + " (ID: " + id + ")";
        }
    }
    
    /**
     * Wrapper class for displaying employee information in combo box.
     */
    private static class EmployeeItem {
        final Integer id;
        final String name;
        
        EmployeeItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name != null ? name : "(None)";
        }
    }
    
    public JobDialog(Frame parent, Job job) {
        super(parent, job == null ? "Add Job" : "Edit Job", true);
        this.jobDAO = new JobDAO();
        this.customerDAO = new CustomerDAO();
        this.employeeDAO = new EmployeeDAO();
        this.photoDAO = new PhotoDAO();
        this.job = job;
        this.saved = false;
        this.jobPhotos = new ArrayList<>();
        
        initializeComponents();
        layoutComponents();
        loadCustomers();
        loadEmployees();
        
        if (job != null) {
            populateFields();
            loadPhotos();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        customerCombo = new JComboBox<>();
        employeeCombo = new JComboBox<>();
        descriptionArea = new JTextArea(DESC_ROWS, DESC_COLS);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        statusCombo = new JComboBox<>(Job.Status.values());
        startDateField = new JTextField(DATE_FIELD_COLS);
        dueDateField = new JTextField(DATE_FIELD_COLS);
        estimatedLaborCostField = new JTextField(DATE_FIELD_COLS);
        estimatedMaterialCostField = new JTextField(DATE_FIELD_COLS);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Create main panel with form and photos side by side
        JPanel mainPanel = new JPanel(new BorderLayout(10, 0));
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        
        // Only show photo panel for existing jobs
        if (job != null && job.getJobId() != null) {
            mainPanel.add(createPhotoPanel(), BorderLayout.EAST);
        }
        
        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addFormRow(panel, gbc, 0, "Customer:", customerCombo);
        addFormRow(panel, gbc, 1, "Assigned To:", employeeCombo);
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        addFormRow(panel, gbc, 3, "Status:", statusCombo);
        addFormRow(panel, gbc, 4, "Start Date:", createDatePanel(startDateField));
        addFormRow(panel, gbc, 5, "Due Date:", createDatePanel(dueDateField));
        addFormRow(panel, gbc, 6, "Est. Labor Cost:", estimatedLaborCostField);
        addFormRow(panel, gbc, 7, "Est. Material Cost:", estimatedMaterialCostField);
        
        return panel;
    }
    
    private JPanel createDatePanel(JTextField dateField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(dateField);
        panel.add(new JLabel(DATE_FORMAT_HINT));
        return panel;
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.findAll();
            customerCombo.removeAllItems();
            for (Customer c : customers) {
                customerCombo.addItem(new CustomerItem(c.getCustomerId(), c.getName()));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading customers: " + e.getMessage());
        }
    }
    
    private void loadEmployees() {
        try {
            List<Employee> employees = employeeDAO.findAll();
            employeeCombo.removeAllItems();
            employeeCombo.addItem(new EmployeeItem(null, "(None)"));
            for (Employee e : employees) {
                employeeCombo.addItem(new EmployeeItem(e.getEmployeeId(), e.getName()));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading employees: " + e.getMessage());
        }
    }
    
    private void populateFields() {
        for (int i = 0; i < customerCombo.getItemCount(); i++) {
            CustomerItem item = customerCombo.getItemAt(i);
            if (item.id.equals(job.getCustomerId())) {
                customerCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Set employee selection
        if (job.getEmployeeId() != null) {
            for (int i = 0; i < employeeCombo.getItemCount(); i++) {
                EmployeeItem item = employeeCombo.getItemAt(i);
                if (item.id != null && item.id.equals(job.getEmployeeId())) {
                    employeeCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            employeeCombo.setSelectedIndex(0); // Select "(None)"
        }
        
        descriptionArea.setText(job.getDescription());
        statusCombo.setSelectedItem(job.getStatus());
        
        if (job.getStartDate() != null) {
            startDateField.setText(job.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (job.getDueDate() != null) {
            dueDateField.setText(job.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (job.getEstimatedLaborCost() != null) {
            estimatedLaborCostField.setText(job.getEstimatedLaborCost().toString());
        }
        if (job.getEstimatedMaterialCost() != null) {
            estimatedMaterialCostField.setText(job.getEstimatedMaterialCost().toString());
        }
    }
    
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            CustomerItem selectedCustomer = (CustomerItem) customerCombo.getSelectedItem();
            LocalDate startDate = parseDateField(startDateField);
            LocalDate dueDate = parseDateField(dueDateField);
            
            if (job == null) {
                createNewJob(selectedCustomer.id, startDate, dueDate);
            } else {
                updateExistingJob(selectedCustomer.id, startDate, dueDate);
            }
            
            saved = true;
            dispose();
        } catch (Exception e) {
            showErrorDialog("Error saving job: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (customerCombo.getSelectedItem() == null) {
            showErrorDialog("Customer is required.");
            customerCombo.requestFocus();
            return false;
        }
        
        if (descriptionArea.getText().trim().isEmpty()) {
            showErrorDialog("Description is required.");
            descriptionArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private LocalDate parseDateField(JTextField field) throws DateTimeParseException {
        String text = field.getText().trim();
        return text.isEmpty() ? null : LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    private void createNewJob(Integer customerId, LocalDate startDate, LocalDate dueDate) throws Exception {
        EmployeeItem selectedEmployee = (EmployeeItem) employeeCombo.getSelectedItem();
        
        Job newJob = new Job(
            customerId,
            null,
            descriptionArea.getText().trim(),
            startDate,
            dueDate,
            (Job.Status) statusCombo.getSelectedItem(),
            parseEstimatedLaborCost(),
            parseEstimatedMaterialCost()
        );
        newJob.setEmployeeId(selectedEmployee != null ? selectedEmployee.id : null);
        jobDAO.create(newJob);
    }
    
    private void updateExistingJob(Integer customerId, LocalDate startDate, LocalDate dueDate) throws Exception {
        EmployeeItem selectedEmployee = (EmployeeItem) employeeCombo.getSelectedItem();
        
        job.setCustomerId(customerId);
        job.setEmployeeId(selectedEmployee != null ? selectedEmployee.id : null);
        job.setDescription(descriptionArea.getText().trim());
        job.setStartDate(startDate);
        job.setDueDate(dueDate);
        job.setStatus((Job.Status) statusCombo.getSelectedItem());
        job.setEstimatedLaborCost(parseEstimatedLaborCost());
        job.setEstimatedMaterialCost(parseEstimatedMaterialCost());
        jobDAO.update(job);
    }
    
    private java.math.BigDecimal parseEstimatedLaborCost() {
        String text = estimatedLaborCostField.getText().trim();
        if (text.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        try {
            return new java.math.BigDecimal(text);
        } catch (NumberFormatException e) {
            return java.math.BigDecimal.ZERO;
        }
    }
    
    private java.math.BigDecimal parseEstimatedMaterialCost() {
        String text = estimatedMaterialCostField.getText().trim();
        if (text.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        try {
            return new java.math.BigDecimal(text);
        } catch (NumberFormatException e) {
            return java.math.BigDecimal.ZERO;
        }
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean wasSaved() {
        return saved;
    }
    
    /**
     * Creates the photo management panel
     */
    private JPanel createPhotoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Job Photos"));
        panel.setPreferredSize(new Dimension(250, 400));
        
        // Thumbnail display area
        photoThumbnailPanel = new JPanel();
        photoThumbnailPanel.setLayout(new BoxLayout(photoThumbnailPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(photoThumbnailPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton uploadButton = new JButton("Upload Photo");
        uploadButton.addActionListener(e -> handlePhotoUpload());
        buttonPanel.add(uploadButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Loads photos for the current job
     */
    private void loadPhotos() {
        if (job == null || job.getJobId() == null) {
            return;
        }
        
        jobPhotos = photoDAO.findByJobId(job.getJobId());
        refreshPhotoThumbnails();
    }
    
    /**
     * Refreshes the photo thumbnail display
     */
    private void refreshPhotoThumbnails() {
        if (photoThumbnailPanel == null) {
            return;
        }
        
        photoThumbnailPanel.removeAll();
        
        for (Photo photo : jobPhotos) {
            JPanel thumbPanel = createThumbnailPanel(photo);
            photoThumbnailPanel.add(thumbPanel);
            photoThumbnailPanel.add(Box.createVerticalStrut(5));
        }
        
        photoThumbnailPanel.revalidate();
        photoThumbnailPanel.repaint();
    }
    
    /**
     * Creates a thumbnail panel for a photo
     */
    private JPanel createThumbnailPanel(Photo photo) {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setMaximumSize(new Dimension(220, 180));
        
        // Load and scale image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 150));
        
        try {
            File imageFile = PhotoStorageUtil.getPhotoFile(photo.getFilePath());
            if (imageFile != null && imageFile.exists()) {
                BufferedImage img = ImageIO.read(imageFile);
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    imageLabel.setText("Image not found");
                }
            } else {
                imageLabel.setText("File not found");
            }
        } catch (Exception e) {
            imageLabel.setText("Error loading image");
        }
        
        // Click to view full size
        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                viewFullSizePhoto(photo);
            }
        });
        
        panel.add(imageLabel, BorderLayout.CENTER);
        
        // Info and delete button
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(photo.getDisplayName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(10f));
        infoPanel.add(nameLabel, BorderLayout.CENTER);
        
        JButton deleteBtn = new JButton("✕");
        deleteBtn.setFont(deleteBtn.getFont().deriveFont(10f));
        deleteBtn.setMargin(new Insets(2, 5, 2, 5));
        deleteBtn.addActionListener(e -> handlePhotoDelete(photo));
        infoPanel.add(deleteBtn, BorderLayout.EAST);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Handles photo upload
     */
    private void handlePhotoUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select Photo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Ask for description
            String description = JOptionPane.showInputDialog(this, 
                "Enter photo description (optional):", 
                "Photo Description", 
                JOptionPane.PLAIN_MESSAGE);
            
            // Save photo
            String relativePath = PhotoStorageUtil.savePhoto(selectedFile, job.getJobId(), description);
            if (relativePath != null) {
                // Create database record
                Photo photo = new Photo(job.getJobId(), relativePath, description);
                Photo savedPhoto = photoDAO.create(photo);
                
                if (savedPhoto != null) {
                    jobPhotos.add(savedPhoto);
                    refreshPhotoThumbnails();
                    JOptionPane.showMessageDialog(this, "Photo uploaded successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to save photo to database", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save photo file. Check file format and size.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handles photo deletion
     */
    private void handlePhotoDelete(Photo photo) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete this photo?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from database
            if (photoDAO.delete(photo.getPhotoId())) {
                // Delete file
                PhotoStorageUtil.deletePhoto(photo.getFilePath());
                
                // Remove from list and refresh
                jobPhotos.remove(photo);
                refreshPhotoThumbnails();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete photo", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Views a photo in full size
     */
    private void viewFullSizePhoto(Photo photo) {
        PhotoViewerDialog viewer = new PhotoViewerDialog(this, photo);
        viewer.setVisible(true);
    }
}
