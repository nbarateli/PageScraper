<%@ page import="control.ScraperDatabase" %>
<%@ page import="misc.Utils" %>
<%@ page import="model.ScrapingManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%--
  Created by IntelliJ IDEA.
  User: Niko
  Date: 11.07.2018
  Time: 16:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  ScraperDatabase database = ((ScrapingManager) getServletConfig().getServletContext().getAttribute("manager")).getDatabase();
%>
<html>
<head>
  <link rel="stylesheet" href="style/main.css">
  <title>Page Scraper</title>
</head>
<body>
<div id="header">Page Scraper</div>
<div id="content">
  <form target="/api/process" id="url-form" method="post">
    <input placeholder="URL" name="url" autocomplete="off">
    <button type="submit">Scrape</button>
  </form>
  <div>
    <p id="message">&nbsp;</p>
  </div>
  <table id="table-header">
    <thead class="fixedHeader">
    <tr>
      <th class="link-row">link</th>

      <th class="src-row">source</th>
    </tr>
    </thead>
  </table>
  <div id="tableContainer" class="tableContainer">

    <table border="0" cellpadding="0" cellspacing="0" width="100%" id="hyperlinks" class="scrollTable">

      <tbody class="scrollContent" id="links">

      <%
        Map<String, List<String>> links = database.getAllLinks();
        for (String k : links.keySet()) {
          for (String el : links.get(k)) {
      %>
      <tr>
        <td>
          <a target="_blank" href="<%=el%>"><span class="text-overflow"><%=Utils.escapeHTML(el)%></span> </a>

        </td>
        <td><a target="_blank" href="<%=k%>"><span class="text-overflow"><%=Utils.escapeHTML(k)%></span></a>
        </td>
      </tr>
      <%
          }
        }
      %>

      </tbody>
    </table>
  </div>

</div>

<script id="template" type="x-tmpl-mustache">
  <td>
    <a target="_blank" href="{{&href}}"><span class="text-overflow">{{href}}</span> </a>

  </td>
  <td><a target="_blank" href="{{&src}}"><span class="text-overflow">{{src}}</span></a>
  </td>
</script>
</body>
<script src="vendor/mustache.min.js"></script>
<script src="script/main.js"></script>
</html>
