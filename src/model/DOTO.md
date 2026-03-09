# TODO & Checklist Qualité Génie Logiciel

## 🔧 À faire (technique)

- [ ] Ajouter dans `Direction` les méthodes de delta (getDeltaX, getDeltaY)
- [ ] Ajouter des postconditions (ou équivalent) dans les méthodes critiques

## 🧵 Analyse Thread-Safety

- [ ] Vérifier la sécurité des threads pour :
  - `Set<PositionGrille>`
  - batiment.stockage
  - case.batiment
- [ ] Solutions possibles :
  - Option A : Synchroniser méthodes critiques
  - Option B : Utiliser AtomicInteger
  - Option C : Collections thread-safe

## 📝 Documentation & Qualité

- [ ] Ajouter des exemples d'utilisation dans la JavaDoc
- [ ] ⚠️ Postconditions non documentées
- [ ] ⚠️ Invariants de classe non mentionnés

## 🕵️‍♂️ Patterns à valider à la fin

- [ ] Strategy (implicite)
- [ ] Template Method
- [ ] Active Object
- [ ] Value Object
- [ ] Flyweight (partiel)

### Recommandations à intégrer

- [ ] Ajouter Factory Pattern pour création de Batiment
- [ ] Implémenter Observer Pattern pour notifications de changements
- [ ] Considérer Command Pattern pour historique des opérations

## 🏛️ Principes SOLID

- [ ] [Voir la page Wikipédia SOLID](https://fr.wikipedia.org/wiki/SOLID_(informatique))

## 🔒 ENCAPSULATION

- [ ] Vérifier l'encapsulation des champs et méthodes
