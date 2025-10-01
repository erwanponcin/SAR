# Design Channel

## 1. `CBroker`

### Concept
Un broker identifie un espace de communication (par son nom unique) et gère les canaux ouverts sur ses ports.  
Un rendez-vous (`accept` ↔ `connect`) associe exactement deux extrémités d’un canal.

### Structures
- **Registre global** : `ConcurrentHashMap<String, CBroker>`  
  → garantit l’unicité des noms et permet à un `connect` de retrouver le bon broker.  
- **Rendezvous par port** : `Map<Integer, Rendezvous>`  
  - Contient : `lock`, file FIFO de `connect`, slot unique pour `accept`.  
  - Un seul `accept` peut être en attente sur un port, plusieurs `connect` peuvent s’y inscrire.  

### Fonctionnement
- **`accept(port)`**  
  - Si un `connect` est déjà en attente, il est immédiatement apparié.  
  - Sinon, l’accept bloque dans un `slot` jusqu’à l’arrivée d’un connect.  
- **`connect(name, port)`**  
  - Si le broker n’existe pas → `null`.  
  - Si un `accept` est présent, paire créée immédiatement.  
  - Sinon, le connect s’aligne en file et attend d’être servi.  

### Concurrence
- Synchronisation par `ReentrantLock` par `Rendezvous`.  
- Conditions `awaitUninterruptibly` pour bloquer sans lever d’exception.  
- FIFO sur les `connect` → garantit l’équité des connexions.

---

## 2. `CChannel`

### Concept
Un canal est constitué de **deux extrémités** (`CChannel`) reliées par **deux tampons circulaires** (un dans chaque sens).  
Chaque extrémité est remise soit à l’acceptant soit au connectant.

### Structures
- **`ChannelBuffer`** : encapsule `CircularBuffer` + `lock` + `Condition notEmpty/notFull`.  
- Dans chaque `CChannel` :  
  - `inbound` : tampon où lire (alimenté par l’autre).  
  - `peer` : référence à l’autre extrémité (utile pour écrire).  
  - Flags volatiles : `localDisconnected`, `remoteDisconnected`.

### Opérations
- **`read(bytes, off, len)`**  
  - Bloque si tampon vide, sauf si remote déconnecté (alors fin de flux).  
  - Lit au moins 1 octet disponible (≤ len).  
- **`write(bytes, off, len)`**  
  - Bloque si tampon du peer plein.  
  - Écrit au moins 1 octet.  
  - Si peer déconnecté, les données sont acceptées mais perdues.  
- **`disconnect()`**  
  - Marque `localDisconnected=true`.  
  - Signale au peer `remoteDisconnected=true`.  
  - Réveille tous les threads bloqués (lecteurs/écrivains).  
- **`disconnected()`**  
  - Vérifie uniquement l’état local.  

### Concurrence
- Lecture et écriture protégées par **locks différents** (un par tampon) → lecture/écriture possibles en parallèle.  
- Deux lectures concurrentes sur la même extrémité **ne sont pas sécurisées** (idem pour deux écritures) : l’application doit les séquencer.  
- Les flags `volatile` assurent la visibilité immédiate des déconnexions.

---

## 3. Synthèse
- **`CBroker`** gère la découverte et la synchronisation bloquante entre `accept` et `connect`.  
- **`CChannel`** fournit une communication point-à-point, bidirectionnelle, bloquante, FIFO, avec gestion claire des déconnexions locales/distantes.  
- Ensemble, ces composants réalisent une **infrastructure de communication fiable et concurrente** entre tâches, fidèle à la spécification.
