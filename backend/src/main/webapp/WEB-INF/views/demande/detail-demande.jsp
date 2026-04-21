<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Detail demande");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<div class="page-header">
    <h1>Detail demande</h1>
    <p>ID: <c:out value="${demande.id}"/></p>
    <div style="display:flex; gap:10px; margin-top:10px; flex-wrap:wrap;">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/demandes/${demande.id}/modifier">Modifier</a>
        <a class="btn btn-outline" href="${pageContext.request.contextPath}/demandes/${demande.id}/imprimer" target="_blank">Voir PDF / Imprimer</a>
    </div>
</div>

<div class="details-container">
    <div class="card">
        <div class="card-header">
            <div class="card-title">Informations demande</div>
        </div>
        <div class="info-row">
            <div class="info-label">Type</div>
            <div class="info-value"><c:out value="${demande.typeDemande}"/></div>
        </div>
        <div class="info-row">
            <div class="info-label">Statut</div>
            <div class="info-value"><c:out value="${demande.statut}"/></div>
        </div>
        <div class="info-row">
            <div class="info-label">Date creation</div>
            <div class="info-value"><c:out value="${demande.dateCreation}"/></div>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <div class="card-title">Demandeur</div>
        </div>
        <div class="info-row">
            <div class="info-label">Nom</div>
            <div class="info-value">
                <c:out value="${demande.demandeur.nom}"/> <c:out value="${demande.demandeur.prenom}"/>
            </div>
        </div>
        <div class="info-row">
            <div class="info-label">Nationalite</div>
            <div class="info-value"><c:out value="${demande.demandeur.nationalite}"/></div>
        </div>
        <div class="info-row">
            <div class="info-label">Telephone</div>
            <div class="info-value"><c:out value="${demande.demandeur.telephone}"/></div>
        </div>
    </div>

</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
