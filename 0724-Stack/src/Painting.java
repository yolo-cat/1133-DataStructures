import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

public class Painting extends JFrame {
    private DrawPanel drawPanel;
    private JButton colorBtn;
    private JSlider thicknessSlider;
    private JButton undoBtn, redoBtn;
    private Color currentColor = Color.BLACK;
    private int currentThickness = 2;

    public Painting() {
        setTitle("簡易畫圖程式");
        setSize(640, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        colorBtn = new JButton("選擇顏色");
        colorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "選擇畫筆顏色", currentColor);
            if (chosen != null) {
                currentColor = chosen;
                drawPanel.setColor(currentColor);
            }
        });
        controlPanel.add(colorBtn);

        thicknessSlider = new JSlider(1, 20, currentThickness);
        thicknessSlider.setMajorTickSpacing(5);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);
        thicknessSlider.addChangeListener(e -> {
            currentThickness = thicknessSlider.getValue();
            drawPanel.setThickness(currentThickness);
        });
        controlPanel.add(new JLabel("粗細:"));
        controlPanel.add(thicknessSlider);

        undoBtn = new JButton("Undo");
        redoBtn = new JButton("Redo");
        undoBtn.addActionListener(e -> {
            drawPanel.undo();
        });
        redoBtn.addActionListener(e -> {
            drawPanel.redo();
        });
        controlPanel.add(undoBtn);
        controlPanel.add(redoBtn);

        add(controlPanel, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Painting frame = new Painting();
            frame.setVisible(true);
        });
    }
}

class DrawPanel extends JPanel {
    private ArrayList<Line> lines = new ArrayList<>();
    private Color color = Color.BLACK;
    private int thickness = 2;
    private int lastX, lastY;
    private boolean drawing = false;
    private ArrayList<Line> currentStroke = null;
    private Stack<ArrayList<Line>> undoStack = new Stack<>();
    private Stack<ArrayList<Line>> redoStack = new Stack<>();
    private final int MAX_UNDO = 5;

    public DrawPanel() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                drawing = true;
                currentStroke = new ArrayList<>();
            }
            public void mouseReleased(MouseEvent e) {
                drawing = false;
                if (currentStroke != null && !currentStroke.isEmpty()) {
                    undoStack.push(currentStroke);
                    if (undoStack.size() > MAX_UNDO) {
                        undoStack.remove(0);
                    }
                    redoStack.clear();
                }
                currentStroke = null;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drawing) {
                    int x = e.getX();
                    int y = e.getY();
                    Line line = new Line(lastX, lastY, x, y, color, thickness);
                    lines.add(line);
                    if (currentStroke != null) {
                        currentStroke.add(line);
                    }
                    lastX = x;
                    lastY = y;
                    repaint();
                }
            }
        });
    }

    public void setColor(Color c) {
        this.color = c;
    }
    public void setThickness(int t) {
        this.thickness = t;
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            ArrayList<Line> lastStroke = undoStack.pop();
            lines.removeAll(lastStroke);
            redoStack.push(lastStroke);
            repaint();
        }
    }
    public void redo() {
        if (!redoStack.isEmpty()) {
            ArrayList<Line> redoStroke = redoStack.pop();
            lines.addAll(redoStroke);
            undoStack.push(redoStroke);
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Line line : lines) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(line.color);
            g2.setStroke(new BasicStroke(line.thickness));
            g2.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
    }
}

class Line {
    int x1, y1, x2, y2, thickness;
    Color color;
    public Line(int x1, int y1, int x2, int y2, Color color, int thickness) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.thickness = thickness;
    }
}
