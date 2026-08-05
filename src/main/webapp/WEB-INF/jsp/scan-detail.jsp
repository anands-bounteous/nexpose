<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Scan #${scan.id}" scope="request"/>
<c:set var="activeNav" value="scans" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3>${scan.name} <span class="badge status ${scan.status}">${scan.status}</span></h3>
    <p><strong>Engine:</strong> ${scan.engineName} &nbsp;|&nbsp;
       <strong>Completed:</strong> ${completedAt}</p>
    <p>
        <a class="btn" href="${pageContext.request.contextPath}/report/xml?scanId=${scan.id}">Download XML report</a>
        <a class="btn secondary" href="${pageContext.request.contextPath}/report/preview?scanId=${scan.id}">Preview layout</a>
    </p>
    <table class="data">
        <thead><tr><th>Asset</th><th>Hostname</th><th>OS</th><th>Vulns</th><th>Risk</th></tr></thead>
        <tbody>
        <c:forEach var="a" items="${scan.assets}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/assets/${a.id}">${a.ipAddress}</a></td>
                <td>${a.hostName}</td>
                <td>${a.operatingSystem}</td>
                <td>${a.vulnerabilities.size()}</td>
                <td>${a.riskScore}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="_footer.jsp"/>
