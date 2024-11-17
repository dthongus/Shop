// Modul mit 8 Funktion die mit der Datenbank agieren
// addAccount() -> Account erstellen
// addProducts() -> Produkt hinzufügen
// delProduct() -> Produkt löschen
// orderProduct() -> Produktbestand erhöhen
// showAccounts() -> Accountliste anzeigen
// showProdukts() -> Produktliste anzeigen
// addToCard() -> Produkt zum Warenkorb hinzufügen
// showCard() -> Zeigt den aktuellen Warenkorb an


// Importieren vom SQL Klassen/Bibliotheken/Frameworks um bestimmte Funktionen zu erhalten
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException


// Verbindung zur Datenbank herstellen
val url = "jdbc:sqlite:Database.db"
val connection = DriverManager.getConnection(url)
val statement = connection.createStatement()


// Funktion zum Hinzufügen eines Accounts
fun addAccount() {
    println("Bitte Namen eingeben:")
    val userNameInput = readln()
    println("Bitte Passwort eingeben:")
    val userPasswordInput = readln()
    println("Bitte Passwort wiederholen:")
    val userRePasswordInput = readln()
    println("Bitte Alter eingeben:")

    val userAgeInput = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${addAccount()}")
        validInput
    } ?: return  // Bei validInput = Null dann soll das Programm beendet werden, ansonst problem mit dem Operator "<" in der
    // folgenden if Anweisung!

    // Falls der neue Nutzer unter 12 Jahre ist, dann abbruch der Funktion und Meldung zurückgeben
    if (userAgeInput < 12) {
        println("\nAchtung! Du bist noch unter 12 Jahre alt! Wir können leider keinen Account für dich erstellen!")
        addAccount()
    // Falls Nutzer über 12 Jahre dann weiter im Programm
    } else {
        // Abfrage bzgl. Zahlungsmethode
        println("Bitte Zahlungsmethode auswählen:\n[1] Paypal\n[2] Überweisung\n[..] Bei Falscheingabe wird kein Zahlungsmittel gespeichert ")
        val userPaymentMethodeInput = readln().toIntOrNull()
        // Umwandlung der Eingabe in ein gültiges Format für die Datenbankspeicherung
        val savePayment = when (userPaymentMethodeInput) {
            1 -> "Paypal"
            2 -> "Überweisung"
            else -> ""
        }
        // Prüft ob die Passworteingaben indentisch sind, ansonsten speichere die Daten in die Datenbank ab
        // Wenn Passwörter nicht übereinstimmen
        if (userPasswordInput != userRePasswordInput) {
            println("Die Passwörter stimmen nicht überein! Bitte erneut versuchen")
            addAccount()
        // Wenn alle Eingaben ok, dann die neuen Accountdaten in die Datenbank speichern
        } else {
            val insertSQL =
                "INSERT INTO accounts (userName, userPassword, userAge, paymentMethod, allocation) VALUES (?, ?, ?, ?, ?)"
            val preparedStatement: PreparedStatement = connection.prepareStatement(insertSQL)
            preparedStatement.setString(1, userNameInput)
            preparedStatement.setString(2, userPasswordInput)
            preparedStatement.setInt(3, userAgeInput)
            preparedStatement.setString(4, savePayment)
            preparedStatement.setInt(5, 1) // Festgelegte Zuweisung "1" für ein Kundenkonto (Adminkonto = 2)

            preparedStatement.executeUpdate()
            preparedStatement.close()

            println("\nAccount wurde erstellt! Herzlich Willkommen $userNameInput")

            // Hauptmenü aufrufen
            mainMenue()
        }
    }
}


