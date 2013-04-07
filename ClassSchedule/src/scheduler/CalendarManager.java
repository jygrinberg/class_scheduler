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

    	Set<Section> sections = c.getSections();
    	for (Section sect : sections) {	
    		if (!sect.getComponent().equals("LEC")) { // temporary: only add lectures for now -- we will do discussions later
    			continue;
    		}
    		
    		for (MeetingSchedule sched : sect.getMeetingSchedules()) {
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
    			String removetxt1 = "\t";
    			String removetxt2 = "\n";
    			days = days.replaceAll("(?i)" + removetxt1, "");
    			days = days.replaceAll("(?i)" + removetxt2, "|");
    			Scanner s = new Scanner(days).useDelimiter("\\s*|\\s*");
    			ArrayList<String> listOfDays = new ArrayList<String>();
    			while (s.hasNext()) {
    				listOfDays.add(s.next());
    			}
    			String dayList = "";
    			for (int i = 0; i < listOfDays.size(); i++) {
    				String str = listOfDays.get(i);
    				str = (str.substring(0,1)).toUpperCase();
    				dayList.concat(str);
    				if (i != listOfDays.size()-1) {
    					dayList.concat(",");
    				}
    			}
    			
    			// Set parameters of calendar event
    			DateTime startDate = DateTime.parseRfc3339(firstDate+"T"+beginTime);
    			event.setStart(new EventDateTime().setDateTime(startDate).setTimeZone("America/Los_Angeles"));
    			DateTime endDate = DateTime.parseRfc3339(firstDate+"T"+endTime);
    			event.setEnd(new EventDateTime().setDateTime(endDate).setTimeZone("America/Los_Angeles"));
    			event.setSummary(courseCode + ": " + location);
    			event.setLocation(location);
    			event.setRecurrence(Arrays.asList("RRULE:FREQ=MONTHLY;BYDAY="+dayList+";UNTIL="+lastDate+"T"+endTime+"-07:00"));

    			Event recurringEvent = calendar.events().insert("primary", event).execute();

    			System.out.println("Event ID: " + recurringEvent.getId());
    		}
    	}
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