// Modul für allgemeine Fehlerbehandlung durch try / catch


// Try Catch Fehlerbehandlung für alle Arten von Fehler
fun <T> safeExecute(action: () -> T): T {
    // Bei gültiger Nutzereingabe:
    return try {
        // Aktion auszuführen
        action()
    } catch (e: Exception) {
        // hat keine Funktion, dient lediglich als Platzhalter um einen Rückgabewert an T zu senden.
        throw e
    }
}


// Fehlerbenachrichtigung ersetzt hierbei das catch im safeExecute, da ich durch den rekursiven Funktionsaufruf der jeweiligen
// Aufruffunktion nicht ins catch gelange. Durch den rekursiven Funktionsaufruf wird also das catch übersprungen.
fun errorMessage() {
    println(" ****** Fehler: Ungültige Eingabe! ******")
}

