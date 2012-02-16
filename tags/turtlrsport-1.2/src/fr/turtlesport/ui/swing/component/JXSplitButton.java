package fr.turtlesport.ui.swing.component;

/*
 * JXSplitButton.java
 *
 * Created on 13 de Janeiro de 2007, 05:42
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * @author M�rio C�sar
 */
public class JXSplitButton extends JButton implements SwingConstants {
  private JLabel     arrowLabel;

  private JPopupMenu dropDownMenu;

  private Dimension  preferedSize;

  private Dimension  maximumSize;

  private Dimension  minimumSize;

  private boolean    preferedSizeSet = false;

  private boolean    maximumSizeSet  = false;

  private boolean    minimumSizeSet  = false;

  private boolean    popupVisible    = false;

  private boolean    isPainting      = false;

  private int        selectedIndex   = 0;

  // Constructors...

  /**
   * Creates a button with no set text or icon.
   */
  public JXSplitButton() {
    this(null, null, null);
  }

  /**
   * Creates a button with an icon.
   * 
   * @param icon
   *          the Icon image to display on the button
   */
  public JXSplitButton(Icon icon) {
    this(null, icon, null);
  }

  /**
   * Creates a button with text.
   * 
   * @param text
   *          the text of the button
   */
  public JXSplitButton(String text) {
    this(text, null, null);
  }

  /**
   * Creates a button where properties are taken from the <code>Action</code>
   * supplied.
   * 
   * @param a
   *          the <code>Action</code> used to specify the new button
   * 
   * @since 1.3
   */
  public JXSplitButton(Action a) {
    this(null, null, null);
    setAction(a);
  }

  /**
   * Creates a button with initial text and an icon.
   * 
   * @param text
   *          the text of the button
   * @param icon
   *          the Icon image to display on the button
   */
  public JXSplitButton(String text, Icon icon) {
    this(text, icon, null);
  }

