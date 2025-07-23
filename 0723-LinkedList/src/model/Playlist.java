package model;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Playlist class manages a collection of songs using LinkedList.
 * Provides functionality for adding/removing songs, navigation (next/previous),
 * and tracking the currently playing song.
 */
public class Playlist {
    private LinkedList<Song> songs;
    private int currentIndex;
    private boolean isLooping;
    
    /**
     * Constructor creates an empty playlist.
     */
    public Playlist() {
        this.songs = new LinkedList<>();
        this.currentIndex = -1;
        this.isLooping = true; // Default to loop mode
    }
    
    /**
     * Adds a song to the end of the playlist.
     * @param song The song to add
     * @return true if successfully added
     */
    public boolean addSong(Song song) {
        if (song == null) {
            return false;
        }
        
        boolean added = songs.add(song);
        
        // If this is the first song, set it as current
        if (added && songs.size() == 1) {
            currentIndex = 0;
        }
        
        return added;
    }
    
    /**
     * Adds a song at a specific position in the playlist.
     * @param index The position to insert the song
     * @param song The song to add
     * @return true if successfully added
     */
    public boolean addSong(int index, Song song) {
        if (song == null || index < 0 || index > songs.size()) {
            return false;
        }
        
        songs.add(index, song);
        
        // Adjust current index if needed
        if (index <= currentIndex) {
            currentIndex++;
        } else if (songs.size() == 1) {
            currentIndex = 0;
        }
        
        return true;
    }
    
    /**
     * Removes a song from the playlist by object reference.
     * @param song The song to remove
     * @return true if successfully removed
     */
    public boolean removeSong(Song song) {
        if (song == null) {
            return false;
        }
        
        int index = songs.indexOf(song);
        if (index != -1) {
            return removeSong(index);
        }
        return false;
    }
    
    /**
     * Removes a song from the playlist by index.
     * @param index The index of the song to remove
     * @return true if successfully removed
     */
    public boolean removeSong(int index) {
        if (index < 0 || index >= songs.size()) {
            return false;
        }
        
        songs.remove(index);
        
        // Adjust current index after removal
        if (songs.isEmpty()) {
            currentIndex = -1;
        } else if (index < currentIndex) {
            currentIndex--;
        } else if (index == currentIndex) {
            // Current song was removed
            if (currentIndex >= songs.size()) {
                currentIndex = songs.size() - 1;
            }
            // If current index becomes -1, it means playlist is empty
        }
        
        return true;
    }
    
    /**
     * Moves to the next song in the playlist.
     * @return true if successfully moved to next song
     */
    public boolean playNext() {
        if (songs.isEmpty()) {
            return false;
        }
        
        if (currentIndex < songs.size() - 1) {
            currentIndex++;
            return true;
        } else if (isLooping) {
            currentIndex = 0; // Loop back to first song
            return true;
        }
        
        return false; // No next song and not looping
    }
    
    /**
     * Moves to the previous song in the playlist.
     * @return true if successfully moved to previous song
     */
    public boolean playPrevious() {
        if (songs.isEmpty()) {
            return false;
        }
        
        if (currentIndex > 0) {
            currentIndex--;
            return true;
        } else if (isLooping) {
            currentIndex = songs.size() - 1; // Loop back to last song
            return true;
        }
        
        return false; // No previous song and not looping
    }
    
    /**
     * Sets the current playing song by index.
     * @param index The index of the song to set as current
     * @return true if successfully set
     */
    public boolean setCurrentSong(int index) {
        if (index < 0 || index >= songs.size()) {
            return false;
        }
        
        currentIndex = index;
        return true;
    }
    
    /**
     * Gets the currently playing song.
     * @return The current song, or null if playlist is empty
     */
    public Song getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < songs.size()) {
            return songs.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Gets the current song index.
     * @return The current index, or -1 if no current song
     */
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    /**
     * Gets a song at a specific index.
     * @param index The index of the song
     * @return The song at the index, or null if index is invalid
     */
    public Song getSong(int index) {
        if (index >= 0 && index < songs.size()) {
            return songs.get(index);
        }
        return null;
    }
    
    /**
     * Gets all songs as a list.
     * @return A copy of the songs list
     */
    public List<Song> getAllSongs() {
        return new ArrayList<>(songs);
    }
    
    /**
     * Gets the size of the playlist.
     * @return The number of songs in the playlist
     */
    public int size() {
        return songs.size();
    }
    
    /**
     * Checks if the playlist is empty.
     * @return true if playlist is empty
     */
    public boolean isEmpty() {
        return songs.isEmpty();
    }
    
    /**
     * Clears all songs from the playlist.
     */
    public void clear() {
        songs.clear();
        currentIndex = -1;
    }
    
    /**
     * Sets the looping mode.
     * @param looping true to enable looping, false to disable
     */
    public void setLooping(boolean looping) {
        this.isLooping = looping;
    }
    
    /**
     * Checks if looping is enabled.
     * @return true if looping is enabled
     */
    public boolean isLooping() {
        return isLooping;
    }
    
    /**
     * Checks if there is a next song available.
     * @return true if there is a next song or looping is enabled
     */
    public boolean hasNext() {
        if (songs.isEmpty()) {
            return false;
        }
        return currentIndex < songs.size() - 1 || isLooping;
    }
    
    /**
     * Checks if there is a previous song available.
     * @return true if there is a previous song or looping is enabled
     */
    public boolean hasPrevious() {
        if (songs.isEmpty()) {
            return false;
        }
        return currentIndex > 0 || isLooping;
    }
    
    /**
     * Gets a formatted display string for the playlist.
     * @return A string representation of the playlist
     */
    public String getPlaylistDisplay() {
        if (songs.isEmpty()) {
            return "Playlist is empty";
        }
        
        StringBuilder display = new StringBuilder();
        display.append("Playlist (").append(songs.size()).append(" songs):\n");
        
        for (int i = 0; i < songs.size(); i++) {
            if (i == currentIndex) {
                display.append("► "); // Current song indicator
            } else {
                display.append("  ");
            }
            display.append(i + 1).append(". ");
            display.append(songs.get(i).getDisplayName());
            display.append(" (").append(songs.get(i).getFormat().toUpperCase()).append(")");
            if (i < songs.size() - 1) {
                display.append("\n");
            }
        }
        
        return display.toString();
    }
    
    @Override
    public String toString() {
        return "Playlist{" +
                "size=" + songs.size() +
                ", currentIndex=" + currentIndex +
                ", isLooping=" + isLooping +
                '}';
    }
}