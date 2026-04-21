<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Modifier pieces");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<div class="page-header">
    <h1>Modifier la demande</h1>
    <p>Tu peux modifier toutes les informations + les pieces</p>
</div>

<div class="details-container">
    <div class="card">
        <div class="card-header">
            <div class="card-title">Demande</div>
        </div>
        <div class="info-row">
            <div class="info-label">ID</div>
            <div class="info-value"><c:out value="${demande.id}"/></div>
        </div>
        <div class="info-row">
            <div class="info-label">Demandeur</div>
            <div class="info-value"><c:out value="${demande.demandeur.nom}"/> <c:out value="${demande.demandeur.prenom}"/></div>
        </div>
        <div class="info-row">
            <div class="info-label">Statut</div>
            <div class="info-value"><c:out value="${demande.statut}"/></div>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <div class="card-title">Modification</div>
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/demandes/${demande.id}/modifier">
                <div class="info-row">
                    <div class="info-label">Type demande</div>
                    <div class="info-value">
                        <select name="typeDemande" required>
                            <c:forEach var="t" items="${typesDemande}">
                                <option value="${t}" <c:if test="${t == demande.typeDemande}">selected</c:if>><c:out value="${t}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="info-row">
                    <div class="info-label">Nom</div>
                    <div class="info-value"><input type="text" name="nom" value="${demande.demandeur.nom}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Prenom</div>
                    <div class="info-value"><input type="text" name="prenom" value="${demande.demandeur.prenom}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Nom jeune fille</div>
                    <div class="info-value"><input type="text" name="nomJeuneFille" value="${demande.demandeur.nomJeuneFille}"></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Date naissance</div>
                    <div class="info-value"><input type="date" name="dateNaissance" value="${demande.demandeur.dateNaissance}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Situation familiale</div>
                    <div class="info-value">
                        <select name="situationFamiliale" required>
                            <c:forEach var="s" items="${situations}">
                                <option value="${s}" <c:if test="${s == demande.demandeur.situationFamiliale}">selected</c:if>><c:out value="${s}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="info-row">
                    <div class="info-label">Nationalite</div>
                    <div class="info-value"><input type="text" name="nationalite" value="${demande.demandeur.nationalite}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Profession</div>
                    <div class="info-value"><input type="text" name="profession" value="${demande.demandeur.profession}"></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Adresse</div>
                    <div class="info-value"><input type="text" name="adresseMadagascar" value="${demande.demandeur.adresseMadagascar}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Telephone</div>
                    <div class="info-value"><input type="text" name="telephone" value="${demande.demandeur.telephone}"></div>
                </div>

                <div class="info-row">
                    <div class="info-label">Reference visa</div>
                    <div class="info-value"><input type="text" name="referenceVisa" value="${demande.visaTransformable.reference}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Date entree</div>
                    <div class="info-value"><input type="date" name="dateEntree" value="${demande.visaTransformable.dateEntree}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Lieu entree</div>
                    <div class="info-value"><input type="text" name="lieuEntree" value="${demande.visaTransformable.lieuEntree}" required></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Date expiration</div>
                    <div class="info-value"><input type="date" name="dateExpiration" value="${demande.visaTransformable.dateExpiration}" required></div>
                </div>

                <div style="margin-top:14px; font-weight:700;">Pieces justificatives</div>
                <c:forEach var="t" items="${typesPieces}">
                    <c:set var="p" value="${piecesParType[t.name()]}"/>
                    <div class="info-row">
                        <div class="info-label">
                            <label style="display:flex; align-items:center; gap:10px; font-weight:600;">
                                <input type="checkbox" name="pieces" value="${t.name()}"
                                       <c:if test="${not empty p and p.sousmise}">checked</c:if> />
                                <span>
                                    <c:out value="${t.label}"/>
                                    <c:if test="${t.obligatoire}"> (Obligatoire)</c:if>
                                    <c:if test="${not t.obligatoire}"> (Facultative)</c:if>
                                </span>
                            </label>
                        </div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${empty p}">Non fournie</c:when>
                                <c:when test="${p.sousmise}">Soumise</c:when>
                                <c:otherwise>Ajoutee</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>

                <div style="display:flex; gap:10px; justify-content:flex-end; margin-top:14px; flex-wrap:wrap;">
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/demandes">Retour</a>
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/demandes/${demande.id}/imprimer" target="_blank">Voir PDF / Imprimer</a>
                    <button class="btn btn-primary" type="submit">Enregistrer</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
