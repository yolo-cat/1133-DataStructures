package util;

import model.Song;
import model.Playlist;

/**
 * LinkedListDemo demonstrates the core LinkedList operations used in the music player.
 * Shows how the playlist manipulates the LinkedList for various operations.
 */
public class LinkedListDemo {
    
    public static void main(String[] args) {
        System.out.println("=== LinkedList-based Music Player Demo ===");
        System.out.println("This demo shows how LinkedList is used for playlist management\n");
        
        // Create a playlist (internally uses LinkedList)
        Playlist playlist = new Playlist();
        System.out.println("1. Created empty playlist");
        System.out.println("   isEmpty(): " + playlist.isEmpty());
        System.out.println("   size(): " + playlist.size());
        System.out.println();
        
        // Demonstrate LinkedList add operations
        System.out.println("2. Adding songs to LinkedList (addLast equivalent):");
        addSongAndShow(playlist, "/music/song1.mp3", "Song 1");
        addSongAndShow(playlist, "/music/song2.wav", "Song 2");
        addSongAndShow(playlist, "/music/song3.mp3", "Song 3");
        addSongAndShow(playlist, "/music/song4.au", "Song 4");
        System.out.println();
        
        // Show current state
        System.out.println("3. Current playlist state:");
        System.out.println(playlist.getPlaylistDisplay());
        System.out.println();
        
        // Demonstrate LinkedList navigation (utilizing bidirectional nature)
        System.out.println("4. LinkedList navigation (next/previous pointers):");
        System.out.println("   Current: " + playlist.getCurrentSong().getDisplayName() + " (index: " + playlist.getCurrentIndex() + ")");
        
        playlist.playNext();
        System.out.println("   After next(): " + playlist.getCurrentSong().getDisplayName() + " (index: " + playlist.getCurrentIndex() + ")");
        
        playlist.playNext();
        System.out.println("   After next(): " + playlist.getCurrentSong().getDisplayName() + " (index: " + playlist.getCurrentIndex() + ")");
        
        playlist.playPrevious();
        System.out.println("   After previous(): " + playlist.getCurrentSong().getDisplayName() + " (index: " + playlist.getCurrentIndex() + ")");
        System.out.println();
        
        // Demonstrate LinkedList insertion (add at specific position)
        System.out.println("5. LinkedList insertion at position 1:");
        Song insertSong = new Song("/music/inserted.mp3", "Inserted Song");
        playlist.addSong(1, insertSong);
        System.out.println(playlist.getPlaylistDisplay());
        System.out.println();
        
        // Demonstrate LinkedList removal by index
        System.out.println("6. LinkedList removal by index (remove at position 2):");
        playlist.removeSong(2);
        System.out.println(playlist.getPlaylistDisplay());
        System.out.println();
        
        // Demonstrate LinkedList removal by object
        System.out.println("7. LinkedList removal by object reference:");
        Song toRemove = playlist.getSong(1);
        System.out.println("   Removing: " + toRemove.getDisplayName());
        playlist.removeSong(toRemove);
        System.out.println(playlist.getPlaylistDisplay());
        System.out.println();
        
        // Demonstrate LinkedList looping behavior
        System.out.println("8. LinkedList looping navigation:");
        // Move to last song
        while (playlist.getCurrentIndex() < playlist.size() - 1) {
            playlist.playNext();
        }
        System.out.println("   At last song: " + playlist.getCurrentSong().getDisplayName());
        
        // Test loop to first
        playlist.playNext();
        System.out.println("   After next() with loop: " + playlist.getCurrentSong().getDisplayName() + " (looped to beginning)");
        
        // Test loop to last
        playlist.playPrevious();
        System.out.println("   After previous() with loop: " + playlist.getCurrentSong().getDisplayName() + " (looped to end)");
        System.out.println();
        
        // Show LinkedList properties
        System.out.println("9. LinkedList advantages demonstrated:");
        System.out.println("   ✓ O(1) insertion at head/tail");
        System.out.println("   ✓ O(1) removal when position is known");
        System.out.println("   ✓ Dynamic sizing (no fixed capacity)");
        System.out.println("   ✓ Bidirectional navigation");
        System.out.println("   ✓ No memory waste (allocates only needed nodes)");
        System.out.println("   ✓ Easy implementation of circular/loop behavior");
        System.out.println();
        
        // Performance comparison note
        System.out.println("10. Why LinkedList for playlists?");
        System.out.println("    - Music playlists need frequent add/remove operations");
        System.out.println("    - Users often navigate sequentially (next/previous)");
        System.out.println("    - Playlist size is dynamic and unknown");
        System.out.println("    - Order matters and needs to be maintained");
        System.out.println("    - Perfect fit for LinkedList's strengths!");
        
        System.out.println("\n=== Demo Completed ===");
    }
    
    private static void addSongAndShow(Playlist playlist, String path, String name) {
        Song song = new Song(path, name);
        boolean added = playlist.addSong(song);
        System.out.println("   Added: " + name + " -> " + (added ? "SUCCESS" : "FAILED"));
        System.out.println("   Size now: " + playlist.size());
    }
}