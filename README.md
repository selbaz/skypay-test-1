# Banking Service Technical Test

Implémentation d'un système de compte bancaire simple respectant les principes de **Clean Architecture** et **TDD**.

## 🛠 Stack Technique
* **Langage :** Java 21
* **Build :** Maven
* **Tests :** JUnit 5, Mockito, AssertJ

## ⚠️ Hypothèses & choix de conception

- Les **transactions sont supposées instantanées** et **suivent strictement l’ordre chronologique** de leur création.  

- La méthode `printStatement` a été développée avec un **objectif de performance**.  
  Elle suppose que le **relevé de compte n’est pas très volumineux**, ce qui permet d’éviter des traitements plus coûteux.


## 🚀 Comment exécuter le projet

### Lancer les tests
Le projet est couvert par des tests unitaires et d'intégration.
```bash
mvn test
