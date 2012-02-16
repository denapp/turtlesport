package fr.turtlesport.ui.swing.component;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Denis Apparicio
 * 
 */
public class DocumentSizeFilter extends DocumentFilter {
  /** Longueur max du texte. */
  private int maxLength;

  /**
   * 
   * @param maxChars
   */
  protected DocumentSizeFilter(int maxLength) {
    this.maxLength = maxLength;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.DocumentFilter.FilterBypass,
   *      int, java.lang.String, javax.swing.text.AttributeSet)
   */
  public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {

    if (str != null
        && (fb.getDocument().getLength() + str.length()) <= maxLength) {
      super.insertString(fb, offs, str, a);
    }
    else {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass,
   *      int, int, java.lang.String, javax.swing.text.AttributeSet)
   */
  public void replace(FilterBypass fb,
                      int offs,
                      int length,
                      String str,
                      AttributeSet a) throws BadLocationException {
    if (str != null
        && (fb.getDocument().getLength() + str.length() - length) <= maxLength) {
      super.replace(fb, offs, length, str, a);
    }
  }
}