// Funktion um ein Produkt zum Bestand hinzuzufügen
fun addProducts() {
    val insertProducts = "INSERT INTO products (product_id, subCategoryLink, name, price, amount, review) VALUES (?, ?, ?, ?, ?, ?)"
    val preparedStatement: PreparedStatement = connection.prepareStatement(insertProducts)
    val statement = connection.createStatement()

    // Menü Auswahl
    println("\nBitte Unterkategorie auswählen")
    println("[1] Gaming-PC\n[2] Business-PC\n[3] Workstations\n[4] Windows-Laptop\n[5] Macbook\n[6] Drucker\n[7] Monitore\n[8] Eingabegeräte")

    // Nutzereingabe auf Richtigkeit prüfen
    val categoryChoice = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${addProducts()}")
        validInput
    }

    // Liste um die Nutzereingabe einem Wert der Unterkategorie zuzuweisen.
    val choice = when(categoryChoice) {
        1 -> "Gaming-PC"
        2 -> "Business-PC"
        3 -> "Workstations"
        4 -> "Windows-Laptop"
        5 -> "Macbook"
        6 -> "Drucker"
        7 -> "Monitore"
        8 -> "Eingabegeräte"
        else -> {
            println("Keine gültige Eingabe!")
            return  // Abbruch der Funktion, wenn eine ungültige Auswahl getroffen wurde
        }
    }

    // Datenbank Abfrage um die höchste Produkt-ID zu ermitteln
    val getLastIdQuery = "SELECT product_id FROM products ORDER BY product_id DESC LIMIT 1"
    val resultSet = statement.executeQuery(getLastIdQuery)

    // Neue Produkt-ID erzeugen (höchste Produkt-ID +1 ansonsten neue Produkt-ID = 1)
    val newProductId = if (resultSet.next()) resultSet.getInt("product_id") + 1 else 1
    resultSet.close()

    // Produktbezeichnung für hinzuzufügendes Produkt abfragen
    println("Produktbezeichnung eingeben:")
    // Hier keine safeExecute Fehlerbehandlung notwendig, da ein String erwartet wird und somit alle Eingaben gültig sind.
    val productName = readln()

    // Preis für das hinzuzufügendes Produkt abfragen
    println("Preis eingeben:")
    // safeExecute notwendig, da nur Double Werte als Eingabe erlaubt sind
    val productPrice = safeExecute {
        val input = readln()
        val validInput = input?.toDoubleOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${addProducts()}")
        validInput
    }

    // Menge für das hinzuzufügendes Produkt abfragen
    println("Menge eingeben:")
    // safeExecute notwendig, da nur Double Werte als Eingabe erlaubt sind
    val productAmount = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${addProducts()}")
        validInput
    }
    
    // Setzen der Werte im PreparedStatement
    preparedStatement.setInt(1, newProductId) // Produkt-ID setzten
    preparedStatement.setString(2, choice) // Unterkategorie setzen für die Zugehörigkeit des neuen Produkts
    preparedStatement.setString(3, productName) // Produktbezeichnung Angabe
    preparedStatement.setDouble(4, productPrice) // Preisangabe
    preparedStatement.setInt(5, productAmount) // Mengenangabe
    preparedStatement.setString(6, "") // Hier kannst du eine Standardbewertung setzen, falls erforderlich

    // Daten in Datenbank speichern
    preparedStatement.executeUpdate()

    // Schließen der Statement-Objekte
    preparedStatement.close()
    statement.close()

    // Bei erfolgreicher Speicherung gib eine Rückmeldung
    println("Das Produkt: -$productName- mit dem Preis für $productPrice € und Bestand i.H.v. $productAmount wurde erfolgreich hinzugefügt!")

    // Admin Menü wieder aufrufen
    adminMenue()
}


// Funktion um ein Produkt vom Bestand zu löschen
fun delProduct() {
    // Bestandsliste anzeigen, damit der Nutzer aus dessen ein Produkt zur Bestandserhöhung auswählen kann
    showProdukts()

    println("### Produkt löschen ###")
    println("Bitte die Produkt-ID eingeben um das Produkt aus dem Bestand zu löschen:")

    // Nutzereingabe und Prüfung dessen
    val userInputID = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw java.lang.IllegalArgumentException("${errorMessage()} ${delProduct()}")
        validInput
    }

        // Verbindung zur Datenbank herstellen
        val url = "jdbc:sqlite:Database.db"
        val connection = DriverManager.getConnection(url)

        // Hier habe ich mal eine Try Catch Fehlerbehandlung für die Datenbank Operation eingebaut,
        // Falls bei der Ausführung der Datenbankoperation ein Fehler auftritt, z.B. durch fehlender
        // Datenbank. Es sollte durch die Verwendung einer SQL Lite Datenbank eigentlich unproblematisch sein, da
        // die Datenbank lokal gespeichert (embedded) und zugegriffen wird. Es ist also relevanter für server basierte
        // Datenbank notwendig, da z.B. Verbindungsprobleme entstehen können.
        // Aber zur Veranschaulichung solch eines Programmcodes habe ich es hier einmalig in meinem Quellcode implementiert.
        try {
            // Löschen der Produktzeile anhand der product_id
            val deleteQuery = "DELETE FROM products WHERE product_id = ?"
            val deleteStatement = connection.prepareStatement(deleteQuery)
            deleteStatement.setInt(1, userInputID)
            val rowsDeleted = deleteStatement.executeUpdate()

            // Bei erfolgreicher Löschung des Produkts gib eine Rückmeldung
            if (rowsDeleted > 0) {
                println("Erfolgreiche Löschung!")
            // Ansonsten gibt eine Fehlermeldung zurück
            } else {
                println("Produkt nicht gefunden!")
            }

        // Falls ein Problem bei der Programmausführung auftritt, dann führe das catch aus
        } catch (e: SQLException) {
            // Fehlerbehandlung, falls ein Fehler auftritt
            println("Fehler!")
        // Zu Ende des try catch soll das Admin Menü wieder aufgerufen werden
        } finally {
            adminMenue()
        }
}


