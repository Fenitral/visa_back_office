
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    request.setAttribute("pageTitle", "Nouvelle demande");
%>
<%@ include file="/WEB-INF/views/layout/header.jspf" %>
<%@ include file="/WEB-INF/views/layout/sidebar.jspf" %>

<style>
    .paper-wrapper {
        max-width: 1100px;
        margin: 0 auto;
    }

    .paper-form {
        background: #fffef9;
        border: 2px solid #27272a;
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
        padding: 22px;
    }

    .paper-top {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
        margin-bottom: 14px;
    }

    .paper-top h1 {
        margin: 6px 0 0;
        font-size: 31px;
        line-height: 1.1;
        letter-spacing: 0.4px;
        text-transform: uppercase;
        color: #0f172a;
    }

    .paper-top p {
        margin: 0;
        font-size: 13px;
        color: #334155;
    }

    .avis-box {
        min-width: 270px;
        border: 3px solid #18181b;
        padding: 10px;
        font-weight: 700;
        background: #fff;
    }

    .avis-box h3 {
        margin: 0 0 8px;
        font-size: 18px;
        text-transform: uppercase;
        border-bottom: 2px solid #18181b;
        padding-bottom: 5px;
    }

    .avis-line {
        border-bottom: 1px dotted #18181b;
        min-height: 16px;
        margin-bottom: 8px;
        font-size: 13px;
    }

    .paper-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 12px 18px;
    }

    .paper-row {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .paper-row.full {
        grid-column: 1 / -1;
    }

    .paper-row label {
        min-width: 180px;
        font-weight: 700;
        color: #0f172a;
        font-size: 14px;
    }

    .paper-input,
    .paper-select {
        flex: 1;
        border: none;
        border-bottom: 2px dotted #18181b;
        background: transparent;
        min-height: 34px;
        padding: 4px 6px;
        font-size: 15px;
        color: #111827;
    }

    .paper-select {
        border: 1px solid #9ca3af;
        border-radius: 4px;
        background: #fff;
    }

    .paper-section {
        border: 2px solid #18181b;
        padding: 12px;
        margin-top: 14px;
        background: #fff;
    }

    .paper-section h2 {
        margin: 0 0 10px;
        font-size: 20px;
        color: #0f172a;
        text-transform: uppercase;
    }

    .inline-options {
        display: flex;
        gap: 18px;
        flex-wrap: wrap;
        align-items: center;
    }

    .inline-options label {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        font-weight: 700;
        color: #0f172a;
    }

    .pieces-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
    }

    .counter {
        padding: 6px 10px;
        border-radius: 999px;
        font-size: 13px;
        font-weight: 700;
        border: 1px solid #a16207;
        color: #92400e;
        background: #fffbeb;
    }

    .counter.ok {
        border-color: #166534;
        color: #166534;
        background: #ecfdf5;
    }

    .pieces-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 8px 18px;
    }

    .piece-item {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 4px 0;
        font-size: 14px;
    }

    .piece-item.hidden-by-type {
        display: none;
    }

    .piece-badge-obligatoire {
        color: #b91c1c;
        font-weight: 700;
        font-size: 12px;
    }

    .piece-badge-facultative {
        color: #047857;
        font-weight: 700;
        font-size: 12px;
    }

    .hint-box {
        margin-top: 10px;
        padding: 10px;
        border: 1px dashed #64748b;
        background: #f8fafc;
        font-size: 13px;
        color: #334155;
    }

    .status-box {
        margin-top: 12px;
        min-height: 24px;
        font-weight: 700;
    }

    .status-box.error {
        color: #b91c1c;
    }

    .status-box.success {
        color: #166534;
    }

    .paper-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 16px;
    }

    .paper-actions .btn {
        min-width: 180px;
        font-size: 16px;
    }

    @media (max-width: 900px) {
        .paper-top {
            flex-direction: column;
        }

        .avis-box {
            min-width: unset;
            width: 100%;
        }

        .paper-grid,
        .pieces-grid {
            grid-template-columns: 1fr;
        }

        .paper-row {
            flex-direction: column;
            align-items: flex-start;
        }

        .paper-row label {
            min-width: 0;
        }

        .paper-input,
        .paper-select {
            width: 100%;
        }
    }
