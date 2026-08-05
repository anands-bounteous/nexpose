<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign in &middot; Nexpose Security Console</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nexpose.css">
</head>
<body>
<div class="login-wrap">
    <div class="login-box">
        <div class="brand">
            <span class="logo">R7</span>
            <span class="name">Nexpose<small>Security Console</small></span>
        </div>
        <h2>Sign in</h2>
        <div class="sub">Use your console or directory credentials.</div>
        <c:if test="${not empty error}">
            <div class="alert error">
                ${error}
                <c:if test="${not empty errorCode}"><br><small>Error code: ${errorCode}</small></c:if>
            </div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" autofocus>
            <label for="password">Password</label>
            <input type="password" id="password" name="password">
            <button class="btn" type="submit" style="width:100%">Sign in</button>
        </form>
        <div class="hint">Default administrator: <strong>nxadmin / nxadmin</strong>.
            Any other username is treated as a directory (LDAP) user.</div>
    </div>
</div>
</body>
</html>
