# Music Player - LinkedList Implementation

This is a complete music player application demonstrating the use of **LinkedList** as the core data structure for playlist management. The project implements a full MVC (Model-View-Controller) architecture with audio playback capabilities.

## 📁 Project Structure

```
src/
├── Main.java                     # Application entry point
├── model/                        # Data models
│   ├── Song.java                # Song information encapsulation
│   └── Playlist.java            # LinkedList-based playlist management
├── view/                         # GUI components
│   └── MusicPlayerApp.java     # Main Swing GUI application
├── controller/                   # Business logic
│   ├── AudioPlayer.java        # Audio playback management
│   └── MusicPlayerController.java # Main controller
└── util/                        # Utility classes
    ├── FileUtils.java           # File operation utilities
    ├── ConsoleDemo.java         # Console demonstration
    └── SimpleTest.java          # Basic functionality tests
```

## 🎵 Features

### Core Functionality
- **LinkedList-based Playlist Management**: Add, remove, and navigate through songs
- **Audio Playback**: Support for MP3, WAV, AU, and AIFF formats
- **Playback Controls**: Play, Pause, Stop, Next, Previous
- **Playlist Navigation**: Click to select songs, automatic next song
- **Loop Mode**: Enable/disable playlist looping
- **File Management**: Drag and drop or file picker for adding songs

### Architecture Features
- **MVC Pattern**: Clean separation of concerns
- **Event-Driven Design**: Responsive UI with event notifications
- **Error Handling**: Comprehensive error handling for file operations
- **Memory Management**: Proper resource cleanup and disposal

## 🚀 How to Run

### Option 1: GUI Application (requires display)
```bash
# Compile the project
javac -d out src/**/*.java src/*.java

# Run the GUI application
java -cp out Main
```

### Option 2: Console Demo (works in any terminal)
```bash
# Compile the project
javac -d out src/**/*.java src/*.java

# Run the console demo
java -cp out util.ConsoleDemo
```

### Option 3: Run Tests
```bash
# Run basic functionality tests
java -cp out util.SimpleTest
```

## 🎛️ Usage Instructions

### GUI Mode
1. **Add Songs**: Click "Add Song" button to select audio files
2. **Remove Songs**: Select a song and click "Remove Song"
3. **Play Controls**: Use the play, pause, stop buttons
4. **Navigation**: Use Previous/Next buttons or click on songs in the list
5. **Loop Mode**: Check/uncheck the "Loop Playlist" option

### Console Mode
The console demo provides a text-based interface with the following options:
- Show current playlist
- Add/remove songs
- Play controls (simulated)
- Song navigation
- Toggle loop mode

## 🔧 Technical Implementation

### LinkedList Usage
The `Playlist` class uses Java's `LinkedList<Song>` to provide:
- **O(1) insertion/deletion** at both ends
- **Efficient navigation** with built-in previous/next pointers
- **Dynamic sizing** for unlimited playlist length
- **Iterator support** for easy traversal

### Key Classes

#### Song.java
- Encapsulates song metadata (file path, name, format, size)
- Validates file existence and format support
- Provides formatted display methods

#### Playlist.java
- Core LinkedList implementation for song management
- Supports adding/removing songs at any position
- Implements navigation logic (next/previous with looping)
- Tracks current playing position

#### AudioPlayer.java
- Wraps Java Sound API for audio playback
- Handles supported audio formats
- Provides playback events and error handling
- Manages playback state (playing/paused/stopped)

#### MusicPlayerController.java
- Coordinates between GUI and data models
- Implements business logic for playlist operations
- Handles event propagation and error management
- Provides high-level API for UI interactions

#### MusicPlayerApp.java
- Complete Swing GUI implementation
- Real-time playlist display with current song highlighting
- File picker integration for adding songs
- Responsive button states based on player status

## 🎯 Design Patterns Used

1. **MVC (Model-View-Controller)**: Clean separation of data, presentation, and logic
2. **Observer Pattern**: Event listeners for UI updates
3. **Facade Pattern**: Controller provides simplified interface to complex subsystems
4. **Strategy Pattern**: Different playback modes and navigation strategies

## 📋 Requirements Met

✅ **Project Structure**: Created model, view, controller, util packages  
✅ **Song Class**: Complete encapsulation of song information  
✅ **Playlist Class**: LinkedList implementation with full navigation  
✅ **AudioPlayer Class**: Java Sound API integration  
✅ **GUI Framework**: Complete Swing application with all controls  
✅ **MVC Architecture**: Proper separation of concerns  
✅ **Audio Format Support**: MP3, WAV, AU, AIFF compatibility  
✅ **Error Handling**: Comprehensive error management  

## 🧪 Testing

The project includes several testing approaches:

### Unit Tests (SimpleTest.java)
- Song creation and validation
- Playlist operations (add, remove, navigate)
- Controller integration testing
- Event system verification

### Console Demo (ConsoleDemo.java)
- Interactive testing of all features
- Real-time playlist management
- Navigation and control testing
- User interface simulation

### Manual Testing
- GUI responsiveness testing
- File format compatibility testing
- Error condition handling
- Memory usage verification

## 🎵 Supported Audio Formats

- **MP3**: Most common compressed audio format
- **WAV**: Uncompressed audio format
- **AU**: Sun Audio format
- **AIFF**: Audio Interchange File Format

## 🔧 System Requirements

- **Java**: JDK 8 or higher
- **Audio**: Java Sound API (included in standard JDK)
- **GUI**: Swing (for GUI mode, included in standard JDK)
- **OS**: Cross-platform (Windows, macOS, Linux)

## 📝 Sample Usage

```java
// Create a playlist
Playlist playlist = new Playlist();

// Add songs
Song song1 = new Song("/path/to/music.mp3");
playlist.addSong(song1);

// Navigate
playlist.playNext();    // Move to next song
playlist.playPrevious(); // Move to previous song

// Control playback
AudioPlayer player = new AudioPlayer();
player.play(playlist.getCurrentSong());
player.pause();
player.stop();
```

## 🏗️ Future Enhancements

- Shuffle mode implementation
- Playlist save/load functionality
- Volume control slider
- Progress bar for current song
- Music library management
- Album artwork display
- Search and filter capabilities

## 📚 Learning Objectives Achieved

1. **Data Structures**: Practical application of LinkedList
2. **Object-Oriented Programming**: Encapsulation, inheritance, polymorphism
3. **Design Patterns**: MVC, Observer, Facade implementations
4. **GUI Programming**: Event-driven Swing application development
5. **File I/O**: Audio file handling and validation
6. **Error Handling**: Robust exception management
7. **Software Architecture**: Clean, maintainable code structure

This implementation demonstrates a real-world application of LinkedList data structures while providing a complete, functional music player with professional-level code organization and error handling.