package gui;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

public class sysOutStream extends OutputStream {
    public JTextPane textPane;
    public static SimpleAttributeSet attributeSet;

    public sysOutStream(JTextPane text) {
        textPane = text;
        attributeSet = new SimpleAttributeSet();
    }

    @Override
    public void write(int b) throws IOException {
        // textPane.append(new String(new byte[]{ (byte)b }));
        Document doc = textPane.getDocument();
        try {
            doc.insertString(doc.getLength(), new String(new byte[] { (byte) b }), attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
}
