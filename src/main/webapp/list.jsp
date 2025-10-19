<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Todo List</title>
    <style>
        table { border-collapse: collapse; width: 900px }
        th, td { border: 1px solid #ccc; padding: 8px; }
        th { background: #f3f3f3; }
        a.button { padding: 4px 8px; border: 1px solid #888; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
<h1>Todos</h1>

<p><a class="button" href="<c:url value='/todos/new'/>">+ New item</a></p>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Description</th>
        <th>Completed</th>
        <th>Created</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="t" items="${items}">
        <tr>
            <td>${t.id}</td>

            <td style="${t.completed ? 'text-decoration: line-through; color: gray;' : ''}">
                <c:out value="${t.description}"/>
            </td>

            <td><c:out value="${t.completed}"/></td>

            <!-- Use the formatted getter -->
            <td>${t.createdAtFormatted}</td>

            <td>
                <a class="button" href="<c:url value='/todos/toggle?id=${t.id}'/>">toggle</a>
                <a class="button" href="<c:url value='/todos/edit?id=${t.id}'/>">edit</a>
                <a class="button" href="<c:url value='/todos/delete?id=${t.id}'/>"
                   onclick="return confirm('Delete item #${t.id}?')">delete</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
