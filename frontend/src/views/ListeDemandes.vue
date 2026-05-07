<template>
  <div class="liste-demandes">
    <div class="header">
      <h2>📊 Liste des Demandes</h2>
      <div class="stats">
        <span class="stat-badge">Total: {{ demandes.length }}</span>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Chargement des demandes...</p>
    </div>

    <!-- Error state -->
    <div v-if="error" class="error">
      <p>❌ Erreur: {{ error }}</p>
      <button @click="chargerDemandes" class="btn-retry">Réessayer</button>
    </div>

    <!-- Empty state -->
    <div v-if="!loading && demandes.length === 0" class="empty">
      <p>Aucune demande trouvée</p>
    </div>

    <!-- Demandes grid -->
    <div v-if="!loading && demandes.length > 0" class="demandes-grid">
      <div 
        v-for="demande in demandes" 
        :key="demande.id"
        class="demande-card"
        @click="$emit('open-details', demande.id)"
      >
        <div class="card-header">
          <h3>#{{ demande.id }}: {{ demande.prenomDemandeur }} {{ demande.nomDemandeur }}</h3>
          <span class="status" :class="getStatusClass(demande.statutDemande)">
            {{ demande.statutDemande }}
          </span>
        </div>

        <div class="card-body">
          <p><strong>Type Visa:</strong> {{ demande.typeVisa }}</p>
          <p><strong>Type Demande:</strong> {{ demande.typeDemande }}</p>
          <p><strong>Passeport:</strong> {{ demande.numeroPasSeport }}</p>
          <p><strong>Référence:</strong> {{ demande.referenceVisa }}</p>
          <p><strong>Date:</strong> {{ formatDate(demande.dateDemande) }}</p>
        </div>

        <div class="card-footer">
          <button class="btn-details">Voir détails →</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ListeDemandes',
  emits: ['open-details'],
  data() {
    return {
      demandes: [],
      loading: true,
      error: null
    }
  },
  mounted() {
    this.chargerDemandes()
  },
  methods: {
    async chargerDemandes() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/demandes')
        this.demandes = response.data
      } catch (err) {
        this.error = err.message || 'Erreur lors du chargement des demandes'
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
        day: 'numeric'
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
    }
  }
}
</script>

<style scoped>
.liste-demandes {
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

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header h2 {
  color: #333;
  font-size: 1.8rem;
}

.stats {
  display: flex;
  gap: 1rem;
}

.stat-badge {
  background: #667eea;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.loading, .error, .empty {
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

.error {
  background: #fee;
  color: #c33;
}

.btn-retry {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.demandes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
}

.demande-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.demande-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 1rem;
}

.card-header h3 {
  font-size: 1.1rem;
  margin: 0;
  flex: 1;
}

.status {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  white-space: nowrap;
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.status-new { background: #ffc107; color: black; }
.status-scanned { background: #17a2b8; color: white; }
.status-approved { background: #28a745; color: white; }
.status-rejected { background: #dc3545; color: white; }
.status-default { background: #6c757d; color: white; }

.card-body {
  padding: 1.5rem;
  flex: 1;
  font-size: 0.95rem;
  color: #555;
}

.card-body p {
  margin: 0.5rem 0;
  line-height: 1.5;
}

.card-body strong {
  color: #333;
}

.card-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #eee;
  background: #f9f9f9;
}

.btn-details {
  width: 100%;
  padding: 0.75rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s;
}

.btn-details:hover {
  background: #764ba2;
}
</style>
