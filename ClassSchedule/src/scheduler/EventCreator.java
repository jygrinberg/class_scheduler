package scheduler;
import java.io.IOException;

import edu.stanford.services.explorecourses.Course;

public class EventCreator {

	/**
	 * This code will be moved to a servlet, which will be called once the user clicks the "Create" button
	 */
	public static void main(String[] args) throws IOException {

		CourseExplorer ce = new CourseExplorer();
		Course c = ce.getCourseByCode("EE101A");
		
	}
}
