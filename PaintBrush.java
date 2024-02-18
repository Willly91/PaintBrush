package project_java;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PaintBrush extends JFrame {

    JButton redButton, greenButton, blueButton, yellowButton, blackButton, cyanButton, loadImageBtn;
    JButton rectangleButton, ovalButton, lineButton, freehandButton, eraserButton, clearButton, undoButton, saveButton;
    JCheckBox dottedCheckbox, filledCheckbox;
    JPanel canvasPanel;
    Color currentColor;
    Point startPoint, endPoint;
    boolean isDragged, isDotted, isFilled;
    SelectedDrawingOption selectedDrawingOption;
    BufferedImage canvasImage;
    
    public PaintBrush() {
        setTitle("Paint App");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        currentColor = Color.BLACK;
        isDragged = false;
        isDotted = false;
        isFilled = false;
        selectedDrawingOption = SelectedDrawingOption.FREEHAND;

        actionsList = new ArrayList<>(); 

        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (canvasImage != null) {
                    g.drawImage(canvasImage, 0, 0, this);
                }
            }
        };

        canvasPanel.setBounds(0, 0, 1200, 800);
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.addMouseListener(new MyMouseListener());
        canvasPanel.addMouseMotionListener(new MyMouseMotionListener());
        add(canvasPanel);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBounds(1205, 0, 190, 760);
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        rectangleButton = createButton("Rectangle");
        ovalButton = createButton("Oval");
        lineButton = createButton("Line");
        freehandButton = createButton("Free Hand");
        eraserButton = createButton("Eraser");
        undoButton = createButton("Undo");
        saveButton = createButton("Save");
        loadImageBtn = createButton("Load Image");

        dottedCheckbox = createCheckbox("Dotted");
        filledCheckbox = createCheckbox("Filled");

        buttonPanel.setLayout(new GridLayout(0, 1, 5, 5));
        buttonPanel.add(redButton);
        buttonPanel.add(greenButton);
        buttonPanel.add(blueButton);
        buttonPanel.add(yellowButton);
        buttonPanel.add(blackButton);
        buttonPanel.add(cyanButton);
        buttonPanel.add(rectangleButton);
        buttonPanel.add(ovalButton);
        buttonPanel.add(lineButton);
        buttonPanel.add(freehandButton);
        buttonPanel.add(eraserButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadImageBtn);
        buttonPanel.add(dottedCheckbox);
        buttonPanel.add(filledCheckbox);

        add(buttonPanel);
        setVisible(true);
    }

    private JButton createButton(Color color) {
        JButton button = new JButton();
        button.setBackground(color);
            public void actionPerformed(ActionEvent ev) {
                currentColor = color;
            }
        });
        return button;
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                switch (label) {
                    case "Rectangle":
                        selectedDrawingOption = SelectedDrawingOption.RECTANGLE;
                        break;
                    case "Oval":
                        selectedDrawingOption = SelectedDrawingOption.OVAL;
                        break;
                    case "Line":
                        selectedDrawingOption = SelectedDrawingOption.LINE;
                        break;
                    case "Free Hand":
                        selectedDrawingOption = SelectedDrawingOption.FREEHAND;
                        break;
                    case "Eraser":
                        selectedDrawingOption = SelectedDrawingOption.ERASER;
                        break;
                    case "Clear All":
                        clearCanvas();
                        break;
                    case "Undo":
                        undoLastAction();
                        break;
                    case "Save":
                        saveImage("Drawing", "jpg");
                        break;
                }
            }
        });
        return button;
    }

    private void clearCanvas() {
        Graphics g = canvasPanel.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasPanel.getWidth(), canvasPanel.getHeight());
        if (canvasImage != null) {
            canvasImage = new BufferedImage(canvasPanel.getWidth(), canvasPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
    }

    private JCheckBox createCheckbox(String label) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (label.equals("Dotted"))
                        isDotted = true;
                    else if (label.equals("Filled"))
                        isFilled = true;
                } 
                else {
                    if (label.equals("Dotted"))
                        isDotted = false;
                    else if (label.equals("Filled"))
                        isFilled = false;
                }
            }
        });
        return checkBox;
    }

    private class MyMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            endPoint = e.getPoint();
            Graphics2D g2d = (Graphics2D) canvasPanel.getGraphics();
            Stroke oldStroke = g2d.getStroke();
            PaintAction action = new PaintAction(startPoint, endPoint, currentColor, selectedDrawingOption, isDotted, isFilled);
            actionsList.add(action); 
            if (selectedDrawingOption == SelectedDrawingOption.RECTANGLE) {
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(startPoint.x - endPoint.x);
                int height = Math.abs(startPoint.y - endPoint.y);
                if (isFilled)
                    g2d.fillRect(x, y, width, height);
                else {
                    if (isDotted) {
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                        g2d.drawRect(x, y, width, height);
                    } 
                    else {
                        g2d.drawRect(x, y, width, height);
                    }
                }
            } 
            else if (selectedDrawingOption == SelectedDrawingOption.OVAL) {
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(startPoint.x - endPoint.x);
                int height = Math.abs(startPoint.y - endPoint.y);
                if (isFilled)
                    g2d.fillOval(x, y, width, height);
                else {
                    if (isDotted) {
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                        g2d.drawOval(x, y, width, height);
                    } 
                    else {
                        g2d.drawOval(x, y, width, height);
                    }
                }
            } 
            else if (selectedDrawingOption == SelectedDrawingOption.LINE) {
                if (isDotted) {
                    g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                } 
                else {
                    g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                }
            }
            g2d.setStroke(oldStroke);
        }
    }

    private class MyMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            if (selectedDrawingOption == SelectedDrawingOption.FREEHAND) {
                Graphics2D g2d = (Graphics2D) canvasPanel.getGraphics();
                g2d.setColor(currentColor);
                int size = 5;
                g2d.fillOval(e.getX() - size / 2, e.getY() - size / 2, size, size);
            } 
            else if (selectedDrawingOption == SelectedDrawingOption.ERASER) {
                Graphics2D g2d = (Graphics2D) canvasPanel.getGraphics();
                int size = 10;
                g2d.setColor(canvasPanel.getBackground());
                g2d.fillRect(e.getX() - size / 2, e.getY() - size / 2, size, size);
                if (canvasImage != null) {
                    Graphics2D imageGraphics = canvasImage.createGraphics();
                    imageGraphics.setColor(canvasPanel.getBackground());
                    imageGraphics.fillRect(e.getX() - size / 2, e.getY() - size / 2, size, size);
                    imageGraphics.dispose();
                }
            }
        }
    }

    private void undoLastAction() {
        if (!actionsList.isEmpty()) {
            actionsList.remove(actionsList.size() - 1); 
            repaintCanvas(); 
        }
    }

    private void repaintCanvas() {
        clearCanvas();
        for (PaintAction action : actionsList) {
            Graphics2D g2d = (Graphics2D) canvasPanel.getGraphics();
            g2d.setColor(action.color);
            Stroke oldStroke = g2d.getStroke();
            if (action.drawingOption == SelectedDrawingOption.RECTANGLE) {
                int x = Math.min(action.startPoint.x, action.endPoint.x);
                int y = Math.min(action.startPoint.y, action.endPoint.y);
                int height = Math.abs(action.startPoint.y - action.endPoint.y);
                if (action.isFilled)
                    g2d.fillRect(x, y, width, height);
                else {
                    if (action.isDotted) {
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                        g2d.drawRect(x, y, width, height);
                    } else {
                        g2d.drawRect(x, y, width, height);
                    }
                }
            } 
            else if (action.drawingOption == SelectedDrawingOption.OVAL) {
                int x = Math.min(action.startPoint.x, action.endPoint.x);
                int y = Math.min(action.startPoint.y, action.endPoint.y);
                int height = Math.abs(action.startPoint.y - action.endPoint.y);
                if (action.isFilled)
                    g2d.fillOval(x, y, width, height);
                else {
                    if (action.isDotted) {
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                    } else {
                        g2d.drawOval(x, y, width, height);
                    }
                }
            } 
            else if (action.drawingOption == SelectedDrawingOption.LINE) {
                if (action.isDotted) {
                    g2d.drawLine(action.startPoint.x, action.startPoint.y, action.endPoint.x, action.endPoint.y);
                } else {
                    g2d.drawLine(action.startPoint.x, action.startPoint.y, action.endPoint.x, action.endPoint.y);
                }
            }
            g2d.setStroke(oldStroke);
        }
    }
}

enum SelectedDrawingOption {
    RECTANGLE, OVAL, LINE, FREEHAND, ERASER
}
