/**
 * DonGUI - Graphic interface of DoOrNot V0.4
 * @author Lin Daqi (A0119423L)
 */

package doornot.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

import sun.audio.*;

public class DonGUIV3 {

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
	private JScrollPane scrollPane_editor;
	private JLabel lblTaskList;
	private JPanel panel;
	private JTextPane infoPane;
	private JEditorPane editor;
	private JLabel overdueLabel;
	private JLabel monthyearLabel;
	private JLabel dateLabel;
	private JLabel buttomFiller;
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
	private ArrayList<String> currentMsgList;
	private JPanel datePanel;
	private int selectedPage = 1;
	private int selectedCmd = -1;
	private JLabel searchLabel;
	private int countdown = 1000;
	private int countdown2 = 500;
	private int countdown3 = 1000;
	private int countdown4 = 1000;
	private Timer timer;
	private Timer timerf;
	private Timer timeri;
	private Timer timerg;
	private int flashcode = -1;
	private int curr = -2;
	private int flashcode2 = -1;
	private int curr2 = -2;
	private boolean delflag = false;
	private String curSearchString = "";
	private MyDialog dlg = new MyDialog(frmDoornot);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DonGUIV3 window = new DonGUIV3();
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
	public DonGUIV3() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

    public static void music() {       
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;

        ContinuousAudioDataStream loop = null;

        try
        {
            InputStream test = new FileInputStream("ring.wav");
            BGM = new AudioStream(test);
            AudioPlayer.player.start(BGM);
        }
        catch(FileNotFoundException e){
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        MGP.start(loop);
    }
    
	
	private void sortByDate() {
		Collections.sort(guiTaskList, new TimeComparator());
	}

	private void branchToPage(String cmd) {
		// typeList.clearSelection();
		switch (cmd) {
		case "c":
		case "console":
			selectedPage = 7;
			list.clearSelection();
			// typeList.setListData(placeholder);
			break;
		case "t":
		case "today":
			selectedPage = 1;
			typeList.setSelectedIndex(0);
			break;
		case "w":
		case "week":
			selectedPage = 2;
			typeList.setSelectedIndex(1);
			break;
		case "u":
		case "future":
			selectedPage = 3;
			typeList.setSelectedIndex(2);
			break;
		case "f":
		case "floating":
			selectedPage = 4;
			typeList.setSelectedIndex(3);
			break;
		case "l":
		case "all":
			selectedPage = 5;
			typeList.setSelectedIndex(4);
			break;
		case "o":
		case "overdue":
			if (overdueList != null && overdueList.size() != 0) {
				selectedPage = 0;
				typeList.clearSelection();
			} else {
				infoPane.setText("No overdue tasks!");
				infoPane.setVisible(true);
				countdown = 2000;
				timer = new Timer(1, al);
				timer.start();
				return;
			}
			break;
		case "r":
		case "results":
			if (searchList != null && searchList.size() != 0) {
				selectedPage = 6;
				typeList.clearSelection();
			} else {
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
		if (cmd.equals("c") || cmd.equals("console") || cmd.equals("t") || cmd.equals("today") || cmd.equals("w") || cmd.equals("week")
				|| cmd.equals("u") || cmd.equals("future") || cmd.equals("f") || cmd.equals("float") || cmd.equals("l")
				|| cmd.equals("all") || cmd.equals("o") || cmd.equals("overdue") || cmd.equals("r") || cmd.equals("results")) {
			branchToPage(cmd);
			return;
		}
		IDonResponse rp = donLogic.runCommand(cmd);
		assert rp != null;
		String fb = "";
		int size = 0;
		if (rp.hasMessages() && rp.getResponseType() != IDonResponse.ResponseType.HELP) {
			lastMsg = rp.getMessages().get(0);
			currentMsgList = new ArrayList<String>();
			size = rp.getMessages().size();
			if (size > 1) {
				for (int i = 0; i < size; i++) {
					currentMsgList.add(rp.getMessages().get(i));
				}
			}
		}
		if (rp.getResponseType() == IDonResponse.ResponseType.HELP) {
			size = 1;
			String helptext = "";
			for(String msg : rp.getMessages()){
				helptext += msg;
				helptext += "\n";
			}
		    editor.setText(helptext);
			selectedPage = 8;
			setTypeData();
		}
		if (rp.hasTasks() && (rp.getResponseType() == IDonResponse.ResponseType.ADD_SUCCESS || 
				rp.getResponseType() == IDonResponse.ResponseType.EDIT_SUCCESS)){
			delflag = false;
			flashcode = judgeType(rp.getTasks().get(0));
			flashcode2 = rp.getTasks().get(0).getID();
			countdown3 = 1000;
			countdown4 = 1000;
			
		}
		if (rp.hasTasks()
				&& (rp.getResponseType() == IDonResponse.ResponseType.SEARCH_SUCCESS
						|| rp.getResponseType() == IDonResponse.ResponseType.DEL_FAILURE || 
						rp.getResponseType() == IDonResponse.ResponseType.EDIT_FAILURE)) {
			scrollPane_textarea.setVisible(false);
			textArea.setVisible(false);
			if(rp.hasMessages()) curSearchString = rp.getMessages().get(0);
			currentMsgList.remove(lastMsg);
			/*
			if(rp.hasMessages()){
				int firstapp = rp.getMessages().get(0).indexOf('\'');
				int lastapp = rp.getMessages().get(0).lastIndexOf('\'');
				curSearchString = rp.getMessages().get(0).substring(firstapp+1, lastapp);
			} else {
				curSearchString = "free time";
			}
			*/
			/*
			fb += "The following tasks match the search:\n";
			for (int i = 0; i < rp.getTasks().size(); i++) {
				fb += String.valueOf(rp.getTasks().get(i).getID()) + ". "
						+ rp.getTasks().get(i).getTitle() + "\n";
			}
			*/
			searchList = new ArrayList<IDonTask>();
			for (IDonTask task : rp.getTasks()) {
				searchList.add(task);
			}
			selectedPage = 6;
			setTypeData();
		}
		if (rp.getResponseType() == IDonResponse.ResponseType.DEL_SUCCESS) {
			if(rp.getTasks().size() > 0){
				flashcode = judgeType(rp.getTasks().get(0));
				countdown3 = 1000;
				delflag = true;
			}
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
			size++;
			currentMsgList = new ArrayList<String>();
			currentMsgList.add(lastMsg);
			currentMsgList.add("Type \"undo\" to undo");
		}
		if (size == 1) {
			fb += lastMsg;
			fb += "\n";
			infoPane.setText(lastMsg);
			infoPane.setVisible(true);
			countdown = 1000;
			if (timer != null)
				timer.stop();
			timer = new Timer(1, al);
			timer.start();
		} else if (size > 1) {
			for (int i = 0; i < currentMsgList.size(); i++) {
				fb += currentMsgList.get(i) + "\n";
			}
			countdown2 = 500;
			if (timer != null)
				timer.stop();
			timer = new Timer(1, alm);
			timer.start();
		}
		display += fb;
		textArea.setText(display);
		textField.setText("");
		renewList();
	}

	private String printDate(Calendar cal, boolean isTimeUsed) {
		SimpleDateFormat df = new SimpleDateFormat();
		if (isTimeUsed)
			df.applyPattern("dd/MM/yyyy HH:mm");
		else
			df.applyPattern("dd/MM/yyyy");
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
		editor.setVisible(false);
		scrollPane_editor.setVisible(false);
		list.setVisible(true);
		scrollPane_list.setVisible(true);
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
			lblTaskList.setText("Task List: " + curSearchString);
		} else if (selectedPage == 7) {
			lblTaskList.setText("Output Console");
			list.setVisible(false);
			scrollPane_list.setVisible(false);
			scrollPane_textarea.setVisible(true);
			textArea.setVisible(true);
		} else if (selectedPage == 8) {
			lblTaskList.setText("Help");
			list.setVisible(false);
			scrollPane_list.setVisible(false);
			scrollPane_editor.setVisible(true);
			editor.setVisible(true);
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
			overdueLabel.setText("overdue: "
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
	
	private int judgeType(IDonTask task){
		if(!task.getStatus()){
			if(dued(task)) return 0;
			else {
				if(isToday(task)) return 1;
				else if(isWithinWeek(task)) return 2;
				else if(task.getStartDate()==null) return 4;
				else return 3;
			}
		} else {
			return 5;
		}
	}

	private void parseType() {
		overdueList = new ArrayList<IDonTask>();
		todayList = new ArrayList<IDonTask>();
		weekList = new ArrayList<IDonTask>();
		farList = new ArrayList<IDonTask>();
		floatList = new ArrayList<IDonTask>();

		for (IDonTask task : guiTaskList) {
			if (!task.getStatus()) {
				if (dued(task)) {
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

	}

	ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(countdown == 1000) buttomFiller.setVisible(false);
			countdown--;
			if (countdown == 0) {
				infoPane.setVisible(false);
				buttomFiller.setVisible(true);
				timer.stop();
			}
		}
	};

	ActionListener alm = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (countdown2 == 500) {
				buttomFiller.setVisible(false);
				infoPane.setText(currentMsgList.remove(0));
				infoPane.setVisible(true);
			}
			if (countdown2 == 0) {
				if (currentMsgList.size() == 0) {
					infoPane.setVisible(false);
					currentMsgList = null;
					buttomFiller.setVisible(true);
					timer.stop();
				} else {
					countdown2 = 501;
				}
			}
			countdown2--;
		}
	};
	
	ActionListener alf = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(countdown3 == 0){
				curr = -2;
				flashcode = -1;
				timerf.stop();
			}
			else countdown3--;
		}
	};
	
	ActionListener ali = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if(countdown4 == 0){
				curr2 = -2;
				flashcode2 = -1;
				timeri.stop();
			}
			else countdown4--;
		}
	};
	