</style>

<div class="page-header">
    <h1>Demande de visa transformable</h1>
    <p>Formulaire aligné aux champs de la table (demandeur, visa, type et pièces).</p>
</div>

<div class="paper-wrapper">
    <form id="demandeForm" class="paper-form" novalidate>
        <div class="paper-top">
            <div>
                <p>Ambassade de Madagascar en France</p>
                <p>04 Avenue Raphael 75016 - Paris</p>
                <h1>Demande de visa transformable en long sejour</h1>
            </div>
            <aside class="avis-box" aria-hidden="true">
                <h3>Avis du chef de poste</h3>
                <div class="avis-line">Visa n°</div>
                <div class="avis-line">Date</div>
                <div class="avis-line">Type</div>
                <div class="avis-line">Nombre de jours</div>
                <div class="avis-line">Nombre d'entrees</div>
                <div class="avis-line">Reference</div>
            </aside>
        </div>

        <section class="paper-section">
            <h2>Section 1 - Etat civil</h2>
            <div class="paper-grid">
                <div class="paper-row">
                    <label for="nom">Nom *</label>
                    <input id="nom" class="paper-input" type="text" required>
                </div>
                <div class="paper-row">
                    <label for="prenom">Prenom *</label>
                    <input id="prenom" class="paper-input" type="text" required>
                </div>
                <div class="paper-row">
                    <label for="nomDeJeuneFille">Nom de jeune fille</label>
                    <input id="nomDeJeuneFille" class="paper-input" type="text">
                </div>
                <div class="paper-row">
                    <label for="dateNaissance">Date de naissance *</label>
                    <input id="dateNaissance" class="paper-input" type="date" required>
                </div>
                <div class="paper-row">
                    <label for="lieuNaissance">Lieu de naissance</label>
                    <input id="lieuNaissance" class="paper-input" type="text" required>
                </div>
                <div class="paper-row">
                    <label for="situationFamille">Situation de famille *</label>
                    <select id="situationFamille" class="paper-select" required>
                        <option value="">Selectionner</option>
                        <option value="CELIBATAIRE">Celibataire</option>
                        <option value="MARIE">Marie(e)</option>
                        <option value="DIVORCE">Divorce(e)</option>
                        <option value="VEUF">Veuf/Veuve</option>
                        <option value="UNION_LIBRE">Union libre</option>
                        <option value="PACS">Pacs</option>
                    </select>
                </div>
                <div class="paper-row">
                    <label for="nationalite">Nationalite *</label>
                    <input id="nationalite" class="paper-input" type="text" required>
                </div>
                <div class="paper-row">
                    <label for="telephone">Telephone</label>
                    <input id="telephone" class="paper-input" type="text" required>
                </div>
                <div class="paper-row">
                    <label for="email">Email</label>
                    <input id="email" class="paper-input" type="email" required>
                </div>
                <div class="paper-row full">
                    <label for="adresse">Adresse a Madagascar *</label>
                    <input id="adresse" class="paper-input" type="text" required>
                </div>
            </div>
        </section>

        <section class="paper-section">
            <h2>Section 2 - Visa transformable</h2>
            <div class="paper-grid">
                <div class="paper-row">
                    <label for="reference">Reference visa *</label>
                    <input id="reference" class="paper-input" type="text" required>
                </div>
                <div class="paper-row">
                    <label for="dateExpiration">Date expiration *</label>
                    <input id="dateExpiration" class="paper-input" type="date" required>
                </div>
                <div class="paper-row">
                    <label for="dateArrivee">Date d'arrivee *</label>
                    <input id="dateArrivee" class="paper-input" type="date" required>
                </div>
                <div class="paper-row">
                    <label for="lieuArrivee">Lieu d'arrivee *</label>
                    <input id="lieuArrivee" class="paper-input" type="text" required>
                </div>
            </div>
        </section>

        <section class="paper-section">
            <h2>Section 3 - Type de visa</h2>
            <div class="inline-options" role="radiogroup" aria-label="Type de visa">
                <label for="typeInvestisseur">
                    <input id="typeInvestisseur" type="radio" name="typeVisa" value="1" required>
                    Investisseur
                </label>
                <label for="typeTravailleur">
                    <input id="typeTravailleur" type="radio" name="typeVisa" value="2" required>
                    Travailleur
                </label>
            </div>
            <div id="typeHint" class="hint-box">Selectionnez un type pour voir les pieces recommandees.</div>
        </section>

        <section class="paper-section">
            <div class="pieces-header">
                <h2>Section 4 - Pieces justificatives</h2>
                <span id="counter" class="counter">0/0 obligatoires</span>
            </div>

            <div class="pieces-grid">
                <c:forEach var="t" items="${typesPieces}">
                    <label class="piece-item" for="piece_${t.name()}">
                        <input id="piece_${t.name()}" type="checkbox" name="pieces" value="${t.name()}" data-obligatoire="${t.obligatoire}">
                        <span>${t.label}</span>
                        <c:if test="${t.obligatoire}"><span class="piece-badge-obligatoire">(OBLIGATOIRE)</span></c:if>
                        <c:if test="${!t.obligatoire}"><span class="piece-badge-facultative">(FACULTATIVE)</span></c:if>
                    </label>
                </c:forEach>
            </div>
        </section>

        <div id="status" class="status-box" role="alert"></div>

        <div class="paper-actions">
            <button id="submitBtn" class="btn btn-primary" type="submit">Enregistrer la demande</button>
        </div>
    </form>
