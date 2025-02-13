package javapaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;



public class PaintTools extends JFrame{
    
    private DrawPanel drawPanel;
    private Color paintColor = Color.BLACK;
    private String shapeTool = "Freehand";
    private boolean isFilled = false;
    private boolean isDotted = false;
    
    public PaintTools ()  {
        
        setTitle("Paint Brush");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        
        JPanel controls = new JPanel();       
        
        JButton blackBtn = new JButton("Black");
        blackBtn.addActionListener(e -> paintColor = Color.BLACK);
        controls.add(blackBtn);
        
        JButton redBtn = new JButton("Red");
        redBtn.setForeground(Color.red);
        redBtn.addActionListener(e -> paintColor = Color.RED);
        controls.add(redBtn);
        
        JButton greenBtn = new JButton("Green");
        greenBtn.setForeground(Color.green);
        greenBtn.addActionListener(e -> paintColor = Color.GREEN);
        controls.add(greenBtn);
        
        JButton blueBtn = new JButton("Blue");
        blueBtn.setForeground(Color.blue);
        blueBtn.addActionListener(e -> paintColor = Color.BLUE);
        controls.add(blueBtn);
        
        JButton rectBtn = new JButton("Rectangle");
        rectBtn.addActionListener(e -> shapeTool = "Rectangle");
        controls.add(rectBtn);
        
        
        JButton ovalBtn = new JButton("Oval");
        ovalBtn.addActionListener(e -> shapeTool = "Oval");
        controls.add(ovalBtn);
        
        JButton lineBtn = new JButton("Line");
        lineBtn.addActionListener(e -> shapeTool = "Line");
        controls.add(lineBtn);
        
        JButton freehandBtn = new JButton("Freehand"); 
        freehandBtn.addActionListener(e -> shapeTool = "Freehand");
        controls.add(freehandBtn);
        
        JButton eraserBtn = new JButton("Eraser");
        eraserBtn.addActionListener(e -> shapeTool = "Eraser");
        controls.add(eraserBtn);
        
        JCheckBox filledChk = new JCheckBox("Filled");       
        filledChk.addActionListener(e -> isFilled = filledChk.isSelected());
        controls.add(filledChk);
        
        JCheckBox dottedChk = new JCheckBox("Dotted");
        dottedChk.addActionListener(e -> isDotted = dottedChk.isSelected());
        controls.add(dottedChk);
        
        JButton clearBtn = new JButton("Clear All");       
        clearBtn.addActionListener(e -> drawPanel.clear());
        controls.add(clearBtn);
        
        JButton undoBtn = new JButton("Undo");        
        undoBtn.addActionListener(e -> drawPanel.undo());
        controls.add(undoBtn);
        
        JButton saveBtn = new JButton("Save");       
        saveBtn.addActionListener(e -> drawPanel.saveImage());
        controls.add(saveBtn);
        
        try {          
            Image rectangleImg = ImageIO.read(getClass().getResource("/imgs/Rectangle.png"));
            rectBtn.setIcon(new ImageIcon(rectangleImg));
            Image ovalImg = ImageIO.read(getClass().getResource("/imgs/Oval.png"));
            ovalBtn.setIcon(new ImageIcon(ovalImg));
            Image lineImg = ImageIO.read(getClass().getResource("/imgs/Line.png"));
            lineBtn.setIcon(new ImageIcon(lineImg));
            Image penImg = ImageIO.read(getClass().getResource("/imgs/Pen.png"));
            freehandBtn.setIcon(new ImageIcon(penImg));
            Image eraserImg = ImageIO.read(getClass().getResource("/imgs/Eraser.png"));
            eraserBtn.setIcon(new ImageIcon(eraserImg));
            Image clearImg = ImageIO.read(getClass().getResource("/imgs/Broom.png"));
            clearBtn.setIcon(new ImageIcon(clearImg));
            Image undoImg = ImageIO.read(getClass().getResource("/imgs/Undo.png"));
            undoBtn.setIcon(new ImageIcon(undoImg));
            Image savePhotoImg = ImageIO.read(getClass().getResource("/imgs/SavePhoto.png"));
            saveBtn.setIcon(new ImageIcon(savePhotoImg));
        }
        catch (IllegalArgumentException | IOException ex ) {
            JOptionPane.showMessageDialog(null,"Can't found icons.", "Error",JOptionPane.ERROR_MESSAGE);
        }
        
        add(controls, BorderLayout.NORTH);
    }
    
    
       private class DrawPanel extends JPanel  implements MouseListener, MouseMotionListener{
        private int numOfImage = 1;
        private BufferedImage layer;
        private Graphics2D g2;
        private int x1, y1, x2 , y2;
        private Stack<BufferedImage> stackOflayers = new Stack<>();
        
        public DrawPanel() {
            addMouseListener(this);
            addMouseMotionListener(this); 
            setBackground(Color.WHITE);
            layer = new BufferedImage(1500, 800, BufferedImage.TYPE_INT_ARGB);
            g2 = layer.createGraphics();
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
                     
        }
        
        private void drawShape(int x, int y) {
            g2.setColor(shapeTool.equals("Eraser") ? Color.WHITE : paintColor);
            g2.setStroke(isDotted ? new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0) : new BasicStroke(2));
            
            switch (shapeTool) {
                case "Rectangle":
                    if (isFilled) g2.fillRect(x1, y1, x - x1, y - y1);
                    else g2.drawRect(x1, y1, x - x1, y - y1);
                    break;
                case "Oval":
                    if (isFilled) g2.fillOval(x1, y1, x - x1, y - y1);
                    else g2.drawOval(x1, y1, x - x1, y - y1);
                    break;
                case "Line":
                    g2.drawLine(x1, y1, x, y);
                    break;
                case "Freehand":
                case "Eraser":
                    g2.fillOval(x, y, 5, 5);
                    break;
            }
            repaint();
        }
        
        private void saveToLayer() {
            BufferedImage overLayer = new BufferedImage(layer.getWidth(), layer.getHeight(), layer.getType());
            Graphics g = overLayer.getGraphics();
            g.drawImage(layer, 0, 0, null);
            g.dispose();
            stackOflayers.push(overLayer);
        }
        
        public void undo() {
            if (!stackOflayers.isEmpty()) {
                layer = stackOflayers.pop();
                g2 = layer.createGraphics();
                repaint();
            }
        }
        
        public void clear() {
            saveToLayer();
            layer = new BufferedImage(1500, 800, BufferedImage.TYPE_INT_ARGB);
            g2 = layer.createGraphics();          
            repaint();
        }
        
        public void saveImage() {
            try {
                ImageIO.write(layer, "PNG", new File("Painting "+ numOfImage + ".png"));
                JOptionPane.showMessageDialog(null, "Image saved successfully.");              
                numOfImage ++;
            } catch (HeadlessException | IOException ex) {
                JOptionPane.showMessageDialog(null,"Error saving image.", "Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(layer, 0, 0, null);
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            saveToLayer();
            x1 = e.getX();
            y1 = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            drawShape(x2, y2);
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseDragged(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            repaint();
            if (shapeTool.equals("Freehand") || shapeTool.equals("Eraser")) {
                drawShape(x2, y2);
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}
    }

}




