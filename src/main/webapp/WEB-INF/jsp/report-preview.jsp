<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Report preview" scope="request"/>
<c:set var="activeNav" value="report" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card">
    <h3>Report layout preview &mdash; ${scan.name}</h3>
    <pre class="xml"><c:out value="${xml}"/></pre>
</div>

<jsp:include page="_footer.jsp"/>
