<%@ page import="java.util.List" %>
<%@ page import="history.HistoryNode" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:useBean id="history" class="history.HistoryBean" scope="session"/>
<html>
<head>
    <title>WEB - Лабораторная #2</title>
    <script>const contextPath = "${pageContext.request.contextPath}/";</script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/light.css" id="csslink">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico">
    <script src="${pageContext.request.contextPath}/jquery-3.4.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/script.js"></script>
</head>
<body>
    <div class="header centered">
        Акатьев Никита Львович группа P3211<br>
        Вариант 485<br>
        <img src="areas.png" id="areas" alt="Areas not loaded WTF"><br>
    </div>
    <form method="POST" id="main-form" action="area-check">
        <table class="label centered">
            <tr><td colspan="3">Значение X:</td></tr>
            <% for (int i = -4; i <= 2; i += 3) { %>
            <tr>
                <td><input type="button" class="X button" value="<%= i %>"></td>
                <td><input type="button" class="X button<%= (i == -1) ? " active" : ""%>" value="<%= i + 1 %>"></td>
                <td><input type="button" class="X button" value="<%= i + 2 %>"></td>
            </tr>
            <% } %>

            <tr><td colspan="3"><label for="textfield">Значение Y:</label></td></tr>
            <tr><td colspan="3"><input id="textfield" name="Y" autocomplete="off" placeholder="(-3; 3)"></td></tr>
            <tr><td colspan="3"><div id="message"></div></td></tr>

            <tr><td colspan="3">Значение R:</td></tr>
            <% for (double i = 1.0; i <= 3.0; i += 0.5) { %>
            <tr>
                <td colspan="3">
                    <label><input type="checkbox" name="R" class="R" value="<%= i %>"><%= i %></label>
                </td>
            </tr>
            <% } %>

            <tr><td colspan="3"><p><input type="submit" class="button" value="Проверить точку"></p></td></tr>
        </table>
        <input type="hidden" id="X_field" name="X" value="0">
    </form>

    <%
        List<HistoryNode> list = history.getNodeList();
        if (!list.isEmpty()) {
    %>
    <table class="label centered">
        <caption>История запросов</caption>

        <% for(HistoryNode node: list) { %>
        <tr><td>X</td><td><%= node.getX() %></td></tr>
        <tr><td>Y</td><td><%= node.getY() %></td></tr>
        <tr><td>R</td><td><%= node.getR() %></td></tr>
        <tr><td colspan="2"><%= node.isHit() ? "Попал в цель!" : "Промазал!" %></td></tr>
        <% } %>

    </table>
    <% } %>

    <div id="timer" class="label centered"></div>
    <input type="button" class="button" id="swapButton" value="Paint It Black">
</body>
</html>