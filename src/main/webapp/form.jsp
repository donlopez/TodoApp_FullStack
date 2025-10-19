<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title><c:choose><c:when test="${empty item}">New</c:when><c:otherwise>Edit</c:otherwise></c:choose> Todo</title>
    <style>
        label { display:block; margin: 8px 0 4px; }
        input[type=text] { width: 480px; padding: 6px; }
        .row { margin: 10px 0; }
        .button { padding: 6px 12px; }
    </style>
</head>
<body>
<h1><c:choose><c:when test="${empty item}">New</c:when><c:otherwise>Edit</c:otherwise></c:choose> Todo</h1>

<form method="post" action="<c:url value='/todos'/>">
    <c:if test="${not empty item}">
        <input type="hidden" name="id" value="${item.id}"/>
    </c:if>

    <label for="description">Description</label>
    <input id="description" name="description"
           type="text" required
           value="<c:out value='${empty item ? "" : item.description}'/>"/>

    <div class="row">
        <label>
            <input type="checkbox" name="completed" value="true"
                   <c:if test="${not empty item and item.completed}">checked</c:if> />
            Completed
        </label>
    </div>

    <div class="row">
        <button class="button" type="submit">Save</button>
        <a class="button" href="<c:url value='/todos/list'/>">Cancel</a>
    </div>
</form>

</body>
</html>
