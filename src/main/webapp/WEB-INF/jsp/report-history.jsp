<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Report history" scope="request"/>
<c:set var="activeNav" value="history" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3>Report history</h3>
    <table class="data">
        <thead><tr><th>Scan</th><th>Format</th><th>Size (bytes)</th><th>Created</th></tr></thead>
        <tbody>
        <c:forEach var="row" items="${history}">
            <tr>
                <td>${row.SCAN_NAME}</td>
                <td>${row.FORMAT}</td>
                <td>${row.SIZE_BYTES}</td>
                <td>${row.CREATED_AT}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty history}"><tr><td colspan="4">No report history.</td></tr></c:if>
        </tbody>
    </table>
</div>

<jsp:include page="_footer.jsp"/>
