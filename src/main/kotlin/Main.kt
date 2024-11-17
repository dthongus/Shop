// Main mit 4 Menüfunktionen
// main() -> Aufruf des Hauptmenüs
// mainMenue() -> Anzeige des Hauoptmenüs
// login() -> Anzeige des Login Menüs
// adminMenue() -> Anzeige des Admin Menüs
// customerMenue() -> Anzeige des Kunden Menüs


import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet


// Funktion zum Aufrufen des Hauptmenüs und erstellen einer Datenbank mit Datenbefüllung
fun main() {
    // Datenbank erstellen und mit Produktdaten für einen ersten Bestand befüllen
    createDatabase()
    // Aufruf des Hauptmenüs
    mainMenue()
}


// Aufruf des Hauptmenü
fun mainMenue() {
    println("\n### Willkommen im PC-Store© Germany ###")
    println("\n### Hauptmenü ###")
    println("[1] Anmelden\n[2] Account erstellen\n[3] Beenden")
    println("\nBitte Auswahl treffen:")

    val userinput = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${mainMenue()}")
        validInput
    }

    // Nutzereingabe wird verarbeitet und an die jeweilige Funktion übergeben
    when (userinput.toInt()) {
        1 -> login()  // Aufruf des Login Menüs
        2 -> addAccount() // Aufruf des Account Menüs zum Anlegen eines neuen Accounts
        3 -> return println("Auf wiedersehen und Danke für den Besuch!") // Beendet das Programm
        else -> {  // Falls Eingabe des Nutzers nicht in der Auswahlliste vorhanden ist
            println("Eingabe nicht erkannt! Bitte erneut eingeben:") // Fehlermeldung
            mainMenue()  // Ruft bei Fehleingabe des Nutzers die Funktion (rekursiv) erneut auf.
        }
    }
}


// Aufruf des Login Menü
fun login() {
    println("\n### Anmeldung ###")
    println("\nGeben Sie Ihren Benutzernamen ein:")
    val username = readln()
    println("Geben Sie Ihr Passwort ein:")
    val password = readln()

    // Verbindung zur Datenbank aufbauen
    val url = "jdbc:sqlite:Database.db"
    val connection = DriverManager.getConnection(url)

    // Warenkorb Daten leeren
    val deleteQuery = "DELETE FROM shoppingCard"
    val deleteStatement = connection.createStatement()
    deleteStatement.executeUpdate(deleteQuery)

    // Prüfen, ob die Eingaben in der Datenbank vorhanden sind
    val query = "SELECT * FROM accounts WHERE userName = ? AND userPassword = ?"
    val statement: PreparedStatement = connection.prepareStatement(query)
    statement.setString(1, username)
    statement.setString(2, password)

    // Ergebnis speichern
    val resultSet: ResultSet = statement.executeQuery()

    // IF-Abfrage, ob Daten vorhanden sind oder nicht
    if (resultSet.next()) {
        println("\nAnmeldung erfolgreich! Herzlich Willkommen $username")

        val checkAllocation = "SELECT allocation FROM accounts WHERE userName = ?"
        val allocationStatement = connection.prepareStatement(checkAllocation)
        allocationStatement.setString(1, username)
        val allocationResultSet = allocationStatement.executeQuery() // Name geändert zu allocationResultSet

        // Überprüfen, ob das ResultSet ein Ergebnis enthält
        if (allocationResultSet.next()) {
            // Abrufen des Wertes der Spalte "allocation" für den gefundenen Eintrag
            val allocation = allocationResultSet.getInt("allocation")

            // Prüfen, ob ein Kunde oder Admin sich eingeloggt hat
            if (allocation == 2) {
                // Falls sich ein Admin angemeldet hat
                deleteStatement.close()
                connection.close()
                adminMenue()  // Prüfen, ob diese Funktion im selben Modul definiert ist

            } else {  // Falls sich ein Kunde angemeldet hat
                // Einfügen der Nutzerdaten in den shoppingCard (Warenkorb)
                val insertProducts = "INSERT INTO shoppingCard (user, paymentMethod) VALUES (?, ?)"
                val productStatement: PreparedStatement = connection.prepareStatement(insertProducts)

                // Zahlungsmethode anhand des Benutzernamens auslesen und in Warenkorb einfügen
                val paymentMethodQuery = "SELECT paymentMethod FROM accounts WHERE userName = ?"
                val paymentMethodStatement: PreparedStatement = connection.prepareStatement(paymentMethodQuery)
                paymentMethodStatement.setString(1, username)

                // Ausführen der Abfrage und Abrufen des paymentMethod-Werts
                val paymentMethodResult: ResultSet = paymentMethodStatement.executeQuery()
                if (paymentMethodResult.next()) {
                    val paymentMethod = paymentMethodResult.getString("paymentMethod")

                    // Setzen der Werte im PreparedStatement
                    productStatement.setString(1, username)
                    productStatement.setString(2, paymentMethod)

                    // Ausführen der SQL-Insert-Anweisung, um die Daten in die Datenbank einzufügen
                    productStatement.executeUpdate()

                    // Schließen der Statement-Objekte und Verbindung
                    productStatement.close()
                    paymentMethodStatement.close()
                    deleteStatement.close()
                    connection.close()

                    customerMenue()  // Hier wird das Kundenmenü aufgerufen
                }
            }
        }
        allocationResultSet.close()  // ResultSet schließen
        allocationStatement.close()  // Statement schließen
    } else {
        println("\nBenutzername oder Passwort ist falsch! Bitte erneut versuchen!")
        resultSet.close()  // Schließen vor erneutem Login-Aufruf
        statement.close()  // Statement schließen
        connection.close()  // Verbindung schließen
        login()
    }
}


// Aufruf des Adminmenü
fun adminMenue() {
    println("\n### Admin Menü ###")
    println("[1] Produkt hinzufügen\n[2] Produkt entfernen\n[3] Produktbestand auffüllen\n" +
            "[4] Accountliste\n[5] Beenden")
    println("Bitte Auswahl treffen:")

    val adminInput = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${adminMenue()}")
        validInput
    }

    when (adminInput) {
        1 -> addProducts()
        2 -> delProduct()
        3 -> orderProduct()
        4 -> showAccounts()
        5 -> {
            println("Auf wiedersehen!")
            return
        }
        else -> println("Eingabe nicht erkannt! Bitte erneut versuchen: ${adminMenue()}")
    }
}


// Aufruf des Kundenmenü
fun customerMenue() {
    println("\n### Mein Kunden Menü ###")
    println("[1] Mein Warenkorb\n[2] zum Shop\n[3] Alle Produkte\n[4] Produkt bewerten")
    println("Bitte Auswahl eingeben:")

    val userInputCustom = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${customerMenue()}")
        validInput
    }

    when (userInputCustom) {
        1 -> showCard()
        2 -> produktList()
        3 -> totalProductList()
        4 -> addReviews()
        else -> {
            println("Eingabe nicht erkannt! Bitte erneut eingeben.")
            customerMenue() // Funktion rekursiv erneut aufrufen
        }
    }

    // Ressourcen schließen
    statement.close()
    connection.close()
}