  public JXSplitButton(String text, Icon icon, JPopupMenu dropDownMenu) {
    // Add the JLabel...
    this.dropDownMenu = dropDownMenu;
    arrowLabel = new JLabel(new ArrowIcon());
    arrowLabel.setHorizontalTextPosition(LEFT);
    if (getComponentOrientation().isLeftToRight()) {
      arrowLabel.setAlignmentX(1.0f);
    }
    else {
      arrowLabel.setAlignmentX(0.0f);
    }
    // super.setLayout(new BorderLayout());
    // super.add(arrowLabel, BorderLayout.EAST);
    super.add(arrowLabel);
    // Create the model
    super.setModel(new DefaultButtonModel());
    // initialize
    super.init(text, icon);
    BasicHTML.updateRenderer(this, getText());
    super.getModel().addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        jXSplitButtonStateChanged(evt);
      }
    });
    arrowLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        arrowLabelMouseEntered(evt);
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        arrowLabelMouseExited(evt);
      }
    });
    arrowLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        arrowLabelMouseClicked(evt);
      }
    });

    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        arrowLabelMouseClicked(null);
      }
    });
  }

  // Private event listeners...

  private void jXSplitButtonStateChanged(javax.swing.event.ChangeEvent evt) {
    if (popupVisible && !JXSplitButton.this.getModel().isSelected()) {
      JXSplitButton.this.getModel().setSelected(true);
      return;
    }
  }

  private void arrowLabelMouseExited(java.awt.event.MouseEvent evt) {
    JXSplitButton.this.getModel().setRollover(false);
  }

  private void arrowLabelMouseEntered(java.awt.event.MouseEvent evt) {
    JXSplitButton.this.getModel().setRollover(true);
  }

  private void arrowLabelMouseClicked(java.awt.event.MouseEvent evt) {
    if (dropDownMenu != null) {
      int height = JXSplitButton.this.getHeight();
      if (UIManager.getLookAndFeel().getName() == "Windows") {
        height--;
      }
      dropDownMenu
          .addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
              dropDownMenuPopupMenuCanceled(evt);
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
              dropDownMenuPopupMenuWillBecomeInvisible(evt);
              ((JPopupMenu) evt.getSource()).removePopupMenuListener(this);
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
              dropDownMenuPopupMenuWillBecomeVisible(evt);
            }
          });

      dropDownMenu.show(JXSplitButton.this, 0, height);
    }
    else {
      ActionEvent e = null;
      e = new ActionEvent(JXSplitButton.this,
                          ActionEvent.ACTION_PERFORMED,
                          super.getActionCommand(),
                          evt.getWhen(),
                          evt.getModifiers());
      super.fireActionPerformed(e);
    }
  }

  private void dropDownMenuPopupMenuWillBecomeVisible(PopupMenuEvent e) {
    popupVisible = true;
    JXSplitButton.this.getModel().setSelected(true);
  }

  private void dropDownMenuPopupMenuWillBecomeInvisible(PopupMenuEvent e) {
    popupVisible = false;
    JXSplitButton.this.getModel().setSelected(false);
  }

  private void dropDownMenuPopupMenuCanceled(PopupMenuEvent e) {
    popupVisible = false;
    JXSplitButton.this.getModel().setSelected(false);
  }

  /**
   * @return Restitue le nombre d'item
   */
  public int getItemCount() {
    return (dropDownMenu == null ? 0 : dropDownMenu.getComponentCount());
  }

  /**
   * @param i
   */
  public void setSelectedIndex(int i) {
    if (dropDownMenu != null
        && dropDownMenu.getComponent(i) instanceof JMenuItem) {
      JMenuItem item = (JMenuItem) dropDownMenu.getComponent(i);
      setIcon(item.getIcon());
      selectedIndex = 0;
    }
  }

  /**
   * Restitue l'index selectionn
   * 
   * @return
   */
  public int getSelectedIndex() {
    return selectedIndex;
  }

  // Properties...

  /**
   * Sets the font for this component.
   * 
   * 
   * @param font
   *          the desired <code>Font</code> for this component
   * @see java.awt.Component#getFont
   * @beaninfo preferred: true bound: true attribute: visualUpdate true
   *           description: The font for the component.
   */
  public void setFont(Font font) {
    super.setFont(font);
    BasicHTML.updateRenderer(this, getText());
  }

  /**
   * If a border has been set on this component, returns the border's insets;
   * otherwise calls <code>super.getInsets</code>.
   * 
   * 
   * @return the value of the insets property
   * @see #setBorder
   */
  public Insets getInsets() {
    Insets retValue = super.getInsets();
    if (isPainting) {
      if (getComponentOrientation().isLeftToRight()) {
        retValue.right = 24;
      }
      else {
        retValue.left = 24;
      }
    }
    else {
      if (getComponentOrientation().isLeftToRight()) {
        retValue.right = 0;
      }
      else {
        retValue.left = 0;
      }
      retValue.top = 0;
      retValue.bottom = 0;
    }
    return retValue;
  }

  /**
   * Returns an <code>Insets</code> object containing this component's inset
   * values. The passed-in <code>Insets</code> object will be reused if
   * possible. Calling methods cannot assume that the same object will be
   * returned, however. All existing values within this object are overwritten.
   * If <code>insets</code> is null, this will allocate a new one.
   * 
   * 
   * @param insets
   *          the <code>Insets</code> object, which can be reused
   * @return the <code>Insets</code> object
   * @see #getInsets
   * @beaninfo expert: true
   */
  public Insets getInsets(Insets insets) {
    Insets retValue = super.getInsets(insets);
    if (isPainting) {
      if (getComponentOrientation().isLeftToRight()) {
        retValue.right = 24;
      }
      else {
        retValue.left = 24;
      }
    }
    else {
      if (getComponentOrientation().isLeftToRight()) {
        retValue.right = 0;
      }
      else {
        retValue.left = 0;
      }
      retValue.top = 0;
      retValue.bottom = 0;
    }
    return retValue;
  }

  /**
   * If the maximum size has been set to a non-<code>null</code> value just
   * returns it. If the UI delegate's <code>getMaximumSize</code> method returns
   * a non-<code>null</code> value then return that; otherwise defer to the
   * component's layout manager.
   * 
   * 
   * @return the value of the <code>maximumSize</code> property
   * @see #setMaximumSize
   * @see ComponentUI
   */
  public Dimension getMaximumSize() {
    if (maximumSizeSet) {
      return (maximumSize);
    }
    Dimension d = getPreferredSize();
    View v = (View) this.getClientProperty(BasicHTML.propertyKey);
    if (v != null) {
      d.width += v.getMaximumSpan(View.X_AXIS)
                 - v.getPreferredSpan(View.X_AXIS);
    }
    super.setMaximumSize(d);
    return d;
  }

  /**
   * Sets the maximum size of this component to a constant value. Subsequent
   * calls to <code>getMaximumSize</code> will always return this value; the
   * component's UI will not be asked to compute it. Setting the maximum size to
   * <code>null</code> restores the default behavior.
   * 
   * 
   * @param maximumSize
   *          a <code>Dimension</code> containing the desired maximum allowable
   *          size
   * @see #getMaximumSize
   * @beaninfo bound: true description: The maximum size of the component.
   */
  public void setMaximumSize(Dimension maximumSize) {
    this.maximumSize = maximumSize;
    super.setMaximumSize(maximumSize);
    maximumSizeSet = true;
  }

  /**
   * If the minimum size has been set to a non-<code>null</code> value just
   * returns it. If the UI delegate's <code>getMinimumSize</code> method returns
   * a non-<code>null</code> value then return that; otherwise defer to the
   * component's layout manager.
   * 
   * 
   * @return the value of the <code>minimumSize</code> property
   * @see #setMinimumSize
   * @see ComponentUI
   */
  public Dimension getMinimumSize() {
    if (minimumSizeSet) {
      return (minimumSize);
    }
    Dimension d = getPreferredSize();
    View v = (View) this.getClientProperty(BasicHTML.propertyKey);
    if (v != null) {
      d.width -= v.getPreferredSpan(View.X_AXIS)
                 - v.getMinimumSpan(View.X_AXIS);
    }
    super.setMinimumSize(d);
    return d;
  }

  /**
   * Sets the minimum size of this component to a constant value. Subsequent
   * calls to <code>getMinimumSize</code> will always return this value; the
   * component's UI will not be asked to compute it. Setting the minimum size to
   * <code>null</code> restores the default behavior.
   * 
   * 
   * @param minimumSize
   *          the new minimum size of this component
   * @see #getMinimumSize
   * @beaninfo bound: true description: The minimum size of the component.
   */
  public void setMinimumSize(Dimension minimumSize) {
    this.minimumSize = minimumSize;
    super.setMinimumSize(minimumSize);
    minimumSizeSet = true;
  }

  /**
   * If the <code>preferredSize</code> has been set to a non-<code>null</code>
   * value just returns it. If the UI delegate's <code>getPreferredSize</code>
   * method returns a non <code>null</code> value then return that; otherwise
   * defer to the component's layout manager.
   * 
   * 
   * @return the value of the <code>preferredSize</code> property
   * @see #setPreferredSize
   * @see ComponentUI
   */
  public Dimension getPreferredSize() {
    if (preferedSizeSet) {
      return (preferedSize);
    }
    Rectangle iconR = new Rectangle();
    Rectangle textR = new Rectangle();
    Rectangle viewR = new Rectangle();
    String text = super.getText();
    Font font = super.getFont();
    Icon icon = (isEnabled()) ? getIcon() : getDisabledIcon();
    isPainting = true;
    int dx = getInsets().left + getInsets().right;
    int dy = getInsets().top + getInsets().bottom;
    isPainting = false;
    if ((icon == null)
        && ((text == null) || ((text != null) && (font == null)))) {
      return new Dimension(dx, dy);
    }
    else if ((text == null) || ((icon != null) && (font == null))) {
      return new Dimension(icon.getIconWidth() + dx, icon.getIconHeight() + dy);
    }
    else {
      FontMetrics fm = this.getFontMetrics(font);
      iconR.x = iconR.y = iconR.width = iconR.height = 0;
      textR.x = textR.y = textR.width = textR.height = 0;
      viewR.x = dx;
      viewR.y = dy;
      viewR.width = viewR.height = Short.MAX_VALUE;
      SwingUtilities.layoutCompoundLabel(this,
                                         fm,
                                         text,
                                         icon,
                                         super.getVerticalAlignment(),
                                         super.getHorizontalAlignment(),
                                         super.getVerticalTextPosition(),
                                         super.getHorizontalTextPosition(),
                                         viewR,
                                         iconR,
                                         textR,
                                         getIconTextGap());
      int x1 = Math.min(iconR.x, textR.x);
      int x2 = Math.max(iconR.x + iconR.width, textR.x + textR.width);
      int y1 = Math.min(iconR.y, textR.y);
      int y2 = Math.max(iconR.y + iconR.height, textR.y + textR.height);
      Dimension rv;
      rv = new Dimension(x2 - x1, y2 - y1);
      rv.width += dx;
      rv.height += dy;
      super.setPreferredSize(rv);
      return rv;
    }
  }

  /**
   * Sets the preferred size of this component. If <code>preferredSize</code> is
   * <code>null</code>, the UI will be asked for the preferred size.
   * 
   * @beaninfo preferred: true bound: true description: The preferred size of
   *           the component.
   */
  public void setPreferredSize(Dimension preferredSize) {
    this.preferedSize = preferredSize;
    super.setPreferredSize(preferredSize);
    preferedSizeSet = true;
  }

  /**
   * Sets the button's text.
   * 
   * @param text
   *          the string used to set the text
   * @see #getText
   * @beaninfo bound: true preferred: true attribute: visualUpdate true
   *           description: The button's text.
   */
  public void setText(String text) {
    super.setText(text);
    BasicHTML.updateRenderer(this, text);
    getPreferredSize();
  }

  // New properties...

  public JPopupMenu getDropDownMenu() {
    return (dropDownMenu);
  }

  public void setDropDownMenu(JPopupMenu dropDownMenu) {
    this.dropDownMenu = dropDownMenu;
  }

  // Methods...

  /**
   * Invoked by Swing to draw components. Applications should not invoke
   * <code>paint</code> directly, but should instead use the
   * <code>repaint</code> method to schedule the component for redrawing.
   * <p>
   * This method actually delegates the work of painting to three protected
   * methods: <code>paintComponent</code>, <code>paintBorder</code>, and
   * <code>paintChildren</code>. They're called in the order listed to ensure
   * that children appear on top of component itself. Generally speaking, the
   * component and its children should not paint in the insets area allocated to
   * the border. Subclasses can just override this method, as always. A subclass
   * that just wants to specialize the UI (look and feel) delegate's
   * <code>paint</code> method should just override <code>paintComponent</code>.
   * 
   * 
   * @param g
   *          the <code>Graphics</code> context in which to paint
   * @see #paintComponent
   * @see #paintBorder
   * @see #paintChildren
   * @see #getComponentGraphics
   * @see #repaint
   */
  public void paint(Graphics g) {
    isPainting = true;
    super.paint(g);
    isPainting = false;
  }

  // Internal classes...

  private class ArrowIcon implements Icon {

    public ArrowIcon() {
      super();
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      // Draw the arrow
      int newY = (getIconHeight() - 5) / 2;
      int[] xPoints = new int[3];
      int[] yPoints = new int[3];
      Color old = g.getColor();
      g.setColor(UIManager.getLookAndFeelDefaults().getColor("Button.shadow"));
      if (getComponentOrientation().isLeftToRight()) {
        // arrowLabel.setAlignmentX(1.0f);
        xPoints[0] = x + 4;
        yPoints[0] = newY;
        xPoints[1] = x + 10;
        yPoints[1] = newY;
        xPoints[2] = x + 7;
        yPoints[2] = newY + 4;
        g.drawLine(x, y + 5, x, getIconHeight() - 5);
        if (!"Nimbus".equals(UIManager.getLookAndFeel().getName())) {
          g.setColor(UIManager.getLookAndFeelDefaults()
              .getColor("Button.highlight"));
          g.drawLine(x + 1, y + 5, x + 1, getIconHeight() - 5);
        }
      }
      else {
        // arrowLabel.setAlignmentX(0.0f);
        xPoints[0] = x + 6;
        yPoints[0] = newY;
        xPoints[1] = x + 12;
        yPoints[1] = newY;
        xPoints[2] = x + 9;
        yPoints[2] = newY + 4;
        g.drawLine(x + 15, y + 5, x + 15, getIconHeight() - 5);
        if (!"Nimbus".equals(UIManager.getLookAndFeel().getName())) {
          g.setColor(UIManager.getLookAndFeelDefaults()
              .getColor("Button.highlight"));
          g.drawLine(x + 16, y + 5, x + 16, getIconHeight() - 5);
        }
      }
      g.setColor(Color.black);
      g.fillPolygon(xPoints, yPoints, 3);
      g.setColor(old);
    }

    public int getIconWidth() {
      return 16;
    }

    public int getIconHeight() {
      int ret = JXSplitButton.this.getSize().height;
      return (ret);
    }
  }

}
