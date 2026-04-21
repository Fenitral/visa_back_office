<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouvelle Demande de Visa Transformable</title>
    <style>
        :root {
            --ink: #1d1a17;
            --paper: #f8f3e8;
            --accent: #113a66;
            --accent-soft: #dce9f8;
            --danger: #9f1c1c;
            --ok: #1a6a36;
            --line: #c7baa1;
        }

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            color: var(--ink);
            font-family: "Georgia", "Times New Roman", serif;
            background:
                radial-gradient(circle at 15% 25%, #e7dcc7 0, transparent 42%),
                radial-gradient(circle at 85% 75%, #f0e6d3 0, transparent 35%),
                var(--paper);
            min-height: 100vh;
            padding: 20px 14px;
        }

        .sheet {
            max-width: 960px;
            margin: 0 auto;
            background: #fffdf9;
            border: 1px solid #b0a18d;
            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
            animation: enter 500ms ease-out;
        }

        @keyframes enter {
            from {
                opacity: 0;
                transform: translateY(14px) scale(0.99);
            }
            to {
                opacity: 1;
                transform: translateY(0) scale(1);
            }
        }

        .header {
            text-align: center;
            padding: 20px 24px 8px;
            border-bottom: 2px solid #b8a98e;
        }

        .header h1 {
            font-size: clamp(1.1rem, 2.1vw, 1.8rem);
            margin: 10px 0 6px;
            text-transform: uppercase;
            letter-spacing: 0.03em;
        }

        .header p {
            margin: 3px 0;
            font-size: 0.98rem;
        }

        .content {
            padding: 20px;
            display: grid;
            gap: 18px;
        }

        .block {
            border: 1px solid var(--line);
            padding: 14px;
            background: linear-gradient(180deg, #fffcf7 0%, #fff 100%);
        }

        .block h2 {
            margin: 0 0 12px;
            font-size: 1.05rem;
            padding-bottom: 8px;
            border-bottom: 1px dashed #bda783;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 12px;
        }

        .field {
            display: grid;
            gap: 5px;
        }

        .field.full {
            grid-column: 1 / -1;
        }

        label {
            font-size: 0.92rem;
            font-weight: 700;
        }

        input,
        select {
            width: 100%;
            border: 1px solid #ad9f88;
            background: #fff;
            color: var(--ink);
            padding: 9px 10px;
            font-size: 0.95rem;
            font-family: inherit;
            transition: border-color 150ms ease, box-shadow 150ms ease;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(17, 58, 102, 0.15);
        }

        .radio-group {
            display: flex;
            flex-wrap: wrap;
            gap: 18px;
            margin-top: 4px;
        }

        .radio {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            border: 1px solid #baa98c;
            padding: 8px 11px;
            background: #fff;
        }

        .radio input {
            width: auto;
            margin: 0;
        }

        .actions {
            display: flex;
            justify-content: flex-end;
            padding: 4px 20px 22px;
        }

        button {
            border: none;
            background: linear-gradient(180deg, #164a7f 0%, #0f355b 100%);
            color: #fff;
            padding: 12px 20px;
            font-size: 0.97rem;
            font-weight: 700;
            letter-spacing: 0.03em;
            text-transform: uppercase;
            cursor: pointer;
        }

        button:disabled {
            opacity: 0.65;
            cursor: not-allowed;
        }

        .status {
            margin: 0 20px 18px;
            padding: 10px 12px;
            font-size: 0.94rem;
            border: 1px solid transparent;
            display: none;
        }

        .status.show {
            display: block;
        }

        .status.ok {
            background: #e8f7ee;
            border-color: #9ad3b0;
            color: var(--ok);
        }

        .status.error {
            background: #fdeaea;
            border-color: #e7a8a8;
            color: var(--danger);
        }

        @media (max-width: 760px) {
            .grid {
                grid-template-columns: 1fr;
            }

            .actions {
                justify-content: stretch;
            }

            button {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="sheet">
    <header class="header">
        <p>Ambassade de Madagascar en France</p>
        <p>04 Avenue Raphael 75016 - Paris</p>
        <h1>Demande de visa transformable en long sejour</h1>
    </header>

    <form id="demandeForm" novalidate>
        <div class="content">
            <section class="block">
                <h2>Section 1 - Etat civil</h2>
                <div class="grid">
                    <div class="field">
                        <label for="nom">Nom *</label>
                        <input id="nom" name="nom" type="text" required>
                    </div>
                    <div class="field">
                        <label for="prenom">Prenom *</label>
                        <input id="prenom" name="prenom" type="text" required>
                    </div>
                    <div class="field">
                        <label for="nom_de_jeune_fille">Nom de jeune fille</label>
                        <input id="nom_de_jeune_fille" name="nom_de_jeune_fille" type="text">
                    </div>
                    <div class="field">
                        <label for="date_de_naissance">Date de naissance *</label>
                        <input id="date_de_naissance" name="date_de_naissance" type="date" required>
                    </div>
                    <div class="field">
                        <label for="lieu_de_naissance">Lieu de naissance *</label>
                        <input id="lieu_de_naissance" name="lieu_de_naissance" type="text" required>
                    </div>
                    <div class="field">
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
                    <div class="field">
                        <label for="nationalite">Nationalite *</label>
                        <input id="nationalite" name="nationalite" type="text" required>
                    </div>
                    <div class="field full">
                        <label for="adresse">Adresse *</label>
                        <input id="adresse" name="adresse" type="text" required>
                    </div>
                    <div class="field">
                        <label for="email">Email *</label>
                        <input id="email" name="email" type="email" required>
                    </div>
                    <div class="field">
                        <label for="telephone">Telephone</label>
                        <input id="telephone" name="telephone" type="text">
                    </div>
                </div>
            </section>

            <section class="block">
                <h2>Section 2 - Visa transformable</h2>
                <div class="grid">
                    <div class="field">
                        <label for="reference">Reference visa *</label>
                        <input id="reference" name="reference" type="text" required>
                    </div>
                    <div class="field">
                        <label for="date_expiration">Date d'expiration *</label>
                        <input id="date_expiration" name="date_expiration" type="date" required>
                    </div>
                    <div class="field">
                        <label for="date_arrivee">Date d'arrivee *</label>
                        <input id="date_arrivee" name="date_arrivee" type="date" required>
                    </div>
                    <div class="field">
                        <label for="lieu_d_arrivee">Lieu d'arrivee *</label>
                        <input id="lieu_d_arrivee" name="lieu_d_arrivee" type="text" required>
                    </div>
                </div>
            </section>

            <section class="block">
                <h2>Section 3 - Type de visa</h2>
                <div class="radio-group">
                    <label class="radio" for="type_1">
                        <input id="type_1" type="radio" name="id_type_visa" value="1" required>
                        Investisseur
                    </label>
                    <label class="radio" for="type_2">
                        <input id="type_2" type="radio" name="id_type_visa" value="2" required>
                        Travailleur
                    </label>
                </div>
            </section>
        </div>

        <div id="status" class="status" role="alert"></div>

        <div class="actions">
            <button id="submitBtn" type="submit">Valider</button>
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
            statusBox.className = "status show " + type;
        }

        function clearStatus() {
            statusBox.className = "status";
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
                id_type_visa: Number(form.id_type_visa.value)
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
</body>
</html>
