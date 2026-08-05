<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Assets" scope="request"/>
<c:set var="activeNav" value="assets" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3>Discovered assets</h3>
    <table class="data">
        <thead><tr><th>IP</th><th>Hostname</th><th>OS</th><th>Fingerprinted</th><th>Vulns</th><th>Risk</th></tr></thead>
        <tbody>
        <c:forEach var="a" items="${assets}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/assets/${a.id}">${a.ipAddress}</a></td>
                <td>${a.hostName}</td>
                <td>${a.operatingSystem}</td>
                <td>${a.fingerprinted ? 'yes' : 'no'}</td>
                <td>${a.vulnerabilities.size()}</td>
                <td>${a.riskScore}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty assets}"><tr><td colspan="6">No assets yet.</td></tr></c:if>
        </tbody>
    </table>
</div>

<jsp:include page="_footer.jsp"/>