</div>

<script>
(function() {
    const form = document.getElementById('demandeForm');
    const statusBox = document.getElementById('status');
    const counterEl = document.getElementById('counter');
    const typeHintEl = document.getElementById('typeHint');
    const allPieceCheckboxes = Array.from(document.querySelectorAll('input[name="pieces"]'));

    const commonPieceCodes = [
        'PASSEPORT',
        'CERTIFICAT_NAISSANCE',
        'JUSTIFICATIF_DOMICILE',
        'ACTE_MARIAGE',
        'ACTE_DIVORCE',
        'CERTIFICAT_SANTE',
        'CASIER_JUDICIAIRE',
        'ASSURANCE_MALADIE',
        'PHOTO_IDENTITE'
    ];

    const pieceCodesByVisaType = {
        '1': ['JUSTIFICATIF_MOYENS', 'RELEVE_COMPTE', 'PREUVE_INVESTISSEMENT'],
        '2': ['LETTRE_EMBAUCHE', 'CONTRAT_TRAVAIL']
    };

    function setStatus(message, type) {
        statusBox.textContent = message || '';
        statusBox.className = 'status-box' + (type ? ' ' + type : '');
    }

    function updateCounter() {
        const selectedType = document.querySelector('input[name="typeVisa"]:checked');
        const applicableCodes = getApplicablePieceCodes(selectedType ? selectedType.value : null);

        const applicableObligatoires = allPieceCheckboxes.filter(function(cb) {
            const isApplicable = applicableCodes.has(cb.value);
            const isObligatoire = cb.getAttribute('data-obligatoire') === 'true';
            return isApplicable && isObligatoire;
        });

        const checkedObligatoires = applicableObligatoires.filter(function(cb) { return cb.checked; }).length;
        const totalObligatoires = applicableObligatoires.length;

        counterEl.textContent = checkedObligatoires + '/' + totalObligatoires + ' obligatoires';
        if (checkedObligatoires === totalObligatoires) {
            counterEl.classList.add('ok');
        } else {
            counterEl.classList.remove('ok');
        }
    }

    function getApplicablePieceCodes(typeVisa) {
        const codes = new Set(commonPieceCodes);
        if (typeVisa && pieceCodesByVisaType[typeVisa]) {
            pieceCodesByVisaType[typeVisa].forEach(function(code) {
                codes.add(code);
            });
        }
        return codes;
    }

    function updatePiecesByType() {
        const selected = document.querySelector('input[name="typeVisa"]:checked');
        const applicableCodes = getApplicablePieceCodes(selected ? selected.value : null);

        allPieceCheckboxes.forEach(function(cb) {
            const label = cb.closest('.piece-item');
            if (!label) {
                return;
            }

            if (applicableCodes.has(cb.value)) {
                label.classList.remove('hidden-by-type');
            } else {
                label.classList.add('hidden-by-type');
                cb.checked = false;
            }
        });
    }

    function updateTypeHint() {
        const selected = document.querySelector('input[name="typeVisa"]:checked');
        if (!selected) {
            typeHintEl.textContent = 'Selectionnez un type pour afficher les pieces specifiees pour ce visa.';
            return;
        }

        if (selected.value === '1') {
            typeHintEl.textContent = 'Investisseur: les pieces PREUVE INVESTISSEMENT, JUSTIFICATIF MOYENS et RELEVE COMPTE sont affichees et cochables.';
        } else {
            typeHintEl.textContent = 'Travailleur: les pieces LETTRE D\'EMBAUCHE et CONTRAT DE TRAVAIL sont affichees et cochables.';
        }
    }

    allPieceCheckboxes.forEach(function(cb) {
        cb.addEventListener('change', updateCounter);
    });

    document.querySelectorAll('input[name="typeVisa"]').forEach(function(radio) {
        radio.addEventListener('change', function() {
            updateTypeHint();
            updatePiecesByType();
            updateCounter();
        });
    });

    function valueOf(id) {
        const el = document.getElementById(id);
        return el ? el.value.trim() : '';
    }

    function validateBeforeSubmit() {
        const requiredIds = ['nom', 'prenom', 'dateNaissance', 'lieuNaissance', 'situationFamille', 'nationalite', 'email', 'telephone', 'adresse', 'reference', 'dateExpiration', 'dateArrivee', 'lieuArrivee'];
        const missing = requiredIds.some(function(id) {
            return !valueOf(id);
        });

        if (missing) {
            setStatus('Veuillez remplir tous les champs obligatoires.', 'error');
            return false;
        }

        const selectedType = document.querySelector('input[name="typeVisa"]:checked');
        if (!selectedType) {
            setStatus('Le type de visa est obligatoire.', 'error');
            return false;
        }

        const applicableCodes = getApplicablePieceCodes(selectedType.value);
        const applicableObligatoires = allPieceCheckboxes.filter(function(cb) {
            const isApplicable = applicableCodes.has(cb.value);
            const isObligatoire = cb.getAttribute('data-obligatoire') === 'true';
            return isApplicable && isObligatoire;
        });

        const checkedObligatoires = applicableObligatoires.filter(function(cb) { return cb.checked; }).length;
        if (checkedObligatoires < applicableObligatoires.length) {
            setStatus('Toutes les pieces obligatoires doivent etre cochees.', 'error');
            return false;
        }

        return true;
    }

    form.addEventListener('submit', async function(event) {
        event.preventDefault();
        setStatus('', '');

        if (!validateBeforeSubmit()) {
            return;
        }

        const selectedType = document.querySelector('input[name="typeVisa"]:checked');
        const payload = {
            nom: valueOf('nom'),
            prenom: valueOf('prenom'),
            nom_de_jeune_fille: valueOf('nomDeJeuneFille') || null,
            date_de_naissance: valueOf('dateNaissance'),
            lieu_de_naissance: valueOf('lieuNaissance') || null,
            situation_de_famille: valueOf('situationFamille'),
            nationalite: valueOf('nationalite'),
            adresse: valueOf('adresse'),
            email: valueOf('email') || null,
            telephone: valueOf('telephone') || null,
            reference: valueOf('reference'),
            date_expiration: valueOf('dateExpiration'),
            date_arrivee: valueOf('dateArrivee'),
            lieu_d_arrivee: valueOf('lieuArrivee'),
            id_type_visa: parseInt(selectedType.value, 10),
            pieces: Array.from(document.querySelectorAll('input[name="pieces"]:checked')).map(function(cb) {
                return cb.value;
            })
        };

        try {
            const response = await fetch('/api/demandes-transformation', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Erreur lors de la creation de la demande.');
            }

            const data = await response.json();
            setStatus('Demande enregistree avec succes. Numero: ' + data.numeroDemande, 'success');
            form.reset();
            updateCounter();
            updateTypeHint();
        } catch (error) {
            setStatus(error.message || 'Erreur serveur.', 'error');
        }
    });

    updateCounter();
    updateTypeHint();
    updatePiecesByType();
})();
</script>

<%@ include file="/WEB-INF/views/layout/footer.jspf" %>

