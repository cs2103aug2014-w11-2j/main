/**
 * DonGUI - Graphic interface of DoOrNot V0.3
 * @author Lin Daqi (A0119423L)
 */

package doornot.gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import doornot.logic.*;
import doornot.storage.*;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Stack;

import javax.swing.Timer;
import javax.swing.JList;
import javax.swing.JLabel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class DonGUIV2 {

	private JFrame frmDoornot;
	DonLogic donLogic = new DonLogic();
	int selectedTask;
	private String display = "";
	private String lastMsg = "";
	private JTextField textField;
	private JButton sendButton;
	private JScrollPane scrollPane_textarea;
	private JTextArea textArea;
	private JList<IDonTask> list;
	private JList<Integer> typeList;
	private JScrollPane scrollPane_list;
	private JLabel lblTaskList;
	private JPanel panel;
	private JTextPane infoPane;
	private JLabel overdueLabel;
	private JLabel monthyearLabel;
	private JLabel dateLabel;
	private Image logo;
	private Integer[] placeholder = { 1, 2, 3, 4, 5 };
	private List<IDonTask> guiTaskList;
	private List<IDonTask> overdueList;
	private List<IDonTask> todayList;
	private List<IDonTask> weekList;
	private List<IDonTask> farList;
	private List<IDonTask> floatList;
	private List<IDonTask> searchList;
	private Stack<String> cmdStack = new Stack<String>();
	private JPanel datePanel;
	private int selectedPage = 1;
	private int selectedCmd = -1;
	private JLabel searchLabel;
	private int countdown = 2000;
	private Timer timer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DonGUIV2 window = new DonGUIV2();
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
	public DonGUIV2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void sortByDate() {
		Collections.sort(guiTaskList, new TimeComparator());
	}
	
	private void branchToPage(String cmd){
		//typeList.clearSelection();
		switch(cmd){
			case "c":
				selectedPage = 7;
				list.clearSelection();
				//typeList.setListData(placeholder);
				break;
			case "t":
				selectedPage = 1;
				typeList.setSelectedIndex(0);
				break;
			case "w":
				selectedPage = 2;
				typeList.setSelectedIndex(1);
				break;
			case "u":
				selectedPage = 3;
				typeList.setSelectedIndex(2);
				break;
			case "f":
				selectedPage = 4;
				typeList.setSelectedIndex(3);
				break;
			case "l":
				selectedPage = 5;
				typeList.setSelectedIndex(4);
				break;
			case "o":
				if(overdueList != null && overdueList.size() != 0) {
					selectedPage = 0;
					typeList.clearSelection();
				}
				else {
					infoPane.setText("No overdue tasks!");
					infoPane.setVisible(true);
					countdown = 2000;
					timer = new Timer(1, al);
					timer.start();
					return;
				}
				break;
			case "r":
				if(searchList != null && searchList.size() != 0) {
					selectedPage = 6;
					typeList.clearSelection();
				}
				else {
					infoPane.setText("No search results!");
					infoPane.setVisible(true);
					countdown = 2000;
					timer = new Timer(1, al);
					timer.start();
					return;
				}
				break;
			default:
		}
		typeList.setCellRenderer(new TypeCellRenderer());
		setTypeData();
	}

	private void renew(String cmd) {
		if (cmd.equals("exit")) {
			donLogic.saveToDrive();
			System.exit(0);
		}
		if (cmd.equals("c") || cmd.equals("t") || cmd.equals("w") || cmd.equals("u") || 
			cmd.equals("f") || cmd.equals("l") || cmd.equals("o") || cmd.equals("r")) {
			branchToPage(cmd);
			return;
		} 
		IDonResponse rp = donLogic.runCommand(cmd);
		assert rp != null;
		String fb = "";
		if (rp.hasMessages()) {
			lastMsg = rp.getMessages().get(0);
			for (int i = 0; i < rp.getMessages().size(); i++) {
				fb += rp.getMessages().get(i) + "\n";
			}
		}
		if (rp.getResponseType() == IDonResponse.ResponseType.HELP) {
			selectedPage = 7;
			setTypeData();
		}
		if (rp.hasTasks()
				&& (rp.getResponseType() == IDonResponse.ResponseType.SEARCH_SUCCESS
						|| rp.getResponseType() == IDonResponse.ResponseType.DEL_FAILURE || rp
						.getResponseType() == IDonResponse.ResponseType.EDIT_FAILURE)) {
			scrollPane_textarea.setVisible(false);
			textArea.setVisible(false);

			fb += "The following tasks match the search:\n";
			for (int i = 0; i < rp.getTasks().size(); i++) {
				fb += String.valueOf(rp.getTasks().get(i).getID()) + ". "
						+ rp.getTasks().get(i).getTitle() + "\n";
			}
			searchList = new ArrayList<IDonTask>();
			for (IDonTask task : rp.getTasks()) {
				searchList.add(task);
			}
			selectedPage = 6;
			setTypeData();
		}
		if (rp.getResponseType() == IDonResponse.ResponseType.DEL_SUCCESS) {
			int selected = -1;
			if (searchList != null) {
				for (int i = 0; i < searchList.size(); i++) {
					if (searchList.get(i).getID() == rp.getTasks().get(0)
							.getID()) {
						selected = i;
					}
				}
				if (selected != -1)
					searchList.remove(selected);
			}
		}
		display += fb;
		infoPane.setText(lastMsg);
		infoPane.setVisible(true);
		countdown = 2000;
		timer = new Timer(1, al);
		timer.start();
		textArea.setText(display);
		textField.setText("");
		renewList();
	}

	private String printDate(Calendar cal, boolean isTimeUsed) {
		SimpleDateFormat df = new SimpleDateFormat();
		if(isTimeUsed) df.applyPattern("dd/MM/yyyy HH:mm");
		else df.applyPattern("dd/MM/yyyy");
		return df.format(cal.getTime());
	}

	private String printTodayTime(Calendar cal) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("HH:mm");
		return df.format(cal.getTime());
	}

	private String printShortDate(Calendar cal) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("dd/MM");
		return df.format(cal.getTime());
	}
	
	private String printDayInWeek(Calendar cal) {
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("EEE");
		return df.format(cal.getTime());
	}

	private void setTypeData() {
		textArea.setVisible(false);
		scrollPane_textarea.setVisible(false);
		scrollPane_list.setVisible(true);
		list.setVisible(true);
		searchLabel.setBorder(null);
		overdueLabel.setBorder(null);
		if (selectedPage == 0) {
			overdueLabel.setBorder(new LineBorder(Color.white, 2));
			list.setListData(Arrays.copyOf(overdueList.toArray(),
					overdueList.size(), IDonTask[].class));
			lblTaskList.setText("Task List: overdue tasks");
		} else if (selectedPage == 1) {
			list.setListData(Arrays.copyOf(todayList.toArray(),
					todayList.size(), IDonTask[].class));
			lblTaskList.setText("Task List: today tasks");
		} else if (selectedPage == 2) {
			list.setListData(Arrays.copyOf(weekList.toArray(), weekList.size(),
					IDonTask[].class));
			lblTaskList.setText("Task List: tasks in 7 days");
		} else if (selectedPage == 3) {
			list.setListData(Arrays.copyOf(farList.toArray(), farList.size(),
					IDonTask[].class));
			lblTaskList.setText("Task List: tasks in future (after 7 days)");
		} else if (selectedPage == 4) {
			list.setListData(Arrays.copyOf(floatList.toArray(),
					floatList.size(), IDonTask[].class));
			lblTaskList.setText("Task List: floating tasks");
		} else if (selectedPage == 5) {
			list.setListData(Arrays.copyOf(guiTaskList.toArray(),
					guiTaskList.size(), IDonTask[].class));
			lblTaskList.setText("Task List: all tasks");
		} else if (selectedPage == 6) {
			searchLabel.setBorder(new LineBorder(Color.white, 2));
			list.setListData(Arrays.copyOf(searchList.toArray(),
					searchList.size(), IDonTask[].class));
			searchLabel.setText("Search results: " + searchList.size());
			lblTaskList.setText("Task List: search results");
		} else if (selectedPage == 7) {
			lblTaskList.setText("Output Console");
			list.setVisible(false);
			scrollPane_list.setVisible(false);
			scrollPane_textarea.setVisible(true);
			textArea.setVisible(true);
		}
	}

	private void renewList() {
		// ArrayList<String> arr = new ArrayList<String>();
		guiTaskList = donLogic.getTaskList();
		sortByDate();
		// list.setListData(Arrays.copyOf(guiTaskList.toArray(),
		// guiTaskList.size(), IDonTask[].class));
		parseType();
		setTypeData();
		typeList.setListData(placeholder);
		if (overdueList.size() == 0) {
			overdueLabel.setVisible(false);
		} else {
			overdueLabel.setVisible(true);
			overdueLabel.setText("overdue tasks: "
					+ new Integer(overdueList.size()).toString());
		}
	}

	private boolean dued(IDonTask task) {
		Calendar current = Calendar.getInstance();
		if (task.getEndDate() != null) {
			if (task.getEndDate().compareTo(current) < 0)
				return true;
			else
				return false;
		} else {
			if (task.getStartDate() != null) {
				if (task.getStartDate().compareTo(current) < 0)
					return true;
				else
					return false;
			} else {
				return false;
			}
		}
	}

	private boolean isToday(IDonTask task) {
		Calendar current = Calendar.getInstance();
		if (task.getType() == IDonTask.TaskType.DEADLINE) {
			if (isSameDay(task.getStartDate(), current))
				return true;
			else
				return false;
		} else if (task.getType() == IDonTask.TaskType.DURATION) {
			if (isWithinDays(current, task.getStartDate(), task.getEndDate()))
				return true;
			else
				return false;
		} else {
			return false;
		}
	}

	private boolean isWithinWeek(IDonTask task) {
		Calendar current = Calendar.getInstance();
		Calendar aWeekLater = Calendar.getInstance();
		aWeekLater.add(Calendar.DAY_OF_YEAR, 7);
		if (task.getType() == IDonTask.TaskType.DEADLINE) {
			if (isWithinDays(task.getStartDate(), current, aWeekLater))
				return true;
			else
				return false;
		} else if (task.getType() == IDonTask.TaskType.DURATION) {
			if (isBefore(task.getEndDate(), current)
					|| isAfter(task.getStartDate(), aWeekLater)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean isSameDay(Calendar c1, Calendar c2) {
		if (c1 == null || c2 == null)
			return false;
		else
			return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1
					.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
	}

	private boolean isBefore(Calendar c1, Calendar c2) {
		if (c1 == null || c2 == null)
			return false;
		else
			return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1
					.get(Calendar.DAY_OF_YEAR) < c2.get(Calendar.DAY_OF_YEAR))
					|| (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR));
	}

	private boolean isAfter(Calendar c1, Calendar c2) {
		if (c1 == null || c2 == null)
			return false;
		else
			return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1
					.get(Calendar.DAY_OF_YEAR) > c2.get(Calendar.DAY_OF_YEAR))
					|| (c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR));
	}

	private boolean isBeforeEqual(Calendar c1, Calendar c2) {
		if (c1 == null || c2 == null)
			return false;
		else
			return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1
					.get(Calendar.DAY_OF_YEAR) <= c2.get(Calendar.DAY_OF_YEAR))
					|| (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR));
	}

	private boolean isAfterEqual(Calendar c1, Calendar c2) {
		if (c1 == null || c2 == null)
			return false;
		else
			return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1
					.get(Calendar.DAY_OF_YEAR) >= c2.get(Calendar.DAY_OF_YEAR))
					|| (c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR));
	}

	private boolean isWithinDays(Calendar c, Calendar c1, Calendar c2) {
		if (c == null || c1 == null || c2 == null)
			return false;
		else
			return isBeforeEqual(c1, c) && isAfterEqual(c2, c);
	}

	private int dateDiff(Calendar c1, Calendar c2) {
		GregorianCalendar gc = new GregorianCalendar();
		if (c1 == null || c2 == null || isBefore(c1, c2))
			return -1;
		else {
			int diff = c1.get(Calendar.DAY_OF_YEAR)
					- c2.get(Calendar.DAY_OF_YEAR);
			if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
				return diff;
			} else {
				return gc.isLeapYear(c2.get(Calendar.YEAR)) ? diff + 366
						: diff + 365;
			}
		}
	}

	private void parseType() {
		overdueList = new ArrayList<IDonTask>();
		todayList = new ArrayList<IDonTask>();
		weekList = new ArrayList<IDonTask>();
		farList = new ArrayList<IDonTask>();
		floatList = new ArrayList<IDonTask>();

		for (IDonTask task : guiTaskList) {
			if (dued(task)) {
				if (!task.getStatus())
					overdueList.add(task);
			} else {
				if (isToday(task)) {
					todayList.add(task);
					weekList.add(task);
				} else if (isWithinWeek(task)) {
					weekList.add(task);
				} else if (task.getStartDate() == null) {
					floatList.add(task);
				} else {
					farList.add(task);
				}
			}
		}

	}

	ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			countdown--;
			if (countdown == 0) {
				infoPane.setVisible(false);
				timer.stop();
			}
		}
	};

	/*
	 * ActionListener al2 = new ActionListener() { public void
	 * actionPerformed(ActionEvent e){ SimpleDateFormat monthyearFormat = new
	 * SimpleDateFormat(); monthyearFormat.applyPattern("hh:mm:ss"); Calendar
	 * current = Calendar.getInstance();
	 * monthyearLabel.setText(monthyearFormat.format(current.getTime())); } };
	 */

	private void initialize() {

		frmDoornot = new JFrame();
		frmDoornot.getContentPane().setBackground(new Color(159, 197, 232));
		frmDoornot.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				donLogic.saveToDrive();
			}
		});
		frmDoornot.setTitle("DoOrNot v0.3");
		frmDoornot.setBounds(100, 100, 665, 520);
		frmDoornot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDoornot.addWindowListener(new WindowEventHandler());
		frmDoornot.getContentPane().setLayout(null);
		frmDoornot.setResizable(false);
		frmDoornot.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				selectedPage = 7;
				list.clearSelection();
				typeList.setListData(placeholder);
				setTypeData();
			}
		});

		frmDoornot.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point p = new Point(e.getX(), e.getY());
				if (p.x > 40 && p.x < 120 && p.y > 0 && p.y < 80) {
					panel.setVisible(true);
				} else {
					panel.setVisible(false);
				}
			}
		});

		lblTaskList = new JLabel("Task List: ");
		lblTaskList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTaskList.setBounds(164, 21, 363, 23);
		frmDoornot.getContentPane().add(lblTaskList);
		panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				Image resizedLogo = logo.getScaledInstance(panel.getWidth(),
						panel.getHeight(), Image.SCALE_SMOOTH);
				super.paintComponent(g);
				g.drawImage(resizedLogo, 0, 0, null);
			}
		};
		panel.setBounds(40, 0, 80, 80);
		panel.setBackground(new Color(159, 197, 232));
		panel.setVisible(false);

		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				selectedPage = 7;
				list.clearSelection();
				typeList.setListData(placeholder);
				setTypeData();
			}
		});

		frmDoornot.getContentPane().add(panel);

		scrollPane_list = new JScrollPane();
		scrollPane_list.setBounds(153, 50, 482, 353);
		frmDoornot.getContentPane().add(scrollPane_list);

		list = new JList<IDonTask>();
		scrollPane_list.setViewportView(list);
		list.setForeground(Color.RED);
		list.setBackground(SystemColor.control);
		list.setFixedCellHeight(70);
		list.setCellRenderer(new TaskCellRenderer());


		sendButton = new JButton("Send");
		sendButton.setBounds(551, 419, 72, 25);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				renew(textField.getText());
			}
		});
		frmDoornot.getContentPane().add(sendButton);
		// frmDoornot.getContentPane().add(helpButton);

		textField = new JTextField();
		textField.setBounds(153, 416, 386, 31);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					cmdStack.push(textField.getText());
					renew(textField.getText());
					selectedCmd = -1;
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (++selectedCmd > cmdStack.size() - 1)
						selectedCmd = cmdStack.size() - 1;
					if (!cmdStack.isEmpty())
						textField.setText(cmdStack.get(cmdStack.size() - 1
								- selectedCmd));
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (--selectedCmd < 0)
						selectedCmd = 0;
					if (!cmdStack.isEmpty())
						textField.setText(cmdStack.get(cmdStack.size() - 1
								- selectedCmd));
				}
			}
		});

		scrollPane_textarea = new JScrollPane();
		scrollPane_textarea.setBounds(153, 50, 482, 353);
		frmDoornot.getContentPane().add(scrollPane_textarea);
		textArea = new JTextArea();
		scrollPane_textarea.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setVisible(false);
		frmDoornot.getContentPane().add(textField);
		textField.setColumns(10);

		datePanel = new JPanel();
		datePanel.setBounds(40, 0, 80, 80);
		datePanel.setBackground(new Color(159, 197, 232));
		frmDoornot.getContentPane().add(datePanel);
		Calendar current = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("d");
		SimpleDateFormat monthyearFormat = new SimpleDateFormat();
		monthyearFormat.applyPattern("MMM YY");
		// monthyearFormat.applyPattern("hh:mm:ss");
		GridBagLayout gbl_datePanel = new GridBagLayout();
		gbl_datePanel.columnWidths = new int[] { 80, 0 };
		gbl_datePanel.rowHeights = new int[] { 50, 30 };
		// gbl_datePanel.columnWeights = new double[]{5, 3};
		gbl_datePanel.rowWeights = new double[] { 5, 3 };
		datePanel.setLayout(gbl_datePanel);

		monthyearLabel = new JLabel("New label");
		monthyearLabel.setForeground(Color.white);
		monthyearLabel.setText(monthyearFormat.format(current.getTime()));
		monthyearLabel.setFont(new Font("Verdana", Font.PLAIN, 15));
		monthyearLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_monthyearLabel = new GridBagConstraints();
		gbc_monthyearLabel.fill = GridBagConstraints.BOTH;
		gbc_monthyearLabel.gridx = 0;
		gbc_monthyearLabel.gridy = 1;
		datePanel.add(monthyearLabel, gbc_monthyearLabel);

		dateLabel = new JLabel();

		dateLabel.setText(dateFormat.format(current.getTime()));
		dateLabel.setForeground(Color.white);
		dateLabel.setFont(new Font("Verdana", Font.BOLD, 28));
		dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_dateLabel = new GridBagConstraints();
		gbc_dateLabel.anchor = GridBagConstraints.NORTH;
		gbc_dateLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_dateLabel.gridx = 0;
		gbc_dateLabel.gridy = 0;
		gbc_dateLabel.insets = new Insets(10, 0, 0, 0);
		datePanel.add(dateLabel, gbc_dateLabel);

		typeList = new JList<Integer>();
		typeList.setBounds(40, 88, 80, 315);
		typeList.setFixedCellHeight(63);
		typeList.setCellRenderer(new TypeCellRenderer());
		typeList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					int index = ((JList<Integer>) e.getSource())
							.getSelectedIndex();
					selectedPage = index + 1;
					setTypeData();
					typeList.setCellRenderer(new TypeCellRenderer());
				}
			}
		});

		frmDoornot.getContentPane().add(typeList);

		overdueLabel = new JLabel();
		overdueLabel.setBounds(15, 419, 136, 22);
		overdueLabel.setBackground(Color.red);
		overdueLabel.setOpaque(true);
		overdueLabel.setVerticalAlignment(SwingConstants.CENTER);
		overdueLabel.setForeground(Color.white);
		overdueLabel.setFont(new Font("VerDana", Font.BOLD, 12));
		overdueLabel.setVisible(false);
		overdueLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				typeList.clearSelection();
				selectedPage = 0;
				setTypeData();
			}
		});

		frmDoornot.getContentPane().add(overdueLabel);

		searchLabel = new JLabel("Search results: N/A");
		searchLabel.setBounds(448, 13, 139, 31);
		searchLabel.setBackground(Color.LIGHT_GRAY);
		searchLabel.setOpaque(true);
		searchLabel.setForeground(Color.white);
		searchLabel.setFont(new Font("VerDana", Font.BOLD, 12));
		searchLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (searchList != null) {
					typeList.clearSelection();
					selectedPage = 6;
					setTypeData();
				}
			}
		});
		frmDoornot.getContentPane().add(searchLabel);

		infoPane = new JTextPane();
		infoPane.setVisible(false);
		infoPane.setBounds(0, 447, 680, 38);
		infoPane.setBackground(new Color(58, 129, 186));
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(attribs, "Verdana");
		StyleConstants.setFontSize(attribs, 16);
		StyleConstants.setForeground(attribs, Color.white);
		// StyleConstants.setForeground(attribs, Color.blue);
		infoPane.setParagraphAttributes(attribs, true);

		frmDoornot.getContentPane().add(infoPane);

		try {
			logo = ImageIO.read(new File("doornot2.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		 * list.addListSelectionListener(new ListSelectionListener() { public
		 * void valueChanged(ListSelectionEvent e) {
		 * if(e.getValueIsAdjusting()){ int index =
		 * ((JList<IDonTask>)e.getSource()).getSelectedIndex(); selectedTask =
		 * donLogic.donStorage.getTaskList().get(index).getID(); } } });
		 */
		renewList();

	}

	class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			donLogic.saveToDrive();
		}
	}

	class TypeCellRenderer extends JLabel implements ListCellRenderer {

		JPanel p = new JPanel();
		JLabel amount = new JLabel();
		JLabel typename = new JLabel();

		// 0=overdue 1=today 2=week 3=far 4=floating 5=all

		public TypeCellRenderer() {
			GridBagLayout gbl = new GridBagLayout();
			gbl.rowHeights = new int[] { 42, 21 };
			gbl.rowWeights = new double[] { 2.0, 1.0 };
			p.setLayout(gbl);
			setOpaque(true);
			setIconTextGap(12);

			amount.setOpaque(false);
			typename.setOpaque(false);

			GridBagConstraints gbc_amount = new GridBagConstraints();
			gbc_amount.fill = GridBagConstraints.BOTH;
			gbc_amount.gridx = 0;
			gbc_amount.gridy = 0;
			p.add(amount, gbc_amount);

			GridBagConstraints gbc_typename = new GridBagConstraints();
			gbc_typename.fill = GridBagConstraints.BOTH;
			gbc_typename.gridx = 0;
			gbc_typename.gridy = 1;
			p.add(typename, gbc_typename);

		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected || selectedPage - 1 == index) {
				p.setBorder(new LineBorder(Color.white, 2));
			} else {
				p.setBorder(null);
			}
			amount.setHorizontalAlignment(SwingConstants.CENTER);
			typename.setHorizontalAlignment(SwingConstants.CENTER);
			Integer entry = (Integer) value;
			int typecode = entry.intValue();
			if (typecode == 1) {
				p.setBackground(new Color(58, 129, 186));
				amount.setFont(new Font("Verdana", Font.BOLD, 20));
				amount.setForeground(Color.white);
				amount.setText(new Integer(todayList.size()).toString());
				typename.setForeground(Color.white);
				typename.setText("Today");
			} else if (typecode == 2) {
				p.setBackground(new Color(58, 129, 200));
				amount.setFont(new Font("VerDana", Font.BOLD, 20));
				amount.setForeground(Color.white);
				amount.setText(new Integer(weekList.size()).toString());
				typename.setForeground(Color.white);
				typename.setText("In 7 days");
			} else if (typecode == 3) {
				p.setBackground(new Color(58, 129, 225));
				amount.setFont(new Font("VerDana", Font.BOLD, 20));
				amount.setForeground(Color.white);
				amount.setText(new Integer(farList.size()).toString());
				typename.setForeground(Color.white);
				typename.setText("future");
			} else if (typecode == 4) {
				p.setBackground(new Color(219, 110, 50));
				amount.setFont(new Font("VerDana", Font.BOLD, 20));
				amount.setForeground(Color.white);
				amount.setText(new Integer(floatList.size()).toString());
				typename.setForeground(Color.white);
				typename.setText("float");
			} else if (typecode == 5) {
				p.setBackground(Color.black);
				amount.setFont(new Font("VerDana", Font.BOLD, 20));
				amount.setForeground(Color.white);
				amount.setText(new Integer(guiTaskList.size()).toString());
				typename.setForeground(Color.white);
				typename.setText("all");
			} else {
				;
			}
			return p;
		}

	}

	class TaskCellRenderer extends JLabel implements ListCellRenderer {

		JPanel p = new JPanel();
		JTextPane id = new JTextPane();
		JTextPane content = new JTextPane();
		JTextPane timerange = new JTextPane();
		JTextPane label = new JTextPane();
		JTextPane realid = new JTextPane();
		
		public TaskCellRenderer() {
			GridBagLayout gbl = new GridBagLayout();
			gbl.columnWidths = new int[] {67, 268, 67, 38, 39};
			gbl.rowHeights = new int[] { 50, 20 };
			gbl.columnWeights = new double[] {67, 268, 67, 38, 39};
			gbl.rowWeights = new double[] { 5.0, 2.0 };
			// GridBagConstraints gbc = new GridBagConstraints();
			// gbc.insets = new Insets(0,5,0,0);
			p.setLayout(gbl);
			setOpaque(true);
			setIconTextGap(12);
			content.setOpaque(true);
			id.setOpaque(true);
			timerange.setOpaque(true);

			GridBagConstraints gbc_id = new GridBagConstraints();
			gbc_id.gridheight = 2;
			// gbc_textArea_2.insets = new Insets(0, 0, 0, 5);
			gbc_id.fill = GridBagConstraints.BOTH;
			gbc_id.gridx = 0;
			gbc_id.gridy = 0;
			gbc_id.insets = new Insets(0, 0, 5, 0);
			p.add(id, gbc_id);
			
			GridBagConstraints gbc_content = new GridBagConstraints();
			gbc_content.fill = GridBagConstraints.BOTH;
			gbc_content.gridx = 1;
			gbc_content.gridy = 0;
			gbc_content.gridwidth = 2;
			p.add(content, gbc_content);

			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.fill = GridBagConstraints.BOTH;
			gbc_label.gridx = 3;
			gbc_label.gridy = 0;
			gbc_label.gridwidth = 2;
			p.add(label, gbc_label);
			
			GridBagConstraints gbc_realid = new GridBagConstraints();
			gbc_realid.fill = GridBagConstraints.BOTH;
			gbc_realid.gridx = 4;
			gbc_realid.gridy = 1;
			gbc_realid.insets = new Insets(0, 0, 5, 0);
			p.add(realid, gbc_realid);
			
			GridBagConstraints gbc_timerange = new GridBagConstraints();
			gbc_timerange.fill = GridBagConstraints.BOTH;
			gbc_timerange.gridx = 1;
			gbc_timerange.gridy = 1;
			gbc_timerange.gridwidth = 3;
			gbc_timerange.insets = new Insets(0, 0, 5, 0);
			p.add(timerange, gbc_timerange);

		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Calendar current = Calendar.getInstance();
			IDonTask entry = (IDonTask) value;
			String entryText = "";
			String timeText = "";
			if (entry.getType() == IDonTask.TaskType.FLOATING) {
				entryText += entry.getTitle();
			} else if (entry.getType() == IDonTask.TaskType.DEADLINE) {
				entryText += entry.getTitle();
				timeText = " (Deadline: " + printDate(entry.getStartDate(), entry.isTimeUsed())
						+ ")";
			} else {
				entryText += entry.getTitle();
				timeText = " (Duration: " + printDate(entry.getStartDate(), entry.isTimeUsed())
						+ " -- " + printDate(entry.getEndDate(), entry.isTimeUsed()) + ")";
			}
			if (entry.getStatus()) {
				entryText += " " + "[Done]";
			}

			if (isToday(entry) && selectedPage == 1) {
				if (entry.getType() == IDonTask.TaskType.DURATION) {
					if (isSameDay(entry.getEndDate(), current)) {
						id.setText(printTodayTime(entry.getEndDate()));
					} else {
						int day = dateDiff(current, entry.getStartDate()) + 1;
						if (day % 10 == 1 && day % 100 != 11)
							id.setText(day + "st\nday");
						else if (day % 10 == 2 && day % 100 != 12)
							id.setText(day + "nd\nday");
						else if (day % 10 == 3 && day % 100 != 13)
							id.setText(day + "rd\nday");
						else
							id.setText(day + "th\nday");
					}
				} else if (entry.getType() == IDonTask.TaskType.DEADLINE) {
					id.setText(printTodayTime(entry.getStartDate()));
				}
			} else {
				if (entry.getType() == IDonTask.TaskType.FLOATING) {
					id.setText("F");
				} else if(entry.getType() == IDonTask.TaskType.DEADLINE){
					id.setText(printShortDate(entry.getStartDate())+ "\n" + printDayInWeek(entry.getStartDate()));
				} else {
					id.setText(printShortDate(entry.getStartDate()) + "\n" 
				+ printShortDate(entry.getEndDate()));
				}
			}
			
			realid.setText(new Integer(entry.getID()).toString());
			realid.setForeground(Color.white);
			realid.setBackground(Color.gray);
			realid.setFont(new Font("Verdana", Font.BOLD, 13));
			timerange.setForeground(Color.white);
			timerange.setBackground(Color.gray);
			timerange.setFont(new Font("Verdana", Font.PLAIN, 12));
			timerange.setText(timeText);
			content.setForeground(Color.white);
			content.setFont(new Font("Arial", Font.BOLD, 16));
			content.setText(entryText);
			id.setFont(new Font("Verdana", Font.BOLD, 17));
			id.setForeground(Color.white);
			// id.setText(new Integer(entry.getID()).toString());
			if (entry.getEndDate() != null) {
				if (entry.getEndDate().compareTo(current) < 0) {
					id.setBackground(new Color(204, 0, 0));
					content.setBackground(new Color(255, 33, 0));
					label.setBackground(new Color(255, 33, 0));
				} else {
					id.setBackground(new Color(58, 129, 186));
					content.setBackground(new Color(0, 168, 255));
					label.setBackground(new Color(0, 168, 255));
					timerange.setBackground(Color.gray);
				}
			} else {
				if (entry.getStartDate() != null) {
					if (entry.getStartDate().compareTo(current) < 0) {
						id.setBackground(new Color(204, 0, 0));
						content.setBackground(new Color(255, 33, 0));
						label.setBackground(new Color(255, 33, 0));
					} else {
						id.setBackground(new Color(58, 129, 186));
						content.setBackground(new Color(0, 168, 255));
						label.setBackground(new Color(0, 168, 255));
					}
				} else {
					id.setBackground(new Color(219, 110, 50));
					content.setBackground(Color.orange);
					label.setBackground(Color.orange);
				}
			}
			
			if(entry.getLabels() != null && entry.getLabels().size() != 0){
				String tag = "";
				for(int i = 0; i < entry.getLabels().size(); i++){
					tag += "  *";
					tag += entry.getLabels().get(i);
					if(i!=entry.getLabels().size()-1)tag += "\n";
				}
				label.setText(tag);
				label.setForeground(Color.white);
				label.setFont(new Font("Arial", Font.ITALIC, 11));
				//label.setBackground(Color.blue);
			} else {
				label.setText("");
			}

			setForeground(Color.white);
			return p;
		}
	}

	public static class TimeComparator implements Comparator<IDonTask> {
		@Override
		public int compare(IDonTask task1, IDonTask task2) {
			if (task1.getType() == IDonTask.TaskType.FLOATING) {
				if (task2.getType() == IDonTask.TaskType.FLOATING) {
					return task1.getTitle().compareTo(task2.getTitle());
				} else {
					return 1;
				}
			} else if (task1.getType() == IDonTask.TaskType.DEADLINE) {
				if (task2.getType() == IDonTask.TaskType.FLOATING) {
					return -1;
				} else if (task2.getType() == IDonTask.TaskType.DEADLINE) {
					int startDateComp = task1.getStartDate().compareTo(
							task2.getStartDate());
					return (startDateComp == 0) ? task1.getTitle().compareTo(
							task2.getTitle()) : startDateComp;
				} else {
					int mixDateComp = task1.getStartDate().compareTo(
							task2.getEndDate());
					return (mixDateComp == 0) ? -1 : mixDateComp;
				}
			} else {
				if (task2.getType() == IDonTask.TaskType.FLOATING) {
					return -1;
				} else if (task2.getType() == IDonTask.TaskType.DEADLINE) {
					int mixDateComp = task1.getEndDate().compareTo(
							task2.getStartDate());
					return (mixDateComp == 0) ? 1 : mixDateComp;
				} else {
					int endDateComp = task1.getEndDate().compareTo(
							task2.getEndDate());
					if (endDateComp == 0) {
						int startDateComp = task1.getStartDate().compareTo(
								task2.getStartDate());
						return (startDateComp == 0) ? task1.getTitle()
								.compareTo(task2.getTitle()) : startDateComp;
					} else {
						return endDateComp;
					}
				}
			}
		}
	}
}
