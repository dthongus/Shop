// Modul mit einer Klasse für die Erstellung einer Produktliste


import java.sql.Connection
import java.sql.DriverManager


// Klasse um die Produkliste anhand der ausgewählten Kategorien des Users aus dem Modul/Funktion Functions/produktList zu erstellen
class Productlist(val productId: Int, val name: String, val price: Double, val amount: Int, val review: String) {

    // Erstellen eine companion object damit die Methode "getProductsBySubCategory" dirket über den Klassennamen aufgerufen werden kann
    // und keine Instanz der Klasse benötigt (Kapselung)
    companion object {

        // Methode zur Erstellung der Produkliste anhand der Produktkategorie
        fun getProductsBySubCategory(subCategoryName: String): List<Productlist> {
            val products = mutableListOf<Productlist>()

            // Verbindung zur Datenbank anhand der Kategorie
            val connection: Connection = DriverManager.getConnection("jdbc:sqlite:Database.db")
            val query = "SELECT product_id, name, price, amount, review FROM products WHERE subCategoryLink = ?"
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, subCategoryName)

            // Durchlauf durch alle Zeilen der Tabelle um daten zu holen ...
            val resultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                val productIdList = resultSet.getInt("product_id")
                val nameList = resultSet.getString("name")
                val priceList = resultSet.getDouble("price")
                val amountList = resultSet.getInt("amount")
                val reviewList = resultSet.getString("review")

                // ... und in die Liste einfügen
                products.add(Productlist(productIdList, nameList, priceList, amountList, reviewList))
            }

            resultSet.close()
            preparedStatement.close()
            connection.close()

            return products
        }
    }
}

