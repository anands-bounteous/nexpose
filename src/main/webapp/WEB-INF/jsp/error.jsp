<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Error" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3><span class="code-error">${empty errorCode ? 'ERROR' : errorCode}</span> &mdash; request failed</h3>
    <div class="alert error">${empty message ? 'An unexpected error occurred.' : message}</div>
    <c:if test="${not empty path}"><p><strong>Path:</strong> ${path}</p></c:if>
    <c:if test="${not empty stackTrace}">
        <h3>Stack trace</h3>
        <pre class="trace"><c:out value="${stackTrace}"/></pre>
    </c:if>
    <a class="btn secondary" href="${pageContext.request.contextPath}/dashboard">Back to dashboard</a>
</div>

<jsp:include page="_footer.jsp"/>
