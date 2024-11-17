// Modul zur Erstellung der Datenbank und die Befüllung mit Daten
// Tabelle tableAccounts -> Accountdaten/Logindaten
// Tabelle tableCard -> Warenkorb
// Tabelle tableMainCategory -> Hauptkategoriedaten
// Tabelle tableSubCategory -> Unterkategoriedaten
// Tabelle tableProducts -> Produktdaten


import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.io.File


// Funktion zur Erstellung der Datenbank und hinzufügen von Produkten
fun createDatabase() {
    // Datenbank Namen vergeben
    val dbFile = File("Database.db")

    // Prüfen ob, bereits eine Datenbank vorhanden ist um diese zu löschen (wurde hinzugefügt, um eine saubere Datenbank während der
    // testphase zu haben, kann aber auch entfernt werden.
    if (dbFile.exists()) { dbFile.delete() }

    // Ort zur Speicherung der Datenbank im Projektordner
    val url = "jdbc:sqlite:Database.db"

    // Verbindung zur SQLite-Datenbank herstellen
    val connection: Connection = DriverManager.getConnection(url)
    connection.autoCommit = true
    val statement = connection.createStatement()

    // Tabelle für Accounts erstellen
    val tableAccounts = """
        CREATE TABLE IF NOT EXISTS accounts (
            userName TEXT NOT NULL,
            userPassword TEXT NOT NULL,
            userAge INT,
            paymentMethod TEXT,
            allocation INT
        );
    """
    // Tabelle für den Warenkorb erstellen
    val tableCard = """
        CREATE TABLE IF NOT EXISTS shoppingCard (
            user TEXT,
            paymentMethod TEXT,
            item TEXT,
            price DOUBLE
        );
    """
    // Tabelle für die Hauptkategorie erstellen
    val tableMainCategory = """
        CREATE TABLE IF NOT EXISTS mainCategory (
            name TEXT NOT NULL
        );
    """

    // Tabelle für die Unterkategorie erstellen
    val tableSubCategory = """
        CREATE TABLE IF NOT EXISTS subCategory (
            mainCategoryLink TEXT NOT NULL,
            name TEXT NOT NULL
        );
    """
    // Tabelle für die Produkte erstellen
    val tableProducts = """
        CREATE TABLE IF NOT EXISTS products (
            product_id INTEGER,
            subCategoryLink TEXT NOT NULL,
            name TEXT NOT NULL,
            price DOUBLE,
            amount INTEGER,
            review TEXT
        );
    """

    // Befehle um die Tabellen zu erstellen
    statement.executeUpdate(tableAccounts)
    statement.executeUpdate(tableCard)
    statement.executeUpdate(tableMainCategory)
    statement.executeUpdate(tableSubCategory)
    statement.executeUpdate(tableProducts)


// Ab hier werden die Tabellen mit Daten/Werte befüllt ------------------------------------------------------------

    // Erstellen eines Kunden- und Adminkonto
    val insertAccount = "INSERT INTO accounts (userName, userPassword, userAge, paymentMethod, allocation) VALUES (?, ?, ?, ?, ?)"
    val preparedStatement: PreparedStatement = connection.prepareStatement(insertAccount)
    // Kunden Account
    preparedStatement.setString(1, "Max")
    preparedStatement.setString(2, "12345")
    preparedStatement.setInt(3, 26)
    preparedStatement.setString(4, "Paypal")
    preparedStatement.setInt(5, 1)
    preparedStatement.executeUpdate()
    // Admin Account
    preparedStatement.setString(1, "Admin")
    preparedStatement.setString(2, "12345")
    preparedStatement.setInt(3, 0)
    preparedStatement.setString(4, "")
    preparedStatement.setInt(5, 2)
    preparedStatement.executeUpdate()

    // Daten in Hauptkategorietabelle einfügen
    val insertMainCategory = "INSERT INTO mainCategory (name) VALUES (?)"
    val preparedStatement2: PreparedStatement = connection.prepareStatement(insertMainCategory)
    preparedStatement2.setString(1, "Desktop-PC")
    preparedStatement2.executeUpdate()
    preparedStatement2.setString(1, "Laptop")
    preparedStatement2.executeUpdate()
    preparedStatement2.setString(1, "Zubehör")
    preparedStatement2.executeUpdate()

    // Daten in Unterkategorietabelle einfügen
    val insertSubCategory = "INSERT INTO subCategory (mainCategoryLink, name) VALUES (?, ?)"
    val preparedStatement3: PreparedStatement = connection.prepareStatement(insertSubCategory)
    // Unterkategorie Desktop-PC
    preparedStatement3.setString(1, "Desktop-PC")
    preparedStatement3.setString(2, "Gaming-PC")
    preparedStatement3.executeUpdate()
    preparedStatement3.setString(1, "Desktop-PC")
    preparedStatement3.setString(2, "Business-PC")
    preparedStatement3.executeUpdate()
    preparedStatement3.setString(1, "Desktop-PC")
    preparedStatement3.setString(2, "Workstations")
    preparedStatement3.executeUpdate()
    // Unterkategorie Laptop
    preparedStatement3.setString(1, "Laptop")
    preparedStatement3.setString(2, "Windows")
    preparedStatement3.executeUpdate()
    preparedStatement3.setString(1, "Laptop")
    preparedStatement3.setString(2, "Macbook")
    preparedStatement3.executeUpdate()
    // Unterkategorie Zubehör
    preparedStatement3.setString(1, "Zubehör")
    preparedStatement3.setString(2, "Drucker")
    preparedStatement3.executeUpdate()
    preparedStatement3.setString(1, "Zubehör")
    preparedStatement3.setString(2, "Monitore")
    preparedStatement3.executeUpdate()
    preparedStatement3.setString(1, "Zubehör")
    preparedStatement3.setString(2, "Eingabegeräte")
    preparedStatement3.executeUpdate()

    // Daten in Produkttabelle einfügen
    val insertProducts = "INSERT INTO products (product_id, subCategoryLink, name, price, amount, review) VALUES (?, ?, ?, ?, ?, ?)"
    val preparedStatement4: PreparedStatement = connection.prepareStatement(insertProducts)
    // 1. Produkte in Gaming-PC
    preparedStatement4.setInt(1, 1)
    preparedStatement4.setString(2, "Gaming-PC")
    preparedStatement4.setString(3, "Asus TUF Gaming X12")
    preparedStatement4.setDouble(4, 1499.44)
    preparedStatement4.setInt(5, 56)
    preparedStatement4.setString(6, "Günstig aber Gut!")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Gaming-PC
    preparedStatement4.setInt(1, 2)
    preparedStatement4.setString(2, "Gaming-PC")
    preparedStatement4.setString(3, "DELL Alienware V50")
    preparedStatement4.setDouble(4, 1999.98)
    preparedStatement4.setInt(5, 12)
    preparedStatement4.setString(6, "Mehr Leistung. hoher Preis")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Business-PC
    preparedStatement4.setInt(1, 3)
    preparedStatement4.setString(2, "Business-PC")
    preparedStatement4.setString(3, "HP EliteDesk 880")
    preparedStatement4.setDouble(4, 999.99)
    preparedStatement4.setInt(5, 60)
    preparedStatement4.setString(6, "")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Business-PC
    preparedStatement4.setInt(1, 4)
    preparedStatement4.setString(2, "Business-PC")
    preparedStatement4.setString(3, "DELL Optiplex 7790")
    preparedStatement4.setDouble(4, 1199.56)
    preparedStatement4.setInt(5, 19)
    preparedStatement4.setString(6, "Ausdauernd und kompakt")
    preparedStatement4.executeUpdate()
    // 3. Produkte in Business-PC
    preparedStatement4.setInt(1, 5)
    preparedStatement4.setString(2, "Business-PC")
    preparedStatement4.setString(3, "Lenovo ThinkCentre Mini")
    preparedStatement4.setDouble(4, 899.89)
    preparedStatement4.setInt(5, 32)
    preparedStatement4.setString(6, "Sehr klein und leise")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Workstation
    preparedStatement4.setInt(1, 6)
    preparedStatement4.setString(2, "Workstation")
    preparedStatement4.setString(3, "HP Z23 Workstation")
    preparedStatement4.setDouble(4, 2198.99)
    preparedStatement4.setInt(5, 10)
    preparedStatement4.setString(6, "")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Workstation
    preparedStatement4.setInt(1, 7)
    preparedStatement4.setString(2, "Workstation")
    preparedStatement4.setString(3, "DELL Precision W55")
    preparedStatement4.setDouble(4, 3198.78)
    preparedStatement4.setInt(5, 3)
    preparedStatement4.setString(6, "")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Windows-Laptop
    preparedStatement4.setInt(1, 8)
    preparedStatement4.setString(2, "Windows")
    preparedStatement4.setString(3, "Samsung Galaxy Book")
    preparedStatement4.setDouble(4, 1580.63)
    preparedStatement4.setInt(5, 41)
    preparedStatement4.setString(6, "Sehr dünn und leicht")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Windows-Laptop
    preparedStatement4.setInt(1, 9)
    preparedStatement4.setString(2, "Windows")
    preparedStatement4.setString(3, "Medion MD5568")
    preparedStatement4.setDouble(4, 699.98)
    preparedStatement4.setInt(5, 33)
    preparedStatement4.setString(6, "")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Macbook
    preparedStatement4.setInt(1, 10)
    preparedStatement4.setString(2, "Macbook")
    preparedStatement4.setString(3, "Macbook Air 13,3")
    preparedStatement4.setDouble(4, 1199.99)
    preparedStatement4.setInt(5, 20)
    preparedStatement4.setString(6, "Ultra kompakt")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Macbook
    preparedStatement4.setInt(1, 11)
    preparedStatement4.setString(2, "Macbook")
    preparedStatement4.setString(3, "Macbook Pro 14")
    preparedStatement4.setDouble(4, 1899.00)
    preparedStatement4.setInt(5, 26)
    preparedStatement4.setString(6, "")
    preparedStatement4.executeUpdate()
    // 3. Produkte in Macbook
    preparedStatement4.setInt(1, 12)
    preparedStatement4.setString(2, "Macbook")
    preparedStatement4.setString(3, "Macbook Pro 16")
    preparedStatement4.setDouble(4, 3599.00)
    preparedStatement4.setInt(5, 15)
    preparedStatement4.setString(6, "")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Drucker
    preparedStatement4.setInt(1, 13)
    preparedStatement4.setString(2, "Drucker")
    preparedStatement4.setString(3, "HP Laserjet HJ10")
    preparedStatement4.setDouble(4, 299.59)
    preparedStatement4.setInt(5, 75)
    preparedStatement4.setString(6, "Schnell und preiswert")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Drucker
    preparedStatement4.setInt(1, 14)
    preparedStatement4.setString(2, "Drucker")
    preparedStatement4.setString(3, "Canon Pixma L56")
    preparedStatement4.setDouble(4, 499.00)
    preparedStatement4.setInt(5, 15)
    preparedStatement4.setString(6, "Sehr Günstig aber Qualität ungenügend")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Monitore
    preparedStatement4.setInt(1, 15)
    preparedStatement4.setString(2, "Monitore")
    preparedStatement4.setString(3, "Dell Ultrasharp x89")
    preparedStatement4.setDouble(4, 1120.00)
    preparedStatement4.setInt(5, 75)
    preparedStatement4.setString(6, "Preis/Leistungssieger")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Monitore
    preparedStatement4.setInt(1, 16)
    preparedStatement4.setString(2, "Monitore")
    preparedStatement4.setString(3, "Benq Desk")
    preparedStatement4.setDouble(4, 300.00)
    preparedStatement4.setInt(5, 75)
    preparedStatement4.setString(6, "Ideal fürs Büro")
    preparedStatement4.executeUpdate()
    // 1. Produkte in Eingabegeräte
    preparedStatement4.setInt(1, 17)
    preparedStatement4.setString(2, "Eingabegeräte")
    preparedStatement4.setString(3, "Logitech MX Master")
    preparedStatement4.setDouble(4, 189.99)
    preparedStatement4.setInt(5, 75)
    preparedStatement4.setString(6, "Absolut genial")
    preparedStatement4.executeUpdate()
    // 2. Produkte in Eingabegeräte
    preparedStatement4.setInt(1, 18)
    preparedStatement4.setString(2, "Eingabegeräte")
    preparedStatement4.setString(3, "Rapoo Homekey")
    preparedStatement4.setDouble(4, 49.99)
    preparedStatement4.setInt(5, 75)
    preparedStatement4.setString(6, "Absolut genial")
    preparedStatement4.executeUpdate()

    // Verbindung schließen
    statement.close()
    connection.close()
}





