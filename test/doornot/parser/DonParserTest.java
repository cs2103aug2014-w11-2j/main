package doornot.parser;
import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import doornot.parser.IDonCommand.CommandType;
/**
 * Test the DonParser and check if it parses the user commands correctly and creates the right
 * DonCommand
 */
//@author A0115503W
public class DonParserTest {

	private static DonParser parser;
	private DonCommand CommandTest = new DonCommand();
	@BeforeClass
	public static void init(){
		parser = new DonParser();
	}
	
	@Test
	public void testAddTask(){

		// test add task
		CommandTest.setType(CommandType.ADD_TASK);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9));
		
		assertEquals(CommandTest.getType(), parser.parseCommand("a \"hihihi 12345678 \" at 09/09/2014").getType());
		assertEquals(CommandTest.getNewName(), parser.parseCommand("add \"hihihi\" at 09/09/2014").getNewName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi 12345678 \" @ 09/09/2014").getNewDeadline());
		
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi 12345678 \" @ 9th september").getNewDeadline());
		
		assertEquals(CommandType.INVALID_DATE, parser.parseCommand("a \"hihihi 12345678 \" at 12/13/2014").getType());
		
		
		// test hours
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9,13,24));
		assertEquals(CommandTest.getNewDeadline().getTime().toString(), 
				parser.parseCommand("add \"hihihi\" @ 09/09/2014 13:24").getNewDeadline().getTime().toString());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi\" @ 09/09 13:24").getNewDeadline());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("add \"hihihi\" @ 9 sep 1.24 pm").getNewDeadline());
		// test invalid
		assertEquals(CommandType.INVALID_COMMAND, parser.parseCommand("ad \"hihihi\" at 09082014").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("a hihihi\" at 09082014").getType());
