<template>
  <div class="recherche-page">
    <button class="btn-back" @click="$emit('back')">← Retour</button>

    <!-- Search Form -->
    <div class="search-container">
      <h2>🔍 Rechercher une Demande</h2>
      
      <div class="search-form">
        <div class="form-group">
          <label for="searchType">Type de recherche:</label>
          <select v-model="searchType" id="searchType">
            <option value="id">Par ID de Demande</option>
            <option value="passport">Par Numéro de Passeport</option>
          </select>
        </div>

        <div class="form-group">
          <label :for="searchType === 'id' ? 'searchId' : 'searchPassport'">
            {{ searchType === 'id' ? 'ID de Demande' : 'Numéro de Passeport' }}:
          </label>
          <input 
            v-if="searchType === 'id'"
            v-model.number="searchId"
            type="number"
            id="searchId"
            placeholder="Ex: 1, 5, 10"
            @keyup.enter="rechercher"
          />
          <input 
            v-else
            v-model="searchPassport"
            type="text"
            id="searchPassport"
            placeholder="Ex: PASS-1, PASS-5"
            @keyup.enter="rechercher"
          />
        </div>

        <button class="btn-search" @click="rechercher" :disabled="isSearching">
          {{ isSearching ? '⏳ Recherche...' : '🔎 Rechercher' }}
        </button>
      </div>

      <div v-if="errorMessage" class="error-message">
        ❌ {{ errorMessage }}
      </div>
    </div>

    <!-- Results Section -->
    <div v-if="hasSearched" class="results-container">
      <!-- Case 1: Search by ID - show main demande + related ones -->
      <div v-if="searchType === 'id' && resultData.principale">
        <div class="result-section">
          <h3>📌 Demande Principale (ID: {{ resultData.principale.id }})</h3>
          <div class="demande-card highlighted">
            <div class="card-header">
              <h3>{{ resultData.principale.prenomDemandeur }} {{ resultData.principale.nomDemandeur }}</h3>
              <span class="status" :class="getStatusClass(resultData.principale.statutDemande)">
                {{ resultData.principale.statutDemande }}
              </span>
            </div>

            <div class="card-body">
              <div class="info-row">
                <span class="label">ID:</span>
                <span class="value">#{{ resultData.principale.id }}</span>
              </div>
              <div class="info-row">
                <span class="label">Type Visa:</span>
                <span class="value">{{ resultData.principale.typeVisa }}</span>
              </div>
              <div class="info-row">
                <span class="label">Type Demande:</span>
                <span class="value">{{ resultData.principale.typeDemande }}</span>
              </div>
              <div class="info-row">
                <span class="label">Passeport:</span>
                <span class="value">{{ resultData.principale.numeroPasSeport }}</span>
              </div>
              <div class="info-row">
                <span class="label">Référence Visa:</span>
                <span class="value">{{ resultData.principale.referenceVisa }}</span>
              </div>
              <div class="info-row">
                <span class="label">Date:</span>
                <span class="value">{{ formatDate(resultData.principale.dateDemande) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Related demandes -->
        <div v-if="resultData.liees && resultData.liees.length > 0" class="result-section">
          <h3>🔗 Autres Demandes du Même Demandeur ({{ resultData.liees.length }})</h3>
          <div class="demandes-grid">
            <div 
              v-for="demande in resultData.liees" 
              :key="demande.id"
              class="demande-card"
              @click="viewDetails(demande.id)"
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
                <p><strong>Date:</strong> {{ formatDate(demande.dateDemande) }}</p>
              </div>

              <div class="card-footer">
                <button class="btn-details">Voir détails →</button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="no-related">
          <p>Aucune autre demande trouvée pour ce demandeur</p>
        </div>
      </div>

      <!-- Case 2: Search by Passport - show all demandes for this passport -->
      <div v-else-if="searchType === 'passport' && resultList.length > 0">
        <div class="result-section">
          <h3>📋 Demandes liées au Passeport: {{ searchPassport }} ({{ resultList.length }})</h3>
          <div class="demandes-grid">
            <div 
              v-for="demande in resultList" 
              :key="demande.id"
              class="demande-card"
              @click="viewDetails(demande.id)"
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
      </div>

      <!-- No results -->
      <div v-else class="no-results">
        <p>❌ Aucun résultat trouvé</p>
        <p v-if="searchType === 'id'" class="hint">Vérifiez que l'ID de demande existe</p>
        <p v-else class="hint">Vérifiez que le numéro de passeport est correct (Ex: PASS-1)</p>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="isSearching" class="loading">
      <div class="spinner"></div>
      <p>Recherche en cours...</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Recherche',
  emits: ['back', 'view-details'],
  data() {
    return {
      searchType: 'id',
      searchId: null,
      searchPassport: '',
      isSearching: false,
      hasSearched: false,
      errorMessage: '',
      resultData: {
        principale: null,
        liees: []
      },
      resultList: []
    }
  },
  methods: {
    async rechercher() {
      if (!this.validateInput()) return

      this.isSearching = true
      this.errorMessage = ''
      this.hasSearched = false
      this.resultData = { principale: null, liees: [] }
      this.resultList = []

      try {
        if (this.searchType === 'id') {
          await this.rechercherParId()
        } else {
          await this.rechercherParPasseport()
        }
        this.hasSearched = true
        this.logResultsToConsole()
      } catch (err) {
        this.errorMessage = err.message || 'Erreur lors de la recherche'
        console.error('Erreur de recherche:', err)
      } finally {
        this.isSearching = false
      }
    },

    async rechercherParId() {
      const response = await axios.get(`/api/demandes/search/id/${this.searchId}`)
      this.resultData = response.data
      console.log('📌 Résultat recherche par ID:', this.resultData)
    },

    async rechercherParPasseport() {
      const response = await axios.get(`/api/demandes/search/passport/${this.searchPassport}`)
      this.resultList = response.data
      console.log('📋 Résultat recherche par Passeport:', this.resultList)
    },

    validateInput() {
      if (this.searchType === 'id') {
        if (!this.searchId || this.searchId <= 0) {
          this.errorMessage = 'Veuillez entrer un ID valide'
          return false
        }
      } else {
        if (!this.searchPassport || this.searchPassport.trim() === '') {
          this.errorMessage = 'Veuillez entrer un numéro de passeport'
          return false
        }
      }
      return true
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
        'REJETEE': 'status-rejected',
        'EN_ATTENTE': 'status-pending'
      }
      return statusMap[status] || 'status-default'
    },

    logResultsToConsole() {
      console.log('🔍 === RÉSULTATS DE RECHERCHE ===')
      if (this.searchType === 'id') {
        console.log('Type: Recherche par ID')
        console.log('ID recherché:', this.searchId)
        console.log('Demande principale:', this.resultData.principale)
        console.log('Demandes liées:', this.resultData.liees)
      } else {
        console.log('Type: Recherche par Passeport')
        console.log('Passeport recherché:', this.searchPassport)
        console.log('Demandes trouvées:', this.resultList)
      }
      console.log('🔍 === FIN RÉSULTATS ===')
    },

    viewDetails(id) {
      this.$emit('view-details', id)
    }
  }
}
</script>

