import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
  private int currentIndex = 0;

  @Override
  public void start(Stage primaryStage) {
    // 檢查 music 資料夾是否存在，若無則建立
    File musicDir = new File("music");
    if (!musicDir.exists()) musicDir.mkdirs();

    // 由 music 資料夾初始化播放清單
    File[] files = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
    if (files != null) {
      for (File file : files) {
        playlist.add(file.getAbsolutePath());
      }
    }
    if (!playlist.isEmpty()) {
      playSong(currentIndex);
    }

    // 顯示播放清單檔案名稱
    ObservableList<String> fileNames = FXCollections.observableArrayList();
    for (String path : playlist) {
      File file = new File(path);
      fileNames.add(file.getName());
    }
    ListView<String> listView = new ListView<>(fileNames);
    listView.getSelectionModel().select(currentIndex);
    listView.setOnMouseClicked(e -> {
      int selected = listView.getSelectionModel().getSelectedIndex();
      if (selected != -1 && selected != currentIndex) {
        currentIndex = selected;
        playSong(currentIndex);
      }
    });

    Button playBtn = new Button("播放");
    playBtn.setOnAction(e -> {
      // 重新讀取 music 資料夾並更新播放清單
      playlist.clear();
      File musicDirReload = new File("music");
      File[] filesReload = musicDirReload.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
      if (filesReload != null) {
        for (File file : filesReload) {
          playlist.add(file.getAbsolutePath());
        }
      }
      // 重新更新 ListView
      fileNames.clear();
      for (String path : playlist) {
        File file = new File(path);
        fileNames.add(file.getName());
      }
      // 若播放清單不為空，播放當前索引歌曲
      if (!playlist.isEmpty()) {
        if (currentIndex >= playlist.size()) {
          currentIndex = 0;
        }
        playSong(currentIndex);
        listView.getSelectionModel().select(currentIndex);
      } else {
        if (mediaPlayer != null) mediaPlayer.stop();
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
      }
    });

    Button nextBtn = new Button("下一首");
    nextBtn.setOnAction(e -> {
      if (currentIndex < playlist.size() - 1) {
        currentIndex++;
        playSong(currentIndex);
        listView.getSelectionModel().select(currentIndex);
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
          playlist.add(newPath);
          fileNames.add(destFile.getName());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    VBox root = new VBox(10, listView, addBtn, playBtn, pauseBtn, stopBtn, prevBtn, nextBtn);
    Scene scene = new Scene(root, 250, 250);

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

  public static void main(String[] args) {
    launch(args);
  }
}