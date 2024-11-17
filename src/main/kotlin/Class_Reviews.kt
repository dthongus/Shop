// Modul mit einer Klasse für die Produktbewertung


import java.sql.DriverManager


// Klasse zur Speicherung der Nutzerbewertung zum ausgewählten Produkt
class productReviewManager {

    private val url = "jdbc:sqlite:Database.db"

    // Methode zur Speicherung der Bewertung
    fun submitReview(productId: Int, review: String) {
        DriverManager.getConnection(url).use { connection ->
            val updateReviewQuery = "UPDATE products SET review = ? WHERE product_id = ?"
            connection.prepareStatement(updateReviewQuery).use { preparedStatement ->
                preparedStatement.setString(1, review)
                preparedStatement.setInt(2, productId)
                val rowsAffected = preparedStatement.executeUpdate()

                // Falls die Bewertung in die Datenbank eingefügt wurde, gib eine Erfolgsmeldung
                if (rowsAffected > 0) {
                    println("\nIhre Bewertung: -$review- wurde erfolgreich gespeichert!")
                    println("Danke für Ihre Bewertung!")
                    customerMenue()
                } else {
                    // Falls kein Produkt anhand der product_id gefunden wurde, gib eine Fehlermeldung aus
                    println("Produkt mit der ID: $productId wurde nicht gefunden.")
                }
            }
        }
    }
}