<style scoped>
.recherche-page {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.btn-back {
  background: #f0f0f0;
  border: 1px solid #ddd;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 2rem;
  font-size: 1rem;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #e0e0e0;
  transform: translateX(-2px);
}

.search-container {
  margin-bottom: 3rem;
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.search-container h2 {
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
}

.search-form {
  display: grid;
  grid-template-columns: 1fr 1fr 200px;
  gap: 1rem;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: bold;
  font-size: 0.95rem;
}

.form-group select,
.form-group input {
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
  background: white;
  color: black;
}

.form-group select:focus,
.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 5px rgba(102, 126, 234, 0.5);
}

.btn-search {
  padding: 0.75rem 1.5rem;
  background: white;
  color: #667eea;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-search:hover:not(:disabled) {
  background: #f0f0f0;
  transform: scale(1.05);
}

.btn-search:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.2);
  border-left: 4px solid white;
  border-radius: 4px;
  font-weight: bold;
}

.results-container {
  animation: slideIn 0.3s ease-out;
}

.result-section {
  margin-bottom: 2rem;
}

.result-section h3 {
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  color: #333;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.5rem;
}

.demande-card {
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
}

.demande-card:hover {
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.2);
  transform: translateY(-4px);
}

.demande-card.highlighted {
  border: 2px solid #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.card-header {
  background: #f0f0f0;
  padding: 1rem;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
}

.status {
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: bold;
}

.status-new { background: #e3f2fd; color: #1976d2; }
.status-scanned { background: #f3e5f5; color: #7b1fa2; }
.status-approved { background: #e8f5e9; color: #388e3c; }
.status-rejected { background: #ffebee; color: #d32f2f; }
.status-pending { background: #fff3e0; color: #f57c00; }
.status-default { background: #eeeeee; color: #616161; }

.card-body {
  padding: 1rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row .label {
  font-weight: bold;
  color: #666;
}

.info-row .value {
  color: #333;
  text-align: right;
}

.card-body p {
  margin: 0.7rem 0;
  color: #555;
}

.card-body p strong {
  color: #333;
}

.card-footer {
  padding: 1rem;
  background: #f9f9f9;
  text-align: right;
  border-top: 1px solid #ddd;
}

.btn-details {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: bold;
}

.btn-details:hover {
  background: #764ba2;
  transform: scale(1.05);
}

.demandes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.no-results {
  text-align: center;
  padding: 2rem;
  background: #fff3e0;
  border: 2px dashed #ff9800;
  border-radius: 8px;
  color: #e65100;
}

.no-results p {
  margin: 0.5rem 0;
  font-size: 1.1rem;
}

.no-results .hint {
  font-size: 0.9rem;
  opacity: 0.8;
}

.no-related {
  text-align: center;
  padding: 2rem;
  background: #f5f5f5;
  border-radius: 8px;
  color: #999;
}

.loading {
  text-align: center;
  padding: 2rem;
}

.spinner {
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1024px) {
  .search-form {
    grid-template-columns: 1fr;
  }

  .btn-search {
    width: 100%;
  }

  .demandes-grid {
    grid-template-columns: 1fr;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .status {
    margin-top: 0.5rem;
    align-self: flex-start;
  }
}
</style>