	ActionListener alg = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			typeList.setCellRenderer(new TypeCellRenderer());
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
		frmDoornot.setTitle("DoOrNot v0.4");
		frmDoornot.setBounds(100, 100, 646, 528);
		//frmDoornot.setUndecorated(true);
		frmDoornot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDoornot.addWindowListener(new WindowEventHandler());
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
		
		frmDoornot.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_F1){
					typeList.setSelectedIndex(0);
					selectedPage = 1;
					setTypeData();
				}
			}
		});
		
		timerg = new Timer(1000, alg);
		timerg.start();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 63, 386, 69, 50, 10,0 };
		gridBagLayout.rowHeights = new int[] { 44, 30, 315, 31, 38, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		frmDoornot.getContentPane().setLayout(gridBagLayout);
		// frmDoornot.getContentPane().add(helpButton);

		textField = new JTextField();
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
				} else if (e.getKeyCode() > 111 && e.getKeyCode() < 117) {
					typeList.setSelectedIndex(e.getKeyCode() - 112);
					selectedPage = e.getKeyCode() - 111;
					setTypeData();
					typeList.setCellRenderer(new TypeCellRenderer());
				}

			}
		});
		
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				Image resizedLogo = logo.getScaledInstance(panel.getWidth(),
						panel.getHeight(), Image.SCALE_SMOOTH);
				super.paintComponent(g);
				g.drawImage(resizedLogo, 0, 0, null);
			}
		};
		panel.setBackground(new Color(159, 197, 232));
		panel.setVisible(false);

		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dlg.setLocationRelativeTo(frmDoornot);
				dlg.setVisible(true);
				selectedPage = 7;
				list.clearSelection();
				typeList.setListData(placeholder);
				setTypeData();
			}
		});

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridheight = 2;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frmDoornot.getContentPane().add(panel, gbc_panel);

		datePanel = new JPanel();
		datePanel.setBackground(new Color(159, 197, 232));
		GridBagConstraints gbc_datePanel = new GridBagConstraints();
		gbc_datePanel.anchor = GridBagConstraints.NORTH;
		gbc_datePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_datePanel.insets = new Insets(0, 0, 5, 5);
		gbc_datePanel.gridheight = 2;
		gbc_datePanel.gridx = 0;
		gbc_datePanel.gridy = 0;
		frmDoornot.getContentPane().add(datePanel, gbc_datePanel);
		// monthyearFormat.applyPattern("hh:mm:ss");
		GridBagLayout gbl_datePanel = new GridBagLayout();
		gbl_datePanel.columnWidths = new int[] { 80, 0 };
		gbl_datePanel.rowHeights = new int[] { 50, 30 };
		// gbl_datePanel.columnWeights = new double[]{5, 3};
		gbl_datePanel.rowWeights = new double[] { 5, 3 };
		datePanel.setLayout(gbl_datePanel);

		Calendar current = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern("d");
		SimpleDateFormat monthyearFormat = new SimpleDateFormat();
		monthyearFormat.applyPattern("MMM YY");

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

		searchLabel = new JLabel("Search results: N/A");
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
		
		buttomFiller = new JLabel("DoOrNot v0.4");
		buttomFiller.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_buttomFiller = new GridBagConstraints();
		//gbc_buttomFiller.anchor = GridBagConstraints.SOUTH;
		gbc_buttomFiller.fill = GridBagConstraints.BOTH;
		gbc_buttomFiller.gridx = 0;
		gbc_buttomFiller.gridy = 4;
		gbc_buttomFiller.gridwidth = 4;
		frmDoornot.getContentPane().add(buttomFiller, gbc_buttomFiller);

		lblTaskList = new JLabel("Task List: ");
		lblTaskList.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTaskList.setForeground(Color.white);
		GridBagConstraints gbc_lblTaskList = new GridBagConstraints();
		gbc_lblTaskList.anchor = GridBagConstraints.SOUTH;
		gbc_lblTaskList.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTaskList.insets = new Insets(0, 0, 5, 5);
		gbc_lblTaskList.gridx = 1;
		gbc_lblTaskList.gridy = 0;
		frmDoornot.getContentPane().add(lblTaskList, gbc_lblTaskList);
		GridBagConstraints gbc_searchLabel = new GridBagConstraints();
		gbc_searchLabel.anchor = GridBagConstraints.EAST;
		gbc_searchLabel.fill = GridBagConstraints.VERTICAL;
		gbc_searchLabel.insets = new Insets(0, 0, 5, 0);
		gbc_searchLabel.gridwidth = 3;
		gbc_searchLabel.gridx = 1;
		gbc_searchLabel.gridy = 0;
		frmDoornot.getContentPane().add(searchLabel, gbc_searchLabel);

		
		scrollPane_editor = new JScrollPane();
		GridBagConstraints gbc_scrollPane_editor = new GridBagConstraints();
		gbc_scrollPane_editor.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_editor.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_editor.gridheight = 2;
		gbc_scrollPane_editor.gridwidth = 3;
		gbc_scrollPane_editor.gridx = 1;
		gbc_scrollPane_editor.gridy = 1;
		gbc_scrollPane_editor.weighty = 1;
		frmDoornot.getContentPane().add(scrollPane_editor, gbc_scrollPane_editor);
		editor = new JEditorPane();
		editor.setEditable(false);
		editor.setVisible(false);
		scrollPane_editor.setViewportView(editor);
		
		scrollPane_textarea = new JScrollPane();
		GridBagConstraints gbc_scrollPane_textarea = new GridBagConstraints();
		gbc_scrollPane_textarea.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_textarea.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_textarea.gridheight = 2;
		gbc_scrollPane_textarea.gridwidth = 3;
		gbc_scrollPane_textarea.gridx = 1;
		gbc_scrollPane_textarea.gridy = 1;
		gbc_scrollPane_textarea.weighty = 1;
		frmDoornot.getContentPane().add(scrollPane_textarea,
				gbc_scrollPane_textarea);
		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		scrollPane_textarea.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setVisible(false);

		scrollPane_list = new JScrollPane();
		GridBagConstraints gbc_scrollPane_list = new GridBagConstraints();
		gbc_scrollPane_list.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_list.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_list.gridheight = 2;
		gbc_scrollPane_list.gridwidth = 3;
		gbc_scrollPane_list.gridx = 1;
		gbc_scrollPane_list.gridy = 1;
		gbc_scrollPane_list.weighty = 1;
		frmDoornot.getContentPane().add(scrollPane_list, gbc_scrollPane_list);

		list = new JList<IDonTask>();
		scrollPane_list.setViewportView(list);
		list.setForeground(Color.RED);
		list.setBackground(SystemColor.control);
		list.setFixedCellHeight(70);
		list.setCellRenderer(new TaskCellRenderer());

		overdueLabel = new JLabel();
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

		typeList = new JList<Integer>();
		typeList.setFixedCellHeight(63);
		typeList.setBackground(new Color(159, 197, 232));
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

		GridBagConstraints gbc_typeList = new GridBagConstraints();
		gbc_typeList.fill = GridBagConstraints.BOTH;
		gbc_typeList.insets = new Insets(0, 0, 5, 5);
		gbc_typeList.gridx = 0;
		gbc_typeList.gridy = 2;
		gbc_typeList.weighty = 1;
		frmDoornot.getContentPane().add(typeList, gbc_typeList);

		GridBagConstraints gbc_overdueLabel = new GridBagConstraints();
		gbc_overdueLabel.fill = GridBagConstraints.BOTH;
		gbc_overdueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_overdueLabel.gridx = 0;
		gbc_overdueLabel.gridy = 3;
		frmDoornot.getContentPane().add(overdueLabel, gbc_overdueLabel);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 3;
		gbc_textField.gridwidth = 2;
		gbc_textField.weightx = 1;
		gbc_textField.anchor = GridBagConstraints.CENTER;
		frmDoornot.getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);

		sendButton = new JButton("Send");
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
		GridBagConstraints gbc_sendButton = new GridBagConstraints();
		//gbc_sendButton.anchor = GridBagConstraints.WEST;
		gbc_sendButton.insets = new Insets(0, 0, 5, 0);
		gbc_sendButton.gridx = 3;
		gbc_sendButton.gridy = 3;
		gbc_sendButton.fill = GridBagConstraints.BOTH;
		//gbc_sendButton.anchor = GridBagConstraints.EAST;
		frmDoornot.getContentPane().add(sendButton, gbc_sendButton);

		SimpleAttributeSet attribs = new SimpleAttributeSet();

		infoPane = new JTextPane();
		infoPane.setVisible(false);
		infoPane.setEditable(false);
		infoPane.setBackground(new Color(58, 129, 186));
		StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(attribs, "Verdana");
		StyleConstants.setFontSize(attribs, 16);
		StyleConstants.setForeground(attribs, Color.white);
		infoPane.setParagraphAttributes(attribs, true);

		GridBagConstraints gbc_infoPane = new GridBagConstraints();
		gbc_infoPane.fill = GridBagConstraints.BOTH;
		gbc_infoPane.gridwidth = 5;
		gbc_infoPane.gridx = 0;
		gbc_infoPane.gridy = 4;
		frmDoornot.getContentPane().add(infoPane, gbc_infoPane);

		try {
			logo = ImageIO.read(new File("doornot.png"));
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
			Integer entry = (Integer) value;
			int typecode = entry.intValue();

			if (isSelected || selectedPage - 1 == index) {
				p.setBorder(new LineBorder(Color.white, 2));
			} else {
				p.setBorder(null);
			}
			
			if(typecode == flashcode){
				if(curr == flashcode){
					if(countdown3 > 0) {
						if(!delflag) p.setBorder(new LineBorder(Color.green, 3));
						else p.setBorder(new LineBorder(Color.red,3));
					}
					else {
						if (isSelected || selectedPage - 1 == index) {
							p.setBorder(new LineBorder(Color.white, 2));
						} else {
							p.setBorder(null);
						}
					}
				} else {
					if(!delflag) p.setBorder(new LineBorder(Color.green, 3));
					else p.setBorder(new LineBorder(Color.red,3));
					countdown3 = 1000;
					curr = flashcode;
					timerf = new Timer(1, alf);	
					timerf.start();
				}
			} else {
				if (isSelected || selectedPage - 1 == index) {
					p.setBorder(new LineBorder(Color.white, 2));
				} else {
					p.setBorder(null);
				}
			}

			amount.setHorizontalAlignment(SwingConstants.CENTER);
			typename.setHorizontalAlignment(SwingConstants.CENTER);
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

		/*
		 * JPanel panel_1 = new JPanel(); GridBagConstraints gbc_panel_1 = new
		 * GridBagConstraints(); gbc_panel_1.anchor = GridBagConstraints.SOUTH;
		 * gbc_panel_1.fill = GridBagConstraints.HORIZONTAL; gbc_panel_1.insets
		 * = new Insets(0, 0, 5, 0); gbc_panel_1.gridx = 3; gbc_panel_1.gridy =
		 * 1; frmDoornot.getContentPane().add(panel_1, gbc_panel_1);
		 * GridBagLayout gbl_panel_1 = new GridBagLayout();
		 * //gbl_panel_1.columnWidths = new int[]{0, 0, 4, 0};
		 * //gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		 * gbl_panel_1.columnWeights = new double[]{1.0, 5.0, 0.0,
		 * Double.MIN_VALUE}; gbl_panel_1.rowWeights = new double[]{1.0, 1.0,
		 * Double.MIN_VALUE}; panel_1.setLayout(gbl_panel_1);
		 */

		// JPanel np = new JPanel();
		// gbc_np.fill = GridBagConstraints.HORIZONTAL;

		// JPanel p = new JPanel(new BorderLayout());
		JPanel p = new JPanel();
		JTextPane id = new JTextPane();
		JTextPane content = new JTextPane();
		JTextPane timerange = new JTextPane();
		JTextPane label = new JTextPane();
		JTextPane realid = new JTextPane();

		public TaskCellRenderer() {
			GridBagLayout gbl = new GridBagLayout();
			gbl.columnWidths = new int[] { 67, 268, 67, 38, 39 };
			gbl.rowHeights = new int[] { 50, 20 };
			gbl.columnWeights = new double[] { 67, 268, 67, 38, 39 };
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
			
			if(entry.getID() == flashcode2 && entry.getID() != -1){
				if(curr2 == flashcode2){
					if(countdown3 > 0) p.setBorder(new LineBorder(Color.green, 3));
					else {
						p.setBorder(null);
					}
				} else {
					p.setBorder(new LineBorder(Color.green, 3));
					countdown4 = 1000;
					curr2 = flashcode;
					timeri = new Timer(1, ali);	
					timeri.start();
				}
			} else {
				p.setBorder(null);
			}

			String entryText = "";
			String timeText = "";
			if (entry.getType() == IDonTask.TaskType.FLOATING) {
				entryText += entry.getTitle();
			} else if (entry.getType() == IDonTask.TaskType.DEADLINE) {
				entryText += entry.getTitle();
				timeText = " (Deadline: "
						+ printDate(entry.getStartDate(), entry.isTimeUsed())
						+ ")";
			} else {
				entryText += entry.getTitle();
				timeText = " (Duration: "
						+ printDate(entry.getStartDate(), entry.isTimeUsed())
						+ " -- "
						+ printDate(entry.getEndDate(), entry.isTimeUsed())
						+ ")";
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
				} else if (entry.getType() == IDonTask.TaskType.DEADLINE) {
					id.setText(printShortDate(entry.getStartDate()) + "\n"
							+ printDayInWeek(entry.getStartDate()));
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
			// content.setSize(335, 50);
			content.setForeground(Color.white);
			content.setFont(new Font("Arial", Font.BOLD, 16));
			content.setText(entryText);
			id.setFont(new Font("Verdana", Font.BOLD, 17));
			id.setForeground(Color.white);
			// id.setText(new Integer(entry.getID()).toString());
			if(entry.getStatus()){
				id.setBackground(new Color(21,161,19));
				content.setBackground(new Color(9,222,7));
				label.setBackground(new Color(9,222,7));
			} else {
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
			}
			if(entry.getID() == -1){
				realid.setText(null);
				/*
				id.setBackground(new Color(255,102,178));
				content.setBackground(new Color(255,153,204));
				label.setBackground(new Color(255,153,204));
				*/
				id.setBackground(Color.gray);
				content.setBackground(Color.LIGHT_GRAY);
				label.setBackground(Color.LIGHT_GRAY);
			}

			if (entry.getLabels() != null && entry.getLabels().size() != 0) {
				String tag = "";
				for (int i = 0; i < entry.getLabels().size(); i++) {
					tag += "  *";
					tag += entry.getLabels().get(i);
					if (i != entry.getLabels().size() - 1)
						tag += "\n";
				}
				label.setText(tag);
				label.setForeground(Color.white);
				label.setFont(new Font("Arial", Font.ITALIC, 11));
				// label.setBackground(Color.blue);
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
	
	class MyDialog extends JDialog {
		  public MyDialog(JFrame parent) {
		    super(parent, "About", true);
		    Container cp = getContentPane();
		    cp.setLayout(new FlowLayout());
		    JTextPane tt = new JTextPane();
		    tt.setText("Developers:\nEu Yong Xue\nHaritha Ramesh\nHu Yifei\nLin Daqi");
		    tt.setEditable(false);
		    /*
		    cp.add(new JLabel("Developers:"));
		    cp.add(new JLabel("Eu Yong Xue"));
		    cp.add(new JLabel("Haritha Ramesh"));
		    cp.add(new JLabel("Hu Yifei"));
		    cp.add(new JLabel("Lin Daqi"));
		    */
		    cp.add(tt);
		    JButton ok = new JButton("OK");
		    ok.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        dispose(); // Closes the dialog
		      }
		    });
		    cp.add(ok);
		    setSize(150, 150);
		    music();
		  }
	}
}
