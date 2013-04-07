package scheduler;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.calendar.CalendarScopes;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.stanford.services.explorecourses.Course;
import edu.stanford.services.explorecourses.MeetingSchedule;
import edu.stanford.services.explorecourses.Section;

/**
 * Manages the connection to Google Calendar.
 */
public class CalendarManager {
	private Calendar calendar = null; // the calendar connection to Google
	private GoogleAuthorizationCodeFlow flow = null;
	private HttpTransport httpTransport;
    private JacksonFactory jsonFactory;
    
    // The clientId and clientSecret are copied from the API Access tab on
    // the Google APIs Console
    private String clientId = "1038612892731-ou0v025qvcgqov5rgoj3fk5utvr1p0ng.apps.googleusercontent.com";
    private String clientSecret = "qQpwJJVyb1NJkwXAyYYFsLAL";

    // Redirect URL for web based applications.
    private String redirectUrl = "http://localhost:8080/ClassSchedule/index.jsp";
    private String scope = "https://www.googleapis.com/auth/calendar";
	
    
	public void createCalendarService(String code) {
		System.out.println("code: " +code);
		
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//String code = br.readLine();
		
		try {
			GoogleTokenResponse GTresponse = flow.newTokenRequest(code).setRedirectUri(redirectUrl).execute();
			GoogleCredential credential = new GoogleCredential().setFromTokenResponse(GTresponse);
			
			//Create a new authorized API client
			calendar = new Calendar.Builder(httpTransport, jsonFactory, credential).build();
		} catch (IOException e) {
			// do nothing...
		}
	}
	
	/**
	 * Creates a gCal event for the Course c.
	 */
	public void createEvent(Course c) throws IOException {
	    if (calendar == null) { 
	    	System.out.println("There was no code parameter but the user is not logged in.");
	    	return;
	    }

	    if (c == null) {
	    	System.out.println("Attempting to create event for null Course");
	    }
	    	    
    	Set<Section> sections = c.getSections();
    	for (Section sect : sections) {	
    		if (!sect.getComponent().equals("LEC")) { // temporary: only add lectures for now -- we will do discussions later
    			continue;
    		}
    		
    		for (MeetingSchedule sched : sect.getMeetingSchedules()) {
//    			Event event = new Event();
//    			
//    			String courseCode = c.getSubjectCodePrefix()+c.getSubjectCodeSuffix();
//    			String location = sched.getLocation();
//    			event.setSummary(courseCode + " - " + location);
//    			event.setLocation(location);
//    			Date startDate = new Date();
//    			Date endDate = new Date(startDate.getTime() + 3600000);
//    			DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
//    			event.setStart(new EventDateTime().setDateTime(start));
//    			DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
//    			event.setEnd(new EventDateTime().setDateTime(end));
//
//    			Event createdEvent = calendar.events().insert("primary", event).execute();
//
//    			System.out.println("Event ID: " + createdEvent.getId());
    			
    			Event event = new Event();
    			
    			// Obtain class schedule data
    			String courseCode = c.getSubjectCodePrefix()+c.getSubjectCodeSuffix();
    			String location = sched.getLocation();
    			String firstDate = sched.getStartDate();
    			String lastDate = sched.getEndDate();
    			String beginTime = sched.getStartTime();
    			String endTime = sched.getEndTime();
    			// days
    			String days = sched.getDays();
    			List<String> listOfDays = Arrays.asList(days.split("\\s\\s*"));

//    			String removetxt1 = "\t";
//    			String removetxt2 = "\n";
//    			days = days.replaceAll("(?i)" + removetxt1, "");
//    			days = days.replaceAll("(?i)" + removetxt2, "|");
//    			Scanner s = new Scanner(days).useDelimiter("\\s*|\\s*");
    			
//    			ArrayList<String> listOfDays = new ArrayList<String>();
//    			while (s.hasNext()) {
//    				listOfDays.add(s.next());
//    			}
    			
    			String dayList = "";
    			for (int i = 0; i < listOfDays.size(); i++) {
    				String day = listOfDays.get(i).toUpperCase();
    				if (day.isEmpty()) continue;

    				if (day.length() > 1) {
        				day = day.substring(0,2);
    				}
    				
    				dayList += day;
    				if (i != listOfDays.size()-1) {
    					dayList += ",";
    				}
    			}
    			    			
    			// Set parameters of calendar event
    			System.out.println("firstDate: " + firstDate);
    			System.out.println("beginTime: " + beginTime);

    			//List<String> recurrenceRules = Arrays.asList("RRULE:FREQ=WEEKLY","BYDAY="+dayList,"UNTIL="+lastDate+"T"+endTime+"-07:00"));
    			
    			for (int d=0; d<listOfDays.size(); d++) {
    				
    				if (listOfDays.get(d).isEmpty()) continue;
    				
    				List<String> list = Arrays.asList(sect.getTerm().split("\\s"));
    				String quarter = list.get(1);
    				System.out.println(quarter);
    				if (!quarter.toLowerCase().equals("spring")) continue;
    				
    				System.out.println("old: "+firstDate);    				
    				firstDate = incrementDateByDayOfWeek(firstDate, listOfDays.get(d));
    				System.out.println("new: "+firstDate);
    				
    				
    				DateTime startDate = DateTime.parseRfc3339(firstDate+"T"+beginTime+".000-07:00");
        			event.setStart(new EventDateTime().setDateTime(startDate).setTimeZone("America/Los_Angeles"));
        			DateTime endDate = DateTime.parseRfc3339(firstDate+"T"+endTime+".000-07:00");
        			event.setEnd(new EventDateTime().setDateTime(endDate).setTimeZone("America/Los_Angeles"));
        			event.setSummary(courseCode + ": " + location);
        			event.setLocation(location);
        			//event.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;BYDAY="+dayList+";UNTIL="+lastDate+"T"+endTime+"-07:00"));
        			//event.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL="+"20131204"+"T"+"100000"+"-07:00"));
        			//event.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20110701T100000-07:00"));
        			List<String> recurrenceRules = new ArrayList<String>();
        			recurrenceRules.add("RRULE:FREQ=WEEKLY");
//        			System.out.println(dayList);
//        			recurrenceRules.add("RRULE:BYDAY="+dayList+";");
//        			recurrenceRules.add("COUNT=4");
        			event.setRecurrence(recurrenceRules);
        			
        			Event recurringEvent = calendar.events().insert("primary", event).execute();

        			System.out.println("Event ID: " + recurringEvent.getId());

    			}
    			
    		}
    	}
	}
	

