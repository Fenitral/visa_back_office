<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Nouvelle demande");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<style>
    .print-sheet {
        max-width: 980px;
        margin: 0 auto;
        background: #fffdf9;
        border: 1px solid #b0a18d;
        box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
    }

    .print-header {
        text-align: center;
        padding: 20px 24px 8px;
        border-bottom: 2px solid #b8a98e;
        font-family: "Georgia", "Times New Roman", serif;
    }

    .print-header h1 {
        font-size: 1.35rem;
        margin: 10px 0 6px;
        text-transform: uppercase;
        letter-spacing: 0.03em;
    }

    .print-header p {
        margin: 3px 0;
        font-size: 0.98rem;
    }

    .print-content {
        padding: 20px;
        display: grid;
        gap: 18px;
        font-family: "Georgia", "Times New Roman", serif;
    }

    .print-block {
        border: 1px solid #c7baa1;
        padding: 14px;
        background: linear-gradient(180deg, #fffcf7 0%, #fff 100%);
    }

    .print-block h2 {
        margin: 0 0 12px;
        font-size: 1.05rem;
        padding-bottom: 8px;
        border-bottom: 1px dashed #bda783;
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }

    .print-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 12px;
    }

    .print-field {
        display: grid;
        gap: 5px;
    }

    .print-field.full {
        grid-column: 1 / -1;
    }

    .print-sheet label {
        font-size: 0.92rem;
        font-weight: 700;
    }

    .print-sheet input,
    .print-sheet select {
        width: 100%;
        border: 1px solid #ad9f88;
        background: #fff;
        color: #1d1a17;
        padding: 9px 10px;
        font-size: 0.95rem;
        font-family: inherit;
    }

    .print-radio-group {
        display: flex;
        flex-wrap: wrap;
        gap: 18px;
        margin-top: 4px;
    }

    .print-radio {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        border: 1px solid #baa98c;
        padding: 8px 11px;
        background: #fff;
    }

    .print-radio input {
        width: auto;
        margin: 0;
    }

    .print-actions {
        display: flex;
        justify-content: flex-end;
        padding: 4px 20px 22px;
    }

    .print-status {
        margin: 0 20px 18px;
        padding: 10px 12px;
        font-size: 0.94rem;
        border: 1px solid transparent;
        display: none;
    }

    .print-status.show {
        display: block;
    }

    .print-status.ok {
        background: #e8f7ee;
        border-color: #9ad3b0;
        color: #1a6a36;
    }

    .print-status.error {
        background: #fdeaea;
        border-color: #e7a8a8;
        color: #9f1c1c;
    }

    @media (max-width: 760px) {
        .print-grid {
            grid-template-columns: 1fr;
        }

        .print-actions {
            justify-content: stretch;
        }

        .print-actions button {
            width: 100%;
        }
    }

    @media print {
        .sidebar,
        .page-header {
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
        .print-actions,
        .print-status {
            display: none !important;
        }
    }
</style>

<div class="page-header">
    <h1>Nouvelle demande</h1>
    <p>Formulaire (mode impression)</p>
</div>

<div class="print-sheet">
    <header class="print-header">
        <p>Ambassade de Madagascar en France</p>
        <p>04 Avenue Raphael 75016 - Paris</p>
        <h1>Demande de visa transformable en long sejour</h1>
    </header>

    <form id="demandeForm" novalidate>
        <div class="print-content">
            <section class="print-block">
                <h2>Section 1 - Etat civil</h2>
                <div class="print-grid">
                    <div class="print-field">
                        <label for="nom">Nom *</label>
                        <input id="nom" name="nom" type="text" required>
                    </div>
                    <div class="print-field">
                        <label for="prenom">Prenom *</label>
                        <input id="prenom" name="prenom" type="text" required>
                    </div>
                    <div class="print-field">
                        <label for="nom_de_jeune_fille">Nom de jeune fille</label>
                        <input id="nom_de_jeune_fille" name="nom_de_jeune_fille" type="text">
                    </div>
                    <div class="print-field">
                        <label for="date_de_naissance">Date de naissance *</label>
                        <input id="date_de_naissance" name="date_de_naissance" type="date" required>
                    </div>
                    <div class="print-field">
                        <label for="lieu_de_naissance">Lieu de naissance *</label>
                        <input id="lieu_de_naissance" name="lieu_de_naissance" type="text" required>
                    </div>
                    <div class="print-field">
                        <label for="situation_de_famille">Situation de famille</label>
                        <select id="situation_de_famille" name="situation_de_famille">
                            <option value="">Selectionner</option>
                            <option value="CELIBATAIRE">Celibataire</option>
                            <option value="MARIE">Marie(e)</option>
                            <option value="DIVORCE">Divorce(e)</option>
                            <option value="VEUF">Veuf(ve)</option>
                            <option value="UNION_LIBRE">Union libre</option>
                            <option value="PACS">Pacs</option>
                        </select>
                    </div>
                    <div class="print-field">
                        <label for="nationalite">Nationalite *</label>
                        <input id="nationalite" name="nationalite" type="text" required>
                    </div>
                    <div class="print-field full">
                        <label for="adresse">Adresse *</label>
                        <input id="adresse" name="adresse" type="text" required>
                    </div>
                    <div class="print-field">
                        <label for="email">Email *</label>
                        <input id="email" name="email" type="email" required>
                    </div>
                    <div class="print-field">
                        <label for="telephone">Telephone</label>
                        <input id="telephone" name="telephone" type="text">
                    </div>
                </div>
            </section>

            <section class="print-block">
                <h2>Section 2 - Visa transformable</h2>
                <div class="print-grid">
                    <div class="print-field">
                        <label for="reference">Reference visa *</label>
                        <input id="reference" name="reference" type="text" required>
                    </div>
                    <div class="print-field">
                        <label for="date_expiration">Date d'expiration *</label>
                        <input id="date_expiration" name="date_expiration" type="date" required>
                    </div>
                    <div class="print-field">
                        <label for="date_arrivee">Date d'arrivee *</label>
                        <input id="date_arrivee" name="date_arrivee" type="date" required>
                    </div>
                    <div class="print-field">
                        <label for="lieu_d_arrivee">Lieu d'arrivee *</label>
                        <input id="lieu_d_arrivee" name="lieu_d_arrivee" type="text" required>
                    </div>
                </div>
            </section>

            <section class="print-block">
                <h2>Section 3 - Type de visa</h2>
                <div class="print-radio-group">
                    <label class="print-radio" for="type_1">
                        <input id="type_1" type="radio" name="id_type_visa" value="1" required>
                        Investisseur
                    </label>
                    <label class="print-radio" for="type_2">
                        <input id="type_2" type="radio" name="id_type_visa" value="2" required>
                        Travailleur
                    </label>
                </div>
            </section>

            <section class="print-block">
                <h2>Section 4 - Pieces justificatives</h2>
                <div class="print-radio-group">
                    <c:forEach var="t" items="${typesPieces}">
                        <label class="print-radio" for="piece_${t.name()}">
                            <input id="piece_${t.name()}" type="checkbox" name="pieces" value="${t.name()}">
                            <c:out value="${t.label}"/>
                            <c:if test="${t.obligatoire}"> (Obligatoire)</c:if>
                            <c:if test="${not t.obligatoire}"> (Facultative)</c:if>
                        </label>
                    </c:forEach>
                </div>
            </section>
        </div>

        <div id="status" class="print-status" role="alert"></div>

        <div class="print-actions">
            <button id="submitBtn" class="btn btn-primary" type="submit">Valider</button>
        </div>
    </form>
</div>

<script>
    (function () {
        const form = document.getElementById("demandeForm");
        const submitBtn = document.getElementById("submitBtn");
        const statusBox = document.getElementById("status");
        const base = "${pageContext.request.contextPath}";

        function showStatus(message, type) {
            statusBox.textContent = message;
            statusBox.className = "print-status show " + type;
        }

        function clearStatus() {
            statusBox.className = "print-status";
            statusBox.textContent = "";
        }

        form.addEventListener("submit", async function (event) {
            event.preventDefault();
            clearStatus();

            if (!form.reportValidity()) {
                return;
            }

            submitBtn.disabled = true;

            const payload = {
                nom: form.nom.value,
                prenom: form.prenom.value,
                nom_de_jeune_fille: form.nom_de_jeune_fille.value,
                date_de_naissance: form.date_de_naissance.value,
                lieu_de_naissance: form.lieu_de_naissance.value,
                situation_de_famille: form.situation_de_famille.value,
                nationalite: form.nationalite.value,
                adresse: form.adresse.value,
                email: form.email.value,
                telephone: form.telephone.value,
                reference: form.reference.value,
                date_expiration: form.date_expiration.value,
                date_arrivee: form.date_arrivee.value,
                lieu_d_arrivee: form.lieu_d_arrivee.value,
                id_type_visa: Number(form.id_type_visa.value),
                pieces: Array.from(form.querySelectorAll('input[name="pieces"]:checked')).map(cb => cb.value)
            };

            try {
                const response = await fetch(base + "/api/demandes-transformation", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    const text = await response.text();
                    throw new Error(text || "Erreur inconnue");
                }

                const data = await response.json();
                showStatus("Demande " + data.numero_demande + " creee", "ok");
                form.reset();
            } catch (error) {
                showStatus("Erreur: " + error.message, "error");
            } finally {
                submitBtn.disabled = false;
            }
        });
    })();
</script>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>
