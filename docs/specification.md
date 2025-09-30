# Spécification des classes Broker / Channel / Task

## 1. Objectif

Mettre en place un cadre de communication **point-à-point** entre tâches.  
Chaque tâche communique via un **broker** et échange des octets à travers un **channel**.  
Un channel est un flux d’octets **bidirectionnel**, **FIFO** et **sans pertes** (sauf en cas de déconnexion).  
Le système doit être **sûr**, **bloquant**, et gérer correctement les déconnexions.

---

## 2. Les classes

### 2.1 `Broker`

Un broker représente une entité de communication identifiée par un nom unique.  
Il permet d’établir des connexions.

- **Constructeur**  
  `Broker(String name)` : crée un broker nommé.  

- **Méthodes**  
  - `Channel accept(int port)`  
    - Attend une connexion entrante sur le port.  
    - Bloque tant qu’aucun `connect` correspondant n’arrive.  
    - Retourne un channel une fois la connexion établie.  
    - Une seule tâche peut `accept` sur un (broker,port).  

  - `Channel connect(String name, int port)`  
    - Demande une connexion vers le broker `name` au port `port`.  
    - Si le broker n’existe pas, retourne `null`.  
    - Si le broker existe mais qu’aucun `accept` n’est encore en attente, bloque jusqu’au rendez-vous.  
    - Retourne un channel actif une fois la connexion faite.  
    - Pas de priorité entre `accept` et `connect` : ils se bloquent mutuellement jusqu’à se rencontrer.  

Plusieurs tâches peuvent partager un même broker. L’implémentation doit donc être **thread-safe**.

---

### 2.2 `Channel`

Un channel est un lien de communication full-duplex.  
Chaque extrémité peut lire et écrire **en parallèle** (lecture et écriture en simultané sont sûres).  

**Attention** :  
- Deux lectures concurrentes sur la même extrémité ne sont pas sûres.  
- Deux écritures concurrentes sur la même extrémité ne sont pas sûres.  
- La synchronisation doit donc être gérée au niveau applicatif si nécessaire.

#### Méthodes

- `int read(byte[] bytes, int offset, int length)`  
  - Lit jusqu’à `length` octets dans `bytes` à partir de `offset`.  
  - Retourne le nombre d’octets lus (>0).  
  - Si aucun octet n’est disponible, bloque jusqu’à lecture possible.  
  - Si le channel est déconnecté **et** qu’il n’y a plus rien à lire, lève `DisconnectedException`.  

- `int write(byte[] bytes, int offset, int length)`  
  - Écrit `length` octets à partir de `bytes[offset]`.  
  - Retourne le nombre d’octets écrits (>0).  
  - Bloque si le canal est plein.  
  - Si le channel est déconnecté côté local : lève `DisconnectedException`.  
  - Si la déconnexion vient du côté distant : 
    - Les lectures continuent tant qu’il reste des octets en transit.  
    - Les écritures sont **acceptées localement mais perdues** (elles disparaissent).  

- `void disconnect()`  
  - Coupe localement la connexion.  
  - Après appel, **seules** les méthodes `disconnected()` sont valides.  
  - Toute lecture/écriture ultérieure lève une `DisconnectedException`.  

- `boolean disconnected()`  
  - Indique si la connexion est coupée localement.  

#### Règles de déconnexion

- Déconnexion **locale** : interdit toute opération de lecture/écriture après coupure.  
- Déconnexion **distante** :  
  - Les octets déjà envoyés doivent rester lisibles.  
  - Une fois ces octets consommés, le channel apparaît déconnecté.  
  - Les écritures locales sont silencieusement ignorées.  

---

### 2.3 `Task`

Une tâche associe un **thread** à un broker. Elle exécute du code applicatif tout en participant à la communication.

- **Constructeur**  
  `Task(Broker b, Runnable r)` : crée une tâche liée au broker `b`, qui exécute `r`.  

- **Méthode statique**  
  `static Broker getBroker()` : retourne le broker associé à la tâche courante.  

