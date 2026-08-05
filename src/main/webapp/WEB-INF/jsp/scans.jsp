<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Scans" scope="request"/>
<c:set var="activeNav" value="scans" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3>All scans</h3>
    <table class="data">
        <thead><tr><th>ID</th><th>Name</th><th>Engine</th><th>Status</th><th>Assets</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${scans}">
            <tr>
                <td>#${s.id}</td>
                <td>${s.name}</td>
                <td>${s.engineName}</td>
                <td><span class="badge status ${s.status}">${s.status}</span></td>
                <td>${s.assets.size()}</td>
                <td><a href="${pageContext.request.contextPath}/scan/${s.id}">View</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty scans}"><tr><td colspan="6">No scans yet.</td></tr></c:if>
        </tbody>
    </table>
</div>

<jsp:include page="_footer.jsp"/>
