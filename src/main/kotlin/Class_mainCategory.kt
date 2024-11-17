// Modul mit einer Klasse für die Erzeugung der Hauptkategorieliste


import java.sql.Connection // Bibliothek zur Interaktion zwischen Programm und Datenbank
import java.sql.DriverManager // Klasse zur Herstellung der Datenbankverbindung


// Klasse, die den Kategorienamen aufnimmt.
class MainCategory(val name: String) {

    companion object {

        // Funktion, die zugehörigen Daten aus der Datenbank in eine Liste speichert und zurückgibt
        fun getAllCategories(): List<MainCategory> {

            // Liste für Hauptkategorie erstellen
            val categories = mutableListOf<MainCategory>()

            // Datenbankverbindung herstellen
            val connection: Connection = DriverManager.getConnection("jdbc:sqlite:Database.db")
            val query = "SELECT name FROM mainCategory"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            // Holt Daten der Hauptkategorie, solange Daten in der Tabelle vorhanden sind und fügt diese in die Liste hinzu.
            while (resultSet.next()) {
                val name = resultSet.getString("name")
                categories.add(MainCategory(name))
            }
            // Datenbank schließen
            resultSet.close()
            statement.close()
            connection.close()

            // Rückgabe der Kategorieliste
            return categories
        }
    }
}
