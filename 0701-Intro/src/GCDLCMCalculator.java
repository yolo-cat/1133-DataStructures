// This Java code is meant to be compiled and run in a Java environment.
// If you are seeing a syntax error, make sure to use a Java compiler (e.g., javac) rather than a Python interpreter.
// 撰寫一個具有GUI介面java程式，可輸入兩個數字，並找到其最大公因數及最小公倍數。
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GCDLCMCalculator extends JFrame implements ActionListener {
  private JTextField num1Field, num2Field;
  private JButton calculateButton;
  private JLabel gcdLabel, lcmLabel;

  public GCDLCMCalculator() {
    setTitle("GCD & LCM Calculator");
    setSize(400, 250);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new GridLayout(5, 2, 10, 10));

    add(new JLabel("Enter first number:"));
    num1Field = new JTextField();
    add(num1Field);

    add(new JLabel("Enter second number:"));
    num2Field = new JTextField();
    add(num2Field);

    calculateButton = new JButton("Calculate");
    calculateButton.addActionListener(this);
    add(calculateButton);

    add(new JLabel("GCD:"));
    gcdLabel = new JLabel();
    add(gcdLabel);

    add(new JLabel("LCM:"));
    lcmLabel = new JLabel();
    add(lcmLabel);

    setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    try {
      int num1 = Integer.parseInt(num1Field.getText());
      int num2 = Integer.parseInt(num2Field.getText());

      if (num1 <= 0 || num2 <= 0) {
        JOptionPane.showMessageDialog(this, "Please enter positive integers.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      int gcd = computeGCD(num1, num2);
      int lcm = (num1 * num2) / gcd;

      gcdLabel.setText(String.valueOf(gcd));
      lcmLabel.setText(String.valueOf(lcm));
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Please enter valid integers.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private int computeGCD(int a, int b) {
    while (b != 0) {
      int temp = b;
      b = a % b;
      a = temp;
    }
    return a;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GCDLCMCalculator());
  }
}
