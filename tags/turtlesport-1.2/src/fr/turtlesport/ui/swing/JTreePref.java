package fr.turtlesport.ui.swing;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.turtlesport.ui.swing.model.ModelPref;

/**
 * Arbre de pr&eacute;f&eacute;rence.
 * 
 * @author Denis Apparicio
 * 
 */
public class JTreePref extends JTree implements TreeSelectionListener {
  private JDialogPreference        owner;

  private DefaultMutableTreeNode   rootNode;

  private DefaultTreeModel         treeModel;

  private JDynamicTreeCellRenderer msgTreeCellRenderer = new JDynamicTreeCellRenderer();

  /**
   * 
   */
  public JTreePref(JDialogPreference owner) {
    super();
    this.owner = owner;
    initialize();
  }

  private void initialize() {
    rootNode = new DefaultMutableTreeNode("");
    treeModel = new DefaultTreeModel(rootNode);
    setModel(treeModel);

    getSelectionModel()
        .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setCellRenderer(msgTreeCellRenderer);
    setShowsRootHandles(true);
    setEditable(false);
    setRootVisible(false);

    // evenement
    addTreeSelectionListener(this);
  }

  /**
   * Remove all nodes except the root node.
   */
  public void clear() {
    rootNode.removeAllChildren();
    treeModel.reload();
  }

  /**
   * Ajoute un enfant au noeud courant.
   * 
   * @param child
   *          objet à ajouter
   * @return le noeud fils
   */
  public DefaultMutableTreeNode addObject(Object child) {
    DefaultMutableTreeNode parentNode = null;
    TreePath parentPath = getSelectionPath();

    if (parentPath == null) {
      parentNode = rootNode;
    }
    else {
      parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
    }
    return addObject(parentNode, child, true);
  }

  /**
   * Ajoute un enfant a un noeud.
   * 
   * @param parent
   *          noeud parent
   * @param child
   *          objet à ajouter
   * @return le noeud fils
   */
  public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                          Object child) {
    return addObject(parent, child, true);
  }

  /**
   * 
   * @param parent
   * @param child
   * @param shouldBeVisible
   * @return le noeud fils
   */
  public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                          Object child,
                                          boolean shouldBeVisible) {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

    if (parent == null) {
      parent = rootNode;
    }

    treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

    // Make sure the user can see the lovely new node.
    if (shouldBeVisible) {
      scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    return childNode;
  }

  /**
   * 
   * @return
   */
  public ModelPref getFirstChild() {
    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeModel
        .getChild(rootNode, 0);
    return (ModelPref) childNode.getUserObject();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
   */
  public void valueChanged(TreeSelectionEvent e) {
    MainGui.getWindow().beforeRunnableSwing();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
        if (node != null && (node.getUserObject() != null)
            && (node.getUserObject() instanceof ModelPref)) {
          ModelPref model = (ModelPref) node.getUserObject();
          model.updateView(owner);
        }
      }
    });

    MainGui.getWindow().afterRunnableSwing();
  }

  /**
   * 
   * @author Denis Apparicio
   * 
   */
  private class JDynamicTreeCellRenderer extends DefaultTreeCellRenderer {

    public JDynamicTreeCellRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
      super.getTreeCellRendererComponent(tree,
                                         value,
                                         sel,
                                         expanded,
                                         leaf,
                                         row,
                                         hasFocus);
      try {
        setIcon(null);
        setFont(GuiFont.FONT_PLAIN);
      }
      catch (Exception e) {
      }
      return this;
    }
  }

}