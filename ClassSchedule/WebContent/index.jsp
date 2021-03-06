<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!--<%@ page import="java.util.ArrayList, scheduler.*, org.jsoup.Jsoup, org.jsoup.nodes.Document, org.jsoup.nodes.Element, org.jsoup.select.Elements" %> -->
<%@ page import="java.util.ArrayList, scheduler.*, edu.stanford.services.explorecourses.Course" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="format.css">
<title>Class Schedule</title>
</head>
<body>

	<div class="header">
	</div>

	<div class="title">
		<h1>Streamlining your class schedule</h1>
	</div>

	<div class="container">
	
	    <h1>Import your Stanford courses to Google calendar.</h1>
	    
	    <div class="image_flow">
		    <img src="stanford_logo.png" class="stanford icon">
		    <img src="import_arrow.jpg" class="arrow icon">
			<img src="gcal_logo.png" class="gcal icon">
		</div>
		
		<form action="AddToCalendarServlet" method="post">
			<h2>1. Enter your course titles, separated by commas</h2>
			<input type="text" name="classes" class="input_field courses" placeholder="ex: CS140, CS229, EE108A">
			
			<h2>2. Log in to Google to add your classes</h2>
			<input type="submit" value="Add to Calendar"/ class="input_button">
		</form>
	
	<% if (session.getAttribute("CalendarManager") != null && session.getAttribute("ClassesEntered") != null) {
	
	CalendarManager cm = (CalendarManager) session.getAttribute("CalendarManager");

	if (request.getParameter("code") != null) { // logs in if necessary
		cm.createCalendarService(request.getParameter("code")); 
	}
	
	ArrayList<Course> classes = (ArrayList<Course>)session.getAttribute("ClassesEntered");
	System.out.println(classes.size());
	for (Course c : classes) {
		cm.createEvent(c);
	}
	
	%>
	<h2 class="add_success_h2"><a href="http://www.google.com/calendar" class="add_success">Classes added!</a></h2>
	<%
	session.setAttribute("ClassesEntered", null);
	
}
	%>
	</div>

</body>
</html> 