// Funktion um den Bestand eines Produkts zu erhöhen
fun orderProduct() {
    // Bestandliste anzeigen
    showProdukts()

    println("\n### Produkt auffüllen")

    // Produkt-ID anhand der Produktliste eingeben um den Bestand für das Produkt zu erhöhen
    println("Bitte die Produkt-ID eingeben um Bestand zu erhöhen:")
    val userInputID = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${adminMenue()}")
        validInput
    }

    // Die Menge für die Bestandserhöhung eingeben
    println("Bitte die Menge zur Bestandserhöhung eingeben:")
    val userInputAmount = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${adminMenue()}")
        validInput
    }

    // Daten in Datenbank speichern
    val query = "SELECT amount FROM products WHERE product_id = ?"
    val statement = connection.prepareStatement(query)
    statement.setInt(1, userInputID)
    val resultSet = statement.executeQuery()

    if (resultSet.next()) {
        // Aktuellen Bestand holen
        val currentAmount = resultSet.getInt("amount")

        // Bestand um den eingegebenen Wert erhöhen
        val newAmount = currentAmount + userInputAmount

        // Speichern des neuen (aktuellen) Bestands
        val updateQuery = "UPDATE products SET amount = ? WHERE product_id = ?"
        val updateStatement = connection.prepareStatement(updateQuery)
        updateStatement.setInt(1, newAmount) // Ändert den Bestandswert Wert
        updateStatement.setInt(2, userInputID) // Bedingung: Bestandserhöhung nur zum ausgewählten Produkt

        // Ausführen der Datenbankoperation
        updateStatement.executeUpdate()

        // Bestätigung der Bestandserhöhung
        println("Der Bestand wurde auf $newAmount erhöht!")

        // Falls die Produkt-ID nicht in der Datenbank vorliegend ist
    } else {
        println("Produkt mit ID $userInputID nicht gefunden.")
    }
    resultSet.close()
    statement.close()

    // Admin Menü wieder aufrufen
    adminMenue()
}


// Funktion zum Anzeigen aller vorhandenen Accounts in der Datenbank
fun showAccounts() {
    println("\n### Accountliste ###")
    val selectSQL = "SELECT * FROM accounts"
    val resultSet = connection.createStatement().executeQuery(selectSQL)

    // Schleife um alle Daten aus der Accounttabelle der Datenbank zu holen und auszugeben
    while (resultSet.next()) {
        val userName = resultSet.getString("userName")
        val password = resultSet.getString("userPassword")
        val userAge = resultSet.getString("userAge")
        val paymentMethod = resultSet.getString("paymentMethod")

        // Ausgabe der Accountdaten
        println("Accountname: $userName, Passwort: $password, Alter: $userAge, Zahlungsmethode: $paymentMethod")
    }
    resultSet.close()

    // Admin Menü wieder aufrufen
    adminMenue()
}


// Funktion zur Anzeige der Produktliste mit Bestandsanzeige
// Hier könnte ich auch die klassische Listenansicht, die ich bei den anderen Funktionen verwendet habe verwenden.
// Jedoch möchte ich hier eine weitere Möglichkeit zur Ansicht einer Produktliste geben.
fun showProdukts() {
    // Datenbank Abfrage, um die Daten aus den Haupt- und Unterkategorien sowie den Produkten zu holen
    val query = """
        SELECT mc.name AS hauptkategorie, sc.name AS unterkategorie, p.product_id AS produktid, 
        p.name AS produktname, p.price AS price, p.amount AS amount, p.review AS review 
        FROM products p
        JOIN subCategory sc ON p.subCategoryLink = sc.name
        JOIN mainCategory mc ON sc.mainCategoryLink = mc.name
    """

    val resultSet = statement.executeQuery(query)

    // Alle Daten holen und in der Ansicht ausgeben
    while (resultSet.next()) {
        val mainCategoryProdukt = resultSet.getString("hauptkategorie")
        val subCategoryProdukt = resultSet.getString("unterkategorie")

        // Daten
        val produktident = resultSet.getString("produktid")
        val productname = resultSet.getString("produktname")
        val pricesing = resultSet.getString("price")
        val inventory = resultSet.getInt("amount")
        val reviews = resultSet.getString("review")

        // Ausgabe der Ansicht
        println("Produkt-ID: $produktident") // Produkt-ID ausgeben
        println("### Kategorie ###") // Überschrift Kategorie
        println("Hauptkategorie: $mainCategoryProdukt") // Hauptkategorie ausgeben
        println("Unterkategorie: $subCategoryProdukt") // Unterkategorie ausgeben
        println("    ### Produktdaten ###") // Überschrift Produkt
        println("    Bezeichnung: $productname") // Produktbezeichnung ausgeben
        println("    Preis: $pricesing") // Preis ausgeben
        println("    Bestand: $inventory") // Bestand ausgeben
        println("    Rezension: $reviews") // Produktbewertung ausgeben
        println("---------------------------------------") // Trennlinie für die bessere Übersichtlichkeit
    }
}


