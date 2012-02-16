package fr.turtlesport.ui.swing.component;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;

/**
 * @author Denis Apparicio
 * 
 */
public class JTextFieldLength extends JTextField {

  /** Nombre max de caracteres. */
  private int maxCharacters;

  /**
   * 
   */
  public JTextFieldLength() {
    super();
  }

  /**
   * @param doc
   * @param text
   * @param columns
   */
  public JTextFieldLength(Document doc, String text, int columns) {
    super(doc, text, columns);
  }

  /**
   * @param columns
   */
  public JTextFieldLength(int columns) {
    super(columns);
  }

  /**
   * @param text
   * @param columns
   */
  public JTextFieldLength(String text, int columns) {
    super(text, columns);
  }

  /**
   * @param text
   */
  public JTextFieldLength(String text) {
    super(text);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.text.JTextComponent#setText(java.lang.String)
   */
  @Override
  public void setText(String text) {
    super.setText((text == null) ? "" : text);
  }

  /**
   * Valorise le nombre maximal de caracter&egarve;s.
   * 
   * @param maxCharacters
   *          le nombre maximal de caracter&egarve;s.
   */
  public void setMaxCharacters(int maxCharacters) {
    ((AbstractDocument) getDocument())
        .setDocumentFilter(new DocumentSizeFilter(maxCharacters));
  }

  /**
   * Restitue le nombre maximal de caracter&egarve;s.
   * 
   * @return le nombre maximal de caracter&egarve;s.
   */
  public int getMaxCharacters() {
    return maxCharacters;
  }

}