//		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("add \"hihihi\" 09082014").getType()); //float
//		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("add \"hihihi\" at 090814").getType()); // parser mistake
//		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("a \"hihihi\" a 09082014").getType()); //float
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("a \"hihih;i\" at 09082014").getType());
		
		
	}
	
	@Test
	public void testAddEvent(){
		// test add event
		CommandTest.setType(CommandType.ADD_EVENT);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add \"hihihi\" from 07/08/2014 to 09/08/2014").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("add \"hihihi\" from 07/08/2014 to 09/08/2014").getNewName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 07/08/2014 to 09/08/2014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi\" from 07/08/2014 to 09/08/2014").getNewEndDate());
		
		assertEquals(CommandTest.getNewStartDate().getTime().toString(), 
				parser.parseCommand("add \"hihihi\" from 7 aug to 9 aug").getNewStartDate().getTime().toString());
		assertEquals(CommandTest.getNewEndDate().getTime().toString(), 
				parser.parseCommand("add \"hihihi\" from 7 aug to 9 aug").getNewEndDate().getTime().toString());
		
		// test time
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7,13,24));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9,15,54));
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 07/08/2014 1.24 pm to 09/08/2014 15:54").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi\" from 07/08/2014 1.24 pm to 09/08/2014 15:54").getNewEndDate());
		
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("add \"hihihi\" from 7 aug 1.24 pm to 9 aug 3.54 pm").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("add \"hihihi\" from 7 aug 1.24 pm to 9 aug 3.54 pm").getNewEndDate());
		
		// test invalid 
		assertEquals(CommandType.INVALID_DATE, 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014_1554").getType());
		assertEquals(CommandType.INVALID_DATE, 
				parser.parseCommand("add \"hihihi\" from 07082014_1324 to 09082014").getType());
		
		assertEquals(CommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi\" fro 07082014 to 09082014").getType());
		assertEquals(CommandType.INVALID_DATE,
				parser.parseCommand("add \"hihihi\" from 0702014 to 0908204").getType());
		assertEquals(CommandType.INVALID_DATE,
				parser.parseCommand("add \"hihihi\" from 0702014 2 0908204").getType());
		assertEquals(CommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi from 0702014 2 0908204").getType());
	}
	
	@Test
	public void testAddFloat(){
		CommandTest.setType(CommandType.ADD_FLOAT);
		CommandTest.setNewName("hihihi");
		
		// test event add
		assertEquals(CommandTest.getType(),
				parser.parseCommand("add \"hihihi\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("a \"hihihi\"").getNewName());
		
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("add \"hihihi\" dfs").getType());
	}
	
	@Test
	public void testMark(){
		CommandTest.setType(CommandType.MARK);
		CommandTest.setName("blah95");
		
		// test mark
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("m \"blah95\"").getName());
		
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("mark \"blah95").getType());
	}
	
	@Test
	public void testMarkID(){
		
		CommandTest.setType(CommandType.MARK_ID);
		CommandTest.setID(666);
		
		// test mark id
		assertEquals(CommandTest.getType(),
				parser.parseCommand("mark 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("mark 666").getID());
	}
	@Test
	public void testDelete(){
		CommandTest.setType(CommandType.DELETE);
		CommandTest.setName("blah95");
		
		// test delete
		assertEquals(CommandTest.getType(),
				parser.parseCommand("del \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("del \"blah95\"").getName());
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("del \"blah95").getType());
	}
	
	@Test
	public void testDeleteID(){
		CommandTest.setType(CommandType.DELETE_ID);
		CommandTest.setID(666);
		
		// test delete ID
		assertEquals(CommandTest.getType(),
				parser.parseCommand("delete 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("d 666").getID());
		
	}
	@Test
	public void testSearchName(){
		CommandTest.setType(CommandType.SEARCH_NAME);
		CommandTest.setName("blah95");
		
		// test search
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search \"blah95\"").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("s \"blah95\"").getName());
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("search \"blah95").getType());
	}
	
	@Test
	public void testSearchID(){
		CommandTest.setType(CommandType.SEARCH_ID);
		CommandTest.setID(666);
		
		// test search ID
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s 666").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("s 666").getID());
	}
	
	@Test
	public void testSearchDate(){
		CommandTest.setType(CommandType.SEARCH_DATE);
		CommandTest.setDeadline(new GregorianCalendar(2014,7,9));
		
		// test search date
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s 09082014").getType());
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("s 09082014").getDeadline());
		
		// test search with time
		CommandTest.setDeadline(new GregorianCalendar(2014,7,9,11,23));
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("s 09082014_1123").getDeadline());
	}
	
	@Test
	public void testSearchGeneral(){

//		TODAY,
//		OVERDUE,
		CommandTest.setType(CommandType.SEARCH_AFTDATE);
		CommandTest.setDeadline(new GregorianCalendar(2014,7,9));
		
		// test search after date
		assertEquals(CommandTest.getType(),
				parser.parseCommand("saf 09082014").getType());
		assertEquals(CommandTest.getDeadline(), 
				parser.parseCommand("saf 09082014").getDeadline());
		
		// test search free
		CommandTest.setType(CommandType.SEARCH_FREE);
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s free").getType());
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search free").getType());
		
		// test search all
		CommandTest.setType(CommandType.SEARCH_ALL);
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s").getType());
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search").getType());
		
		// test search undone
		CommandTest.setType(CommandType.SEARCH_UNDONE);
		assertEquals(CommandTest.getType(),
				parser.parseCommand("sud").getType());
		assertEquals(CommandTest.getType(),
				parser.parseCommand("s undone").getType());
		assertEquals(CommandTest.getType(),
				parser.parseCommand("search undone").getType());
		
		// test today
		CommandTest.setType(CommandType.TODAY);
		assertEquals(CommandTest.getType(),
				parser.parseCommand("today").getType());
		
		// test overdue
		CommandTest.setType(CommandType.OVERDUE);
		assertEquals(CommandTest.getType(),
				parser.parseCommand("od").getType());
		assertEquals(CommandTest.getType(),
				parser.parseCommand("overdue").getType());
		
	}
	@Test
	public void testEditName(){
		
		// test edit 
		CommandTest.setType(CommandType.EDIT_NAME);
		CommandTest.setName("hihihi");
		CommandTest.setNewName("HEHEHE");
		
		assertEquals(CommandTest.getType(), 
				parser.parseCommand("ed \"hihihi\" to \"HEHEHE\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("e \"hihihi\" to \"HEHEHE\"").getNewName());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("EDIT \"hihihi\" to \"HEHEHE\"").getName());
		//test invalid
		assertEquals(CommandType.INVALID_FORMAT, 
				parser.parseCommand("EDIT \"hihihi to \"HEHEHE\"").getType());
		// tets edit ID
		CommandTest.setType(CommandType.EDIT_ID_NAME);
		CommandTest.setID(666);
		CommandTest.setNewName("hehehe");
		
		assertEquals(CommandTest.getType(), 
				parser.parseCommand("edit 666 to \"hehehe\"").getType());
		assertEquals(CommandTest.getNewName(), 
				parser.parseCommand("edit 666 to \"hehehe\"").getNewName());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to \"hehehe\"").getID());
		
		
	}
	
	@Test
	public void testEditEvent(){
		
		// test edit event
		CommandTest.setType(CommandType.EDIT_EVENT);
		CommandTest.setName("hihihi");
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit \"hihihi\" to from 07/08/2014 to 09/08/2014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("edit \"hihihi\" to from 07/08/2014 to 09/08/2014").getName());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit \"hihihi\" to from 07/08/2014 to 09/08/2014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit \"hihihi\" to from 07/08 to 09/08").getNewEndDate());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit \"hihihi\" to from 7 aug to 9 aug").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit \"hihihi\" to from 7 aug to 9 aug").getNewEndDate());
		
		// test edit ID event
		CommandTest.setType(CommandType.EDIT_ID_EVENT);
		CommandTest.setID(666);
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit 666 to from 07/08/2014 to 09/08/2014").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to from 07/08/2014 to 09/08/2014").getID());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 07/08/2014 to 09/08/2014").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 07/08/2014 to 09/08/2014").getNewEndDate());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 7 aug to 9 aug").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 7 aug to 9 aug").getNewEndDate());
		
		// tets with time
		CommandTest.setNewStartDate(new GregorianCalendar(2014,7,7,13,55));
		CommandTest.setNewEndDate(new GregorianCalendar(2014,7,9,11,44));
		
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 07/08/2014 13:55 to 09/08/2014 11:44").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 07/08/2014 13:55 to 09/08/2014 11:44").getNewEndDate());
		assertEquals(CommandTest.getNewStartDate(), 
				parser.parseCommand("edit 666 to from 7 aug 13:55 to 9 aug 11:44").getNewStartDate());
		assertEquals(CommandTest.getNewEndDate(), 
				parser.parseCommand("edit 666 to from 7 aug 13:55 to 9 aug 11:44").getNewEndDate());
	}
	
	@Test
	public void testEditDate(){
		// test edit date
		CommandTest.setType(CommandType.EDIT_DATE);
		CommandTest.setName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("edit \"hihihi\" to 09/08/2014").getType());
		assertEquals(CommandTest.getName(), 
				parser.parseCommand("edit \"hihihi\" to 09/08/2014").getName());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 09/08/2014").getNewDeadline());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 9 aug").getNewDeadline());
		
		// tets with time
		CommandTest.setNewDeadline(new GregorianCalendar(2014,7,9,1,23));
		
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 09/08/2014 1.23 am").getNewDeadline());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit \"hihihi\" to 9 aug 1.23 am").getNewDeadline());
		
		// tets ID edit date
		CommandTest.setType(CommandType.EDIT_ID_DATE);
		CommandTest.setID(666);
		CommandTest.setNewDeadline(new GregorianCalendar(2014,7,9));
		
		
		assertEquals(CommandTest.getType(),
				parser.parseCommand("e 666 to 09/08/2014").getType());
		assertEquals(CommandTest.getID(), 
				parser.parseCommand("edit 666 to 09/08/2014").getID());
		assertEquals(CommandTest.getNewDeadline().getTime().toString(), 
				parser.parseCommand("edit 666 to 09/08/2014").getNewDeadline().getTime().toString());
		assertEquals(CommandTest.getNewDeadline(), 
				parser.parseCommand("edit 666 to 9 aug").getNewDeadline());
		
	
	}
	@Test
	public void testHelp(){
		
		// test help
		CommandTest.setType(CommandType.HELP_GENERAL);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help").getType());
		
		CommandTest.setType(CommandType.HELP_ADD);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help add").getType());
		
		CommandTest.setType(CommandType.HELP_EDIT);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help edit").getType());
		
		CommandTest.setType(CommandType.HELP_SEARCH);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help search").getType());
		
		CommandTest.setType(CommandType.HELP_DELETE);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help del").getType());
		assertEquals(CommandTest.getType(), parser.parseCommand("help delete").getType());
		
		CommandTest.setType(CommandType.HELP_MARK);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help mark").getType());

		CommandTest.setType(CommandType.HELP_UNDO);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help undo").getType());
		
		CommandTest.setType(CommandType.HELP_REDO);
		
		assertEquals(CommandTest.getType(), parser.parseCommand("help redo").getType());
		// test invalid
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("help commands").getType());
		assertEquals(CommandType.INVALID_COMMAND, parser.parseCommand("helpppp add").getType());
		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("help adds").getType());
	}


}
