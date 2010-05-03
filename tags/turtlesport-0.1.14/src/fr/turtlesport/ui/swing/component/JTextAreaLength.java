package fr.turtlesport.ui.swing.component;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;

/**
 * @author Denis Apparicio
 * 
 */
public class JTextAreaLength extends JTextArea {

  public JTextAreaLength() {
    super();
  }

  public JTextAreaLength(Document doc, String text, int rows, int columns) {
    super(doc, text, rows, columns);
  }

  public JTextAreaLength(Document doc) {
    super(doc);
  }

  public JTextAreaLength(int rows, int columns) {
    super(rows, columns);
  }

  public JTextAreaLength(String text, int rows, int columns) {
    super(text, rows, columns);
  }

  public JTextAreaLength(String text) {
    super(text);
  }

  /**
   * Valorise le nombre maximaum de caract&egrave;re.
   * 
   * @param maxCharacters
   *          la nouvelle valeur.
   */
  public void setMaxiMumCharacters(int maxCharacters) {
    ((AbstractDocument) getDocument())
        .setDocumentFilter(new DocumentSizeFilter(maxCharacters));
  }

}
