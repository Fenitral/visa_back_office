<template>
  <div class="app-container">
    <!-- Navigation -->
    <nav class="navbar">
      <div class="nav-container">
        <h1 class="nav-logo" @click="currentPage = 'list'">📋 Suivi Demandes Visa</h1>
        <div class="nav-buttons">
          <button 
            class="nav-btn" 
            :class="{ active: currentPage === 'list' }"
            @click="currentPage = 'list'"
          >
            Liste des demandes
          </button>
          <button 
            class="nav-btn" 
            :class="{ active: currentPage === 'recherche' }"
            @click="currentPage = 'recherche'"
          >
            🔍 Rechercher
          </button>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Liste des demandes -->
      <ListeDemandes v-if="currentPage === 'list'" @open-details="openDetails" />
      
      <!-- Recherche -->
      <Recherche 
        v-if="currentPage === 'recherche'" 
        @back="currentPage = 'list'"
        @view-details="openDetails"
      />
      
      <!-- Détails de la demande -->
      <DetailsDemande 
        v-if="currentPage === 'details'" 
        :demande-id="selectedDemandeId"
        @back="currentPage = previousPage"
      />
      
      <!-- Page publique de suivi -->
      <FollowUp 
        v-if="currentPage === 'followup'" 
        :demande-id="selectedDemandeId"
      />
    </main>

    <!-- Footer -->
    <footer class="app-footer">
      <p>&copy; 2026 Système de Suivi Demandes Visa | Sprint 4 - QR Code</p>
    </footer>
  </div>
</template>

<script>
import ListeDemandes from './views/ListeDemandes.vue'
import DetailsDemande from './views/DetailsDemande.vue'
import FollowUp from './views/FollowUp.vue'
import Recherche from './views/Recherche.vue'

export default {
  name: 'App',
  components: {
    ListeDemandes,
    DetailsDemande,
    FollowUp,
    Recherche
  },
  data() {
    return {
      currentPage: 'list',
      previousPage: 'list',
      selectedDemandeId: null
    }
  },
  methods: {
    openDetails(id) {
      this.previousPage = this.currentPage
      this.selectedDemandeId = id
      this.currentPage = 'details'
    }
  },
  mounted() {
    // Support pour accéder à la page de suivi public via URL
    const params = new URLSearchParams(window.location.search)
    if (params.has('demande')) {
      this.selectedDemandeId = parseInt(params.get('demande'))
      this.currentPage = 'followup'
    }
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.navbar {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 1rem 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.nav-logo {
  font-size: 1.5rem;
  color: #667eea;
  cursor: pointer;
  font-weight: 600;
}

.nav-buttons {
  display: flex;
  gap: 1rem;
}

.nav-btn {
  padding: 0.5rem 1rem;
  background: #f0f0f0;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
}

.nav-btn:hover {
  background: #e0e0e0;
}

.nav-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 2rem auto;
  width: 100%;
  padding: 0 20px;
}

.app-footer {
  background: rgba(0, 0, 0, 0.1);
  color: white;
  text-align: center;
  padding: 2rem;
  margin-top: auto;
}
</style>
