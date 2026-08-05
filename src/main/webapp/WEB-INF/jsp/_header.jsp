<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty pageTitle ? 'Nexpose' : pageTitle} &middot; Nexpose Security Console</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nexpose.css">
</head>
<body>
<div class="layout">
    <aside class="sidebar">
        <div class="brand">
            <span class="logo">R7</span>
            <span class="name">Nexpose<small>Security Console</small></span>
        </div>
        <ul class="nav">
            <li><a class="${activeNav=='dashboard'?'active':''}" href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
            <li><a class="${activeNav=='scan'?'active':''}" href="${pageContext.request.contextPath}/scan">New Scan</a></li>
            <li><a class="${activeNav=='scans'?'active':''}" href="${pageContext.request.contextPath}/scans">Scans</a></li>
            <li><a class="${activeNav=='assets'?'active':''}" href="${pageContext.request.contextPath}/assets">Assets</a></li>
            <li><a class="${activeNav=='report'?'active':''}" href="${pageContext.request.contextPath}/report">Reports</a></li>
            <li><a class="${activeNav=='history'?'active':''}" href="${pageContext.request.contextPath}/report/history">Report History</a></li>
        </ul>
        <div class="foot">Nexpose POC &middot; v1.0.0</div>
    </aside>
    <div class="main">
        <div class="topbar">
            <div class="title">${empty pageTitle ? 'Nexpose' : pageTitle}</div>
            <div class="user">
                <c:choose>
                    <c:when test="${not empty sessionScope.CURRENT_USER}">
                        Signed in as <strong>${sessionScope.CURRENT_USER.displayName}</strong>
                        <a href="${pageContext.request.contextPath}/logout">Sign out</a>
                    </c:when>
                    <c:otherwise>Not signed in</c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="content">
