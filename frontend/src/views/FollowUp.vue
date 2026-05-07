<template>
  <div class="followup-page">
    <!-- Loading state -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Chargement de votre demande...</p>
    </div>

    <!-- Error state -->
    <div v-if="error" class="error">
      <h2>❌ Demande Non Trouvée</h2>
      <p>{{ error }}</p>
      <button @click="chargerDemande" class="btn-retry">Réessayer</button>
    </div>

    <!-- Success state -->
    <div v-if="!loading && demande" class="followup-container">
      <!-- Header -->
      <div class="followup-header">
        <div class="header-content">
          <h1>📋 Suivi de Votre Demande</h1>
          <p class="ref-number">#{{ demande.id }}</p>
        </div>
        <div class="status-big" :class="getStatusClass(demande.statutDemande)">
          {{ demande.statutDemande }}
        </div>
      </div>

      <!-- Demandeur Info -->
      <div class="info-section">
        <h2>📝 Informations du Demandeur</h2>
        <div class="info-box">
          <div class="info-row">
            <span class="label">Nom et Prénom:</span>
            <span class="value">{{ demande.prenomDemandeur }} {{ demande.nomDemandeur }}</span>
          </div>
          <div class="info-row">
            <span class="label">Passeport:</span>
            <span class="value">{{ demande.numeroPasSeport }}</span>
          </div>
          <div class="info-row">
            <span class="label">Référence Visa:</span>
            <span class="value">{{ demande.referenceVisa }}</span>
          </div>
          <div class="info-row">
            <span class="label">Type de Visa:</span>
            <span class="value">{{ demande.typeVisa }}</span>
          </div>
          <div class="info-row">
            <span class="label">Type de Demande:</span>
            <span class="value">{{ demande.typeDemande }}</span>
          </div>
          <div class="info-row">
            <span class="label">Date de Création:</span>
            <span class="value">{{ formatDate(demande.dateDemande) }}</span>
          </div>
        </div>
      </div>

      <!-- Timeline de Suivi -->
      <div class="status-section">
        <h2>📊 Historique de Suivi</h2>
        
        <div v-if="demande.historiques && demande.historiques.length > 0" class="status-timeline">
          <div 
            v-for="(hist, index) in sortedHistoriques" 
            :key="hist.id"
            class="status-point"
          >
            <div class="status-circle" :class="getStatusClass(hist.statut)">
              <span class="status-number">{{ index + 1 }}</span>
            </div>
            <div class="status-details">
              <h3>{{ hist.statut }}</h3>
              <p class="status-time">{{ formatDate(hist.dateChangement) }}</p>
              <p v-if="hist.commentaire" class="status-comment">{{ hist.commentaire }}</p>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          Aucun historique disponible
        </div>
      </div>

      <!-- Pièces Justificatives -->
      <div v-if="demande.pieces && demande.pieces.length > 0" class="documents-section">
        <h2>📄 Documents Fournis</h2>
        <div class="documents-list">
          <div 
            v-for="piece in demande.pieces" 
            :key="piece.idPiece"
            class="document-item"
            :class="{ provided: piece.fourni, missing: !piece.fourni }"
          >
            <div class="doc-status">
              <span v-if="piece.fourni" class="doc-icon">✅</span>
              <span v-else class="doc-icon">⚠️</span>
            </div>
            <div class="doc-info">
              <p class="doc-name">{{ piece.nomPiece }}</p>
              <p v-if="piece.obligatoire" class="doc-required">Obligatoire</p>
            </div>
            <div class="doc-status-text">
              <span v-if="piece.fourni" class="badge provided">Reçu</span>
              <span v-else class="badge missing">En attente</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Message de statut -->
      <div class="status-message" :class="getStatusClass(demande.statutDemande)">
        <h3>{{ getStatusMessage(demande.statutDemande).title }}</h3>
        <p>{{ getStatusMessage(demande.statutDemande).message }}</p>
      </div>

      <!-- Footer -->
      <div class="followup-footer">
        <p>Pour toute question, veuillez contacter notre centre d'assistance.</p>
        <p class="footer-date">Mise à jour: {{ new Date().toLocaleDateString('fr-FR') }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'FollowUp',
  props: {
    demandeId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      demande: null,
      loading: true,
      error: null
    }
  },
  computed: {
    sortedHistoriques() {
      if (!this.demande || !this.demande.historiques) return []
      return [...this.demande.historiques].sort((a, b) => {
        return new Date(a.dateChangement) - new Date(b.dateChangement)
      })
    }
  },
  mounted() {
    this.chargerDemande()
  },
  methods: {
    async chargerDemande() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/demandes/${this.demandeId}`)
        this.demande = response.data
      } catch (err) {
        this.error = err.message || 'Demande non trouvée'
        console.error(err)
      } finally {
        this.loading = false
      }
    },
    formatDate(date) {
      if (!date) return 'N/A'
      return new Date(date).toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    getStatusClass(status) {
      const statusMap = {
        'CREE': 'status-new',
        'SCANNEE': 'status-scanned',
        'APPROUVEE': 'status-approved',
        'REJETEE': 'status-rejected'
      }
      return statusMap[status] || 'status-default'
    },
    getStatusMessage(status) {
      const messages = {
        'CREE': {
          title: 'Demande Créée',
          message: 'Votre demande a été créée et enregistrée dans notre système. Veuillez consulter régulièrement cette page pour les mises à jour.'
        },
        'SCANNEE': {
          title: 'Demande Scannée',
          message: 'Tous vos documents ont été reçus et sont actuellement en cours de traitement.'
        },
        'APPROUVEE': {
          title: 'Demande Approuvée ✓',
          message: 'Félicitations! Votre demande de visa a été approuvée. Vous pouvez retirer votre visa.'
        },
        'REJETEE': {
          title: 'Demande Rejetée',
          message: 'Votre demande a été rejetée. Veuillez contacter notre centre d\'assistance pour plus de détails.'
        }
      }
      return messages[status] || { title: 'Statut Inconnu', message: 'Veuillez réessayer plus tard.' }
    }
  }
}
</script>

<style scoped>
.followup-page {
  max-width: 800px;
  margin: 0 auto;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.loading {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  text-align: center;
  padding: 3rem 2rem;
  background: #fee;
  color: #c33;
  border-radius: 12px;
  border: 2px solid #fcc;
}

.error h2 {
  margin: 0 0 1rem 0;
}

.btn-retry {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.followup-container {
  display: grid;
  gap: 2rem;
}

.followup-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 3rem 2rem;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
}

.ref-number {
  margin: 0;
  font-size: 1.2rem;
  opacity: 0.9;
}

.status-big {
  font-size: 1.5rem;
  font-weight: 700;
  padding: 1rem 2rem;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  min-width: 150px;
  text-align: center;
}

.status-big.status-new { background: rgba(255, 193, 7, 0.3); }
.status-big.status-scanned { background: rgba(23, 162, 184, 0.3); }
.status-big.status-approved { background: rgba(40, 167, 69, 0.3); }
.status-big.status-rejected { background: rgba(220, 53, 69, 0.3); }

/* Sections */
.info-section,
.status-section,
.documents-section {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-section h2,
.status-section h2,
.documents-section h2,
.status-message h3 {
  color: #333;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
}

.info-box {
  display: grid;
  gap: 1rem;
}

.info-row {
  display: grid;
  grid-template-columns: 150px 1fr;
  align-items: center;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.label {
  font-weight: 600;
  color: #667eea;
}

.value {
  color: #333;
}

/* Status Timeline */
.status-timeline {
  display: grid;
  gap: 2rem;
}

.status-point {
  display: flex;
  gap: 1.5rem;
}

.status-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-circle.status-new { background: #ffc107; color: black; }
.status-circle.status-scanned { background: #17a2b8; }
.status-circle.status-approved { background: #28a745; }
.status-circle.status-rejected { background: #dc3545; }

.status-details h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.status-time {
  color: #999;
  font-size: 0.9rem;
  margin: 0.5rem 0;
}

.status-comment {
  color: #666;
  font-style: italic;
  margin-top: 0.5rem;
}

/* Documents */
.documents-list {
  display: grid;
  gap: 1rem;
}

.document-item {
  display: grid;
  grid-template-columns: 50px 1fr auto;
  gap: 1rem;
  align-items: center;
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 4px solid #ddd;
}

.document-item.provided {
  border-left-color: #28a745;
  background: #f0fdf4;
}

.document-item.missing {
  border-left-color: #dc3545;
  background: #fef0f0;
}

.doc-icon {
  font-size: 1.5rem;
  display: flex;
  justify-content: center;
}

.doc-name {
  font-weight: 600;
  color: #333;
  margin: 0;
}

.doc-required {
  color: #ff9800;
  font-size: 0.8rem;
  margin: 0.25rem 0 0 0;
}

.badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  white-space: nowrap;
}

.badge.provided {
  background: #28a745;
  color: white;
}

.badge.missing {
  background: #dc3545;
  color: white;
}

/* Status Message */
.status-message {
  padding: 2rem;
  border-radius: 12px;
  color: white;
}

.status-message h3 {
  margin: 0 0 0.5rem 0;
  color: white;
}

.status-message p {
  margin: 0;
  opacity: 0.95;
}

.status-message.status-new {
  background: linear-gradient(135deg, #ffc107, #ff9800);
}

.status-message.status-scanned {
  background: linear-gradient(135deg, #17a2b8, #138496);
}

.status-message.status-approved {
  background: linear-gradient(135deg, #28a745, #20c997);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.2);
}

.status-message.status-rejected {
  background: linear-gradient(135deg, #dc3545, #c82333);
}

/* Footer */
.followup-footer {
  text-align: center;
  color: white;
  padding: 2rem;
}

.followup-footer p {
  margin: 0.5rem 0;
}

.footer-date {
  font-size: 0.9rem;
  opacity: 0.8;
}

.empty-state {
  text-align: center;
  color: #999;
  padding: 2rem;
}

@media (max-width: 768px) {
  .followup-header {
    flex-direction: column;
    gap: 1rem;
  }

  .info-row {
    grid-template-columns: 1fr;
  }

  .document-item {
    grid-template-columns: 1fr;
  }
}
</style>
