<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
    request.setAttribute("pageTitle", "Nouvelle demande de visa");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<div class="page-header">
    <h1>Nouvelle demande de visa</h1>
</div>

<c:if test="${not empty success}">
    <div class="alert alert-success"><c:out value="${success}"/></div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<form:form method="post" modelAttribute="demandeRequest" cssClass="form-container">
    <h2>Informations demandeur</h2>
    <div class="form-row">
        <div class="form-group">
            <label>Nom *</label>
            <form:input path="demandeur.nom" cssClass="form-input" required="true"/>
        </div>
        <div class="form-group">
            <label>Prénom *</label>
            <form:input path="demandeur.prenom" cssClass="form-input" required="true"/>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group">
            <label>Nom jeune fille</label>
            <form:input path="demandeur.nomJeuneFille" cssClass="form-input"/>
        </div>
        <div class="form-group">
            <label>Date de naissance *</label>
            <form:input path="demandeur.dateNaissance" type="date" cssClass="form-input" required="true"/>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group">
            <label>Situation familiale *</label>
            <form:select path="demandeur.situationFamiliale" cssClass="form-select" required="true">
                <form:options items="${situations}" itemLabel="label" itemValue="name"/>
            </form:select>
        </div>
        <div class="form-group">
            <label>Nationalité *</label>
            <form:input path="demandeur.nationalite" cssClass="form-input" required="true"/>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group">
            <label>Profession</label>
            <form:input path="demandeur.profession" cssClass="form-input"/>
        </div>
        <div class="form-group">
            <label>Téléphone</label>
            <form:input path="demandeur.telephone" cssClass="form-input"/>
        </div>
    </div>
    <div class="form-group full">
        <label>Adresse à Madagascar *</label>
        <form:input path="demandeur.adresseMadagascar" cssClass="form-input" required="true"/>
    </div>

    <h2>Visa transformable</h2>
    <div class="form-row">
        <div class="form-group">
            <label>Référence visa *</label>
            <form:input path="visaTransformable.reference" cssClass="form-input" required="true"/>
        </div>
        <div class="form-group">
            <label>Date entrée *</label>
            <form:input path="visaTransformable.dateEntree" type="date" cssClass="form-input" required="true"/>
        </div>
    </div>
    <div class="form-row">
        <div class="form-group">
            <label>Lieu entrée *</label>
            <form:input path="visaTransformable.lieuEntree" cssClass="form-input" required="true"/>
        </div>
        <div class="form-group">
            <label>Date expiration *</label>
            <form:input path="visaTransformable.dateExpiration" type="date" cssClass="form-input" required="true"/>
        </div>
    </div>

    <h2>Type de demande</h2>
    <div class="form-group">
        <label>Type demande *</label>
        <form:radiobuttons path="typeDemande" items="${types}" itemValue="name" itemLabel="label" cssClass="radio-group" onclick="togglePieces(this.value)"/>
    </div>

    <div id="pieces-section">
        <h3>Pièces justificatives selon type de visa</h3>
        <div id="investisseur-pieces" class="visa-pieces" style="display:none;">
            <h4>Investisseur :</h4>
            <div class="piece-item piece-obligatoire">✓ Statut de la Société</div>
            <div class="piece-item piece-obligatoire">✓ Extrait d'inscription au registre de commerce</div>
            <div class="piece-item piece-obligatoire">✓ Carte fiscale</div>
        </div>
        <div id="travailleur-pieces" class="visa-pieces" style="display:none;">
            <h4>Travailleur :</h4>
            <div class="piece-item piece-obligatoire">✓ Autorisation emploi délivrée à Madagascar par le Ministère de la Fonction publique</div>
            <div class="piece-item piece-obligatoire">✓ Attestation d'emploi délivré par l'employeur (Original)</div>
        </div>
    </div>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Enregistrer demande</button>
        <a href="${pageContext.request.contextPath}/demandes" class="btn btn-secondary">Annuler</a>
    </div>
</form:form>

<script>
function togglePieces(type) {
    const investor = document.getElementById('investisseur-pieces');
    const travailleur = document.getElementById('travailleur-pieces');
    
    investor.style.display = 'none';
    travailleur.style.display = 'none';
    
    if (type === 'INVESTISSEUR') {
        investor.style.display = 'block';
    } else if (type === 'TRAVAILLEUR') {
        travailleur.style.display = 'block';
    }
}
</script>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>

