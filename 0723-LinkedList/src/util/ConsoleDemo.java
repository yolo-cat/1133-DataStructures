package util;

import model.Song;
import model.Playlist;
import controller.MusicPlayerController;
import java.util.Scanner;

/**
 * ConsoleDemo provides a console-based demonstration of the music player functionality.
 * This allows testing the core features without requiring a GUI environment.
 */
public class ConsoleDemo {
    private MusicPlayerController controller;
    private Scanner scanner;
    
    public ConsoleDemo() {
        this.controller = new MusicPlayerController();
        this.scanner = new Scanner(System.in);
        
        // Set up event listener for console output
        controller.setEventListener(new MusicPlayerController.PlayerEventListener() {
            @Override
            public void onSongChanged(Song song, int index) {
                if (song != null) {
                    System.out.println("♪ Current song: " + song.getDisplayName() + 
                                     " (" + song.getFormat().toUpperCase() + ")");
                } else {
                    System.out.println("♪ No song selected");
                }
            }
            
            @Override
            public void onPlaybackStateChanged(boolean isPlaying, boolean isPaused) {
                if (isPlaying) {
                    System.out.println("♪ Status: Playing");
                } else if (isPaused) {
                    System.out.println("♪ Status: Paused");
                } else {
                    System.out.println("♪ Status: Stopped");
                }
            }
            
            @Override
            public void onPlaylistChanged() {
                System.out.println("♪ Playlist updated");
            }
            
            @Override
            public void onError(String message) {
                System.out.println("♪ Error: " + message);
            }
        });
    }
    
    public void run() {
        System.out.println("=== Music Player Console Demo ===");
        System.out.println("This demo shows the LinkedList-based playlist functionality");
        System.out.println();
        
        // Add some demo songs (these won't actually play without real audio files)
        addDemoSongs();
        
        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    showPlaylist();
                    break;
                case "2":
                    addSong();
                    break;
                case "3":
                    removeSong();
                    break;
                case "4":
                    playCurrentSong();
                    break;
                case "5":
                    pauseSong();
                    break;
                case "6":
                    stopSong();
                    break;
                case "7":
                    playNext();
                    break;
                case "8":
                    playPrevious();
                    break;
                case "9":
                    selectSong();
                    break;
                case "10":
                    toggleLoop();
                    break;
                case "11":
                    clearPlaylist();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
        
        System.out.println("Demo ended. Thank you!");
        controller.dispose();
    }
    
    private void showMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Show Playlist");
        System.out.println("2. Add Song");
        System.out.println("3. Remove Song");
        System.out.println("4. Play Current Song");
        System.out.println("5. Pause");
        System.out.println("6. Stop");
        System.out.println("7. Next Song");
        System.out.println("8. Previous Song");
        System.out.println("9. Select Song");
        System.out.println("10. Toggle Loop Mode");
        System.out.println("11. Clear Playlist");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void addDemoSongs() {
        System.out.println("Adding demo songs to playlist...");
        
        // Create demo songs (these are fictional files for demonstration)
        String[] demoFiles = {
            "/demo/song1.mp3",
            "/demo/song2.wav", 
            "/demo/song3.mp3",
            "/demo/song4.au"
        };
        
        for (String file : demoFiles) {
            try {
                // Create demo songs with custom names since files don't exist
                Song song = new Song(file, "Demo Song " + (controller.getPlaylist().size() + 1));
                controller.getPlaylist().addSong(song);
            } catch (Exception e) {
                // Expected since demo files don't exist
            }
        }
        
        System.out.println("Demo playlist created with " + controller.getPlaylist().size() + " songs");
    }
    
    private void showPlaylist() {
        Playlist playlist = controller.getPlaylist();
        System.out.println("\n=== Current Playlist ===");
        System.out.println(playlist.getPlaylistDisplay());
        System.out.println("Loop mode: " + (playlist.isLooping() ? "ON" : "OFF"));
    }
    
    private void addSong() {
        System.out.print("Enter song file path: ");
        String filePath = scanner.nextLine().trim();
        
        if (!filePath.isEmpty()) {
            if (controller.addSong(filePath)) {
                System.out.println("Song added successfully!");
            } else {
                System.out.println("Failed to add song. Check if file exists and is a supported format.");
            }
        }
    }
    
    private void removeSong() {
        showPlaylist();
        System.out.print("Enter song number to remove (1-" + controller.getPlaylist().size() + "): ");
        try {
            int songNum = Integer.parseInt(scanner.nextLine().trim());
            if (controller.removeSong(songNum - 1)) {
                System.out.println("Song removed successfully!");
            } else {
                System.out.println("Invalid song number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void playCurrentSong() {
        if (controller.play()) {
            System.out.println("Attempting to play current song...");
        } else {
            System.out.println("Cannot play - no song selected or file not found.");
        }
    }
    
    private void pauseSong() {
        if (controller.pause()) {
            System.out.println("Playback paused.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }
    
    private void stopSong() {
        if (controller.stop()) {
            System.out.println("Playback stopped.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }
    
    private void playNext() {
        if (controller.playNext()) {
            System.out.println("Moving to next song...");
        } else {
            System.out.println("No next song available.");
        }
    }
    
    private void playPrevious() {
        if (controller.playPrevious()) {
            System.out.println("Moving to previous song...");
        } else {
            System.out.println("No previous song available.");
        }
    }
    
    private void selectSong() {
        showPlaylist();
        System.out.print("Enter song number to select (1-" + controller.getPlaylist().size() + "): ");
        try {
            int songNum = Integer.parseInt(scanner.nextLine().trim());
            if (controller.selectSong(songNum - 1, false)) {
                System.out.println("Song selected successfully!");
            } else {
                System.out.println("Invalid song number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void toggleLoop() {
        boolean currentLoop = controller.isLooping();
        controller.setLooping(!currentLoop);
        System.out.println("Loop mode " + (!currentLoop ? "enabled" : "disabled"));
    }
    
    private void clearPlaylist() {
        if (controller.getPlaylist().size() > 0) {
            System.out.print("Are you sure you want to clear the playlist? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y") || confirm.equals("yes")) {
                controller.clearPlaylist();
                System.out.println("Playlist cleared.");
            }
        } else {
            System.out.println("Playlist is already empty.");
        }
    }
    
    public static void main(String[] args) {
        new ConsoleDemo().run();
    }
}