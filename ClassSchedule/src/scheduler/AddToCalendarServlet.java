package scheduler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.io.IOException;
import edu.stanford.services.explorecourses.Course;


/**
 * Servlet implementation class AddToCalendarServlet
 */
@WebServlet("/AddToCalendarServlet")
public class AddToCalendarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddToCalendarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
				
		String myClass = request.getParameter("class"); // TODO: parse array of classes
		
		// Trim whitespace in the String
		myClass = myClass.replaceAll("\\s","");
		System.out.println(myClass);
		
		CourseExplorer ce = new CourseExplorer();
		Course c = ce.getCourseByCode(myClass);
		
		// Keep track of classes that have been entered
		ArrayList<Course> classes = new ArrayList<Course>();
		classes.add(c);
		request.getSession().setAttribute("ClassesEntered", classes);
		
		String redirectURIwithCode = "http://localhost:8080/ClassSchedule/index.jsp";
		if (request.getSession().getAttribute("CalendarManager") == null) {
			CalendarManager cm = new CalendarManager();
			request.getSession().setAttribute("CalendarManager", cm);
			redirectURIwithCode = cm.setUp();
			System.out.println(redirectURIwithCode);
		}
		
		response.sendRedirect(redirectURIwithCode);
//		RequestDispatcher dispatch = request.getRequestDispatcher(redirectURIwithCode);
//		dispatch.forward(request, response);		
	}
}