<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<c:set var="activeNav" value="dashboard" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="grid">
    <div class="card stat"><div class="label">Scans</div><div class="value">${stats.scanCount}</div></div>
    <div class="card stat"><div class="label">Assets</div><div class="value">${stats.assetCount}</div></div>
    <div class="card stat"><div class="label">Vulnerabilities</div><div class="value">${stats.vulnerabilityCount}</div></div>
    <div class="card stat crit"><div class="label">Critical</div><div class="value">${stats.criticalCount}</div></div>
</div>

<div class="card">
    <h3>Recent scans</h3>
    <table class="data">
        <thead><tr><th>ID</th><th>Name</th><th>Engine</th><th>Status</th><th>Assets</th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${recentScans}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/scan/${s.id}">#${s.id}</a></td>
                <td>${s.name}</td>
                <td>${s.engineName}</td>
                <td><span class="badge status ${s.status}">${s.status}</span></td>
                <td>${s.assets.size()}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty recentScans}"><tr><td colspan="5">No scans yet.</td></tr></c:if>
        </tbody>
    </table>
</div>

<jsp:include page="_footer.jsp"/>
