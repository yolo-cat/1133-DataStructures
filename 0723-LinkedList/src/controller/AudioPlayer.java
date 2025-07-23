package controller;

import model.Song;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * AudioPlayer class manages audio playback functionality.
 * Encapsulates Java Sound API for playing audio files.
 * Supports basic playback controls: play, pause, stop.
 */
public class AudioPlayer {
    private Clip audioClip;
    private AudioInputStream audioStream;
    private boolean isPaused;
    private boolean isPlaying;
    private long pausePosition;
    private Song currentSong;
    private PlaybackListener listener;
    
    /**
     * Interface for playback event notifications.
     */
    public interface PlaybackListener {
        void onPlaybackStarted(Song song);
        void onPlaybackPaused(Song song);
        void onPlaybackStopped(Song song);
        void onPlaybackCompleted(Song song);
        void onPlaybackError(Song song, String error);
    }
    
    /**
     * Constructor creates a new AudioPlayer.
     */
    public AudioPlayer() {
        this.isPaused = false;
        this.isPlaying = false;
        this.pausePosition = 0;
    }
    
    /**
     * Sets the playback listener for event notifications.
     * @param listener The listener to set
     */
    public void setPlaybackListener(PlaybackListener listener) {
        this.listener = listener;
    }
    
    /**
     * Plays a song from the beginning.
     * @param song The song to play
     * @return true if playback started successfully
     */
    public boolean play(Song song) {
        if (song == null) {
            notifyError(song, "Song is null");
            return false;
        }
        
        if (!song.isValidFile()) {
            notifyError(song, "File does not exist or is not readable: " + song.getFilePath());
            return false;
        }
        
        if (!song.isSupportedFormat()) {
            notifyError(song, "Unsupported audio format: " + song.getFormat());
            return false;
        }
        
        // Stop current playback if any
        stop();
        
        try {
            File audioFile = new File(song.getFilePath());
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            
            // Add listener for playback completion
            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && !isPaused) {
                    // Playback completed naturally
                    isPlaying = false;
                    notifyCompleted(currentSong);
                }
            });
            
            audioClip.start();
            isPlaying = true;
            isPaused = false;
            pausePosition = 0;
            currentSong = song;
            
            notifyStarted(song);
            return true;
            
        } catch (UnsupportedAudioFileException e) {
            notifyError(song, "Unsupported audio file format: " + e.getMessage());
        } catch (IOException e) {
            notifyError(song, "Error reading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            notifyError(song, "Audio line unavailable: " + e.getMessage());
        } catch (Exception e) {
            notifyError(song, "Unexpected error: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pauses the current playback.
     * @return true if successfully paused
     */
    public boolean pause() {
        if (audioClip != null && isPlaying && !isPaused) {
            pausePosition = audioClip.getMicrosecondPosition();
            audioClip.stop();
            isPaused = true;
            isPlaying = false;
            
            notifyPaused(currentSong);
            return true;
        }
        return false;
    }
    
    /**
     * Resumes playback from the paused position.
     * @return true if successfully resumed
     */
    public boolean resume() {
        if (audioClip != null && isPaused) {
            audioClip.setMicrosecondPosition(pausePosition);
            audioClip.start();
            isPaused = false;
            isPlaying = true;
            
            notifyStarted(currentSong);
            return true;
        }
        return false;
    }
    
    /**
     * Stops the current playback.
     * @return true if successfully stopped
     */
    public boolean stop() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
            audioClip = null;
        }
        
        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                // Ignore close errors
            }
            audioStream = null;
        }
        
        boolean wasPlaying = isPlaying || isPaused;
        isPlaying = false;
        isPaused = false;
        pausePosition = 0;
        
        if (wasPlaying) {
            notifyStopped(currentSong);
        }
        
        return true;
    }
    
    /**
     * Checks if audio is currently playing.
     * @return true if playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * Checks if audio is currently paused.
     * @return true if paused
     */
    public boolean isPaused() {
        return isPaused;
    }
    
    /**
     * Gets the currently loaded song.
     * @return The current song, or null if none
     */
    public Song getCurrentSong() {
        return currentSong;
    }
    
    /**
     * Gets the current playback position in microseconds.
     * @return Current position, or 0 if not playing
     */
    public long getCurrentPosition() {
        if (audioClip != null) {
            return isPaused ? pausePosition : audioClip.getMicrosecondPosition();
        }
        return 0;
    }
    
    /**
     * Gets the total duration of the current song in microseconds.
     * @return Total duration, or 0 if no song loaded
     */
    public long getTotalDuration() {
        if (audioClip != null) {
            return audioClip.getMicrosecondLength();
        }
        return 0;
    }
    
    /**
     * Checks if the audio system supports the given audio format.
     * @param song The song to check
     * @return true if format is supported
     */
    public static boolean isFormatSupported(Song song) {
        if (song == null || !song.isValidFile()) {
            return false;
        }
        
        try {
            File audioFile = new File(song.getFilePath());
            AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
            stream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Notification helper methods
    private void notifyStarted(Song song) {
        if (listener != null) {
            listener.onPlaybackStarted(song);
        }
    }
    
    private void notifyPaused(Song song) {
        if (listener != null) {
            listener.onPlaybackPaused(song);
        }
    }
    
    private void notifyStopped(Song song) {
        if (listener != null) {
            listener.onPlaybackStopped(song);
        }
    }
    
    private void notifyCompleted(Song song) {
        if (listener != null) {
            listener.onPlaybackCompleted(song);
        }
    }
    
    private void notifyError(Song song, String error) {
        if (listener != null) {
            listener.onPlaybackError(song, error);
        }
    }
    
    /**
     * Cleanup resources when the player is no longer needed.
     */
    public void dispose() {
        stop();
        listener = null;
        currentSong = null;
    }
}