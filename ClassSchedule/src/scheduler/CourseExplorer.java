package scheduler;
import java.util.HashSet;
import java.util.Set;

import edu.stanford.services.explorecourses.Course;
import edu.stanford.services.explorecourses.ExploreCoursesConnection;
import edu.stanford.services.explorecourses.MeetingSchedule;
import edu.stanford.services.explorecourses.Section;

/**
 * Manages the connection to ExploreCourses.
 */
public class CourseExplorer {
	
	/**
	 * Returns the first course retrieved named 'courseCode'.
	 */
	public static Course getCourseByCode(String courseCode) {
		System.out.println(courseCode);
		System.setProperty ("jsse.enableSNIExtension", "false");
	    ExploreCoursesConnection connection = new ExploreCoursesConnection();
	    
	    Set<Course> courses = new HashSet<Course>();
	    try {
	    	courses = connection.getCoursesByQuery(courseCode);
	    } catch (Exception e) {
	    	// do nothing...
	    }
	    
	    for (Course c : courses) {
	    	System.out.println("Fetched course code: " + c.getSubjectCodePrefix()+c.getSubjectCodeSuffix());
	    	System.out.println("Course code: " + courseCode);
	    	if (courseCode.equals((c.getSubjectCodePrefix()+c.getSubjectCodeSuffix()))) { //confirm course code matches courseCode exactly
		    	Set<Section> sections = c.getSections();
		    	for (Section sect : sections) {	
		    		for (MeetingSchedule sched : sect.getMeetingSchedules()) {
		    			String startDate = sched.getStartDate();
		    			String endDate = sched.getEndDate();
		    			String days = sched.getDays();
		    			String removetxt1 = "\t";
		    			String removetxt2 = "\n";
		    			days = days.replaceAll("(?i)" + removetxt1, "");
		    			days = days.replaceAll("(?i)" + removetxt2, "|");
		    			String startTime = sched.getStartTime();
		    			String endTime = sched.getEndTime();
		    			String location = sched.getLocation();
		    			System.out.println(c.getSubjectCodePrefix()+c.getSubjectCodeSuffix()+": "+c.getTitle());
		    			System.out.println("   Section: "+sect.getSectionNumber());
		    			System.out.println("   Component: "+sect.getComponent());
		    			System.out.println("   First day: "+startDate);
		    			System.out.println("   Last day: "+endDate);
		    			System.out.println("   Days: "+days);
		    			System.out.println("   Starts: "+startTime);
		    			System.out.println("   Ends: "+endTime);
		    			System.out.println("   Location: "+location);
		    	    	return c;
		    		}
		    	}
	    	}
	    }
	    
	    return null; // no courses found
	}
} 