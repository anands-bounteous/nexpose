<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${asset.ipAddress}" scope="request"/>
<c:set var="activeNav" value="assets" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3>${asset.ipAddress} &mdash; ${asset.hostName}</h3>
    <p><strong>OS:</strong> ${asset.operatingSystem} &nbsp;|&nbsp;
       <strong>Risk:</strong> ${asset.riskScore} &nbsp;|&nbsp;
       <strong>Fingerprinted:</strong> ${asset.fingerprinted ? 'yes' : 'no'} &nbsp;|&nbsp;
       <%-- BUG (SI-3150): inverted -- shows "no" for a live asset and vice versa. --%>
       <strong>Live:</strong> ${!asset.live ? 'yes' : 'no'}</p>
    <table class="data">
        <thead><tr><th>Vulnerability</th><th>CVE</th><th>Severity</th><th>CVSS</th><th>Port</th></tr></thead>
        <tbody>
        <c:forEach var="v" items="${asset.vulnerabilities}">
            <tr>
                <td>${v.title}</td>
                <td>${v.cve}</td>
                <td><span class="badge ${v.recomputeSeverityLabel()}">${v.recomputeSeverityLabel()}</span></td>
                <td>${v.cvssScore}</td>
                <td>${v.port}/${v.protocol}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty asset.vulnerabilities}"><tr><td colspan="5">No vulnerabilities recorded.</td></tr></c:if>
        </tbody>
    </table>
</div>

<jsp:include page="_footer.jsp"/>
