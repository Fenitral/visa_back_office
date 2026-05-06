#!/usr/bin/env bash
# 🚀 SPRINT 4 - START HERE!
# This file contains the exact commands to get started

##############################################################################
# ÉTAPE 1: Lancer le Backend
##############################################################################
echo "📦 ÉTAPE 1: Lancer le Backend"
echo "================================"
echo ""
echo "Ouvrez un PREMIER terminal et lancez:"
echo ""
echo "  cd d:\\STUDY\\S6\\MNaina\\trinome\\visa\\visa\\visa_back_office\\backend"
echo "  mvn spring-boot:run"
echo ""
echo "⏳ Attendre le message: 'Application ready in X seconds'"
echo "✓ Backend prêt sur: http://localhost:8084"
echo ""
echo "Tester: http://localhost:8084/api/demandes"
echo ""

##############################################################################
# ÉTAPE 2: Lancer le Frontend
##############################################################################
echo ""
echo "💻 ÉTAPE 2: Lancer le Frontend"
echo "==============================="
echo ""
echo "Ouvrez un DEUXIÈME terminal (le premier reste actif) et lancez:"
echo ""
echo "  cd d:\\STUDY\\S6\\MNaina\\trinome\\visa\\visa\\visa_back_office\\frontend"
echo "  npm install"
echo "  npm run dev"
echo ""
echo "⏳ Attendre le message: 'Local: http://localhost:5173'"
echo "✓ Frontend prêt sur: http://localhost:5173"
echo ""

##############################################################################
# ÉTAPE 3: Tests
##############################################################################
echo ""
echo "✅ ÉTAPE 3: Tester l'application"
echo "=================================="
echo ""
echo "Test 1: Vérifier l'API"
echo "  → Ouvrir: http://localhost:8084/api/demandes"
echo "  → Doit voir du JSON"
echo ""
echo "Test 2: Voir la liste"
echo "  → Ouvrir: http://localhost:5173"
echo "  → Doit voir une grille de demandes"
echo ""
echo "Test 3: Voir les détails"
echo "  → Cliquer sur une demande"
echo "  → Doit voir le QR Code"
echo ""
echo "Test 4: Scanner le QR"
echo "  → Copier l'URL affichée"
echo "  → Ouvrir dans un nouvel onglet"
echo "  → Doit voir la page 'Suivi Public'"
echo ""
echo "Test 5: Mobile responsive"
echo "  → Ouvrir DevTools (F12)"
echo "  → Cliquer Device Toggle"
echo "  → Doit être lisible sur mobile"
echo ""
echo "Test 6: Sans erreurs"
echo "  → Ouvrir Console (F12)"
echo "  → Pas d'erreurs rouges"
echo ""

##############################################################################
# ÉTAPES SUIVANTES
##############################################################################
echo ""
echo "📖 PROCHAINES ÉTAPES"
echo "===================="
echo ""
echo "Pour comprendre le système:"
echo "  1. Lire: SPRINT4_EXECUTIVE_SUMMARY.md (5 min)"
echo "  2. Lire: SPRINT4_QUICKSTART.md (5 min)"
echo "  3. Lire: ARCHITECTURE.md (30 min)"
echo ""
echo "Pour développer:"
echo "  1. Consulter: frontend/README.md"
echo "  2. Regarder le code: src/views/*.vue"
echo "  3. Voir les endpoints: DemandeRestController.java"
echo ""
echo "Pour déployer:"
echo "  1. Lire: DEPLOYMENT_GUIDE.md"
echo "  2. Choisir une plateforme (Nginx/Docker/Vercel/AWS)"
echo "  3. Suivre les étapes"
echo ""

##############################################################################
# GUIDE COMPLET
##############################################################################
echo ""
echo "📚 DOCUMENTATION COMPLÈTE"
echo "========================="
echo ""
echo "Voir INDEX_DOCUMENTATION.md pour:"
echo "  ✓ Guide complet de navigation"
echo "  ✓ Recommandations par rôle"
echo "  ✓ FAQ rapide"
echo "  ✓ Temps de lecture"
echo ""

##############################################################################
# STRUCTURE CRÉÉE
##############################################################################
echo ""
echo "✨ CE QUI A ÉTÉ CRÉÉ"
echo "===================="
echo ""
echo "Backend (Sprint 4 modifié):"
echo "  ✓ DemandeRestController.java (API)"
echo "  ✓ HistoriqueStatutDTO.java (Données)"
echo "  ✓ DemandeResponseDTO.java (modifié)"
echo "  ✓ DemandeMapper.java (modifié)"
echo ""
echo "Frontend (Nouveau - complet):"
echo "  ✓ Application Vue.js 3 avec Vite"
echo "  ✓ 3 vues: Liste, Détails, Suivi Public"
echo "  ✓ QR Code intégré"
echo "  ✓ Responsive design"
echo ""
echo "Documentation (8 fichiers):"
echo "  ✓ SPRINT4_EXECUTIVE_SUMMARY.md"
echo "  ✓ SPRINT4_QUICKSTART.md"
echo "  ✓ SPRINT4_QR_CODE.md"
echo "  ✓ SPRINT4_CHECKLIST.md"
echo "  ✓ ARCHITECTURE.md"
echo "  ✓ DEPLOYMENT_GUIDE.md"
echo "  ✓ INDEX_DOCUMENTATION.md"
echo "  ✓ frontend/README.md"
echo ""

##############################################################################
# STATUS
##############################################################################
echo ""
echo "🎉 SPRINT 4 - COMPLET!"
echo "======================"
echo ""
echo "Status: ✅ LIVRÉ ET VALIDÉ"
echo ""
echo "Prêt pour:"
echo "  ✓ Développement local"
echo "  ✓ Tests utilisateurs"
echo "  ✓ Déploiement production"
echo ""
echo "Architecture:"
echo "  ✓ Backend: Spring Boot API REST"
echo "  ✓ Frontend: Vue.js 3 + Vite"
echo "  ✓ API: 2 endpoints (/api/demandes, /api/demandes/{id})"
echo "  ✓ QR Code: Généré automatiquement"
echo ""

##############################################################################
# AIDE
##############################################################################
echo ""
echo "⚠️  PROBLÈMES?"
echo "=============="
echo ""
echo "Backend ne démarre pas:"
echo "  → Vérifier: cd backend && mvn clean compile"
echo "  → Voir logs pour erreurs"
echo ""
echo "Frontend ne démarre pas:"
echo "  → Vérifier: npm install (dans frontend/)"
echo "  → Vérifier Node.js: node --version (doit être 16+)"
echo ""
echo "API retourne erreur 404:"
echo "  → S'assurer backend lancé: http://localhost:8084/api/test"
echo ""
echo "QR Code ne s'affiche pas:"
echo "  → Rafraîchir la page (F5)"
echo "  → Vérifier console pour erreurs (F12)"
echo ""

##############################################################################
# FIN
##############################################################################
echo ""
echo "✅ Ready to go!"
echo ""
echo "Suivez les 3 étapes au-dessus et testez!"
echo ""
