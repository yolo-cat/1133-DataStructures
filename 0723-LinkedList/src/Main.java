import view.MusicPlayerApp;

/**
 * Main class - Entry point for the Music Player application.
 * This music player demonstrates the use of LinkedList for playlist management
 * and provides a complete GUI interface for audio playback.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Music Player Application...");
        System.out.println("Features:");
        System.out.println("- LinkedList-based playlist management");
        System.out.println("- Add/Remove songs functionality");
        System.out.println("- Play, Pause, Stop, Next, Previous controls");
        System.out.println("- Support for MP3, WAV, AU, AIFF formats");
        System.out.println("- Playlist looping mode");
        
        // Launch the GUI application
        MusicPlayerApp.main(args);
    }
}
