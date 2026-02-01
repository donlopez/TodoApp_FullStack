<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>
        <c:choose>
            <c:when test="${empty item}">New</c:when>
            <c:otherwise>Edit</c:otherwise>
        </c:choose>
        Todo
    </title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<div class="layout">
    <%@ include file="menu.jspf" %>

    <main class="content">
        <h1>
            <c:choose>
                <c:when test="${empty item}">New</c:when>
                <c:otherwise>Edit</c:otherwise>
            </c:choose>
            Todo
        </h1>

        <!-- Current priority: use item's priority if present, otherwise MEDIUM -->
        <c:set var="currentPriority" value="${empty item ? 'MEDIUM' : item.priority}" />

        <form action="<c:url value='/todos'/>" method="post">
            <c:if test="${not empty item}">
                <input type="hidden" name="id" value="${item.id}"/>
            </c:if>

            <div class="row">
                <label for="description">Description</label>
                <input type="text" id="description" name="description"
                       value="<c:out value='${item.description}'/>" required/>
            </div>

            <div class="row">
                <label for="priority">Priority</label>
                <select id="priority" name="priority">
                    <option value="HIGH"
                            <c:if test="${currentPriority == 'HIGH'}">selected</c:if>>
                        High
                    </option>
                    <option value="MEDIUM"
                            <c:if test="${currentPriority == 'MEDIUM'}">selected</c:if>>
                        Medium
                    </option>
                    <option value="LOW"
                            <c:if test="${currentPriority == 'LOW'}">selected</c:if>>
                        Low
                    </option>
                </select>
            </div>

            <!-- Due date (optional) -->
            <div class="row">
                <label for="dueDate">Due date</label>
                <input type="datetime-local"
                       id="dueDate"
                       name="dueDate"
                       value="${not empty item ? item.dueDateInputValue : ''}"/>
            </div>

            <div class="row">
                <label>
                    <input type="checkbox" name="completed" value="true"
                           <c:if test="${not empty item && item.completed}">checked</c:if> />
                    Completed
                </label>
            </div>

            <div class="row">
                <button class="button primary" type="submit">Save</button>
                <a class="button" href="<c:url value='/todos/list'/>">Cancel</a>
            </div>
        </form>
    </main>
</div>
</body>
</html>