	private String incrementDateByDayOfWeek(String date, String dayOfWeek) {
		dayOfWeek = dayOfWeek.toLowerCase();
		if (dayOfWeek.equals("monday")) {
			return incrementDateByNum(date, 0);
		} else if (dayOfWeek.equals("tuesday")) {
			return incrementDateByNum(date, 1);
		} else if (dayOfWeek.equals("wednesday")) {
			return incrementDateByNum(date, 2);
		} else if (dayOfWeek.equals("thursday")) {
			return incrementDateByNum(date, 3);
		} else if (dayOfWeek.equals("friday")) {
			return incrementDateByNum(date, 4);
		}
		return "";
	}
	
	private String incrementDateByNum(String date, int numDays) {
		int newDayOfMonth = Integer.parseInt(date.substring(date.length()-2, date.length())) + numDays;
		date = date.substring(0, date.length()-2);
		if (newDayOfMonth < 10) {
			date += "0" + newDayOfMonth +"";
		} else {
			date += newDayOfMonth +"";			
		}
		return date;
	}

	/**
	 * Establishes the Calendar connection with Google. 
	 */
	public String setUp() {
		httpTransport = new NetHttpTransport();
	    jsonFactory = new JacksonFactory();
	
	    flow = new GoogleAuthorizationCodeFlow.Builder(
	            httpTransport, jsonFactory, clientId, clientSecret,
	        Arrays.asList(CalendarScopes.CALENDAR)).setAccessType("online")
	            .setApprovalPrompt("auto").build();
		
		String url = flow.newAuthorizationUrl().setRedirectUri(redirectUrl).build();
		System.out.println("Please open the following URL in your browser then type the authorization code:");
		
		System.out.println("  " + url);
		
		return url;
	}
}