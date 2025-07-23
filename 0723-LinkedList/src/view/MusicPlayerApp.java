package view;

import controller.MusicPlayerController;
import model.Song;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * MusicPlayerApp is the main GUI application for the music player.
 * Provides a user-friendly interface for playlist management and playback control.
 */
public class MusicPlayerApp extends JFrame implements MusicPlayerController.PlayerEventListener {
    private MusicPlayerController controller;
    
    // GUI Components
    private DefaultListModel<String> playlistModel;
    private JList<String> playlistDisplay;
    private JLabel currentSongLabel;
    private JLabel statusLabel;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton nextButton;
    private JButton previousButton;
    private JButton addButton;
    private JButton removeButton;
    private JCheckBox loopCheckBox;
    
    /**
     * Constructor creates and initializes the music player application.
     */
    public MusicPlayerApp() {
        controller = new MusicPlayerController();
        controller.setEventListener(this);
        
        initializeGUI();
        updateControlsState();
    }
    
    /**
     * Initializes the GUI components and layout.
     */
    private void initializeGUI() {
        setTitle("Music Player - LinkedList Implementation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Create main layout
        setLayout(new BorderLayout());
        
        // Create top panel for playlist management
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Create center panel for playlist display
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Create bottom panel for playback controls
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the top panel with playlist management buttons.
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add Song");
        addButton.addActionListener(e -> addSong());
        buttonPanel.add(addButton);
        
        removeButton = new JButton("Remove Song");
        removeButton.addActionListener(e -> removeSong());
        buttonPanel.add(removeButton);
        
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearPlaylist());
        buttonPanel.add(clearButton);
        
        loopCheckBox = new JCheckBox("Loop Playlist", true);
        loopCheckBox.addActionListener(e -> controller.setLooping(loopCheckBox.isSelected()));
        buttonPanel.add(loopCheckBox);
        
        panel.add(buttonPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * Creates the center panel with playlist display.
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Playlist display
        playlistModel = new DefaultListModel<>();
        playlistDisplay = new JList<>(playlistModel);
        playlistDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistDisplay.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = playlistDisplay.getSelectedIndex();
                if (selectedIndex >= 0) {
                    controller.selectSong(selectedIndex, false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(playlistDisplay);
        scrollPane.setPreferredSize(new Dimension(550, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Current song display
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Current Song"));
        
        currentSongLabel = new JLabel("No song selected");
        currentSongLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentSongLabel.setFont(currentSongLabel.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(currentSongLabel, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the bottom panel with playback controls.
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        previousButton = new JButton("⏮ Previous");
        previousButton.addActionListener(e -> controller.playPrevious());
        controlPanel.add(previousButton);
        
        playButton = new JButton("▶ Play");
        playButton.addActionListener(e -> controller.play());
        controlPanel.add(playButton);
        
        pauseButton = new JButton("⏸ Pause");
        pauseButton.addActionListener(e -> controller.pause());
        controlPanel.add(pauseButton);
        
        stopButton = new JButton("⏹ Stop");
        stopButton.addActionListener(e -> controller.stop());
        controlPanel.add(stopButton);
        
        nextButton = new JButton("⏭ Next");
        nextButton.addActionListener(e -> controller.playNext());
        controlPanel.add(nextButton);
        
        panel.add(controlPanel, BorderLayout.CENTER);
        
        // Status display
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Handles adding a new song to the playlist.
     */
    private void addSong() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".mp3") || name.endsWith(".wav") || 
                       name.endsWith(".au") || name.endsWith(".aiff");
            }
            
            @Override
            public String getDescription() {
                return "Audio Files (*.mp3, *.wav, *.au, *.aiff)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            controller.addSong(filePath);
        }
    }
    
    /**
     * Handles removing the selected song from the playlist.
     */
    private void removeSong() {
        int selectedIndex = playlistDisplay.getSelectedIndex();
        if (selectedIndex >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this song?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.removeSong(selectedIndex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a song to remove.");
        }
    }
    
    /**
     * Handles clearing the entire playlist.
     */
    private void clearPlaylist() {
        if (controller.getPlaylist().size() > 0) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the entire playlist?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                controller.clearPlaylist();
            }
        }
    }
    
    /**
     * Updates the state of control buttons based on player state.
     */
    private void updateControlsState() {
        boolean hasPlaylist = controller.getPlaylist().size() > 0;
        boolean isPlaying = controller.isPlaying();
        boolean isPaused = controller.isPaused();
        
        playButton.setEnabled(hasPlaylist && !isPlaying);
        pauseButton.setEnabled(isPlaying);
        stopButton.setEnabled(isPlaying || isPaused);
        nextButton.setEnabled(hasPlaylist && controller.getPlaylist().hasNext());
        previousButton.setEnabled(hasPlaylist && controller.getPlaylist().hasPrevious());
        removeButton.setEnabled(hasPlaylist && playlistDisplay.getSelectedIndex() >= 0);
    }
    
    /**
     * Updates the playlist display with current songs.
     */
    private void updatePlaylistDisplay() {
        playlistModel.clear();
        List<Song> songs = controller.getPlaylist().getAllSongs();
        int currentIndex = controller.getPlaylist().getCurrentIndex();
        
        for (int i = 0; i < songs.size(); i++) {
            String display = (i + 1) + ". " + songs.get(i).getDisplayName();
            if (i == currentIndex) {
                display = "► " + display;
            }
            playlistModel.addElement(display);
        }
        
        // Update selection
        if (currentIndex >= 0 && currentIndex < playlistModel.size()) {
            playlistDisplay.setSelectedIndex(currentIndex);
        }
    }
    
    // MusicPlayerController.PlayerEventListener implementation
    @Override
    public void onSongChanged(Song song, int index) {
        SwingUtilities.invokeLater(() -> {
            if (song != null) {
                currentSongLabel.setText(song.getDisplayName() + " (" + song.getFormat().toUpperCase() + ")");
            } else {
                currentSongLabel.setText("No song selected");
            }
            updatePlaylistDisplay();
            updateControlsState();
        });
    }
    
    @Override
    public void onPlaybackStateChanged(boolean isPlaying, boolean isPaused) {
        SwingUtilities.invokeLater(() -> {
            if (isPlaying) {
                statusLabel.setText("Playing");
            } else if (isPaused) {
                statusLabel.setText("Paused");
            } else {
                statusLabel.setText("Stopped");
            }
            updateControlsState();
        });
    }
    
    @Override
    public void onPlaylistChanged() {
        SwingUtilities.invokeLater(() -> {
            updatePlaylistDisplay();
            updateControlsState();
        });
    }
    
    @Override
    public void onError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error: " + message);
        });
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            new MusicPlayerApp().setVisible(true);
        });
    }
    
    /**
     * Cleanup resources when the application is closed.
     */
    @Override
    public void dispose() {
        if (controller != null) {
            controller.dispose();
        }
        super.dispose();
    }
}