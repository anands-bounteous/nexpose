<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="New Scan" scope="request"/>
<c:set var="activeNav" value="scan" scope="request"/>
<jsp:include page="_header.jsp"/>

<div class="card form-card">
    <h3>Configure scan</h3>
    <form method="post" action="${pageContext.request.contextPath}/scan">
        <label for="scanName">Scan name</label>
        <input type="text" id="scanName" name="scanName" value="Ad-hoc scan">

        <label for="targets">Targets (IPs, CIDR ranges, or hostnames)</label>
        <textarea id="targets" name="targets" placeholder="10.0.0.0/29&#10;192.168.1.20&#10;web01.lab.rapid7.com">10.0.0.10
10.0.0.11
10.0.0.12</textarea>
        <div class="hint">One target per line or comma separated. Engine: <strong>${engineName}</strong>.</div>

        <div class="checkbox">
            <input type="checkbox" id="background" name="background" value="true">
            <label for="background">Run in background (asynchronous scan)</label>
        </div>

        <button class="btn" type="submit">Start scan</button>
    </form>
</div>

<jsp:include page="_footer.jsp"/>
