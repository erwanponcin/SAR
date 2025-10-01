# Design Message Queues Synchronous

Surcouche construite au-dessus des Brokers / Channels, permettant de travailler avec **messages complets** au lieu de flux d’octets.

---

## QueueBroker
- Fabrique de `MessageQueue`.
- Utilise le `Broker` pour établir une connexion (accept / connect).
- Retourne une `MessageQueue` qui encapsule le `Channel`.

---

## MessageQueue
Surcouche d’un `Channel` pour gérer des **messages atomiques**.

- **send(msg)**  
  - Vérifie que la file n’est pas fermée.  
  - Écrit la longueur.  
  - Écrit le contenu.  
  - Bloquant jusqu’à écriture complète.  
- **receive()**  
  - Bloque jusqu’à lecture complète d’un message.  
  - Lit la taille.  
  - Lit exactement `taille` octets → retourne le message.  
  - Si déconnexion avant la fin → exception.  
- **close()** : ferme le `Channel`, réveille les opérations bloquées.  
- **closed()** : indique l’état fermé (local ou distant).
