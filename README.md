# Taxi Sociale App – Progetto IoT 2024/2025

## Descrizione del progetto

Questo progetto nasce con l'obiettivo di sviluppare una piattaforma intelligente e user-friendly per la gestione e la prenotazione di corse condivise, pensata per favorire la mobilità sostenibile e la collaborazione tra utenti.

L'applicazione consente agli utenti di:

- Registrarsi come passeggeri o autisti;
- Prenotare corse disponibili;
- Visualizzare in tempo reale lo stato delle proprie prenotazioni;
- Interagire tramite chatbot;
- Gestire il proprio profilo personale.

La soluzione è progettata per garantire:

- Facilità d'uso, con un'interfaccia intuitiva e accessibile;
- Sicurezza nella gestione dei dati personali e delle transazioni;
- Scalabilità, per supportare un numero crescente di utenti e corse;
- Integrazione con servizi di notifica, via email, per aggiornamenti in tempo reale.

## Come funziona UserServiceProject

**UserServiceProject** è il microservizio responsabile della gestione degli utenti all'interno della piattaforma Taxi Sociale. Le sue principali funzionalità includono:

- **Registrazione utenti**: consente la creazione di nuovi utenti (passeggeri, autisti, amministratori) tramite endpoint REST dedicati. Durante la registrazione vengono effettuati controlli su email e username univoci e la password viene cifrata.
- **Autenticazione e autorizzazione**: implementa un sistema di autenticazione tramite JWT (JSON Web Token). Al login, se le credenziali sono corrette, viene generato un token JWT che l'utente utilizza per autenticarsi nelle richieste successive.
- **Gestione profilo**: permette la visualizzazione e l'aggiornamento dei dati personali dell'utente, differenziando le informazioni in base al ruolo (utente o autista).
- **Sicurezza**: protegge le API tramite filtri di sicurezza e validazione dei token JWT, garantendo che solo gli utenti autenticati possano accedere alle risorse protette.
- **Gestione disponibilità autisti**: consente agli autisti di aggiornare la propria disponibilità e le fasce orarie in cui sono attivi, oltre al numero di posti disponibili.
- **Eliminazione utenti**: permette la cancellazione di utenti e autisti, verificando che non ci siano corse attive associate prima di procedere.
- **Storico e log**: mantiene uno storico delle azioni rilevanti (backlog) per ogni utente, utile per audit e tracciamento.
- **Integrazione con altri microservizi**: comunica con TripServiceProject per verificare lo stato delle corse prima di eliminare un autista e per altre operazioni correlate.

### Flusso tipico di autenticazione e gestione utente

1. **Registrazione**: l'utente invia i propri dati tramite l'endpoint `/api/registration/`. Il servizio verifica l'unicità di email e username, cifra la password e salva l'utente nel database MongoDB.
2. **Login**: l'utente effettua il login tramite `/api/users/authenticate`, ricevendo un JWT se le credenziali sono corrette.
3. **Accesso alle risorse**: per accedere alle risorse protette, l'utente include il JWT nell'header Authorization. Il servizio valida il token e consente l'accesso alle API.
4. **Gestione profilo**: l'utente può visualizzare e aggiornare i propri dati, con endpoint che restituiscono informazioni personalizzate in base al ruolo.
5. **Eliminazione**: prima di eliminare un autista, il servizio verifica tramite TripServiceProject che non ci siano corse attive associate.

### Tecnologie principali
- Spring Boot
- Spring Security (JWT)
- MongoDB
- REST API
- RabbitMQ (per la comunicazione asincrona tra microservizi)

Questa architettura garantisce sicurezza, scalabilità e facilità di integrazione con gli altri componenti della piattaforma Taxi Sociale.