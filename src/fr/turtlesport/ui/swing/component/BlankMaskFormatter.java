package fr.turtlesport.ui.swing.component;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

/**
 * @author Denis Apparicio
 * 
 */
public class BlankMaskFormatter extends MaskFormatter {
  private String blankRepresentation;

  public BlankMaskFormatter() {
    super();
  }

  public BlankMaskFormatter(String mask) throws ParseException {
    super(mask);
  }

  /**
   * Update our blank representation whenever the mask is updated.
   */
  @Override
  public void setMask(String mask) throws ParseException {
    super.setMask(mask);
    updateBlankRepresentation();
  }

  /**
   * Update our blank representation whenever the mask is updated.
   */
  @Override
  public void setPlaceholderCharacter(char placeholder) {
    super.setPlaceholderCharacter(placeholder);
    updateBlankRepresentation();
  }

  /**
   * Override the stringToValue method to check the blank representation.
   */
  @Override
  public Object stringToValue(String value) throws ParseException {
    if (blankRepresentation != null && blankRepresentation.equals(value)) {
      return null;
    }
    return  super.stringToValue(value);
  }

  private void updateBlankRepresentation() {
    try {
      // calling valueToString on the parent class with a null attribute will
      // get the 'blank'
      // representation.
      blankRepresentation = valueToString(null);
    }
    catch (ParseException e) {
      blankRepresentation = null;
    }
  }
}
