package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.log4j.xml.DOMConfigurator;

import fr.turtlesport.Configuration;
import fr.turtlesport.Launcher;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DatabaseManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogCompareRun extends JDialog {

	private JComboBox jComboxBoxFirstRun;
	private JComboBox jComboxBoxSecondRun;
	private JPanel jPanelRun;

	public JDialogCompareRun() {
		super();
		initialize();
	}

	public JDialogCompareRun(Frame owner) {
		super(owner, true);
		initialize();
	}

	private void initialize() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getJPanelRun(), BorderLayout.NORTH);
		setContentPane(contentPane);
		this.setSize(880, 700);
	}

	private JPanel getJPanelRun() {
		if (jPanelRun == null) {
			jPanelRun = new JPanel();
			jPanelRun.setLayout(new BoxLayout(jPanelRun, BoxLayout.X_AXIS));

			jComboxBoxFirstRun = new JComboBox();
			jComboxBoxFirstRun.setFont(GuiFont.FONT_PLAIN);

			jComboxBoxSecondRun = new JComboBox();
			jComboxBoxSecondRun.setFont(GuiFont.FONT_PLAIN);

			List<DataRun> list = null;
			try {
				list = RunTableManager.getInstance().retreive(-1);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			MyDefautlCellRenderer renderer = new MyDefautlCellRenderer();
			jComboxBoxFirstRun.setRenderer(renderer);
			jComboxBoxSecondRun.setRenderer(renderer);

			jComboxBoxSecondRun.setModel(new MyDefaultComboBoxModel(list));
			jComboxBoxFirstRun.setModel(new MyDefaultComboBoxModel(list));

			jPanelRun.add(jComboxBoxFirstRun);
			jPanelRun.add(jComboxBoxSecondRun);

		}
		return jPanelRun;
	}

	private class MyDefautlCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			JLabel cmp = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);

			if (value != null && value instanceof DataRun) {
				DataRun dataRun = (DataRun) value;

				String text = LanguageManager.getManager().getCurrentLang()
						.getDateFormatter().format(dataRun.getTime())
						+ " "
						+ new SimpleDateFormat("kk:mm:ss").format(dataRun
								.getTime()) + " ";
				try {
					text += DistanceUnit.formatWithUnit(dataRun
							.getComputeDistanceTot());
				} catch (SQLException e) {
				}

				cmp.setText(text);
			}
			
			return cmp;
		}
	}

	private class MyDefaultComboBoxModel extends DefaultComboBoxModel {
		private List<DataRun> list;

		public MyDefaultComboBoxModel(List<DataRun> list) {
			this.list = list;
			System.out.println(list.size());
		}

		@Override
		public Object getElementAt(int index) {
			return list.get(index);
		}

		@Override
		public int getSize() {
			return list.size();
		}
	}

	public static void main(String[] args) {
		try {
			Location.initialize();
			// positionne les traces
			String dirExe = Location.dirNameExecution(Launcher.class);
			File file = new File(dirExe, "log4J.xml");
			DOMConfigurator.configure(file.toURI().toURL());

			Configuration.initialize();

			DatabaseManager.initDatabase(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JDialogCompareRun dlg = new JDialogCompareRun();
		dlg.setVisible(true);
	}
}
