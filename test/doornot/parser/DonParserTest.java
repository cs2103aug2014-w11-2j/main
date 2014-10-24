package doornot.parser;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import doornot.CalHelper;
import doornot.logic.AbstractDonCommand.GeneralCommandType;
import doornot.logic.DonAddLabelCommand.AddLabelType;
import doornot.logic.DonCreateCommand.AddType;
import doornot.logic.DonDelabelCommand;
import doornot.logic.DonDelabelCommand.DelabelType;
import doornot.logic.DonDeleteCommand.DeleteType;
import doornot.logic.DonEditCommand.EditType;
import doornot.logic.DonFindCommand.SearchType;
import doornot.logic.DonHelpCommand.HelpType;
import doornot.logic.DonMarkCommand.MarkType;
import doornot.logic.DonAddLabelCommand;
import doornot.logic.DonCreateCommand;
import doornot.logic.DonDeleteCommand;
import doornot.logic.DonEditCommand;
import doornot.logic.DonFindCommand;
import doornot.logic.DonHelpCommand;
import doornot.logic.DonMarkCommand;
/**
 * Test the DonParser and check if it parses the user commands correctly and creates the right
 * DonCommand
 */
//@author A0115503W
public class DonParserTest {

	private static DonParser parser;
	//private AbstractDonCommand CommandTest = new AbstractDonCommand();
	@BeforeClass
	public static void init(){
		parser = new DonParser();
	}
	
	@Test
	public void testAddTask(){

		// test add task
		/*
		CommandTest.setType(CommandType.ADD_TASK);
		CommandTest.setNewName("hihihi");
		CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9,23,59));
		*/
		
		DonCreateCommand create1 = (DonCreateCommand) parser.parseCommand("a \"hihihi 12345678 \" at 09/09/2014");
		DonCreateCommand create2 = (DonCreateCommand) parser.parseCommand("add \"hihihi 12345678 \" @ 9th september");
		DonCreateCommand create3 = (DonCreateCommand) parser.parseCommand("add \"hihihi\" @ 09/09/2014 13:24");
		DonCreateCommand create4 = (DonCreateCommand) parser.parseCommand("add \"hihihi\" @ 9 sep 1.24 pm");
		
		assertEquals(AddType.DEADLINE, create1.getType());
		assertEquals("hihihi 12345678 ", create1.getTaskTitle());

		assertEquals(true, CalHelper.relevantEquals(new GregorianCalendar(2014,8,9,23,59), create1.getStartDate()));
		
		assertEquals(true, CalHelper.relevantEquals(new GregorianCalendar(2014,8,9,23,59), create2.getStartDate()));
		
		assertEquals(GeneralCommandType.INVALID_DATE, parser.parseCommand("a \"hihihi 12345678 \" at 12/13/2014").getGeneralType());
		
		
		// test hours
		//CommandTest.setNewDeadline(new GregorianCalendar(2014,8,9,13,24));
		Calendar septCal = new GregorianCalendar(2014,8,9,13,24);
		assertEquals(septCal.getTime().toString(), create3.getStartDate().getTime().toString());
		assertEquals(true, CalHelper.relevantEquals(septCal, create3.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(septCal, create4.getStartDate()));
		// test invalid
		assertEquals(GeneralCommandType.INVALID_COMMAND, parser.parseCommand("ad \"hihihi\" at 09082014").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("a hihihi\" at 09082014").getGeneralType());
//		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("add \"hihihi\" 09082014").getGeneralType()); //float
//		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("add \"hihihi\" at 090814").getGeneralType()); // parser mistake
//		assertEquals(CommandType.INVALID_FORMAT, parser.parseCommand("a \"hihihi\" a 09082014").getGeneralType()); //float
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("a \"hihih;i\" at 09082014").getGeneralType());
		
		
	}
	
