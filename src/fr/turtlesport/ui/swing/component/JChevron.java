package fr.turtlesport.ui.swing.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

import fr.turtlesport.ui.swing.img.ImagesRepository;

/**
 * @author Denis Apparicio
 * 
 */
public class JChevron extends JButton implements ActionListener {
  private static Icon ICON_UP   = ImagesRepository
                                    .getImageIcon("arrow-up-double-2.png");

  private static Icon ICON_DOWN = ImagesRepository
                                    .getImageIcon("arrow-down-double-2.png");

  private Action      actionUp;

  private Action      actionDown;

  public JChevron(boolean isUp) {
    super();
    if (isUp) {
      up();
    }
    else {
      down();
    }
//    setBorder(BorderFactory.createEmptyBorder());
//    setContentAreaFilled(false);

    setBorder(BorderFactory.createEmptyBorder());
    setContentAreaFilled(false);
    setBorderPainted(false);

    addActionListener(this);
  }

  public boolean isUp() {
    return ICON_UP == getIcon();
  }

  public void up() {
    setIcon(ICON_UP);
  }

  public void down() {
    setIcon(ICON_DOWN);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (isUp()) {
      down();
      if (actionDown != null) {
        actionDown.actionPerformed(e);
      }
    }
    else {
      up();
      if (actionUp != null) {
        actionUp.actionPerformed(e);
      }
    }
  }

  public Action getActionUp() {
    return actionUp;
  }

  public void setActionUp(Action actionUp) {
    this.actionUp = actionUp;
  }

  public Action getActionDown() {
    return actionDown;
  }

  public void setActionDown(Action actionDown) {
    this.actionDown = actionDown;
  }

}
