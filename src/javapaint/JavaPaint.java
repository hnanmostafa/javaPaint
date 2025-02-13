package javapaint;

import javax.swing.SwingUtilities;


public class JavaPaint {

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaintTools().setVisible(true));   
        
    } 
}
