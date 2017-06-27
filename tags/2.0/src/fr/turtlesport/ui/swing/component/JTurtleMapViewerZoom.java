package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.JXPanel;

import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;

/**
 * @author Denis Apparicio
 * 
 */
public class JTurtleMapViewerZoom extends JXPanel {

  private JButtonCustom    jButtonZoomPlus;

  private JButtonCustom    jButtonZoomMoins;

  private Dimension        dimButton = new Dimension(20, 20);

  private JTurtleMapViewer mainMap;

  private JPanel           jPanelButton;

  /**
   * 
   */
  public JTurtleMapViewerZoom() {
    initialize();
    mainMap.setRestrictOutsidePanning(true);
  }

  /**
   * Restitue la map.
   * 
   * @return la map.
   */
  public JXMapViewer getMainMap() {
    return this.mainMap;
  }

  /**
   * Valorise le zoom.
   * 
   * @param zoom
   *          la nouvelle valeur.
   */
  public void setZoom(int zoom) {
    mainMap.setZoom(zoom);
  }

  /**
   * @return Restitue le zoom.
   */
  public int getZoom() {
    return mainMap.getZoom();
  }

  private void initialize() {
    GridBagConstraints gridBagConstraints;

    mainMap = new JTurtleMapViewer();
    mainMap.setLayout(new GridBagLayout());
    mainMap.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    mainMap.add(getJPanelButton(), gridBagConstraints);

    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    setLayout(new BorderLayout(0, 0));
    add(mainMap, BorderLayout.CENTER);

    // Evenements
    jButtonZoomMoins.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        setZoom(getZoom() + 1);
      }
    });
    jButtonZoomPlus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        setZoom(getZoom() - 1);
      }
    });
  }

  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      jPanelButton = new JPanel();
      jPanelButton.setOpaque(false);
      jPanelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));

      jPanelButton.add(getJButtonZoomMoins());
      jPanelButton.add(getJButtonZoomPlus());
    }
    return jPanelButton;
  }

  /**
   * This method initializes jButtonZoomPlus
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonZoomPlus() {
    if (jButtonZoomPlus == null) {
      jButtonZoomPlus = new JButtonCustom();
      jButtonZoomPlus.setIcon(ImagesDiagramRepository.getImageIcon("plus.png"));

      jButtonZoomPlus.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomPlus.setMaximumSize(dimButton);
      jButtonZoomPlus.setMinimumSize(dimButton);
      jButtonZoomPlus.setOpaque(false);
      jButtonZoomPlus.setPreferredSize(dimButton);
    }
    return jButtonZoomPlus;
  }

  /**
   * This method initializes jButtonZoomMoins
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonZoomMoins() {
    if (jButtonZoomMoins == null) {
      jButtonZoomMoins = new JButtonCustom();
      jButtonZoomMoins.setIcon(ImagesDiagramRepository
          .getImageIcon("minus.png"));

      jButtonZoomMoins.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomMoins.setMaximumSize(dimButton);
      jButtonZoomMoins.setMinimumSize(dimButton);
      jButtonZoomMoins.setOpaque(false);
      jButtonZoomMoins.setPreferredSize(dimButton);
    }
    return jButtonZoomMoins;
  }
  
}