	@Test
	public void testAddEvent(){
		// test add event

		
		DonCreateCommand create1 = (DonCreateCommand) parser.parseCommand("add \"hihihi\" from 07/08/2014 to 09/08/2014");
		DonCreateCommand create2 = (DonCreateCommand) parser.parseCommand("add \"hihihi\" from 7 aug to 9 aug");
		
		Calendar startDate1 = new GregorianCalendar(2014,7,7,23,59);
		Calendar endDate1 = new GregorianCalendar(2014,7,9,23,59);
		
		assertEquals(AddType.EVENT, create1.getType());
		assertEquals("hihihi", create1.getTaskTitle());
		assertEquals(true, CalHelper.relevantEquals(startDate1, create1.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate1, create1.getEndDate()));
		
		assertEquals(true, CalHelper.relevantEquals(startDate1, create2.getStartDate()));
		assertEquals(endDate1.getTime().toString(), create2.getEndDate().getTime().toString());
		
		// test time
		startDate1 = new GregorianCalendar(2014,7,7,13,24);
		endDate1 = new GregorianCalendar(2014,7,9,15,54);
		
		DonCreateCommand create3 = (DonCreateCommand) parser.parseCommand("add \"hihihi\" from 07/08/2014 1.24 pm to 09/08/2014 15:54");
		DonCreateCommand create4 = (DonCreateCommand) parser.parseCommand("add \"hihihi\" from 7 aug 1.24 pm to 9 aug 3.54 pm");
		assertEquals(true, CalHelper.relevantEquals(startDate1, create3.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate1, create3.getEndDate()));
		
		assertEquals(true, CalHelper.relevantEquals(startDate1, create4.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate1, create4.getEndDate()));
		
		// test invalid 
		assertEquals(GeneralCommandType.INVALID_DATE, 
				parser.parseCommand("add \"hihihi\" from 07082014 to 09082014_1554").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_DATE, 
				parser.parseCommand("add \"hihihi\" from 07082014_1324 to 09082014").getGeneralType());
		
		assertEquals(GeneralCommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi\" fro 07082014 to 09082014").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_DATE,
				parser.parseCommand("add \"hihihi\" from 0702014 to 0908204").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_DATE,
				parser.parseCommand("add \"hihihi\" from 0702014 2 0908204").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_FORMAT,
				parser.parseCommand("add \"hihihi from 0702014 2 0908204").getGeneralType());
	}
	
	@Test
	public void testAddFloat(){
		DonCreateCommand create1 = (DonCreateCommand) parser.parseCommand("add \"hihihi\"");
		// test event add
		assertEquals(AddType.FLOATING, create1.getType());
		assertEquals("hihihi", create1.getTaskTitle());
		
		// test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, 
				parser.parseCommand("add \"hihihi\" dfs").getGeneralType());
	}
	
	@Test
	public void testMark(){
		
		DonMarkCommand mark1 = (DonMarkCommand) parser.parseCommand("mark \"blah95\"");
		
		// test mark
		assertEquals(MarkType.MARK_STRING, mark1.getMarkType());
		assertEquals("blah95", mark1.getSearchTitle());
		
		// test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, 
				parser.parseCommand("mark \"blah95").getGeneralType());
	}
	
	@Test
	public void testMarkID(){
		DonMarkCommand mark1 = (DonMarkCommand) parser.parseCommand("mark 666");
		// test mark id
		assertEquals(MarkType.MARK_ID, mark1.getMarkType());
		assertEquals(666, mark1.getSearchID());
	}
	@Test
	public void testDelete(){
		DonDeleteCommand delete1 = (DonDeleteCommand) parser.parseCommand("del \"blah95\"");
		// test delete
		assertEquals(DeleteType.DELETE_TITLE, delete1.getType());
		assertEquals("blah95", delete1.getSearchTitle());
		// test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("del \"blah95").getGeneralType());
	}
	
	@Test
	public void testDeleteID(){
		DonDeleteCommand delete1 = (DonDeleteCommand) parser.parseCommand("delete 666");
		// test delete ID
		assertEquals(DeleteType.DELETE_ID, delete1.getType());
		assertEquals(666, delete1.getSearchID());
		
	}
	@Test
	public void testSearchName(){
		DonFindCommand search = (DonFindCommand) parser.parseCommand("search \"blah95\"");
		// test search
		assertEquals(SearchType.SEARCH_NAME, search.getType());
		assertEquals("blah95", search.getSearchTitle());
		// test invalid
//		assertEquals(CommandType.INVALID_FORMAT, 
//				parser.parseCommand("search \"blah95").getGeneralType());
	}
	
	@Test
	public void testSearchID(){
		DonFindCommand search = (DonFindCommand) parser.parseCommand("s 666");
		// test search ID
		assertEquals(SearchType.SEARCH_ID, search.getType());
		assertEquals(666, search.getSearchID());
	}
	
	@Test
	public void testSearchDate(){
		DonFindCommand search1 = (DonFindCommand) parser.parseCommand("s 09/08/2014");
		DonFindCommand search2 = (DonFindCommand) parser.parseCommand("s 9 aug");
		Calendar date1 = new GregorianCalendar(2014,7,9,23,59);
		// test search date
		assertEquals(SearchType.SEARCH_DATE, search1.getType());
		assertEquals(date1.getTime().toString(), search1.getSearchStartDate().getTime().toString());
		assertEquals(true, CalHelper.relevantEquals(date1, search2.getSearchStartDate()));
		
		// test search with time
		DonFindCommand search3 = (DonFindCommand) parser.parseCommand("s 09/08/2014 11:23");
		DonFindCommand search4 = (DonFindCommand) parser.parseCommand("s 9 aug 11:23");
		date1 = new GregorianCalendar(2014,7,9,11,23);
		assertEquals(true, CalHelper.relevantEquals(date1, search3.getSearchStartDate()));
		assertEquals(true, CalHelper.relevantEquals(date1, search4.getSearchStartDate()));
	}
	
	@Test
	public void testSearchGeneral(){

//		TODAY,
//		OVERDUE,
		DonFindCommand search1 = (DonFindCommand) parser.parseCommand("saf 09/08/2014");
		DonFindCommand search2 = (DonFindCommand) parser.parseCommand("saf 9 aug");
		Calendar date1 = new GregorianCalendar(2014,7,10,23,59); //search after commands will have a modified startdate after getting parsed if they are provided without time
		// test search after date
		assertEquals(SearchType.SEARCH_AFTDATE, search1.getType());
		assertEquals(true, CalHelper.relevantEquals(date1, search1.getSearchStartDate()));
		assertEquals(true, CalHelper.relevantEquals(date1, search2.getSearchStartDate()));
		
		// test search free
		DonFindCommand search3 = (DonFindCommand) parser.parseCommand("s free");
		DonFindCommand search4 = (DonFindCommand) parser.parseCommand("search free");
		assertEquals(SearchType.SEARCH_FREE, search3.getType());
		assertEquals(SearchType.SEARCH_FREE, search4.getType());
		
		// test search all
		DonFindCommand search5 = (DonFindCommand) parser.parseCommand("s");
		DonFindCommand search6 = (DonFindCommand) parser.parseCommand("search");
		assertEquals(SearchType.SEARCH_ALL, search5.getType());
		assertEquals(SearchType.SEARCH_ALL, search6.getType());
		
		// test search undone
		DonFindCommand search7 = (DonFindCommand) parser.parseCommand("sud");
		DonFindCommand search8 = (DonFindCommand) parser.parseCommand("s undone");
		DonFindCommand search9 = (DonFindCommand) parser.parseCommand("search undone");
		assertEquals(SearchType.SEARCH_UNDONE, search7.getType());
		assertEquals(SearchType.SEARCH_UNDONE, search8.getType());
		assertEquals(SearchType.SEARCH_UNDONE, search9.getType());
		
		// test today
		DonFindCommand search10 = (DonFindCommand) parser.parseCommand("today");
		assertEquals(SearchType.TODAY, search10.getType());
		
		// test overdue
		DonFindCommand search11 = (DonFindCommand) parser.parseCommand("od");
		DonFindCommand search12 = (DonFindCommand) parser.parseCommand("overdue");
		assertEquals(SearchType.OVERDUE, search11.getType());
		assertEquals(SearchType.OVERDUE, search12.getType());
	}
	@Test
	public void testEditName(){
		// test edit 
		DonEditCommand edit1 = (DonEditCommand) parser.parseCommand("ed \"hihihi\" to \"HEHEHE\"");
		
		assertEquals(EditType.NAME_NAME, edit1.getType());
		assertEquals("HEHEHE", edit1.getNewTitle());
		assertEquals("hihihi", edit1.getSearchTitle());
		//test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("EDIT \"hihihi to \"HEHEHE\"").getGeneralType());
		// tets edit ID
		DonEditCommand edit2 = (DonEditCommand) parser.parseCommand("edit 666 to \"hehehe\"");
		
		assertEquals(EditType.ID_NAME, edit2.getType());
		assertEquals("hehehe", edit2.getNewTitle());
		assertEquals(666, edit2.getSearchID());		
	}
	
	@Test
	public void testEditEvent(){
		
		// test edit event
		DonEditCommand edit1 = (DonEditCommand) parser.parseCommand("edit \"hihihi\" to from 07/08/2014 to 09/08/2014");
		Calendar startDate = new GregorianCalendar(2014,7,7,23,59), endDate = new GregorianCalendar(2014,7,9,23,59);
		
		assertEquals(EditType.NAME_EVENT, edit1.getType());
		assertEquals("hihihi", edit1.getSearchTitle());
		assertEquals(true, CalHelper.relevantEquals(startDate, edit1.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit1.getNewEndDate()));

		DonEditCommand edit2 = (DonEditCommand) parser.parseCommand("edit \"hihihi\" to from 7 aug to 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit2.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit2.getNewEndDate()));
		
		// test edit ID event
		startDate = new GregorianCalendar(2014,7,7,23,59);
		endDate = new GregorianCalendar(2014,7,9,23,59);
		
		DonEditCommand edit3 = (DonEditCommand) parser.parseCommand("edit 666 to from 07/08/2014 to 09/08/2014");
		
		assertEquals(EditType.ID_EVENT, edit3.getType());
		assertEquals(666, edit3.getSearchID());
		assertEquals(true, CalHelper.relevantEquals(startDate, edit3.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit3.getNewEndDate()));
		
		DonEditCommand edit4 = (DonEditCommand) parser.parseCommand("edit 666 to from 7 aug to 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit4.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit4.getNewEndDate()));
		
		// tets with time
		startDate = new GregorianCalendar(2014,7,7,13,55);
		endDate = new GregorianCalendar(2014,7,9,11,44);
		
		DonEditCommand edit5 = (DonEditCommand) parser.parseCommand("edit 666 to from 07/08/2014 13:55 to 09/08/2014 11:44");
		
		assertEquals(true, CalHelper.relevantEquals(startDate, edit5.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit5.getNewEndDate()));
		
		DonEditCommand edit6 = (DonEditCommand) parser.parseCommand("edit 666 to from 7 aug 13:55 to 9 aug 11:44");
		
		assertEquals(true, CalHelper.relevantEquals(startDate, edit6.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit6.getNewEndDate()));
	}
	
	@Test
	public void testEditDate(){
		// test edit date
		DonEditCommand edit1 = (DonEditCommand) parser.parseCommand("edit \"hihihi\" to 09/08/2014");
		Calendar startDate = new GregorianCalendar(2014,7,9,23,59);
		
		assertEquals(EditType.NAME_DATE, edit1.getType());
		assertEquals("hihihi", edit1.getSearchTitle());
		assertEquals(true, CalHelper.relevantEquals(startDate, edit1.getNewDeadline()));

		DonEditCommand edit2 = (DonEditCommand) parser.parseCommand("edit \"hihihi\" to 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit2.getNewDeadline()));
		
		// tets with time
		startDate = new GregorianCalendar(2014,7,9,1,23);
		DonEditCommand edit3 = (DonEditCommand) parser.parseCommand("edit \"hihihi\" to 09/08/2014 1.23 am");
		
		assertEquals(true, CalHelper.relevantEquals(startDate, edit3.getNewDeadline()));
		
		DonEditCommand edit4 = (DonEditCommand) parser.parseCommand("edit \"hihihi\" to 9 aug 1.23 am");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit4.getNewDeadline()));
		
		// tets ID edit date
		startDate = new GregorianCalendar(2014,7,9,23,59);
		
		DonEditCommand edit5 = (DonEditCommand) parser.parseCommand("e 666 to 09/08/2014");
		assertEquals(EditType.ID_DATE, edit5.getType());
		assertEquals(666, edit5.getSearchID());
		assertEquals(startDate.getTime().toString(), edit5.getNewDeadline().getTime().toString());

		DonEditCommand edit6 = (DonEditCommand) parser.parseCommand("edit 666 to 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit6.getNewDeadline()));		
	
	}
	@Test
	public void testHelp(){
		
		// test help
		DonHelpCommand help1 = (DonHelpCommand) parser.parseCommand("help");
		
		assertEquals(HelpType.HELP_GENERAL, help1.getRequestedCommand());
		
		DonHelpCommand help2 = (DonHelpCommand) parser.parseCommand("help add");
		
		assertEquals(HelpType.HELP_ADD, help2.getRequestedCommand());
		
		DonHelpCommand help3 = (DonHelpCommand) parser.parseCommand("help edit");
		
		assertEquals(HelpType.HELP_EDIT, help3.getRequestedCommand());
		
		DonHelpCommand help4 = (DonHelpCommand) parser.parseCommand("help search");
		
		assertEquals(HelpType.HELP_SEARCH, help4.getRequestedCommand());
		
		DonHelpCommand help5 = (DonHelpCommand) parser.parseCommand("help del");
		DonHelpCommand help6 = (DonHelpCommand) parser.parseCommand("help delete");
		
		assertEquals(HelpType.HELP_DELETE, help5.getRequestedCommand());
		assertEquals(HelpType.HELP_DELETE, help6.getRequestedCommand());
		
		DonHelpCommand help7 = (DonHelpCommand) parser.parseCommand("help mark");
			
		assertEquals(HelpType.HELP_MARK, help7.getRequestedCommand());

		DonHelpCommand help8 = (DonHelpCommand) parser.parseCommand("help undo");
		
		assertEquals(HelpType.HELP_UNDO, help8.getRequestedCommand());
		
		DonHelpCommand help9 = (DonHelpCommand) parser.parseCommand("help redo");
		
		assertEquals(HelpType.HELP_REDO, help9.getRequestedCommand());

		// test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("help commands").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_COMMAND, parser.parseCommand("helpppp add").getGeneralType());
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("help adds").getGeneralType());
	}

	@Test
	public void testLabel(){
		
		// test label 
		DonAddLabelCommand label1 = (DonAddLabelCommand) parser.parseCommand("label \"hihihi\" \"projects\"");
		
		assertEquals(AddLabelType.LABEL_NAME, label1.getAddLabelType());
		assertEquals("projects", label1.getNewLabel());
		assertEquals("hihihi", label1.getSearchTitle());
		//test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("label \"hihihi\" \"projects").getGeneralType());
		
		// tets label ID
		
		DonAddLabelCommand label2 = (DonAddLabelCommand) parser.parseCommand("label 666 \"projects\"");
		assertEquals(AddLabelType.LABEL_ID, label2.getAddLabelType());
		assertEquals("projects", label2.getNewLabel());
		assertEquals(666, label2.getSearchID());
	}
	@Test
	public void testDelabel(){
		// test delabel 
		
		DonDelabelCommand delabel1 = (DonDelabelCommand) parser.parseCommand("delabel \"hihihi\" \"projects\"");

		assertEquals(DelabelType.LABEL_NAME, delabel1.getDelabelType());
		assertEquals("projects", delabel1.getSearchLabel());
		assertEquals("hihihi", delabel1.getSearchTitle());

		//test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("delabel \"hihihi\" \"projects").getGeneralType());

		// tets delabel ID
		DonDelabelCommand delabel2 = (DonDelabelCommand) parser.parseCommand("delabel 666 \"projects\"");
		assertEquals(DelabelType.LABEL_ID, delabel2.getDelabelType());
		assertEquals("projects", delabel2.getSearchLabel());
		assertEquals(666, delabel2.getSearchID());
		
	}
	@Test
	public void testSearchLabel(){
		DonFindCommand search1 = (DonFindCommand) parser.parseCommand("sl \"projects\"");		
		
		// test search label
		assertEquals(SearchType.SEARCH_LABEL, search1.getType());
		assertEquals("projects", search1.getSearchTitle());
		
		// test invalid
		assertEquals(GeneralCommandType.INVALID_FORMAT, parser.parseCommand("sl \"projects").getGeneralType());
	}
}
