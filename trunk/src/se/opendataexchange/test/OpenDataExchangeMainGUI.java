package se.opendataexchange.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import se.opendataexchange.common.AddressSpace;
import se.opendataexchange.common.AddressUnit;
import se.opendataexchange.common.AddressValue;
import se.opendataexchange.common.ErrorInfo;
import se.opendataexchange.common.IAddressValueChanged;
import se.opendataexchange.common.InvalidAddressException;
import se.opendataexchange.controller.OpenDataExchangeController;

public class OpenDataExchangeMainGUI extends JFrame implements ActionListener, IAddressValueChanged{//, IControlSystemStatusChanged{

	private static final long serialVersionUID = 6479727847879012241L;
	
	private JLabel errCode, statusCode;
	private JTextArea errorLog;
	private JTable table;
	private ODETable tableData;
	private JTree tree;
	private JButton addButton, delButton;
	private OpenDataExchangeController controller;
	
	public OpenDataExchangeMainGUI(String controllerPath){
		controller = OpenDataExchangeController.getController(controllerPath);
		init();
	}

	public OpenDataExchangeMainGUI(OpenDataExchangeController controller) {
		this.controller = controller;
		init();
	}

	private void init() {
		controller.init();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel dataPanel = new JPanel();
		JPanel diagPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel errorPanel = new JPanel();
		JPanel statusPanel = new JPanel();
		JLabel statusLabel = new JLabel("ErrorCode: ");
		JLabel errLabel = new JLabel("ErrorCode: "); 
		//statusCode = new JLabel("0");
		statusCode = new JLabel("Not Implemented");
		//errCode = new JLabel("0");
		errCode = new JLabel("Not Implemented");
		errorLog = new JTextArea(10, 20);
		
		/* DataTab start */
		dataPanel.setLayout(new BorderLayout());
		statusPanel.add(statusLabel);
		statusPanel.add(statusCode);
		dataPanel.add(statusPanel, BorderLayout.NORTH);
		//Create Tree
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("AddressSpace");
		buildTree(top);
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scrollTree = new JScrollPane(tree);
		scrollTree.setPreferredSize(new Dimension(300, 150));
		dataPanel.add(scrollTree, BorderLayout.WEST);
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		addButton = new JButton("->");
		delButton = new JButton("<-");
		addButton.addActionListener(this);
		delButton.addActionListener(this);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 100)));
		buttonPanel.add(addButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		buttonPanel.add(delButton);
		dataPanel.add(buttonPanel, BorderLayout.CENTER);
		
		tableData = new ODETable(controller);
		table = new JTable(tableData);
		JScrollPane scrollPane = new JScrollPane(table);
		dataPanel.add(scrollPane, BorderLayout.EAST);
		/* DataTab end */
		
		/* DiagTab start */
		diagPanel.setLayout(new BorderLayout());
		errorLog.setEditable(false);
		errorPanel.add(errLabel);
		errorPanel.add(errCode);
		diagPanel.add(errorPanel, BorderLayout.NORTH);
		diagPanel.add(errorLog, BorderLayout.CENTER);
		/* DiagTab end */
		tabbedPane.addTab("Data", dataPanel);
		tabbedPane.addTab("Diagnosis", diagPanel);
		add(tabbedPane);
		pack();
		OpenDataExchangeController.getErrorValue().subscribe(this);
		setVisible(true);
		//controller.addControlSystemStatusListener(this);
	}
	
	private void buildTree(DefaultMutableTreeNode parent) {
		for(AddressSpace space : controller.getAddressSpaces()){
			for(AddressValue value : space.getAddressValues()){
				if(!value.equals(OpenDataExchangeController.getErrorValue()))
					parent.add(new ValueNode(value));
			}
			for(AddressUnit unit : space.getAddressUnits()){
				DefaultMutableTreeNode newparent = new UnitNode(unit);
				parent.add(newparent);
				buildTree(unit, newparent);
			}
		}
	}
		
	private void buildTree(AddressUnit unit, DefaultMutableTreeNode parent){
		for(AddressValue value : unit.getMapValues().values()){
			if(!value.equals(OpenDataExchangeController.getErrorValue()))
				parent.add(new ValueNode(value));
		}
		for(AddressUnit units : unit.getMapUnits().values()){
			DefaultMutableTreeNode newparent = new UnitNode(units);
			parent.add(newparent);
			buildTree(unit, newparent);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(delButton)){
			for(int row : table.getSelectedRows())
				tableData.unsubscribe(row);
		} else if(e.getSource().equals(addButton)){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if(node == null)
				return;
			Object nodeInfo = node.getUserObject();
		    if (node.isLeaf()) {
		        AddressValue val = (AddressValue)nodeInfo;
		        tableData.subscribe(val);
		    } else if(node.getLevel() > 0){
		    	AddressUnit unit = (AddressUnit)nodeInfo;
		    	tableData.subscribe(unit);
		    } else{
		    	for(AddressSpace space : controller.getAddressSpaces()){
		    		for(AddressValue value : space.getAddressValues()){
		    			if(!value.equals(OpenDataExchangeController.getErrorValue()))
		    				tableData.subscribe(value);
		    		}
		    		for(AddressUnit unit : space.getAddressUnits()){
		    			tableData.subscribe(unit);
		    		}
		    	}
		    }
		}
	}
	
	public void valueHasChanged(String name, Object value, Date ts) {
		/*ErrorInfo e = (ErrorInfo)value.getValue();
		String time = (DateFormat.getDateTimeInstance()).format(value.getTimestamp());
		String mes = e.getMessage();
		Long code = e.getErrorCode();
		if(code != Long.parseLong(errCode.getText())){
			errCode.setText(code.toString());
			statusCode.setText(code.toString());
			errorLog.append(time + "  :  " + code + ": " + mes + "\n");
		}*/
		//errorLog.append(time + "  :  " + mes + "\n");
	}
	
	private class ValueNode extends DefaultMutableTreeNode{
		private static final long serialVersionUID = 4633551883292356148L;

		public ValueNode(AddressValue value) {
			super(value);
		}

		public String toString(){
			String temp[] = ((AddressValue) userObject).getName().split("/");
			return temp[temp.length-1];
		}
	}
	
	private class UnitNode extends DefaultMutableTreeNode{
		private static final long serialVersionUID = 2805380699062877283L;

		public UnitNode(AddressUnit unit) {
			super(unit);
		}

		public String toString(){
			String temp[] = ((AddressUnit) userObject).getName().split("/");
			return temp[temp.length-1];
		}
	}
	
	private class ODETable extends AbstractTableModel implements IAddressValueChanged{

		private static final long serialVersionUID = 4827224874788038400L;
		private OpenDataExchangeController controller;
		private int rows;
		private HashMap<Integer, String> subscribed;
		private HashMap<String, Object> oldvalues;

		public ODETable(OpenDataExchangeController controller) {
			this.controller = controller;
			subscribed = new HashMap<Integer, String>();
			oldvalues = new HashMap<String, Object>();
			rows = countValues();
		}
		
		public void subscribe(AddressUnit unit) {
			for(AddressValue value : unit.getMapValues().values())
				subscribe(value);
		}

		public void unsubscribe(int row) {
			String removed = subscribed.remove(row);
			if(removed == null)
				return;
			try {
				controller.unsubscribeForAddressValue(removed, this);
				//removed.unsubscribe(this);
			} catch (InvalidAddressException e) {
				e.printStackTrace();
			}
			repaint();
		}

		public void subscribe(AddressValue value){
			if(subscribed.containsValue(value))
				return;
			int i = 0;
			while(subscribed.containsKey(i))
				++i;
			try {
				controller.subscribeForAddressValue(value.getName(), this);
				subscribed.put(i, value.getName());
			} catch (InvalidAddressException e) {
				e.printStackTrace();
			}
			//value.subscribe(this);
			repaint();
		}

		private int countValues() {
			int i=0;
			for(AddressSpace space : controller.getAddressSpaces()){
				i += space.getAddressValues().length;
				for(AddressUnit unit : space.getAddressUnits())
					i += countUnit(unit);
			}
			return i;
		}
		
		private int countUnit(AddressUnit unit){
			int i = unit.getMapValues().size();
			for(AddressUnit units : unit.getMapUnits().values())
					i += countUnit(units);
			return i;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return rows;
		}

		@Override
		public Object getValueAt(int row, int col) {
			String name = subscribed.get(row);
			if(name == null)
				return null;
			switch(col){
			case 0:
				String temp[] = name.split("/");
				return temp[temp.length-1];
			case 1:
				if(name.equals(OpenDataExchangeController.getErrorValue().getName()) && oldvalues.get(name) != null)
					return ((ErrorInfo) oldvalues.get(name)).getMessage();
				return oldvalues.get(name);
			}
			return null;
		}
		
		@Override
		public String getColumnName(int columnIndex){
			switch(columnIndex){
			case 0:
				return "ValueAddress";
			case 1:
				return "Value";
			}
			return "";
		}

		@Override
		public void valueHasChanged(String name, Object value, Date timestamp) {
			if(subscribed.containsValue(name) && value != null && !value.equals(oldvalues.get(name))){
				oldvalues.put(name, value);
				repaint();
			}
		}
	}

}
