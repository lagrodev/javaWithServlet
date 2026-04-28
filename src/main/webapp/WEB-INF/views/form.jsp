<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>${empty contact ? 'Новый контакт' : 'Редактирование'}</title>
    <style>
        body {
            font-family: sans-serif;
            max-width: 500px;
            margin: 40px auto;
        }

        label {
            display: block;
            margin-top: 12px;
            font-weight: bold;
        }

        input[type=text] {
            width: 100%;
            padding: 6px;
            margin-top: 4px;
            box-sizing: border-box;
        }

        .hint {
            font-size: 12px;
            color: #888;
        }

        button {
            margin-top: 16px;
            padding: 8px 20px;
        }

        a {
            display: inline-block;
            margin-top: 12px;
        }
    </style>
</head>
<body>

<h2>${empty contact ? 'Новый контакт' : 'Редактирование: '.concat(contact.fullName)}</h2>

<form method="post" action="${pageContext.request.contextPath}/contacts/form">

    <%-- при редактировании передаём id скрытым полем --%>
    <c:if test="${not empty contact}">
        <input type="hidden" name="id" value="${contact.id}"/>
    </c:if>

    <label>Фамилия
        <input type="text" name="lastName" value="${contact.lastName}" required/>
    </label>

    <label>Имя
        <input type="text" name="firstName" value="${contact.firstName}" required/>
    </label>

    <label>Телефоны
        <input type="text" name="phones"
               value="<c:forEach var='p' items='${contact.phoneNumbers}' varStatus='s'>${p}<c:if test='${!s.last}'>, </c:if></c:forEach>"
               placeholder="+79991234567, +79997654321"
               required/>
        <span class="hint">До 3 номеров через запятую</span>
    </label>

    <button type="submit">${empty contact ? 'Добавить' : 'Сохранить'}</button>
</form>

<a href="${pageContext.request.contextPath}/contacts">← Назад</a>

</body>
</html>