<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reports" scope="request"/>
<c:set var="activeNav" value="report" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card form-card">
    <h3>Generate XML report</h3>
    <form method="get" action="${pageContext.request.contextPath}/report/xml">
        <label for="scanId">Scan</label>
        <select id="scanId" name="scanId">
            <c:forEach var="s" items="${scans}">
                <option value="${s.id}">#${s.id} &mdash; ${s.name} (${s.assets.size()} assets)</option>
            </c:forEach>
        </select>
        <div class="hint">The XML report lists every asset and its vulnerabilities.</div>
        <button class="btn" type="submit">Download XML</button>
    </form>
</div>

<div class="card form-card" style="margin-top:16px">
    <h3>Preview sectioned layout</h3>
    <form method="get" action="${pageContext.request.contextPath}/report/preview">
        <label for="scanId2">Scan</label>
        <select id="scanId2" name="scanId">
            <c:forEach var="s" items="${scans}">
                <option value="${s.id}">#${s.id} &mdash; ${s.name}</option>
            </c:forEach>
        </select>
        <button class="btn secondary" type="submit">Preview</button>
    </form>
</div>

<jsp:include page="_footer.jsp"/>
