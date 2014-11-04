package doornot.parser;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import doornot.util.CalHelper;
import doornot.util.DonAddLabelCommand;
import doornot.util.DonCreateCommand;
import doornot.util.DonDelabelCommand;
import doornot.util.DonDeleteCommand;
import doornot.util.DonEditCommand;
import doornot.util.DonFindCommand;
import doornot.util.DonHelpCommand;
import doornot.util.DonInvalidCommand;
import doornot.util.DonInvalidCommand.InvalidType;
import doornot.util.DonMarkCommand;
import doornot.util.AbstractDonCommand.GeneralCommandType;
import doornot.util.DonAddLabelCommand.AddLabelType;
import doornot.util.DonCreateCommand.AddType;
import doornot.util.DonDelabelCommand.DelabelType;
import doornot.util.DonDeleteCommand.DeleteType;
import doornot.util.DonEditCommand.EditType;
import doornot.util.DonFindCommand.SearchType;
import doornot.util.DonHelpCommand.HelpType;
import doornot.util.DonMarkCommand.MarkType;

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
		
		
		DonCreateCommand create1 = (DonCreateCommand) parser.parseCommand("a hihihi 12345678 by 09/09/2014");
		DonCreateCommand create2 = (DonCreateCommand) parser.parseCommand("add hihihi 12345678 by 9th september");
		DonCreateCommand create3 = (DonCreateCommand) parser.parseCommand("add hihihi by 09/09/2014 13:24");
		DonCreateCommand create4 = (DonCreateCommand) parser.parseCommand("add hihihi by 9 sep 1.24 pm");
		
		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("a hihihi 12345678 by 12/13/2014");
		DonInvalidCommand invalid2 = (DonInvalidCommand) parser.parseCommand("ad hihihi by 09082014");
