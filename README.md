# Analyse de Partage de Vélos avec Apache Spark

## Introduction

Ce projet propose une analyse complète des données de location de vélos en utilisant Apache Spark et son API SQL. L'objectif principal est d'exploiter la puissance de Spark pour effectuer des analyses approfondies sur les comportements des utilisateurs, identifier les tendances temporelles et optimiser les opérations d'un système de partage de vélos.

Le projet utilise Java comme langage de programmation principal et s'appuie sur Spark SQL pour interroger et analyser les données de manière efficace. Cette solution permet de traiter de grandes quantités de données tout en maintenant des performances optimales grâce à l'architecture distribuée de Spark.

## Table des Matières

1. [Prérequis](#prérequis)
2. [Structure du Projet](#structure-du-projet)
3. [Configuration](#configuration)
4. [Dataset](#dataset)
5. [Installation et Exécution](#installation-et-exécution)
6. [Analyses Réalisées](#analyses-réalisées)
7. [Résultats](#résultats)
8. [Conclusion](#conclusion)

## Prérequis

Avant de commencer, assurez-vous d'avoir installé les éléments suivants :

- **Java JDK 8** ou supérieur
- **Apache Maven** 3.6 ou supérieur
- **Apache Spark 3.5.0**
- **Winutils** (pour les utilisateurs Windows)

### Configuration de Hadoop pour Windows

Si vous utilisez Windows, vous devez configurer Hadoop :

1. Téléchargez `winutils.exe` et `hadoop.dll`
2. Créez un dossier `C:\hadoop\bin`
3. Placez les fichiers téléchargés dans ce dossier
4. Configurez la variable d'environnement `HADOOP_HOME` pointant vers `C:\hadoop`

## Structure du Projet

```
SparkBikeAnalysis/
│
├── pom.xml
├── bike_sharing.csv
├── README.md
└── src/
    └── main/
        └── java/
            └── ma/
                └── enset/
                    └── spark/
                        └── BikeAnalysis.java
```

## Configuration

### Fichier pom.xml

Le fichier Maven contient les dépendances nécessaires pour le projet :

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>ma.enset</groupId>
    <artifactId>SparkBikeAnalysis</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <spark.version>3.5.0</spark.version>
    </properties>
    
    <dependencies>
        <!-- Spark Core -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.12</artifactId>
            <version>${spark.version}</version>
        </dependency>
        
        <!-- Spark SQL -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_2.12</artifactId>
            <version>${spark.version}</version>
        </dependency>
        
        <!-- Logger -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.36</version>
        </dependency>
    </dependencies>
</project>
```

## Dataset

### Fichier bike_sharing.csv

Le dataset contient les informations suivantes sur chaque location :

```csv
rental_id,user_id,age,gender,start_time,end_time,start_station,end_station,duration_minutes,price
1,101,25,M,2025-01-01 08:30:00,2025-01-01 09:10:00,Station A,Station B,40,5.0
2,102,35,F,2025-01-01 09:00:00,2025-01-01 09:20:00,Station B,Station C,20,3.0
3,103,45,M,2025-01-01 07:15:00,2025-01-01 08:00:00,Station A,Station A,45,6.0
```

### Description des Colonnes

- **rental_id** : Identifiant unique de la location
- **user_id** : Identifiant de l'utilisateur
- **age** : Age de l'utilisateur
- **gender** : Genre de l'utilisateur (M/F)
- **start_time** : Date et heure de début de location
- **end_time** : Date et heure de fin de location
- **start_station** : Station de départ
- **end_station** : Station d'arrivée
- **duration_minutes** : Durée de la location en minutes
- **price** : Prix de la location

## Installation et Exécution

### Étape 1 : Cloner ou Télécharger le Projet

Placez tous les fichiers dans un répertoire de votre choix.

### Étape 2 : Compiler le Projet

Ouvrez un terminal dans le répertoire du projet et exécutez :

```bash
mvn clean package
```

Cette commande va :
- Télécharger toutes les dépendances nécessaires
- Compiler le code Java
- Créer un fichier JAR exécutable

### Étape 3 : Exécuter l'Application

#### Option 1 : Exécution via IDE

Ouvrez le projet dans votre IDE préféré (IntelliJ IDEA, Eclipse, etc.) et exécutez la classe `BikeAnalysis.java`.

#### Option 2 : Exécution en ligne de commande

```bash
mvn exec:java -Dexec.mainClass="ma.enset.spark.BikeAnalysis"
```

#### Option 3 : Exécution avec Spark Submit

```bash
spark-submit --class ma.enset.spark.BikeAnalysis \
             --master local[*] \
             target/SparkBikeAnalysis-1.0-SNAPSHOT.jar
```

## Analyses Réalisées

Le programme effectue six sections d'analyse distinctes :

### Section 1 : Chargement et Exploration des Données

Cette section initialise Spark et charge le dataset CSV.

```java
Dataset<Row> df = spark.read()
        .option("header", "true")
        .option("inferSchema", "true")
        .csv("bike_sharing.csv");
```

**Opérations effectuées :**
- Affichage du schéma du dataset
- Visualisation des 5 premières lignes
- Comptage du nombre total de locations

### Section 2 : Création d'une Vue Temporaire

```java
df.createOrReplaceTempView("bike_rentals_view");
```

Cette vue permet d'exécuter des requêtes SQL sur le DataFrame.

### Section 3 : Requêtes SQL de Base

**3.1 Filtrage des locations de plus de 30 minutes**
```sql
SELECT * FROM bike_rentals_view WHERE duration_minutes > 30
```

**3.2 Locations démarrant à Station A**
```sql
SELECT * FROM bike_rentals_view WHERE start_station = 'Station A'
```

**3.3 Calcul du revenu total**
```sql
SELECT SUM(price) as total_revenue FROM bike_rentals_view
```

### Section 4 : Requêtes d'Agrégation

**4.1 Nombre de locations par station**
```sql
SELECT start_station, COUNT(*) as count 
FROM bike_rentals_view 
GROUP BY start_station
```

**4.2 Durée moyenne par station**
```sql
SELECT start_station, AVG(duration_minutes) as avg_duration 
FROM bike_rentals_view 
GROUP BY start_station
```

**4.3 Station avec le plus de locations**
```sql
SELECT start_station, COUNT(*) as count 
FROM bike_rentals_view 
GROUP BY start_station 
ORDER BY count DESC 
LIMIT 1
```

### Section 5 : Analyse Temporelle

**5.2 Identification des heures de pointe**
```sql
SELECT hour(to_timestamp(start_time)) as hour, COUNT(*) as count 
FROM bike_rentals_view 
GROUP BY hour 
ORDER BY count DESC
```

**5.3 Station la plus populaire en matinée (7h-12h)**
```sql
SELECT start_station, COUNT(*) as count 
FROM bike_rentals_view 
WHERE hour(to_timestamp(start_time)) BETWEEN 7 AND 12 
GROUP BY start_station 
ORDER BY count DESC 
LIMIT 1
```

### Section 6 : Analyse du Comportement des Utilisateurs

**6.1 Age moyen des utilisateurs**
```sql
SELECT AVG(age) as average_age FROM bike_rentals_view
```

**6.2 Distribution par genre**
```sql
SELECT gender, COUNT(*) as count 
FROM bike_rentals_view 
GROUP BY gender
```

**6.3 Groupe d'âge le plus actif**
```sql
SELECT 
  CASE 
    WHEN age BETWEEN 18 AND 30 THEN '18-30' 
    WHEN age BETWEEN 31 AND 40 THEN '31-40' 
    WHEN age BETWEEN 41 AND 50 THEN '41-50' 
    ELSE '51+' 
  END as age_group, 
  COUNT(*) as count 
FROM bike_rentals_view 
GROUP BY age_group 
ORDER BY count DESC
```

## Résultats

### Section 1 : Exploration des Données

#### Image 1 : Schéma du Dataset et Premières Lignes

![Section 1 - Data Loading & Exploration](https://github.com/FatihaELHABTI/tp_spark_bike_analysis/blob/main/imgs/capture1.png)

Cette capture d'écran présente la première section du programme :
- **Le schéma complet du dataset** avec tous les types de données détectés automatiquement par Spark
- **Les 5 premières lignes** du fichier CSV, permettant de visualiser un échantillon représentatif
- **Le comptage total** : 3 locations dans le dataset

Le schéma du dataset révèle la structure suivante :

```
root
 |-- rental_id: integer (nullable = true)
 |-- user_id: integer (nullable = true)
 |-- age: integer (nullable = true)
 |-- gender: string (nullable = true)
 |-- start_time: timestamp (nullable = true)
 |-- end_time: timestamp (nullable = true)
 |-- start_station: string (nullable = true)
 |-- end_station: string (nullable = true)
 |-- duration_minutes: integer (nullable = true)
 |-- price: double (nullable = true)
```

**Observations importantes :**
- Spark a correctement inféré les types de données (integer, string, timestamp, double)
- Toutes les colonnes sont nullables, ce qui offre de la flexibilité pour les données manquantes
- Les timestamps sont correctement formatés pour permettre les analyses temporelles
- Les 3 enregistrements montrent une diversité de profils utilisateurs et de comportements

**Nombre total de locations :** 3

### Section 2 et 3 : Vue Temporaire et Requêtes SQL de Base

#### Image 2 : Création de la Vue et Premières Requêtes

![Section 2 & 3 - Temporary View and Basic Queries](https://github.com/FatihaELHABTI/tp_spark_bike_analysis/blob/main/imgs/capture2.png)

Cette capture montre plusieurs éléments clés :

**Section 2 - Création de la Vue Temporaire :**
- Confirmation de la création réussie de la vue temporaire `bike_rentals_view`
- Cette vue permet d'exécuter des requêtes SQL sur le DataFrame

**Section 3 - Requêtes SQL de Base :**

**3.1 Locations de plus de 30 minutes :**
- 2 locations correspondent à ce critère (durées : 40 min et 45 min)
- Rental ID 1 : 40 minutes, prix 5.0 (Station A → Station B)
- Rental ID 3 : 45 minutes, prix 6.0 (Station A → Station A, trajet circulaire)

**3.2 Locations démarrant à Station A :**
- 2 locations commencent à Station A
- Ces locations montrent une diversité dans les destinations

### Section 3 (suite) et Section 4 : Revenu et Agrégations

#### Image 3 : Calcul du Revenu Total et Analyses par Station

![Section 3 & 4 - Revenue Calculation and Aggregations](https://github.com/FatihaELHABTI/tp_spark_bike_analysis/blob/main/imgs/capture3.png)

Cette capture présente des métriques clés :

**3.3 Revenu total généré :** 14.0

Cette métrique représente le chiffre d'affaires total du système de partage de vélos (somme : 5.0 + 3.0 + 6.0).

**Section 4 - Requêtes d'Agrégation :**

**4.1 Nombre de locations par station de départ :**
- **Station B** : 1 location (33%)
- **Station A** : 2 locations (67%)

Station A domine clairement en tant que point de départ préféré des utilisateurs.

**4.2 Durée moyenne de location par station :**
- **Station B** : 20.0 minutes
- **Station A** : 42.5 minutes

Les locations démarrant à Station A ont une durée moyenne plus de deux fois supérieure à celles de Station B, ce qui pourrait indiquer des trajets plus longs ou des destinations plus éloignées.

### Section 4 (suite) et Section 5 : Station Populaire et Analyse Temporelle

#### Image 4 : Station la Plus Populaire et Heures de Pointe

![Section 4 & 5 - Most Popular Station and Time Analysis](https://github.com/FatihaELHABTI/tp_spark_bike_analysis/blob/main/imgs/capture4.png)

Cette capture montre la transition entre les analyses de station et temporelles :

**4.3 Station avec le plus grand nombre de locations :**
- **Station A** avec 2 locations

Cette requête utilise ORDER BY et LIMIT pour identifier la station dominante. Un avertissement concernant les métriques de collecte des déchets apparaît dans les logs (message rouge), ce qui est normal en environnement de développement Spark.

**Analyse de la Section 4 :** 
- Station A est la station la plus active du réseau
- Elle concentre 66% des locations totales
- La durée moyenne de location élevée (42.5 min) suggère des trajets plus longs

**Section 5 - Analyse Temporelle :**

**5.2 Nombre de vélos loués par heure (Identification des heures de pointe) :**
- **9h** : 1 location
- **8h** : 1 location
- **7h** : 1 location

Les résultats sont triés par ordre décroissant. Avec ce dataset, les trois heures matinales montrent une activité égale (1 location chacune).

### Section 5 (suite) et Section 6 : Matinée et Comportement Utilisateurs

#### Image 5 : Station Populaire en Matinée et Profil Démographique

![Section 5 & 6 - Morning Station and User Demographics](https://github.com/FatihaELHABTI/tp_spark_bike_analysis/blob/main/imgs/capture5.png)

Cette capture présente la fin de l'analyse temporelle et le début de l'analyse comportementale :

**5.3 Station la plus populaire durant la matinée (7h-12h) :**
- **Station A** avec 2 locations

Cette analyse filtre les locations effectuées entre 7h et 12h en utilisant la fonction `hour()` de Spark SQL. Station A confirme sa position dominante avec 100% des locations matinales.

**Analyse temporelle complète :**
- Les locations sont réparties uniformément sur la matinée
- Station A domine cette période
- Cela pourrait indiquer un flux de déplacements domicile-travail

**Section 6 - Analyse du Comportement des Utilisateurs :**

**6.1 Age moyen des utilisateurs :** 35.0 ans

L'âge moyen de 35 ans suggère que le service attire principalement des adultes en âge actif.

**6.2 Distribution par genre :**
- **Femmes (F)** : 1 utilisatrice (33%)
- **Hommes (M)** : 2 utilisateurs (67%)

La répartition montre une prédominance masculine qui pourrait nécessiter des actions marketing ciblées vers les femmes.

### Section 6 (suite) : Groupes d'Âge et Fin de l'Analyse

#### Image 6 : Distribution par Groupes d'Âge et Conclusion

![Section 6 - Age Groups and Analysis End](https://github.com/FatihaELHABTI/tp_spark_bike_analysis/blob/main/imgs/capture6.png)

Cette dernière capture présente les résultats finaux de l'analyse :

**6.3 Groupe d'âge louant le plus de vélos :**
- **18-30 ans** : 1 location (33%)
- **41-50 ans** : 1 location (33%)
- **31-40 ans** : 1 location (33%)

Cette analyse utilise une instruction CASE WHEN pour catégoriser les utilisateurs en groupes d'âge. Les résultats montrent une distribution parfaitement équilibrée entre les trois groupes d'âge adultes.

**Analyse comportementale complète :**
- Tous les groupes d'âge sont également représentés (33% chacun)
- Aucun groupe ne domine, suggérant un attrait universel du service
- L'absence d'utilisateurs dans la catégorie "51+" pourrait indiquer une opportunité de marché
- La prédominance masculine (67%) pourrait nécessiter des actions marketing ciblées vers les femmes

**Insights stratégiques :**
1. Le profil type est un homme de 35 ans
2. Le service attire équitablement les jeunes adultes, les trentenaires et les quadragénaires
3. Opportunité d'expansion vers les seniors (51+)
4. Potentiel d'amélioration de la parité hommes-femmes

**Fin de l'Analyse**

La dernière ligne confirme que le programme s'est terminé avec succès :
```
FIN DE L'ANALYSE
Process finished with exit code 0
```

Le code de sortie 0 indique qu'aucune erreur n'est survenue lors de l'exécution complète de l'analyse.

## Points Techniques Importants

### Configuration de Spark

```java
SparkSession spark = SparkSession.builder()
        .appName("Bike Sharing Analysis")
        .master("local[*]")
        .getOrCreate();
```

- **master("local[*]")** : Exécution en mode local avec tous les cœurs disponibles
- **appName** : Nom de l'application visible dans l'interface Spark UI

### Gestion des Logs

```java
System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
```

Cette configuration réduit la verbosité des logs pour se concentrer sur les résultats de l'analyse.

### Fonctions Spark SQL Utilisées

- **COUNT()** : Comptage des enregistrements
- **SUM()** : Somme des valeurs
- **AVG()** : Moyenne des valeurs
- **hour()** : Extraction de l'heure d'un timestamp
- **to_timestamp()** : Conversion en timestamp
- **CASE WHEN** : Création de catégories conditionnelles
- **GROUP BY** : Regroupement des données
- **ORDER BY** : Tri des résultats
- **LIMIT** : Limitation du nombre de résultats

## Conclusion

Ce projet démontre la puissance d'Apache Spark pour l'analyse de données de mobilité urbaine. Grâce à Spark SQL, nous avons pu :

1. **Analyser efficacement** les données de location de vélos
2. **Identifier des tendances** dans l'utilisation du système
3. **Comprendre le comportement** des utilisateurs
4. **Optimiser potentiellement** la distribution des vélos

### Points Clés de l'Analyse

- **Station A** est la station la plus populaire, particulièrement en matinée
- Les **utilisateurs** ont en moyenne 35 ans
- La **durée moyenne** de location varie significativement selon la station
- Le **revenu total** peut être facilement calculé et suivi

### Extensions Possibles

Ce projet peut être étendu de plusieurs manières :

1. **Analyse prédictive** : Prédire la demande future de vélos par station et par heure
2. **Optimisation** : Recommander le repositionnement des vélos entre stations
3. **Visualisation** : Créer des tableaux de bord interactifs avec les résultats
4. **Streaming** : Traiter les données en temps réel avec Spark Streaming
5. **Machine Learning** : Segmenter les utilisateurs selon leurs comportements
6. **Analyse géospatiale** : Cartographier les flux de déplacement

### Avantages de l'Utilisation de Spark

- **Performance** : Traitement rapide grâce au calcul distribué
- **Scalabilité** : Capable de traiter des datasets de plusieurs téraoctets
- **Flexibilité** : Support de SQL, Java, Scala, Python et R
- **Écosystème riche** : Intégration avec Hadoop, Kafka, et autres outils Big Data

Ce projet constitue une base solide pour tout système d'analyse de données de mobilité partagée et peut être facilement adapté à d'autres contextes similaires tels que le covoiturage, les trottinettes électriques ou les véhicules en libre-service.
