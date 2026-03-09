# Projet-PCII-Groupe

Documentation:
<https://docs.google.com/document/d/1-BhmfGsZMEbjikOBkcdd_VNKfAl341frTOrar7PYgYo/edit?usp=sharing>

Diagramme de Gantt:
<https://docs.google.com/spreadsheets/d/1t5F3BJhigyTUmA8UtGCP11jiXa81Ue2nVTwKPMdGs0E/edit?usp=sharing>



### Compile

Fichiers `src`

```bash
javac -d target/classes $(find src -name "*.java")
```

Fichiers `tests` (Note: faut compiler les fichiers src avant de compiler les tests)

```bash
javac -d target/test-classes -cp target/classes $(find tests -name "*.java")
```

#### Execute

Fichier src (remplacer `pkg` par le nom du package (model, view ou controller), `TOTO` par le nom de la classe)
```bash
java -cp target/classes pkg.TOTO
```

Fichier test (remplacer `TOTO` par le nom de la classe)

```bash
java -cp target/classes:target/test-classes tests.TOTO
```
