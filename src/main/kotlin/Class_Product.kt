// Modul mit einer Daten-Klasse und Klasse für die Produktlistenanzeige
// Klasse beinhaltet 6 Funktionen
   // getAllProducts() -> Alle Produkte in Liste zurückgeben
   // printProductList() -> Ausgabe aller Produkte
   // sortName() -> Produktliste nach Produktbezeichnung sortieren
   // sortPrice() -> Produktliste nach Preis sortieren
   // filterProductname() -> Produkte nach Produktbezeichnung filtern
   // filterPrice() -> Produkte nach Preis filtern


import java.sql.DriverManager // Klasse zur Herstellung der Datenbankverbindung


// Datenklasse mit einem Objekt und deren Attribute mit Datentypdekleration
// Attribute dienen als Daten der Produktliste, welche an die untere Klasse "showProductList" vererbt werden
data class Product(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val review: String
)


// Klasse zur Anzeige einer gesamten Produktliste
class showProductList {

    private val url = "jdbc:sqlite:Database.db"

    // Methode um Daten aus der Datenbanktabelle "products" zu holen un in eine Liste zu speichern und zurückzugeben
    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()

        // Datenbank Verbindung herstellen
        DriverManager.getConnection(url).use { connection ->
            // Daten aus "product_id, name, price, amount, review" Spalten holen
            val query = "SELECT product_id, name, price, amount, review FROM products"
            connection.createStatement().use { statement ->
                val resultSet = statement.executeQuery(query)

                // Durchlauf durch alle Zeilen der Tabelle um daten zu holen ...
                while (resultSet.next()) {
                    val product = Product(
                        productId = resultSet.getInt("product_id"),
                        name = resultSet.getString("name"),
                        price = resultSet.getDouble("price"),
                        quantity = resultSet.getInt("amount"),
                        review = resultSet.getString("review")
                    )
                    // ... und in die Liste einfügen
                    products.add(product)
                }
            }
        }
        // Rückgabe der Liste
        return products
    }


    // Methode zur Ausgabe der (unsortierten) Produktliste wie in Datenbank vorliegend
    fun printProductList(products: List<Product>) {
        products.forEach { product ->
            println("Produkt-ID: ${product.productId} | Produktbezeichnung: ${product.name} | Preis: ${product.price} | Menge: ${product.quantity} | Bewertung: ${product.review ?: "Keine Bewertung"} ")
        }
    }


    // Sortiert die Liste nach dem Produktnamen
    fun sortName(products: List<Product>): List<Product> {
        return products.sortedBy { it.name }
    }


    // Sortiert die Liste nach dem Preis
    fun sortPrice(products: List<Product>): List<Product> {
        return products.sortedBy { it.price }
    }


    // Filtern nach Produktbezeichnung
    fun filterProductname(products: List<Product>, name: String): List<Product> {
        return products.filter { it.name.contains(name, ignoreCase = true) }
    }


    // Filtern nach Preisbereich
    fun filterPrice(products: List<Product>, minPrice: Double, maxPrice: Double): List<Product> {
        return products.filter { it.price in minPrice..maxPrice }
    }
}