// Funktion um Produkt zum Warenkorb hinzuzufügen
fun addToCard(product_id: Int) {
    val selectQuery = "SELECT name, price FROM products WHERE product_id = ?"
    val selectStatement = connection?.prepareStatement(selectQuery)
    selectStatement?.setInt(1, product_id)
    val resultSet = selectStatement?.executeQuery()

    // Prüfen ob Produkt vorhanden ist
    if (resultSet != null && resultSet.next()) {
        // Wenn vorhanden, dann Produktbezeichnung und Preis Daten holen
        val itemName = resultSet.getString("name")
        val itemPrice = resultSet.getDouble("price")

        // Produktbezeichnung und Preis in den Warenkorb speichern
        val insertQuery = "INSERT INTO shoppingCard (item, price) VALUES (?, ?)"
        val insertStatement = connection.prepareStatement(insertQuery)
        insertStatement.setString(1, itemName)
        insertStatement.setDouble(2, itemPrice)
        insertStatement.executeUpdate()

        // Bei erfolgreichem Hinzufügen des Produkt in den Warenkorb, gib eine Meldung zurück
        println("Produkt '$itemName' zum Warenkorb hinzugefügt.")

        insertStatement.close()

        // Aufrufen des Produkt Menüs
        produktList()

        // Wenn kein Produkt von der Nutzereingabe vorhanden ist
    } else {
        println("Produkt mit ID $product_id nicht gefunden.")
    }
    resultSet?.close()
    selectStatement?.close()
}


// Funktion um den Warenkorb einzusehen
fun showCard() {
        // Daten aus der Warenkorb Tabelle der Datenbank holen
        val query = "SELECT * FROM shoppingCard"
        val statement = connection?.createStatement()
        val resultSet = statement?.executeQuery(query)

        // Variablen für die Daten des Nutzers, Zahlungsmethode und Gesamtpreis erzeugen
        var user: String? = null
        var paymentMethod: String? = null
        var totalPrice = 0.0

        println("\n### Warenkorb ###")

        // Liste für Warenkorb erstellen
        val items = mutableListOf<String>()

        // Prüfen ob sich der Datenbank cursor auf der ersten Zeile befindet.
        // Diese Bedingung verhindert, dass die Daten aus der ersten Zeile für das Holen eines Produkts verhindert wird


        // Variable zur Prüfung, ob sich der Datenbank cursor sich in der ersten Zeile befindet.
        // True = Ja, False = Nein. Bei True dann speichere keine Daten in die Liste. Bei False speichere die Daten ab
        var firstLine = true  // Flag, um zu überprüfen, ob es sich um die erste Zeile handelt

        // Schleife um die Daten des Produkts in die Liste einzufügen, oder wenn der cursor = true dann überspringe
        while (resultSet?.next() == true) {

            // Daten des Nutzers und Zahlungsmethode holen (nur einmaliger Aufruf)
            if (user == null) {
                user = resultSet.getString("user") // Nutzername/Accountname in Variable speichern
                paymentMethod = resultSet.getString("paymentMethod") // // Zahlungsmethode in Variable speichern
            }

            // Hole die Daten für Artikel und Preis
            val item = resultSet.getString("item")
            val price = resultSet.getDouble("price")

            // Addiere den Preis zum Gesamtpreis
            totalPrice += price

            // Wenn sich der cursor sich nicht in der ersten Zeile befindet, dann speichere die Wert in die Liste
            if (!firstLine) {
                items.add("$item - $price €")
            }

            // Cursor auf false setzten, damit das Programm annimmt, dass sich der cursor nicht in der
            // ersten Zeile befindet und die while Schleife alle weiteren Daten/Werte der vorhandenen Produkte
            // in die Liste speichert
            firstLine = false
        }

        // Ausgabe der User- und Zahlungsmethode (nur einmal)
        if (user != null && paymentMethod != null) {
            println("Hallo $user!")
            println("Deine voreingestellte Zahlungsmethode: $paymentMethod")
        }

        // Daten zum Produkt werden ausgegeben
        if (items.isNotEmpty()) {
            println("\n### Produkte in deinem Warenkorb ###")
            // Wiederholt die Ausgabe der Produkte
            items.forEach { println(it) }
        } else {
            println("Keine Produkte im Warenkorb.")
        }

        // Ausgabe des Gesamtpreises
        println("\nGesamtpreis: %.2f €".format(totalPrice))

        resultSet?.close()
        statement?.close()

        // Zurück zum Kunden Menü
        customerMenue()
}