//		DonInvalidCommand invalid3 = (DonInvalidCommand) parser.parseCommand("a done by 09082014");
		DonInvalidCommand invalid4 = (DonInvalidCommand) parser.parseCommand("a hihih;i by 09082014");
		
		assertEquals(AddType.DEADLINE, create1.getType());
		assertEquals("hihihi 12345678", create1.getTaskTitle());

		assertEquals(true, CalHelper.relevantEquals(new GregorianCalendar(2014,8,9,23,59), create1.getStartDate()));
		
		assertEquals(true, CalHelper.relevantEquals(new GregorianCalendar(2014,8,9,23,59), create2.getStartDate()));
		
		assertEquals(InvalidType.INVALID_DATE, invalid1.getType());
		
		// test hours
		
		Calendar septCal = new GregorianCalendar(2014,8,9,13,24);

		
		assertEquals(septCal.getTime().toString(), create3.getStartDate().getTime().toString());
		assertEquals(true, CalHelper.relevantEquals(septCal, create3.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(septCal, create4.getStartDate()));
		
		// test invalid
		assertEquals(InvalidType.INVALID_COMMAND, invalid2.getType());
		assertEquals("ad", invalid2.getStringInput());
//		assertEquals(InvalidType.INVALID_FORMAT, invalid3.getType());
//		assertEquals("a", invalid3.getStringInput());
		assertEquals(InvalidType.INVALID_FORMAT, invalid4.getType());
		assertEquals("a", invalid4.getStringInput());
		
	}
	
	@Test
	public void testAddEvent(){
		// test add event

		
		DonCreateCommand create1 = (DonCreateCommand) parser.parseCommand("add hihihi from 07/08/2014 to 09/08/2014");
		DonCreateCommand create2 = (DonCreateCommand) parser.parseCommand("add hihihi from 7 aug to 9 aug");
		
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
		
		DonCreateCommand create3 = (DonCreateCommand) parser.parseCommand("add hihihi from 07/08/2014 1.24 pm to 09/08/2014 15:54");
		DonCreateCommand create4 = (DonCreateCommand) parser.parseCommand("add hihihi from 7 aug 1.24 pm to 9 aug 3.54 pm");
		assertEquals(true, CalHelper.relevantEquals(startDate1, create3.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate1, create3.getEndDate()));
		
		assertEquals(true, CalHelper.relevantEquals(startDate1, create4.getStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate1, create4.getEndDate()));
		
		// test invalid 
		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("add hihihi fro 07082014 to 09082014");
		DonInvalidCommand invalid2 = (DonInvalidCommand) parser.parseCommand("add hihihi from 0702014 to 0908204");
		DonInvalidCommand invalid3 = (DonInvalidCommand) parser.parseCommand("add hihihi from 0702014 2 0908204");
		
		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
		assertEquals("add", invalid1.getStringInput());
		assertEquals(InvalidType.INVALID_DATE, invalid2.getType());
		assertEquals(InvalidType.INVALID_DATE, invalid3.getType());
	}
	
	@Test
	public void testAddFloat(){
		DonCreateCommand create1 = (DonCreateCommand) parser.parseCommand("addf overdue work");
		// test event add
		assertEquals(AddType.FLOATING, create1.getType());
		assertEquals("overdue work", create1.getTaskTitle());
		
		// test invalid
//		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("addf overdue");
		
//		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
//		assertEquals("addf", invalid1.getStringInput());
	}
	
	@Test
	public void testMark(){
		
		DonMarkCommand mark1 = (DonMarkCommand) parser.parseCommand("mark blah95");
		
		// test mark
		assertEquals(MarkType.MARK_STRING, mark1.getMarkType());
		assertEquals("blah95", mark1.getSearchTitle());
		
		// test batch mark
		DonMarkCommand mark3 = (DonMarkCommand) parser.parseCommand("mark overdue");
		DonMarkCommand mark4 = (DonMarkCommand) parser.parseCommand("mark float");
		
		assertEquals(MarkType.MARK_OVERDUE, mark3.getMarkType());
		assertEquals(MarkType.MARK_FLOAT, mark4.getMarkType());
		
		// test invalid
//		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("mark undone");
		
//		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
//		assertEquals("mark", invalid1.getStringInput());
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
		DonDeleteCommand delete1 = (DonDeleteCommand) parser.parseCommand("del blah95");
		// test delete
		assertEquals(DeleteType.DELETE_TITLE, delete1.getType());
		assertEquals("blah95", delete1.getSearchTitle());
		
		// test batch delete
		DonDeleteCommand delete2 = (DonDeleteCommand) parser.parseCommand("del od");
		DonDeleteCommand delete3 = (DonDeleteCommand) parser.parseCommand("del fl");
		DonDeleteCommand delete4 = (DonDeleteCommand) parser.parseCommand("del #funny business");
		DonDeleteCommand delete5 = (DonDeleteCommand) parser.parseCommand("del done");
		
		assertEquals(DeleteType.DELETE_OVERDUE, delete2.getType());
		assertEquals(DeleteType.DELETE_FLOAT, delete3.getType());
		assertEquals(DeleteType.DELETE_LABEL, delete4.getType());
		assertEquals("funny business", delete4.getSearchTitle());
		assertEquals(DeleteType.DELETE_DONE, delete5.getType());
		
		// test invalid
//		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("del undone");
		
//		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
//		assertEquals("del", invalid1.getStringInput());
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
		DonFindCommand search = (DonFindCommand) parser.parseCommand("search blah95");
		// test search
		assertEquals(SearchType.SEARCH_NAME, search.getType());
		assertEquals("blah95", search.getSearchTitle());
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
		DonFindCommand search1 = (DonFindCommand) parser.parseCommand("son 09/08/2014");
		DonFindCommand search2 = (DonFindCommand) parser.parseCommand("son 9 aug");
		Calendar date1 = new GregorianCalendar(2014,7,9,23,59);
		// test search date
		assertEquals(SearchType.SEARCH_DATE, search1.getType());
		assertEquals(date1.getTime().toString(), search1.getSearchStartDate().getTime().toString());
		assertEquals(true, CalHelper.relevantEquals(date1, search2.getSearchStartDate()));
		
		// test search with time
		DonFindCommand search3 = (DonFindCommand) parser.parseCommand("son 09/08/2014 11:23");
		DonFindCommand search4 = (DonFindCommand) parser.parseCommand("son 9 aug 11:23");
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
		DonFindCommand search3 = (DonFindCommand) parser.parseCommand("free");
//		DonFindCommand search4 = (DonFindCommand) parser.parseCommand("search free");
		assertEquals(SearchType.SEARCH_FREE, search3.getType());
//		assertEquals(SearchType.SEARCH_FREE, search4.getType());
		
		// test search all
		DonFindCommand search5 = (DonFindCommand) parser.parseCommand("s");
		DonFindCommand search6 = (DonFindCommand) parser.parseCommand("search");
		assertEquals(SearchType.SEARCH_ALL, search5.getType());
		assertEquals(SearchType.SEARCH_ALL, search6.getType());
		
		// test search undone
		DonFindCommand search7 = (DonFindCommand) parser.parseCommand("sud");
//		DonFindCommand search8 = (DonFindCommand) parser.parseCommand("s undone");
//		DonFindCommand search9 = (DonFindCommand) parser.parseCommand("search undone");
		assertEquals(SearchType.SEARCH_UNDONE, search7.getType());
//		assertEquals(SearchType.SEARCH_UNDONE, search8.getType());
//		assertEquals(SearchType.SEARCH_UNDONE, search9.getType());
		
		// test search done
		DonFindCommand search16 = (DonFindCommand) parser.parseCommand("sd");
//		DonFindCommand search17 = (DonFindCommand) parser.parseCommand("s done");
//		DonFindCommand search18 = (DonFindCommand) parser.parseCommand("search done");
		assertEquals(SearchType.SEARCH_DONE, search16.getType());
//		assertEquals(SearchType.SEARCH_DONE, search17.getType());
//		assertEquals(SearchType.SEARCH_DONE, search18.getType());
		
		// test today
		DonFindCommand search10 = (DonFindCommand) parser.parseCommand("today");
		assertEquals(SearchType.TODAY, search10.getType());
		
		// test overdue
		DonFindCommand search11 = (DonFindCommand) parser.parseCommand("od");
		DonFindCommand search12 = (DonFindCommand) parser.parseCommand("overdue");
		assertEquals(SearchType.OVERDUE, search11.getType());
		assertEquals(SearchType.OVERDUE, search12.getType());
		
		// test future
		DonFindCommand search13 = (DonFindCommand) parser.parseCommand("future");
		assertEquals(SearchType.FUTURE, search13.getType());
				
		// test results
		DonFindCommand search14 = (DonFindCommand) parser.parseCommand("results");
		assertEquals(SearchType.RESULTS, search14.getType());
		
		// test float
		DonFindCommand search15 = (DonFindCommand) parser.parseCommand("fl");
		assertEquals(SearchType.FLOAT, search15.getType());	
		
		// test search done
		DonFindCommand search19 = (DonFindCommand) parser.parseCommand("s");
		DonFindCommand search20 = (DonFindCommand) parser.parseCommand("all");
		assertEquals(SearchType.SEARCH_ALL, search19.getType());
		assertEquals(SearchType.SEARCH_ALL, search20.getType());
	}
	@Test
	public void testEditName(){
		// test edit 
		DonEditCommand edit1 = (DonEditCommand) parser.parseCommand("ed hihihi to \"HEHEHE\"");
		
		assertEquals(EditType.NAME_NAME, edit1.getType());
		assertEquals("HEHEHE", edit1.getNewTitle());
		assertEquals("hihihi", edit1.getSearchTitle());
		
		//test invalid
		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("EDIT hihihi to HEHEHE\"");
		
		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
		assertEquals("EDIT", invalid1.getStringInput());
		
		// test edit ID
		DonEditCommand edit2 = (DonEditCommand) parser.parseCommand("edit 666 to \"hehehe\"");
		
		assertEquals(EditType.ID_NAME, edit2.getType());
		assertEquals("hehehe", edit2.getNewTitle());
		assertEquals(666, edit2.getSearchID());		
	}
	
	@Test
	public void testEditEvent(){
		
		// test edit event
		DonEditCommand edit1 = (DonEditCommand) parser.parseCommand("edit hihihi from 07/08/2014 to 09/08/2014");
		Calendar startDate = new GregorianCalendar(2014,7,7,23,59), endDate = new GregorianCalendar(2014,7,9,23,59);
		
		assertEquals(EditType.NAME_EVENT, edit1.getType());
		assertEquals("hihihi", edit1.getSearchTitle());
		assertEquals(true, CalHelper.relevantEquals(startDate, edit1.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit1.getNewEndDate()));

		DonEditCommand edit2 = (DonEditCommand) parser.parseCommand("edit hihihi from 7 aug to 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit2.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit2.getNewEndDate()));
		
		// test edit ID event
		startDate = new GregorianCalendar(2014,7,7,23,59);
		endDate = new GregorianCalendar(2014,7,9,23,59);
		
		DonEditCommand edit3 = (DonEditCommand) parser.parseCommand("edit 666 from 07/08/2014 to 09/08/2014");
		
		assertEquals(EditType.ID_EVENT, edit3.getType());
		assertEquals(666, edit3.getSearchID());
		assertEquals(true, CalHelper.relevantEquals(startDate, edit3.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit3.getNewEndDate()));
		
		DonEditCommand edit4 = (DonEditCommand) parser.parseCommand("edit 666 from 7 aug to 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit4.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit4.getNewEndDate()));
		
		// tets with time
		startDate = new GregorianCalendar(2014,7,7,13,55);
		endDate = new GregorianCalendar(2014,7,9,11,44);
		
		DonEditCommand edit5 = (DonEditCommand) parser.parseCommand("edit 666 from 07/08/2014 13:55 to 09/08/2014 11:44");
		
		assertEquals(true, CalHelper.relevantEquals(startDate, edit5.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit5.getNewEndDate()));
		
		DonEditCommand edit6 = (DonEditCommand) parser.parseCommand("edit 666 from 7 aug 13:55 to 9 aug 11:44");
		
		assertEquals(true, CalHelper.relevantEquals(startDate, edit6.getNewStartDate()));
		assertEquals(true, CalHelper.relevantEquals(endDate, edit6.getNewEndDate()));
	}
	
	@Test
	public void testEditDate(){
		// test edit date
		DonEditCommand edit1 = (DonEditCommand) parser.parseCommand("edit hihihi by 09/08/2014");
		Calendar startDate = new GregorianCalendar(2014,7,9,23,59);
		
		assertEquals(EditType.NAME_DATE, edit1.getType());
		assertEquals("hihihi", edit1.getSearchTitle());
		assertEquals(true, CalHelper.relevantEquals(startDate, edit1.getNewDeadline()));

		DonEditCommand edit2 = (DonEditCommand) parser.parseCommand("edit hihihi by 9 aug");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit2.getNewDeadline()));
		
		// tets with time
		startDate = new GregorianCalendar(2014,7,9,1,23);
		DonEditCommand edit3 = (DonEditCommand) parser.parseCommand("edit hihihi by 09/08/2014 1.23 am");
		
		assertEquals(true, CalHelper.relevantEquals(startDate, edit3.getNewDeadline()));
		
		DonEditCommand edit4 = (DonEditCommand) parser.parseCommand("edit hihihi by 9 aug 1.23 am");
		assertEquals(true, CalHelper.relevantEquals(startDate, edit4.getNewDeadline()));
		
		// tets ID edit date
		startDate = new GregorianCalendar(2014,7,9,23,59);
		
		DonEditCommand edit5 = (DonEditCommand) parser.parseCommand("e 666 by 09/08/2014");
		assertEquals(EditType.ID_DATE, edit5.getType());
		assertEquals(666, edit5.getSearchID());
		assertEquals(startDate.getTime().toString(), edit5.getNewDeadline().getTime().toString());

		DonEditCommand edit6 = (DonEditCommand) parser.parseCommand("edit 666 by 9 aug");
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
		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("help commands");
		DonInvalidCommand invalid2 = (DonInvalidCommand) parser.parseCommand("helpppp add");
		DonInvalidCommand invalid3 = (DonInvalidCommand) parser.parseCommand("help adds");
		
		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
		assertEquals("help", invalid1.getStringInput());
		assertEquals(InvalidType.INVALID_COMMAND, invalid2.getType());
		assertEquals("helpppp", invalid2.getStringInput());
		assertEquals(InvalidType.INVALID_FORMAT, invalid3.getType());
		assertEquals("help", invalid3.getStringInput());
	}

	@Test
	public void testLabel(){
		
		// test label 
		DonAddLabelCommand label1 = (DonAddLabelCommand) parser.parseCommand("label hihihi #projects");
		
		assertEquals(AddLabelType.LABEL_NAME, label1.getAddLabelType());
		assertEquals("projects", label1.getNewLabel());
		assertEquals("hihihi", label1.getSearchTitle());
		
		// tets label ID
		
		DonAddLabelCommand label2 = (DonAddLabelCommand) parser.parseCommand("label 666 #projects");
		assertEquals(AddLabelType.LABEL_ID, label2.getAddLabelType());
		assertEquals("projects", label2.getNewLabel());
		assertEquals(666, label2.getSearchID());
	}
	@Test
	public void testDelabel(){
		// test delabel 
		
		DonDelabelCommand delabel1 = (DonDelabelCommand) parser.parseCommand("dl hihihi #projects");

		assertEquals(DelabelType.LABEL_NAME, delabel1.getDelabelType());
		assertEquals("projects", delabel1.getSearchLabel());
		assertEquals("hihihi", delabel1.getSearchTitle());

		// tets delabel ID
		DonDelabelCommand delabel2 = (DonDelabelCommand) parser.parseCommand("delabel 666 #projects");
		assertEquals(DelabelType.LABEL_ID, delabel2.getDelabelType());
		assertEquals("projects", delabel2.getSearchLabel());
		assertEquals(666, delabel2.getSearchID());
		
		//test delabel all id
		DonDelabelCommand delabel3 = (DonDelabelCommand) parser.parseCommand("delabel 666");
		assertEquals(DelabelType.LABEL_ALL_ID, delabel3.getDelabelType());
		assertEquals(666, delabel3.getSearchID());
		
		//test delabel all name
		DonDelabelCommand delabel4 = (DonDelabelCommand) parser.parseCommand("delabel blah95");
		assertEquals(DelabelType.LABEL_ALL_NAME, delabel4.getDelabelType());
		assertEquals("blah95", delabel4.getSearchTitle());
		
		
	}
	@Test
	public void testSearchLabel(){
		DonFindCommand search1 = (DonFindCommand) parser.parseCommand("sl #projects");		
		
		// test search label
		assertEquals(SearchType.SEARCH_LABEL, search1.getType());
		assertEquals("projects", search1.getSearchTitle());
		
		// test invalid
		DonInvalidCommand invalid1 = (DonInvalidCommand) parser.parseCommand("sl projects");
		
		assertEquals(InvalidType.INVALID_FORMAT, invalid1.getType());
		assertEquals("sl", invalid1.getStringInput());

		
	}
}
