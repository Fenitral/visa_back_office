<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Liste des demandes");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<div class="page-header">
    <h1>Liste des demandes</h1>
    <p>Total: <c:out value="${total}"/></p>
</div>

<c:if test="${not empty success}">
    <div class="alert alert-success"><c:out value="${success}"/></div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<div class="table-container">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Demandeur</th>
            <th>Type</th>
            <th>Statut</th>
            <th>Pieces obligatoires</th>
            <th>Dossier complet</th>
            <th>Date creation</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="d" items="${demandes}">
            <tr>
                <td><c:out value="${d.id}"/></td>
                <td>
                    <c:out value="${d.demandeur.nom}"/>
                    <c:out value="${d.demandeur.prenom}"/>
                </td>
                <td><c:out value="${d.typeDemande}"/></td>
                <td><c:out value="${d.statut}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${d.piecesObligatoiresCompletes}">Oui</c:when>
                        <c:otherwise>Non</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${d.dossierComplet}">Oui</c:when>
                        <c:otherwise>Non</c:otherwise>
                    </c:choose>
                </td>
                <td><c:out value="${d.dateCreation}"/></td>
                <td>
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/demandes/${d.id}">Voir</a>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/demandes/${d.id}/modifier">Modif</a>
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/demandes/${d.id}/imprimer" target="_blank">PDF</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty demandes}">
            <tr>
                <td colspan="8">Aucune demande.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
