/**
* DonGUI - Graphic interface of DoOrNot V0.1
* @author Lin Daqi (A0119423L)
*/

package doornot.gui;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import doornot.logic.*;
import doornot.storage.IDonTask;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JList;
import javax.swing.JLabel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JPanel;

public class DonGUI {

	private JFrame frmDoornot;
	DonLogic donLogic = new DonLogic();
	int selectedTask;
	private String display = "";
	private JTextField textField;
	private JButton sendButton;
	private JScrollPane scrollPane_textarea;
	private JTextArea textArea;
	private JList<String> list;
	private JScrollPane scrollPane_list;
	private JButton saveButton;
	private JLabel lblTaskList;
	private JButton helpButton;
	private JButton deleteButton;
	private JPanel panel;
	private Image logo;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DonGUI window = new DonGUI();
					window.frmDoornot.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DonGUI() {	
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void renew(String cmd){
		if(cmd.equals("exit")) {
			donLogic.saveToDrive();
			System.exit(0);
		}
		IDonResponse rp = donLogic.runCommand(cmd);
		String fb = "";
		if(rp.hasMessages()) {
			for(int i = 0; i < rp.getMessages().size(); i++){
				fb += rp.getMessages().get(i) + "\n";
			}
		}
		if(rp.hasTasks() && rp.getResponseType() == IDonResponse.ResponseType.SEARCH_SUCCESS) {
			fb += "The following tasks match the search:\n";
			for(int i = 0; i < rp.getTasks().size(); i++) {
				fb += String.valueOf(rp.getTasks().get(i).getID()) + ". "
						+ rp.getTasks().get(i).getTitle() + "\n";
			}
		}
		display += fb;
		textArea.setText(display);
		textField.setText("");
		renewList();
	}
	
	private String printDate(Calendar cal){
		String rp = "";
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("dd/MM/yyyy hh:mm");
		rp = df.format(cal.getTime());
		return rp;
	}
	
	private void renewList(){
		ArrayList<String> arr = new ArrayList<String>();
		List<IDonTask> tasks = donLogic.getTaskList();
		for(int i = 0; i < tasks.size(); i++){
			String newEntry = "";
			newEntry += String.valueOf(tasks.get(i).getID()) + ". " + tasks.get(i).getTitle();
			if(tasks.get(i).getType() == IDonTask.TaskType.DEADLINE){
				newEntry += " (Deadline: " + printDate(tasks.get(i).getStartDate()) + ")";
			} else if (tasks.get(i).getType() == IDonTask.TaskType.DURATION){
				newEntry += " (Duration: " + printDate(tasks.get(i).getStartDate()) +
						" -- " + printDate(tasks.get(i).getEndDate()) + ")"; 
			} else {
				newEntry += " (floating task)";
			}
			if(tasks.get(i).getStatus()) newEntry += "[done]";
			arr.add(newEntry);
		}
		list.setListData(Arrays.copyOf(arr.toArray(), arr.size(), String[].class));
	}
	private void initialize() {
		frmDoornot = new JFrame();
		frmDoornot.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				donLogic.saveToDrive();
			}
		});
		frmDoornot.setTitle("DoOrNot v0.1");
		frmDoornot.setBounds(100, 100, 500, 400);
		frmDoornot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDoornot.addWindowListener(new WindowEventHandler());

		frmDoornot.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("92dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(57dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(112dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(0dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		scrollPane_textarea = new JScrollPane();
		frmDoornot.getContentPane().add(scrollPane_textarea, "2, 2, 4, 1, fill, fill");
		
		textArea = new JTextArea();
		scrollPane_textarea.setViewportView(textArea);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					renew(textField.getText());
				}
			}
		});
		
		try {
			logo = ImageIO.read(new File("doornot.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		panel = new JPanel() {
            protected void paintComponent(Graphics g) {
    			Image resizedLogo = logo.getScaledInstance(panel.getWidth(),panel.getHeight(),Image.SCALE_SMOOTH);
                super.paintComponent(g);
                g.drawImage(resizedLogo, 0, 0, null);
            }
		};
		frmDoornot.getContentPane().add(panel, "6, 2, fill, fill");
		
		
		
		lblTaskList = new JLabel("Task List");
		frmDoornot.getContentPane().add(lblTaskList, "2, 4");
		
		scrollPane_list = new JScrollPane();
		frmDoornot.getContentPane().add(scrollPane_list, "2, 6, 4, 5, fill, fill");
		
		list = new JList<String>();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					int index = ((JList<String>)e.getSource()).getSelectedIndex();
					selectedTask = donLogic.getTaskList().get(index).getID();
				}
			}
		});
		scrollPane_list.setViewportView(list);
		
		saveButton = new JButton("save to disk");
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				donLogic.saveToDrive();
				display += "Your schedule is saved.\n";
				textArea.setText(display);
			}
		});
		
		deleteButton = new JButton("Delete");
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				renew("delete " + String.valueOf(selectedTask));
			}
		});
		frmDoornot.getContentPane().add(deleteButton, "6, 6");
		
		helpButton = new JButton("Help");
		helpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				display += "Type add \"[Task Name]\" to add a floating task"
						+ "\nType add \"[Task Name]\" at DDMMYYYY_hhmm(or DDMMYYYY) to add a deadline task"
						+ "\nType add \"[Task Name]\" from DDMMYYYY_hhmm(or DDMMYYYY) to DDMMYYYY_hhmm(or DDMMYYYY) to add a duration event"
						+ "\nType delete [Task Name] to delete a task, if more than one task match the name, a list of "
						+ "ids of the tasks will be shown and you can choose an id"
						+ "\nType delete [id] to delete a task with the specified id"
						+ "\nType search [Task Name] to search tasks, a list of ids of the tasks that match the name will be shown"
						+ "\nType search DDMMYYYY_hhmm or DDMMYYYY to search for tasks that start(due) or occur on that date/time"
						+ "\nType edit [id] [New Task Name] to change the name of an existing task" 
						+ "\n Type edit [Task Name] [New Task Name] to change the name of an existing task, if more than one task match"
						+ "the name, a list of ids of the tasks will be shown and you can choose an id"
						+ "\n Type edit [id]/[Task Name] [New Start Date] ([New End State]) to change the start date(deadline) (and end"
						+ "date if provided) of the specified task"
						+ "\n Type undo to undo the former action"
						+ "\n Type mark [id]/[Task Name] to mark a task done/undone"
						+ "\n Shortcuts: add/a search/s delete/del/d edit/ed/e mark/m";
				textArea.setText(display);
			}
		});
		frmDoornot.getContentPane().add(helpButton, "6, 8");
		frmDoornot.getContentPane().add(saveButton, "6, 10");
		frmDoornot.getContentPane().add(textField, "2, 12, 4, 1, fill, default");
		textField.setColumns(10);
		renewList();
		
		
		sendButton = new JButton("Send");
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				renew(textField.getText());
			}
		});
		frmDoornot.getContentPane().add(sendButton, "6, 12");

	}

	class WindowEventHandler extends WindowAdapter {
		public void windowClosing (WindowEvent e) {
			donLogic.saveToDrive();
		}
	}
}
