package util;

import model.Song;
import model.Playlist;
import controller.MusicPlayerController;

/**
 * SimpleTest provides basic unit tests for the music player core functionality.
 * Tests the LinkedList-based playlist management and song operations.
 */
public class SimpleTest {
    
    public static void main(String[] args) {
        System.out.println("=== Music Player Core Functionality Tests ===");
        System.out.println();
        
        runSongTests();
        runPlaylistTests();
        runControllerTests();
        
        System.out.println("=== All Tests Completed ===");
    }
    
    private static void runSongTests() {
        System.out.println("1. Testing Song Class:");
        
        try {
            // Test Song creation
            Song song1 = new Song("/test/music.mp3", "Test Song");
            System.out.println("✓ Song creation successful");
            System.out.println("  Display name: " + song1.getDisplayName());
            System.out.println("  Format: " + song1.getFormat());
            System.out.println("  File name: " + song1.getFileName());
            
            // Test Song equality
            Song song2 = new Song("/test/music.mp3", "Different Name");
            System.out.println("✓ Song equality test: " + (song1.equals(song2) ? "PASS" : "FAIL"));
            
            // Test supported format check
            System.out.println("✓ Format supported: " + song1.isSupportedFormat());
            
        } catch (Exception e) {
            System.out.println("✗ Song test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void runPlaylistTests() {
        System.out.println("2. Testing Playlist Class (LinkedList):");
        
        try {
            Playlist playlist = new Playlist();
            System.out.println("✓ Playlist created, empty: " + playlist.isEmpty());
            
            // Add songs
            Song song1 = new Song("/test/song1.mp3", "Song 1");
            Song song2 = new Song("/test/song2.wav", "Song 2");
            Song song3 = new Song("/test/song3.mp3", "Song 3");
            
            playlist.addSong(song1);
            playlist.addSong(song2);
            playlist.addSong(song3);
            
            System.out.println("✓ Added 3 songs, playlist size: " + playlist.size());
            System.out.println("✓ Current song: " + playlist.getCurrentSong().getDisplayName());
            
            // Test navigation
            playlist.playNext();
            System.out.println("✓ Next song: " + playlist.getCurrentSong().getDisplayName());
            
            playlist.playPrevious();
            System.out.println("✓ Previous song: " + playlist.getCurrentSong().getDisplayName());
            
            // Test looping
            System.out.println("✓ Has next: " + playlist.hasNext());
            System.out.println("✓ Has previous: " + playlist.hasPrevious());
            
            // Test removal
            playlist.removeSong(1);
            System.out.println("✓ Removed song, new size: " + playlist.size());
            
            // Test playlist display
            System.out.println("✓ Playlist display:");
            System.out.println(playlist.getPlaylistDisplay());
            
        } catch (Exception e) {
            System.out.println("✗ Playlist test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void runControllerTests() {
        System.out.println("3. Testing MusicPlayerController:");
        
        try {
            MusicPlayerController controller = new MusicPlayerController();
            
            // Set up event listener
            controller.setEventListener(new MusicPlayerController.PlayerEventListener() {
                @Override
                public void onSongChanged(Song song, int index) {
                    System.out.println("  Event: Song changed to " + 
                                     (song != null ? song.getDisplayName() : "null") + 
                                     " at index " + index);
                }
                
                @Override
                public void onPlaybackStateChanged(boolean isPlaying, boolean isPaused) {
                    System.out.println("  Event: Playback state - Playing: " + isPlaying + 
                                     ", Paused: " + isPaused);
                }
                
                @Override
                public void onPlaylistChanged() {
                    System.out.println("  Event: Playlist changed");
                }
                
                @Override
                public void onError(String message) {
                    System.out.println("  Event: Error - " + message);
                }
            });
            
            System.out.println("✓ Controller created with event listener");
            
            // Test adding songs (will trigger events)
            controller.addSong("/test/song1.mp3");
            controller.addSong("/test/song2.wav");
            
            System.out.println("✓ Added songs to controller");
            System.out.println("✓ Playlist size: " + controller.getPlaylist().size());
            
            // Test navigation
            controller.playNext();
            controller.playPrevious();
            
            // Test playback (will fail due to missing files, but tests the logic)
            controller.play();
            
            System.out.println("✓ Controller tests completed");
            
            controller.dispose();
            
        } catch (Exception e) {
            System.out.println("✗ Controller test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
}