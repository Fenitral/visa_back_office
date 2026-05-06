<template>
  <div class="details-demande">
    <!-- Back button -->
    <button class="btn-back" @click="$emit('back')">← Retour</button>

    <!-- Loading state -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Chargement des détails...</p>
    </div>

    <!-- Error state -->
    <div v-if="error" class="error">
      <p>❌ {{ error }}</p>
      <button @click="chargerDemande" class="btn-retry">Réessayer</button>
    </div>

    <!-- Demande details -->
    <div v-if="!loading && demande" class="details-container">
      <!-- Main info -->
      <div class="info-card">
        <div class="info-header">
          <h2>#{{ demande.id }}: {{ demande.prenomDemandeur }} {{ demande.nomDemandeur }}</h2>
          <span class="status" :class="getStatusClass(demande.statutDemande)">
            {{ demande.statutDemande }}
          </span>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <label>Type de Visa</label>
            <p>{{ demande.typeVisa }}</p>
          </div>
          <div class="info-item">
            <label>Type de Demande</label>
            <p>{{ demande.typeDemande }}</p>
          </div>
          <div class="info-item">
            <label>Numéro de Passeport</label>
            <p>{{ demande.numeroPasSeport }}</p>
          </div>
          <div class="info-item">
            <label>Référence Visa</label>
            <p>{{ demande.referenceVisa }}</p>
          </div>
          <div class="info-item">
            <label>Date de Demande</label>
            <p>{{ formatDate(demande.dateDemande) }}</p>
          </div>
        </div>
      </div>

      <!-- QR Code Section -->
      <div class="qrcode-card">
        <h3>📱 QR Code de Suivi</h3>
        <p class="qr-description">Scannez ce code pour accéder au suivi de votre demande</p>
        
        <div class="qrcode-container">
          <qrcode-vue 
            :value="qrCodeUrl" 
            :options="{ width: 250, color: { dark: '#667eea', light: '#ffffff' } }"
          />
        </div>

        <div class="qr-url">
          <p><strong>URL encodée:</strong></p>
          <input 
            type="text" 
            :value="qrCodeUrl" 
            readonly 
            class="qr-input"
            @click="copyToClipboard"
          >
          <button @click="copyToClipboard" class="btn-copy">📋 Copier l'URL</button>
        </div>
      </div>

      <!-- Historique des statuts -->
      <div class="historique-card">
        <h3>📋 Historique des Statuts</h3>
        <div v-if="demande.historiques && demande.historiques.length > 0" class="timeline">
          <div 
            v-for="(hist, index) in demande.historiques" 
            :key="hist.id"
            class="timeline-item"
          >
            <div class="timeline-marker" :class="getStatusClass(hist.statut)"></div>
            <div class="timeline-content">
              <h4>{{ hist.statut }}</h4>
              <p class="timeline-date">{{ formatDate(hist.dateChangement) }}</p>
              <p v-if="hist.commentaire" class="timeline-comment">{{ hist.commentaire }}</p>
            </div>
          </div>
        </div>
        <div v-else class="empty">
          Aucun historique disponible
        </div>
      </div>

      <!-- Pièces justificatives -->
      <div v-if="demande.pieces && demande.pieces.length > 0" class="pieces-card">
        <h3>📄 Pièces Justificatives</h3>
        <div class="pieces-list">
          <div 
            v-for="piece in demande.pieces" 
            :key="piece.idPiece"
            class="piece-item"
          >
            <span class="piece-icon">📎</span>
            <span class="piece-name">{{ piece.nomPiece }}</span>
            <span v-if="piece.obligatoire" class="piece-required">Obligatoire</span>
            <span v-if="piece.fourni" class="piece-provided">✓ Fourni</span>
            <span v-else class="piece-missing">⚠ Manquant</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import QrcodeVue from 'qrcode.vue'

