<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Todo App – Home</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<div class="layout">
    <%@ include file="menu.jspf" %>

    <main class="content home-content">
        <div class="home-header">
            <h1 class="home-title">Welcome to the Todo App</h1>
            <p class="home-subtitle">
                Track your tasks, set priorities, and keep your day under control with a simple neon-themed dashboard.
            </p>
        </div>

        <!-- Status messages -->
        <c:if test="${param.logout == '1'}">
            <div class="info">You have been signed out.</div>
        </c:if>

        <c:if test="${param.login == 'required'}">
            <div class="error">Please sign in to access your todo list.</div>
        </c:if>

        <c:choose>
            <%-- Already signed in --%>
            <c:when test="${not empty sessionScope.currentUserName}">
                <p>Hi, <strong>${sessionScope.currentUserName}</strong>!
                    Use the menu on the left to manage your tasks.</p>
            </c:when>

            <%-- Not signed in: show login card --%>
            <c:otherwise>
                <div class="login-card">
                    <h2>Sign in</h2>
                    <p>Log in with your <strong>username or email</strong> (from the <code>users</code> table).</p>

                    <c:if test="${param.error == 'empty'}">
                        <div class="error">Please enter a username or email.</div>
                    </c:if>
                    <c:if test="${param.error == 'notfound'}">
                        <div class="error">User not found. Check your username or email.</div>
                    </c:if>

                    <form action="<c:url value='/login'/>" method="post">
                        <label for="login">Username or Email</label>
                        <input type="text" id="login" name="login"/>

                        <button class="button primary" type="submit">Continue</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>
</body>
</html>
