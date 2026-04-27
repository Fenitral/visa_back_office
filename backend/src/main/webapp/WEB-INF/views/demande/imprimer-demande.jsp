<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Imprimer demande");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<style>
    @media print {
        .sidebar,
        .page-header,
        .print-actions {
            display: none !important;
        }
        .main-content {
            margin-left: 0 !important;
            padding: 0 !important;
        }
        body {
            background: #fff !important;
        }
        .print-sheet {
            box-shadow: none !important;
            border: 1px solid #000 !important;
        }
    }

    .print-sheet {
        max-width: 980px;
        margin: 0 auto;
        background: #fff;
        border: 1px solid #d9d9d9;
    }
    .print-header {
        text-align: center;
        padding: 18px 20px 10px;
        border-bottom: 1px solid #d9d9d9;
    }
    .print-header h1 {
        margin: 8px 0 4px;
        font-size: 1.2rem;
        text-transform: uppercase;
    }
    .print-content {
        padding: 18px 20px;
        display: grid;
        gap: 14px;
    }
    .print-block {
        border: 1px solid #e6e6e6;
        padding: 12px;
        background: #fff;
    }
    .print-block h2 {
        margin: 0 0 10px;
        font-size: 1rem;
    }
    .row {
        display: grid;
        grid-template-columns: 220px 1fr;
        gap: 10px;
        padding: 6px 0;
        border-bottom: 1px dashed #eee;
    }
    .row:last-child { border-bottom: none; }
    .label { font-weight: 700; }
    .print-actions { display:flex; justify-content:flex-end; gap:10px; margin: 12px auto; max-width: 980px; }

    .pieces-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 8px 12px;
    }
    @media (max-width: 760px) {
        .pieces-grid { grid-template-columns: 1fr; }
        .row { grid-template-columns: 1fr; }
    }
</style>

<div class="page-header">
    <h1>Demande (version PDF / impression)</h1>
    <p>ID: <c:out value="${demande.id}"/></p>
</div>

<div class="print-actions">
    <a class="btn btn-outline" href="${pageContext.request.contextPath}/demandes/${demande.id}/modifier">Modifier</a>
    <button class="btn btn-primary" type="button" onclick="window.print()">Imprimer</button>
</div>

<div class="print-sheet">
    <header class="print-header">
        <p>Ambassade de Madagascar en France</p>
        <p>04 Avenue Raphael 75016 - Paris</p>
        <h1>Demande de visa</h1>
    </header>

    <div class="print-content">
        <section class="print-block">
            <h2>Informations demande</h2>
            <div class="row"><div class="label">Type</div><div><c:out value="${demande.typeDemande}"/></div></div>
            <div class="row"><div class="label">Statut</div><div><c:out value="${demande.statut}"/></div></div>
            <div class="row"><div class="label">Pieces obligatoires</div><div><c:out value="${demande.piecesObligatoiresCompletes}"/></div></div>
            <div class="row"><div class="label">Dossier complet</div><div><c:out value="${demande.dossierComplet}"/></div></div>
            <div class="row"><div class="label">Date creation</div><div><c:out value="${demande.dateCreation}"/></div></div>
        </section>

        <section class="print-block">
            <h2>Demandeur</h2>
            <div class="row"><div class="label">Nom</div><div><c:out value="${demande.demandeur.nom}"/> <c:out value="${demande.demandeur.prenom}"/></div></div>
            <div class="row"><div class="label">Nationalite</div><div><c:out value="${demande.demandeur.nationalite}"/></div></div>
            <div class="row"><div class="label">Telephone</div><div><c:out value="${demande.demandeur.telephone}"/></div></div>
            <div class="row"><div class="label">Adresse</div><div><c:out value="${demande.demandeur.adresseMadagascar}"/></div></div>
        </section>

        <section class="print-block">
            <h2>Visa transformable</h2>
            <div class="row"><div class="label">Reference</div><div><c:out value="${demande.visaTransformable.reference}"/></div></div>
            <div class="row"><div class="label">Date entree</div><div><c:out value="${demande.visaTransformable.dateEntree}"/></div></div>
            <div class="row"><div class="label">Lieu entree</div><div><c:out value="${demande.visaTransformable.lieuEntree}"/></div></div>
            <div class="row"><div class="label">Date expiration</div><div><c:out value="${demande.visaTransformable.dateExpiration}"/></div></div>
        </section>

        <section class="print-block">
            <h2>Pieces justificatives</h2>
            <div class="pieces-grid">
                <c:forEach var="t" items="${typesPieces}">
                    <c:set var="p" value="${piecesParType[t.name()]}"/>
                    <label style="display:flex; gap:10px; align-items:flex-start;">
                        <input type="checkbox" disabled <c:if test="${not empty p and p.sousmise}">checked</c:if> />
                        <span>
                            <c:out value="${t.label}"/>
                            <c:if test="${t.obligatoire}"> (Obligatoire)</c:if>
                            <c:if test="${not t.obligatoire}"> (Facultative)</c:if>
                        </span>
                    </label>
                </c:forEach>
            </div>
        </section>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
