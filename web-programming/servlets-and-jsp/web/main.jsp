<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:useBean id="history" class="history.HistoryBean" scope="session"/>

<c:set var="contextPath" value="${pageContext.request.contextPath}/" />
<c:set var="lightTheme" value="${cookie.lightTheme.value}" />

<c:if test="${not cookie.containsKey('lightTheme')}">
    <% response.addCookie(new Cookie("lightTheme", "true")); %>
    <c:set var="lightTheme" value="true" />
</c:if>

<c:set var="themePath" value="${contextPath}${lightTheme ? 'light.css' : 'dark.css'}" />

<html>
<head>
    <title>WEB - Лабораторная #2</title>
    <script>
        const contextPath = "${contextPath}";
        let lightTheme = ${lightTheme};
    </script>
    <link rel="stylesheet" type="text/css" href="${themePath}" id="csslink">
    <link rel="shortcut icon" href="${contextPath}favicon.ico">
    <script src="${contextPath}jquery-3.4.1.min.js"></script>
    <script src="${contextPath}script.js"></script>
    <script src="${contextPath}canvas.js"></script>
</head>
<body>
    <div id="bg-text">2</div>
    <table><tr><td style="width:50%">
        <div class="header centered">
            Акатьев Никита Львович группа P3211<br>
            Вариант 485<br>
        </div>
        <div class="label centered">
            <label for="radius-selector">Выберите радиус из списка: </label><br>
            <select id="radius-selector"></select><br>
            <canvas id="canvas" height="400" width="400">Damn yo browser is old man...</canvas>
            <div id="message"></div>
        </div>
        <form method="POST" id="main-form" action="area-check">
            <table class="label centered">
                <tr><td colspan="3">Значение X:</td></tr>
                <c:forEach var="i" begin="0" end="6" step="3">
                    <tr>
                        <td><input type="button" class="X button" value="${i - 4}"></td>
                        <td><input type="button" class="X button${i eq 3 ? " active" : ""}" value="${i - 3}"></td>
                        <td><input type="button" class="X button" value="${i - 2}"></td>
                    </tr>
                </c:forEach>

                <tr><td colspan="3"><label for="textfield">Значение Y:</label></td></tr>
                <tr><td colspan="3"><input id="textfield" name="Y" autocomplete="off" placeholder="(-3; 3)"></td></tr>

                <tr><td colspan="3">Значение R:</td></tr>
                <c:forEach var="i" begin="2" end="6" step="1">
                    <tr>
                        <td colspan="3">
                            <label><input type="checkbox" name="R" class="R" value="${i / 2.0}">${i / 2.0}</label>
                        </td>
                    </tr>
                </c:forEach>

                <tr><td colspan="3"><p><input type="submit" class="button" value="Проверить точку"></p></td></tr>
            </table>
            <input type="hidden" id="X_field" name="X" value="0">
        </form>
    </td>
    <td style="width:50%">
        <c:if test="${not empty history.nodeList}">
            <table class="label centered">
                <caption>История запросов</caption>
                <tr><th>X</th><th>Y</th><th>R</th><th>Результат</th></tr>

                <c:forEach items="${history.nodeList}" var="node">
                    <tr>
                        <td class="history-x">${node.x}</td>
                        <td class="history-y">${node.y}</td>
                        <td>${node.r}</td>
                        <td>${node.hit ? "Попал в цель!" : "Промазал!"}</td>
                    </tr>
                </c:forEach>
            </table>

        </c:if>
    </td></tr></table>
    <div id="timer" class="label centered"></div>
    <input type="button" class="button" id="swapButton" value="Theme Change">
</body>
</html>
