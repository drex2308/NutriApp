<%--
    Author: Dhanush Venkataramu
    Last Modified: 8th April 2024

    This JSP file (`index.jsp`) generates a welcome message and link to dashboard.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>  <!-- Set the content type and encoding for the JSP page -->
<!DOCTYPE html> <!-- Declare the document type as HTML5 -->
<html> <!-- Start of the HTML document -->
<head> <!-- Start of the head section -->
    <title>JSP - Hello World</title> <!-- Set the title of the web page -->
</head> <!-- End of the head section -->
<body> <!-- Start of the body section -->
<center><h1>Hello! Welcome! Namaste !</h1><center> <!-- Display a greeting message -->
<br/> <!-- Line break for spacing -->
<center><a href="./api/dashboard">Dashboard</a></center> <!-- Link to the Dashboard servlet -->
</body> <!-- End of the body section -->
</html> <!-- End of the HTML document -->
