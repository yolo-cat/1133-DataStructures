import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ExpressionConverterGUI {
    private JFrame mainFrame;
    private JTextField inputField;
    private JTextArea resultArea;
    private JButton convertButton;
    private JButton clearButton;

    public ExpressionConverterGUI() {
        mainFrame = new JFrame("中序轉後序/前序 表達式轉換器");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 350);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout(10, 10));

        // 輸入區
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel inputLabel = new JLabel("請輸入中序表達式:");
        inputField = new JTextField();
        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        convertButton = new JButton("轉換");
        clearButton = new JButton("清除");
        buttonPanel.add(convertButton);
        buttonPanel.add(clearButton);

        // 結果區
        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // 組合
        mainFrame.add(inputPanel, BorderLayout.NORTH);
        mainFrame.add(buttonPanel, BorderLayout.CENTER);
        mainFrame.add(scrollPane, BorderLayout.SOUTH);

        // 事件處理
        convertButton.addActionListener(e -> convertExpression());
        clearButton.addActionListener(e -> clearAll());
        inputField.addActionListener(e -> convertExpression()); // 按Enter也可觸發

        mainFrame.setVisible(true);
    }

    private void convertExpression() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            showError("請輸入中序表達式！");
            return;
        }
        try {
            String result = Expressions.convertExpression(input);
            resultArea.setText(result);
        } catch (Exception ex) {
            showError("格式錯誤或不支援的表達式！\n" + ex.getMessage());
        }
    }

    private void clearAll() {
        inputField.setText("");
        resultArea.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(mainFrame, msg, "錯誤", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpressionConverterGUI::new);
    }
}
