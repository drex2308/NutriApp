<%--
    Author: Dhanush Venkataramu
    Last Modified: 8th April 2024

    This JSP file (`dashboard.jsp`) generates a dashboard view for operational analytics and logs.
    It utilizes JavaServer Pages (JSP) technology to dynamically populate data obtained from the servlet.
--%>

<%@ page import="org.bson.Document" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        /* CSS styles */
        .analytics-list li { padding: 5px 0; }
        .analytics-list li::before { content: '•'; color: #333; display: inline-block; width: 1em; margin-left: -1em; }
        .table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        .table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        .table th { background-color: #f2f2f2; color: black; }
        .table tr:nth-child(even) { background-color: #f9f9f9; }
        .table tr:nth-child(odd) { background-color: #ffffff; }
        .table tr:hover { background-color: #f1f1f1; }
        .container { padding: 0 15px; }
        html { box-sizing: border-box; }
        *, *:before, *:after { box-sizing: inherit; }
        h1, h2 { color: #333; padding-left: 15px; }
        body { margin-top: 20px; font-family: Arial, sans-serif; }
    </style>
</head>
<body>
<div class='container'>
    <h1><center>Dashboard</center></h1>

    <div class='row'>
        <h2>Operational Analytics</h2>
        <ol>
            <%-- Displaying the average delay of the 3rd API request --%>
            <li>Average 3rd API delay : ${averageDelay} ms</li>

            <%-- Displaying the top 3 ingredients selected by the user --%>
            <li>
                Top 3 Ingredients by User
                <ul class='sublist'>
                    <%-- Iterating over the list of top ingredients and displaying them --%>
                    <% for (Document doc : (List<Document>)request.getAttribute("topIngredients")) { %>
                    <li><%= doc.getString("_id") %></li>
                    <% } %>
                </ul>
            </li>

            <%-- Displaying the top 3 measures selected by the user --%>
            <li>
                Top 3 Measures selected by User
                <ul class='sublist'>
                    <%-- Iterating over the list of top measures and displaying them --%>
                    <% for (Document doc : (List<Document>)request.getAttribute("topMeasures")) { %>
                    <li><%= doc.getString("_id") %></li>
                    <% } %>
                </ul>
            </li>
        </ol>
    </div>

    <h2>Logs</h2>
    <div class='row mt-4'>
        <table class='table'>
            <thead>
            <tr>
                <th>Origin</th>
                <th>Destination</th>
                <th>Device-Type</th>
                <th>HTTP Method</th>
                <th>API-Endpoint</th>
                <th>Call Body</th>
                <th>TimeStamp</th>
                <th>Processing Delay (ms)</th>
            </tr>
            </thead>
            <tbody>
            <%-- Iterating over the list of log entries and displaying them --%>
            <% for (Document doc : (List<Document>)request.getAttribute("logEntries")) { %>
            <tr>
                <td><%= doc.getString("origin") %></td>
                <td><%= doc.getString("dest") %></td>
                <td><%= doc.getString("device") %></td>
                <td><%= doc.getString("method") %></td>
                <td><%= doc.getString("api_endpoint") %></td>

                <%-- Truncating long call body texts if needed --%>
                <%  String body = doc.getString("body");
                    if (body.length() > 400) {
                        body = body.substring(0, 50) + "....";
                    } %>

                <td><%= body %></td>
                <td><%= ((Date) doc.get("timestamp")).toString() %></td>
                <td><%= doc.getLong("delay") %></td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
