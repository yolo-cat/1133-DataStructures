import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
  private ListView<HBox> listView; // 改為 ListView<HBox>
  private boolean showPlaylist = true;
  private int currentIndex = 0;
  private Label currentSongLabel = new Label("目前未播放");
  private ProgressBar progressBar = new ProgressBar(0);

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
    // 移除自動播放
    // if (!playlist.isEmpty()) {
    //   playSong(currentIndex);
    // }

    // 初始化顯示名稱
    updateNames();
    listView = new ListView<>();
    updateListView();
    // 點擊歌曲名稱播放
    listView.setOnMouseClicked(e -> {
      int selected = listView.getSelectionModel().getSelectedIndex();
      if (showPlaylist && selected != -1 && selected != currentIndex) {
        currentIndex = selected;
        playSong(currentIndex);
        updateListView();
      }
    });
    // 切換按鈕
    ToggleButton toggleBtn = new ToggleButton("切換到音樂庫");
    toggleBtn.setOnAction(e -> {
      showPlaylist = !showPlaylist;
      if (showPlaylist) {
        toggleBtn.setText("切換到音樂庫");
      } else {
        toggleBtn.setText("切換到播放清單");
      }
      updateListView();
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
          // 新增檔案時，無論目前顯示哪個模式都加入播放清單
          playlist.add(newPath);
          currentIndex = playlist.size() - 1;
          updateNames();
          updateListView();
          listView.getSelectionModel().select(currentIndex);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    // 美化元件
    currentSongLabel.setFont(Font.font("Arial", 16));
    currentSongLabel.setTextFill(Color.DARKBLUE);
    currentSongLabel.setPadding(new Insets(8, 0, 8, 0));
    progressBar.setPrefWidth(350);
    progressBar.setStyle("-fx-accent: #4CAF50;");
    listView.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f8ff;");
    // 控制按鈕區
    HBox controlBox = new HBox(10);
    controlBox.setAlignment(Pos.CENTER_LEFT); // 與進度條左對齊
    controlBox.setPadding(new Insets(10, 0, 10, 0));
    controlBox.getChildren().addAll(playBtn, pauseBtn, stopBtn, prevBtn, nextBtn);
    // 上方管理區
    HBox topBox = new HBox(10);
    topBox.setAlignment(Pos.CENTER_LEFT);
    topBox.setPadding(new Insets(10, 10, 0, 10));
    topBox.getChildren().addAll(toggleBtn, addBtn);
    // 主面板
    VBox root = new VBox(0,
      currentSongLabel,
      new HBox(0, progressBar), // 用 HBox 包裹進度條，方便與 controlBox 對齊
      controlBox,
      topBox,
      listView
    );
    root.setPadding(new Insets(10));
    root.setSpacing(8);
    root.setStyle("-fx-background-color: #f0f4f8;");
    Scene scene = new Scene(root, 600, 800);

    primaryStage.setTitle("MP3 播放器 (LinkedList 版)");
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
    File file = new File(mp3File);
    currentSongLabel.setText("正在播放：" + file.getName());
    progressBar.setProgress(0);
    mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
      Duration total = mediaPlayer.getTotalDuration();
      if (total != null && total.toMillis() > 0) {
        progressBar.setProgress(newTime.toMillis() / total.toMillis());
      } else {
        progressBar.setProgress(0);
      }
    });
    mediaPlayer.setOnEndOfMedia(() -> {
      progressBar.setProgress(1);
      // 自動播放下一首
      if (showPlaylist && currentIndex < playlist.size() - 1) {
        currentIndex++;
        playSong(currentIndex);
        updateListView();
      }
    });
  }

  // 更新 ListView 內容
  private void updateListView() {
    ObservableList<HBox> items = FXCollections.observableArrayList();
    if (showPlaylist) {
      for (int i = 0; i < playlist.size(); i++) {
        File file = new File(playlist.get(i));
        String name = (i == currentIndex ? ">> " : "") + file.getName();
        Button removeBtn = new Button("移除");
        int idx = i;
        removeBtn.setOnAction(e -> {
          playlist.remove(idx);
          if (currentIndex >= playlist.size()) currentIndex = playlist.size() - 1;
          updateListView();
        });
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        Button playBtn = new Button(name);
        playBtn.setOnAction(e -> {
          currentIndex = idx;
          playSong(currentIndex);
          updateListView();
        });
        hbox.getChildren().addAll(playBtn, removeBtn);
        items.add(hbox);
      }
    } else {
      for (int i = 0; i < musicLibrary.size(); i++) {
        File file = new File(musicLibrary.get(i));
        // 若目前播放的檔案等於此檔案，前面加上 ">> "
        String name = (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING && mediaPlayer.getMedia().getSource().equals(new File(musicLibrary.get(i)).toURI().toString()) ? ">> " : "") + file.getName();
        Button addBtn = new Button("加入播放清單");
        int idx = i;
        addBtn.setOnAction(e -> {
          if (!playlist.contains(musicLibrary.get(idx))) {
            playlist.add(musicLibrary.get(idx));
            updateListView();
          }
        });
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        Button playBtn = new Button(name);
        playBtn.setOnAction(e -> {
          // 音樂庫模式下，點擊檔案名稱直接播放該檔案
          if (mediaPlayer != null) mediaPlayer.stop();
          Media media = new Media(new File(musicLibrary.get(idx)).toURI().toString());
          mediaPlayer = new MediaPlayer(media);
          mediaPlayer.play();
          currentSongLabel.setText("正在播放：" + file.getName());
          progressBar.setProgress(0);
          mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            Duration total = mediaPlayer.getTotalDuration();
            if (total != null && total.toMillis() > 0) {
              progressBar.setProgress(newTime.toMillis() / total.toMillis());
            } else {
              progressBar.setProgress(0);
            }
          });
          mediaPlayer.setOnEndOfMedia(() -> progressBar.setProgress(1));
          // 播放時即時刷新列表，讓 ">> " 標示正確
          updateListView();
        });
        hbox.getChildren().addAll(playBtn, addBtn);
        items.add(hbox);
      }
    }
    listView.setItems(items);
    if (showPlaylist && currentIndex >= 0 && currentIndex < items.size()) {
      listView.getSelectionModel().select(currentIndex);
    }
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