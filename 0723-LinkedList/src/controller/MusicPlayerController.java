package controller;

import model.Song;
import model.Playlist;

/**
 * MusicPlayerController manages the interaction between the playlist and audio player.
 * Provides high-level controls for the music player functionality.
 */
public class MusicPlayerController implements AudioPlayer.PlaybackListener {
    private Playlist playlist;
    private AudioPlayer audioPlayer;
    private PlayerEventListener eventListener;
    
    /**
     * Interface for notifying UI about player events.
     */
    public interface PlayerEventListener {
        void onSongChanged(Song song, int index);
        void onPlaybackStateChanged(boolean isPlaying, boolean isPaused);
        void onPlaylistChanged();
        void onError(String message);
    }
    
    /**
     * Constructor creates a new music player controller.
     */
    public MusicPlayerController() {
        this.playlist = new Playlist();
        this.audioPlayer = new AudioPlayer();
        this.audioPlayer.setPlaybackListener(this);
    }
    
    /**
     * Sets the event listener for UI notifications.
     * @param listener The listener to set
     */
    public void setEventListener(PlayerEventListener listener) {
        this.eventListener = listener;
    }
    
    /**
     * Adds a song to the playlist.
     * @param filePath The path to the song file
     * @return true if successfully added
     */
    public boolean addSong(String filePath) {
        try {
            Song song = new Song(filePath);
            boolean added = playlist.addSong(song);
            if (added) {
                notifyPlaylistChanged();
            }
            return added;
        } catch (Exception e) {
            notifyError("Failed to add song: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Removes a song from the playlist by index.
     * @param index The index of the song to remove
     * @return true if successfully removed
     */
    public boolean removeSong(int index) {
        boolean removed = playlist.removeSong(index);
        if (removed) {
            // If current song was removed and audio is playing, stop it
            if (audioPlayer.isPlaying() || audioPlayer.isPaused()) {
                Song currentAudioSong = audioPlayer.getCurrentSong();
                Song currentPlaylistSong = playlist.getCurrentSong();
                
                if (currentAudioSong != null && 
                    (currentPlaylistSong == null || !currentAudioSong.equals(currentPlaylistSong))) {
                    audioPlayer.stop();
                }
            }
            
            notifyPlaylistChanged();
            notifySongChanged();
        }
        return removed;
    }
    
    /**
     * Plays the current song in the playlist.
     * @return true if playback started successfully
     */
    public boolean play() {
        Song currentSong = playlist.getCurrentSong();
        if (currentSong == null) {
            notifyError("No song selected to play");
            return false;
        }
        
        // If the same song is paused, resume it
        if (audioPlayer.isPaused() && 
            audioPlayer.getCurrentSong() != null && 
            audioPlayer.getCurrentSong().equals(currentSong)) {
            return audioPlayer.resume();
        }
        
        // Otherwise, play the current song from the beginning
        return audioPlayer.play(currentSong);
    }
    
    /**
     * Pauses the current playback.
     * @return true if successfully paused
     */
    public boolean pause() {
        return audioPlayer.pause();
    }
    
    /**
     * Stops the current playback.
     * @return true if successfully stopped
     */
    public boolean stop() {
        return audioPlayer.stop();
    }
    
    /**
     * Moves to and plays the next song.
     * @return true if successfully moved to next song
     */
    public boolean playNext() {
        if (playlist.playNext()) {
            notifySongChanged();
            return play();
        }
        return false;
    }
    
    /**
     * Moves to and plays the previous song.
     * @return true if successfully moved to previous song
     */
    public boolean playPrevious() {
        if (playlist.playPrevious()) {
            notifySongChanged();
            return play();
        }
        return false;
    }
    
    /**
     * Sets the current song by index and optionally plays it.
     * @param index The index of the song to select
     * @param autoPlay Whether to automatically start playing
     * @return true if successfully selected
     */
    public boolean selectSong(int index, boolean autoPlay) {
        if (playlist.setCurrentSong(index)) {
            notifySongChanged();
            if (autoPlay) {
                return play();
            }
            return true;
        }
        return false;
    }
    
    /**
     * Gets the current playlist.
     * @return The playlist
     */
    public Playlist getPlaylist() {
        return playlist;
    }
    
    /**
     * Gets the audio player.
     * @return The audio player
     */
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
    
    /**
     * Checks if audio is currently playing.
     * @return true if playing
     */
    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }
    
    /**
     * Checks if audio is currently paused.
     * @return true if paused
     */
    public boolean isPaused() {
        return audioPlayer.isPaused();
    }
    
    /**
     * Sets the playlist looping mode.
     * @param looping true to enable looping
     */
    public void setLooping(boolean looping) {
        playlist.setLooping(looping);
    }
    
    /**
     * Checks if looping is enabled.
     * @return true if looping is enabled
     */
    public boolean isLooping() {
        return playlist.isLooping();
    }
    
    /**
     * Clears the entire playlist.
     */
    public void clearPlaylist() {
        audioPlayer.stop();
        playlist.clear();
        notifyPlaylistChanged();
        notifySongChanged();
    }
    
    // AudioPlayer.PlaybackListener implementation
    @Override
    public void onPlaybackStarted(Song song) {
        notifyPlaybackStateChanged();
    }
    
    @Override
    public void onPlaybackPaused(Song song) {
        notifyPlaybackStateChanged();
    }
    
    @Override
    public void onPlaybackStopped(Song song) {
        notifyPlaybackStateChanged();
    }
    
    @Override
    public void onPlaybackCompleted(Song song) {
        // Automatically play next song when current song completes
        if (playlist.hasNext()) {
            playNext();
        } else {
            notifyPlaybackStateChanged();
        }
    }
    
    @Override
    public void onPlaybackError(Song song, String error) {
        notifyError("Playback error: " + error);
        notifyPlaybackStateChanged();
    }
    
    // Notification helper methods
    private void notifySongChanged() {
        if (eventListener != null) {
            Song currentSong = playlist.getCurrentSong();
            int currentIndex = playlist.getCurrentIndex();
            eventListener.onSongChanged(currentSong, currentIndex);
        }
    }
    
    private void notifyPlaybackStateChanged() {
        if (eventListener != null) {
            eventListener.onPlaybackStateChanged(audioPlayer.isPlaying(), audioPlayer.isPaused());
        }
    }
    
    private void notifyPlaylistChanged() {
        if (eventListener != null) {
            eventListener.onPlaylistChanged();
        }
    }
    
    private void notifyError(String message) {
        if (eventListener != null) {
            eventListener.onError(message);
        }
    }
    
    /**
     * Cleanup resources when the controller is no longer needed.
     */
    public void dispose() {
        audioPlayer.dispose();
        eventListener = null;
    }
}