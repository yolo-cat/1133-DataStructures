import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

public class Mp3PlayerDemo extends Application {
  private MediaPlayer mediaPlayer;
  private final LinkedList<String> playlist = new LinkedList<>();
  private final LinkedList<String> musicLibrary = new LinkedList<>();
  private ObservableList<String> playlistNames = FXCollections.observableArrayList();
  private ObservableList<String> libraryNames = FXCollections.observableArrayList();
  private ListView<String> listView;
  private boolean showPlaylist = true;
  private int currentIndex = 0;

  @Override
  public void start(Stage primaryStage) {
    // 檢查 music 資料夾是否存在，若無則建立
    File musicDir = new File("music");
    if (!musicDir.exists()) musicDir.mkdirs();

    // 初始化音樂庫
    File[] files = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
    if (files != null) {
      for (File file : files) {
        musicLibrary.add(file.getAbsolutePath());
      }
    }
    // 預設播放清單與音樂庫相同
    playlist.clear();
    playlist.addAll(musicLibrary);
    if (!playlist.isEmpty()) {
      playSong(currentIndex);
    }

    // 初始化顯示名稱
    updateNames();
    listView = new ListView<>(playlistNames);
    listView.getSelectionModel().select(currentIndex);
    listView.setOnMouseClicked(e -> {
      int selected = listView.getSelectionModel().getSelectedIndex();
      if (selected != -1 && selected != currentIndex) {
        currentIndex = selected;
        playSong(currentIndex);
        updateNames();
      }
    });
    // 切換按鈕
    ToggleButton toggleBtn = new ToggleButton("切換到音樂庫");
    toggleBtn.setOnAction(e -> {
      showPlaylist = !showPlaylist;
      if (showPlaylist) {
        listView.setItems(playlistNames);
        toggleBtn.setText("切換到音樂庫");
      } else {
        listView.setItems(libraryNames);
        toggleBtn.setText("切換到播放清單");
      }
      listView.getSelectionModel().select(currentIndex);
    });

    Button playBtn = new Button("播放");
    playBtn.setOnAction(e -> {
      // 重新讀取 music 資料夾並更新音樂庫
      musicLibrary.clear();
      File musicDirReload = new File("music");
      File[] filesReload = musicDirReload.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
      if (filesReload != null) {
        for (File file : filesReload) {
          musicLibrary.add(file.getAbsolutePath());
        }
      }
      // 若播放清單與音樂庫不同，可根據需求同步
      if (showPlaylist) {
        playlist.clear();
        playlist.addAll(musicLibrary);
        if (currentIndex >= playlist.size()) currentIndex = 0;
        updateNames();
        if (!playlist.isEmpty()) {
          playSong(currentIndex);
          listView.getSelectionModel().select(currentIndex);
        } else {
          if (mediaPlayer != null) mediaPlayer.stop();
        }
      } else {
        updateNames();
      }
    });

    Button pauseBtn = new Button("暫停");
    pauseBtn.setOnAction(e -> {
      if (mediaPlayer != null) {
        mediaPlayer.pause();
      }
    });

    Button stopBtn = new Button("停止");
    stopBtn.setOnAction(e -> {
      if (mediaPlayer != null) {
        mediaPlayer.stop();
      }
    });

    Button prevBtn = new Button("上一首");
    prevBtn.setOnAction(e -> {
      if (currentIndex > 0) {
        currentIndex--;
        playSong(currentIndex);
        listView.getSelectionModel().select(currentIndex);
        // 更新 ListView 標示
        for (int i = 0; i < playlistNames.size(); i++) {
          File file = new File(playlist.get(i));
          if (i == currentIndex) {
            playlistNames.set(i, ">> " + file.getName());
          } else {
            playlistNames.set(i, file.getName());
          }
        }
      }
    });

    Button nextBtn = new Button("下一首");
    nextBtn.setOnAction(e -> {
      if (currentIndex < playlist.size() - 1) {
        currentIndex++;
        playSong(currentIndex);
        listView.getSelectionModel().select(currentIndex);
        // 更新 ListView 標示
        for (int i = 0; i < playlistNames.size(); i++) {
          File file = new File(playlist.get(i));
          if (i == currentIndex) {
            playlistNames.set(i, ">> " + file.getName());
          } else {
            playlistNames.set(i, file.getName());
          }
        }
      }
    });

    // 加入音樂按鈕
    Button addBtn = new Button("加入音樂");
    addBtn.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("選擇音樂檔案");
      fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("音樂檔案", "*.mp3", "*.wav")
      );
      File selectedFile = fileChooser.showOpenDialog(primaryStage);
      if (selectedFile != null) {
        try {
          File musicDir1 = new File("music");
          if (!musicDir1.exists()) musicDir1.mkdirs();
          String baseName = selectedFile.getName();
          String name = baseName;
          String nameNoExt = baseName;
          String ext = "";
          int dotIdx = baseName.lastIndexOf('.');
          if (dotIdx != -1) {
            nameNoExt = baseName.substring(0, dotIdx);
            ext = baseName.substring(dotIdx);
          }
          int count = 1;
          File destFile = new File(musicDir1, name);
          while (destFile.exists()) {
            name = nameNoExt + "_" + count + ext;
            destFile = new File(musicDir1, name);
            count++;
          }
          Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
          String newPath = destFile.getAbsolutePath();
          musicLibrary.add(newPath);
          if (showPlaylist) {
            playlist.add(newPath);
            currentIndex = playlist.size() - 1;
          }
          updateNames();
          listView.getSelectionModel().select(currentIndex);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    VBox root = new VBox(10, toggleBtn, listView, addBtn, playBtn, pauseBtn, stopBtn, prevBtn, nextBtn);
    Scene scene = new Scene(root, 400, 400);

    primaryStage.setTitle("MP3 播放器");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void playSong(int index) {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
    }
    String mp3File = playlist.get(index);
    Media media = new Media(new File(mp3File).toURI().toString());
    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();
  }

  private void updateNames() {
    playlistNames.clear();
    for (int i = 0; i < playlist.size(); i++) {
      File file = new File(playlist.get(i));
      if (i == currentIndex) {
        playlistNames.add(">> " + file.getName());
      } else {
        playlistNames.add(file.getName());
      }
    }
    libraryNames.clear();
    for (int i = 0; i < musicLibrary.size(); i++) {
      File file = new File(musicLibrary.get(i));
      libraryNames.add(file.getName());
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}