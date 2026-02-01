<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Todo List</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<div class="layout">
    <%@ include file="menu.jspf" %>

    <main class="content">
        <h1>Your Todos</h1>

        <c:if test="${not empty sessionScope.currentUserName}">
            <p>Hi, <strong>${sessionScope.currentUserName}</strong>! These are your tasks.</p>
        </c:if>

        <!-- Toolbar: Add + global CRUD -->
        <div class="toolbar">
            <a class="button primary" href="<c:url value='/todos/new'/>">+ Add new task</a>

            <div class="toolbar-crud">
                <button type="button" class="button" onclick="onToggleSelected()">toggle</button>
                <button type="button" class="button" onclick="onEditSelected()">edit</button>
                <button type="button" class="button" onclick="onDeleteSelected()">delete</button>
            </div>
        </div>

        <div id="selection-warning" class="error" style="display:none; max-width:1000px;">
            Please select a task row first.
        </div>

        <div class="table-wrapper">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Description</th>
                    <th>Priority</th>
                    <th>Created</th>
                    <th>Due</th>
                    <th>Completed?</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="t" items="${items}">
                    <tr data-id="${t.id}"
                        onclick="selectRow(this)"
                        class="selectable-row
                        <c:choose>
                            <c:when test='${t.priority == "HIGH"}'> row-high</c:when>
                            <c:when test='${t.priority == "MEDIUM"}'> row-medium</c:when>
                            <c:otherwise> row-low</c:otherwise>
                        </c:choose>">

                        <td>${t.id}</td>

                        <td class="<c:if test='${t.completed}'>completed</c:if>">
                                ${t.description}
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${t.priority == 'HIGH'}">
                                    <span class="priority-pill priority-high">High</span>
                                </c:when>
                                <c:when test="${t.priority == 'LOW'}">
                                    <span class="priority-pill priority-low">Low</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="priority-pill priority-medium">Medium</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <!-- Created: full date + time same line -->
                        <td>${t.createdAtFormatted}</td>

                        <!-- Due: date + time on separate lines -->
                        <td>
                            <c:if test="${not empty t.dueDateFormatted}">
                                ${t.dueDateFormatted}<br/>
                                <span class="due-time">${t.dueTimeFormatted}</span>
                            </c:if>
                        </td>

                        <td><c:out value="${t.completed ? 'Yes' : 'No'}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </main>
</div>

<script type="text/javascript">
    let selectedRowId = null;
    let selectedRowEl = null;

    function clearWarning() {
        const warn = document.getElementById('selection-warning');
        if (warn) warn.style.display = 'none';
    }

    function showWarning() {
        const warn = document.getElementById('selection-warning');
        if (warn) warn.style.display = 'block';
    }

    function selectRow(tr) {
        clearWarning();
        if (selectedRowEl) {
            selectedRowEl.classList.remove('selected-row');
        }
        selectedRowEl = tr;
        selectedRowEl.classList.add('selected-row');
        selectedRowId = tr.getAttribute('data-id');
    }

    function ensureSelection() {
        if (!selectedRowId) {
            showWarning();
            return false;
        }
        return true;
    }

    function onToggleSelected() {
        if (!ensureSelection()) return;
        window.location.href = '<c:url value="/todos/toggle"/>' + '?id=' + selectedRowId;
    }

    function onEditSelected() {
        if (!ensureSelection()) return;
        window.location.href = '<c:url value="/todos/edit"/>' + '?id=' + selectedRowId;
    }

    function onDeleteSelected() {
        if (!ensureSelection()) return;
        if (!confirm('Delete item #' + selectedRowId + '?')) return;
        window.location.href = '<c:url value="/todos/delete"/>' + '?id=' + selectedRowId;
    }
</script>
</body>
</html>
