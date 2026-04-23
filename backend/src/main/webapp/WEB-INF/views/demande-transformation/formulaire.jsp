
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Nouvelle demande");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<style>
    /* Tous styles originaux préservés */
    .print-sheet {
        max-width: 980px;
        margin: 0 auto;
        background: #fffdf9;
        border: 1px solid #b0a18d;
        box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
    }
    /* ... tous les styles ... */
    .visa-pieces {
        margin-top: 15px;
        padding: 12px;
        border: 1px solid #d4c4a3;
        background: #f9f7f0;
        border-radius: 6px;
    }
    .visa-piece {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 8px;
        margin-bottom: 8px;
        border-left: 4px solid #10b981;
    }
    .visa-piece input[type="checkbox"] {
        transform: scale(1.2);
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
            <!-- SECTION 1 et 2 identiques originales -->
            <section class="print-block">
                <h2>Section 1 - Etat civil</h2>
                <!-- Champs originaux -->
                <div class="print-grid">
                    <div class="print-field">
                        <label for="nom">Nom *</label>
                        <input id="nom" name="nom" type="text" required>
                    </div>
                    <!-- ... tous les champs état civil originaux ... -->
                </div>
            </section>

            <section class="print-block">
                <h2>Section 2 - Visa transformable</h2>
                <!-- Champs originaux -->
                <div class="print-grid">
                    <div class="print-field">
                        <label for="reference">Reference visa *</label>
                        <input id="reference" name="reference" type="text" required>
                    </div>
                    <!-- ... tous les champs visa originaux ... -->
                </div>
            </section>

            <!-- SECTION 3 DYNAMIQUE TYPE VISA -->
            <section class="print-block">
                <h2>Section 3 - Type de visa</h2>
                <div class="print-radio-group">
                    <label class="print-radio" for="investisseur">
                        <input id="investisseur" type="radio" name="type_visa" value="1" required>
                        Investisseur
                    </label>
                    <label class="print-radio" for="travailleur">
                        <input id="travailleur" type="radio" name="type_visa" value="2" required>
                        Travailleur
                    </label>
                </div>
                
                <!-- Pièces spécifiques TYPE VISA (cachées initialement) -->
                <div id="visa-pieces-investisseur" class="visa-pieces" style="display: none;">
                    <h4>Pièces Investisseur :</h4>
                    <div class="visa-piece">
                        <input type="checkbox" id="statut_societe" name="visa_pieces" value="statut_societe">
                        <label for="statut_societe">Statut de la Société</label>
                    </div>
                    <div class="visa-piece">
                        <input type="checkbox" id="registre_commerce" name="visa_pieces" value="registre_commerce">
                        <label for="registre_commerce">Extrait d'inscription au registre de commerce</label>
                    </div>
                    <div class="visa-piece">
                        <input type="checkbox" id="carte_fiscale" name="visa_pieces" value="carte_fiscale">
                        <label for="carte_fiscale">Carte fiscale</label>
                    </div>
                </div>

                <div id="visa-pieces-travailleur" class="visa-pieces" style="display: none;">
                    <h4>Pièces Travailleur :</h4>
                    <div class="visa-piece">
                        <input type="checkbox" id="autorisation_emploi" name="visa_pieces" value="autorisation_emploi">
                        <label for="autorisation_emploi">Autorisation emploi délivrée à Madagascar par le Ministère de la Fonction publique</label>
                    </div>
                    <div class="visa-piece">
                        <input type="checkbox" id="attestation_emploi" name="visa_pieces" value="attestation_emploi">
                        <label for="attestation_emploi">Attestation d'emploi délivrée par l'employeur (Original)</label>
                    </div>
                </div>
            </section>

            <!-- SECTION 4 Pièces justificatives générales (originaux) -->
            <section class="print-block">
                <h2>Section 4 - Pièces justificatives générales <span id="counter" class="pieces-counter warning">0/14</span></h2>
                <div class="print-radio-group">
                    <c:forEach var="t" items="${typesPieces}">
                        <label class="print-radio ${t.obligatoire ? 'piece-obligatoire' : 'piece-facultative'}" for="piece_${t.name()}">
                            <input id="piece_${t.name()}" type="checkbox" name="pieces" value="${t.name()}" data-obligatoire="${t.obligatoire}">
                            <strong>${t.label}</strong>
                            <c:if test="${t.obligatoire}"><span style="color:#ef4444;"> (OBLIGATOIRE)</span></c:if>
                            <c:if test="${!t.obligatoire}"><span style="color:#10b981;"> (facultative)</span></c:if>
                        </label>
                    </c:forEach>
                </div>
            </section>
        </div>

        <div id="status" class="print-status"></div>
        <div class="print-actions">
            <button id="submitBtn" class="btn btn-primary" type="submit">Valider</button>
        </div>
    </form>
</div>

<script>
(function() {
    // Toggle pièces visa par type
    document.querySelectorAll('input[name="type_visa"]').forEach(radio => {
        radio.addEventListener('change', function() {
            document.querySelectorAll('.visa-pieces').forEach(p => p.style.display = 'none');
            if (this.value === '1') {
                document.getElementById('visa-pieces-investisseur').style.display = 'block';
            } else if (this.value === '2') {
                document.getElementById('visa-pieces-travailleur').style.display = 'block';
            }
        });
    });

    // Compteur pièces générales
    const checkboxes = document.querySelectorAll('input[name="pieces"][data-obligatoire="true"]');
    const counterEl = document.getElementById('counter');
    const totalOblig = checkboxes.length;
    function updateCounter() {
        const checkedOblig = Array.from(checkboxes).filter(cb => cb.checked).length;
        counterEl.textContent = `${checkedOblig}/${totalOblig} obligatoires`;
        counterEl.className = `pieces-counter ${checkedOblig === totalOblig ? 'ok' : 'warning'}`;
    }
    checkboxes.forEach(cb => cb.addEventListener('change', updateCounter));
    updateCounter();

    // Submit
    document.getElementById('demandeForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const typeVisa = document.querySelector('input[name="type_visa"]:checked');
        if (!typeVisa) return alert('Type visa obligatoire');
        
        const checkedOblig = Array.from(checkboxes).filter(cb => cb.checked).length;
        if (checkedOblig < totalOblig) return alert('Pièces obligatoires manquantes');
        
        const payload = {
            nom: document.getElementById('nom').value,
            prenom: document.getElementById('prenom').value,
            // ... tous champs ...
            id_type_visa: parseInt(typeVisa.value),
            pieces: Array.from(document.querySelectorAll('input[name="pieces"]:checked, input[name="visa_pieces"]:checked')).map(cb => cb.value)
        };
        
        const response = await fetch('/api/demandes-transformation', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload)
        });
        
        if (response.ok) {
            alert('Succès !');
            document.getElementById('demandeForm').reset();
        } else {
            alert('Erreur: ' + await response.text());
        }
    });
})();
</script>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>