export default {
  name: 'DetailsDemande',
  components: {
    QrcodeVue
  },
  props: {
    demandeId: {
      type: Number,
      required: true
    }
  },
  emits: ['back'],
  data() {
    return {
      demande: null,
      loading: true,
      error: null
    }
  },
  computed: {
    qrCodeUrl() {
      if (!this.demande) return ''
      const baseUrl = window.location.origin
      return `${baseUrl}/?demande=${this.demande.id}`
    }
  },
  mounted() {
    this.chargerDemande()
  },
  watch: {
    demandeId() {
      this.chargerDemande()
    }
  },
  methods: {
    async chargerDemande() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/demandes/${this.demandeId}`)
        this.demande = response.data
      } catch (err) {
        this.error = err.message || 'Erreur lors du chargement des détails'
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
    copyToClipboard() {
      navigator.clipboard.writeText(this.qrCodeUrl)
      alert('URL copiée dans le presse-papiers!')
    }
  }
}
</script>

<style scoped>
.details-demande {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.btn-back {
  margin-bottom: 1.5rem;
  padding: 0.75rem 1.5rem;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid #667eea;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  color: #667eea;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #667eea;
  color: white;
}

.loading, .error {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.details-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

.info-card, .qrcode-card, .historique-card, .pieces-card {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid #eee;
}

.info-header h2 {
  color: #333;
  flex: 1;
  margin: 0;
}

.status {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  white-space: nowrap;
}

.status-new { background: #ffc107; color: black; }
.status-scanned { background: #17a2b8; color: white; }
.status-approved { background: #28a745; color: white; }
.status-rejected { background: #dc3545; color: white; }
.status-default { background: #6c757d; color: white; }

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.info-item {
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 4px;
}

.info-item label {
  display: block;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.info-item p {
  color: #555;
  margin: 0;
}

/* QR Code Section */
.qrcode-card {
  text-align: center;
}

.qr-description {
  color: #666;
  margin-bottom: 1.5rem;
}

.qrcode-container {
  margin: 2rem 0;
  padding: 2rem;
  background: #f9f9f9;
  border-radius: 8px;
  display: flex;
  justify-content: center;
}

.qr-url {
  margin-top: 2rem;
  text-align: left;
}

.qr-url strong {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.qr-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.btn-copy {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.btn-copy:hover {
  background: #764ba2;
}

/* Historique Timeline */
.timeline {
  position: relative;
  padding-left: 2rem;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #ddd;
}

.timeline-item {
  display: flex;
  margin-bottom: 2rem;
  position: relative;
}

.timeline-marker {
  position: absolute;
  left: -2.5rem;
  top: 0.25rem;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 0 0 3px #667eea;
}

.timeline-marker.status-new { box-shadow: 0 0 0 3px #ffc107; }
.timeline-marker.status-scanned { box-shadow: 0 0 0 3px #17a2b8; }
.timeline-marker.status-approved { box-shadow: 0 0 0 3px #28a745; }
.timeline-marker.status-rejected { box-shadow: 0 0 0 3px #dc3545; }

.timeline-content {
  flex: 1;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 4px;
}

.timeline-content h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.timeline-date {
  color: #999;
  font-size: 0.9rem;
  margin: 0.25rem 0;
}

.timeline-comment {
  color: #555;
  font-style: italic;
  margin-top: 0.5rem;
}

/* Pièces */
.pieces-list {
  display: grid;
  gap: 1rem;
}

.piece-item {
  display: grid;
  grid-template-columns: 30px 1fr auto auto;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 4px;
}

.piece-icon {
  font-size: 1.2rem;
}

.piece-name {
  color: #333;
  font-weight: 500;
}

.piece-required {
  background: #ff9800;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
}

.piece-provided {
  background: #28a745;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
}

.piece-missing {
  background: #dc3545;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
}

.empty {
  color: #999;
  padding: 2rem;
  text-align: center;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .piece-item {
    grid-template-columns: 1fr;
  }
}
</style>
