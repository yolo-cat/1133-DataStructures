import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageViewer extends JFrame {
  private JLabel imageLabel;
  private JButton prevButton, nextButton, chooseDirButton;
  private File[] imageFiles;
  private int currentIndex = -1;

  public ImageViewer() {
    super("看圖軟體");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // 圖片顯示區
    imageLabel = new JLabel("請選擇圖片資料夾", JLabel.CENTER);
    add(imageLabel, BorderLayout.CENTER);

    // 按鈕區域
    JPanel buttonPanel = new JPanel();
    prevButton = new JButton("上一張");
    chooseDirButton = new JButton("選取圖片所在目錄");
    nextButton = new JButton("下一張");

    prevButton.addActionListener(e -> showImage(currentIndex - 1));
    nextButton.addActionListener(e -> showImage(currentIndex + 1));
    chooseDirButton.addActionListener(e -> selectImageFolder());

    buttonPanel.add(prevButton);
    buttonPanel.add(chooseDirButton);
    buttonPanel.add(nextButton);

    add(buttonPanel, BorderLayout.SOUTH);
    setSize(800, 600);
    setVisible(true);
  }

  private void selectImageFolder() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
      File dir = chooser.getSelectedFile();
      imageFiles = dir.listFiles((dir1, name) ->
          name.toLowerCase().matches(".*\\.(jpg|jpeg|png|bmp|gif)$")
      );
      if (imageFiles != null && imageFiles.length > 0) {
        Arrays.sort(imageFiles);
        currentIndex = 0;
        showImage(currentIndex);
      }
    }
  }

  private void showImage(int index) {
    if (imageFiles == null || index < 0 || index >= imageFiles.length) return;

    try {
      BufferedImage originalImage = ImageIO.read(imageFiles[index]);
      int width = originalImage.getWidth() / 2;
      int height = originalImage.getHeight() / 2;
      Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      imageLabel.setIcon(new ImageIcon(scaledImage));
      imageLabel.setText(null);
      currentIndex = index;
    } catch (Exception e) {
      imageLabel.setText("讀取圖片失敗");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(ImageViewer::new);
  }
}
