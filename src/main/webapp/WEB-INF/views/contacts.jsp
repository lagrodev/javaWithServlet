<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head><title>Справочник</title></head>
<body>
<h1>Контакты</h1>
<a href="${pageContext.request.contextPath}/contacts/form">+ Добавить</a>

<table border="1">
    <tr>
        <th>Фамилия</th>
        <th>Имя</th>
        <th>Телефоны</th>
        <th></th>
    </tr>
    <c:forEach var="c" items="${contacts}">
        <tr>
            <td>${c.lastName}</td>
            <td>${c.firstName}</td>
            <td><c:forEach var="p" items="${c.phoneNumbers}" varStatus="s">
                ${p}<c:if test="${!s.last}">, </c:if>
            </c:forEach></td>
            <td>
                <a href="${pageContext.request.contextPath}/contacts/form?id=${c.id}">✏</a>
                <form method="post" action="${pageContext.request.contextPath}/contacts" style="display:inline">
                    <input type="hidden" name="action" value="delete"/>
                    <input type="hidden" name="id" value="${c.id}"/>
                    <button type="submit">🗑</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>