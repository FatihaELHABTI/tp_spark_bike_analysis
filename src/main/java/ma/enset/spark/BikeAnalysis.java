package ma.enset.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

public class BikeAnalysis {
    public static void main(String[] args) {
        // 1. Configurer le chemin Hadoop pour Windows
        System.setProperty("hadoop.home.dir", "C:\\hadoop");

        // 2. Configuration des logs
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");

        // Initialisation de la Session Spark
        SparkSession spark = SparkSession.builder()
                .appName("Bike Sharing Analysis")
                .master("local[*]")
                .getOrCreate();

        String separator = "---------------------------------------------------------------------------------";

        // =================================================================================
        // --- 1. Data Loading & Exploration ---
        // =================================================================================
        System.out.println("\n" + separator);
        System.out.println("=== SECTION 1: DATA LOADING & EXPLORATION ===");
        System.out.println(separator);

        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv("bike_sharing.csv");

        System.out.println("\n>>> 1.2 Schéma du Dataset :");
        df.printSchema();

        System.out.println("\n>>> 1.3 Affichage des 5 premières lignes :");
        df.show(5);

        long count = df.count();
        System.out.println("\n>>> 1.4 Nombre total de locations dans le dataset : " + count);


        // =================================================================================
        // --- 2. Create a Temporary View ---
        // =================================================================================
        System.out.println("\n" + separator);
        System.out.println("=== SECTION 2: CREATE A TEMPORARY VIEW ===");
        System.out.println(separator);

        df.createOrReplaceTempView("bike_rentals_view");
        System.out.println("Vue temporaire 'bike_rentals_view' créée avec succès.");


        // =================================================================================
        // --- 3. Basic SQL Queries ---
        // =================================================================================
        System.out.println("\n" + separator);
        System.out.println("=== SECTION 3: BASIC SQL QUERIES ===");
        System.out.println(separator);

        System.out.println("\n>>> 3.1 Liste des locations de plus de 30 minutes :");
        spark.sql("SELECT * FROM bike_rentals_view WHERE duration_minutes > 30").show();

        System.out.println("\n>>> 3.2 Liste des locations commençant à 'Station A' :");
        spark.sql("SELECT * FROM bike_rentals_view WHERE start_station = 'Station A'").show();

        System.out.println("\n>>> 3.3 Calcul du revenu total (Somme de la colonne price) :");
        spark.sql("SELECT SUM(price) as total_revenue FROM bike_rentals_view").show();


        // =================================================================================
        // --- 4. Aggregation Queries ---
        // =================================================================================
        System.out.println("\n" + separator);
        System.out.println("=== SECTION 4: AGGREGATION QUERIES ===");
        System.out.println(separator);

        System.out.println("\n>>> 4.1 Nombre de locations par station de départ :");
        spark.sql("SELECT start_station, COUNT(*) as count FROM bike_rentals_view GROUP BY start_station").show();

        System.out.println("\n>>> 4.2 Durée moyenne de location par station de départ :");
        spark.sql("SELECT start_station, AVG(duration_minutes) as avg_duration FROM bike_rentals_view GROUP BY start_station").show();

        System.out.println("\n>>> 4.3 Station avec le plus grand nombre de locations :");
        spark.sql("SELECT start_station, COUNT(*) as count FROM bike_rentals_view " +
                "GROUP BY start_station ORDER BY count DESC LIMIT 1").show();


        // =================================================================================
        // --- 5. Time-Based Analysis ---
        // =================================================================================
        System.out.println("\n" + separator);
        System.out.println("=== SECTION 5: TIME-BASED ANALYSIS ===");
        System.out.println(separator);

        System.out.println("\n>>> 5.2 Nombre de vélos loués par heure (Identification des heures de pointe) :");
        spark.sql("SELECT hour(to_timestamp(start_time)) as hour, COUNT(*) as count " +
                "FROM bike_rentals_view GROUP BY hour ORDER BY count DESC").show();

        System.out.println("\n>>> 5.3 Station la plus populaire durant la matinée (7h - 12h) :");
        spark.sql("SELECT start_station, COUNT(*) as count " +
                "FROM bike_rentals_view " +
                "WHERE hour(to_timestamp(start_time)) BETWEEN 7 AND 12 " +
                "GROUP BY start_station ORDER BY count DESC LIMIT 1").show();


        // =================================================================================
        // --- 6. User Behavior Analysis ---
        // =================================================================================
        System.out.println("\n" + separator);
        System.out.println("=== SECTION 6: USER BEHAVIOR ANALYSIS ===");
        System.out.println(separator);

        System.out.println("\n>>> 6.1 Âge moyen des utilisateurs :");
        spark.sql("SELECT AVG(age) as average_age FROM bike_rentals_view").show();

        System.out.println("\n>>> 6.2 Nombre d'utilisateurs par genre :");
        spark.sql("SELECT gender, COUNT(*) as count FROM bike_rentals_view GROUP BY gender").show();

        System.out.println("\n>>> 6.3 Groupe d'âge louant le plus de vélos :");
        spark.sql("SELECT " +
                "CASE " +
                "  WHEN age BETWEEN 18 AND 30 THEN '18-30' " +
                "  WHEN age BETWEEN 31 AND 40 THEN '31-40' " +
                "  WHEN age BETWEEN 41 AND 50 THEN '41-50' " +
                "  ELSE '51+' END as age_group, COUNT(*) as count " +
                "FROM bike_rentals_view " +
                "GROUP BY age_group ORDER BY count DESC").show();

        System.out.println("\n" + separator);
        System.out.println("FIN DE L'ANALYSE");
        System.out.println(separator);

        spark.stop();
    }
}