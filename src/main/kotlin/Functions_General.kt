// Modul mit 7 Funktionen für Auswahlmenüs
// addReviews() -> Funktion um eine Produktbewertung abzugeben
// produktList() ->  Zeigt die Produktliste anhand der Auswahl von Kategorien
// totalProductList() -> Zeigt Produktliste mit allen Produkten
// filterOptions() -> Filteroptionen für totalProductList()


fun main() { addReviews()}

// Funktion um Produktbewertung abzugeben
fun addReviews() {
    // Aufruf der Funktion zur Anzeige der Produktliste
    val allProducts = showProductList().getAllProducts()
    println("\n### Produktliste ###")
    showProductList().printProductList(allProducts)

    // Abfrage welches Produkt bewertet werden soll
    println("\nBitte die Produkt-ID eingeben für das zu bewertende Produkt:")
    val productId = safeExecute {
        val input = readln()
        val validInput = input.toIntOrNull()
            ?: throw IllegalArgumentException("${errorMessage()} ${addReviews()} ")
        validInput
    }

    // Bewertung eingeben
    println("Geben Sie die Bewertung ein:")
    val review = readln()

    // Aufruf der Klasse zur Speicherung der Bewertung
    val reviewManager = productReviewManager()
    reviewManager.submitReview(productId, review)
}


// Aufruf der Auswahlliste für Produkte durch Kategorieauswahl
fun produktList() {
    println("\n### Hauptkategorie ###")
    val mainCategories = MainCategory.getAllCategories()
    mainCategories.forEachIndexed { index, category -> println("${index + 1}. ${category.name}") }

    // Nutzereingabe für die Haupt-Kategorieauswahl
    // .minus(1) ist für die Indizierung notwendig, da die erste Zeile im Index = 0 ist und der Nutzer aber
    // eine 1 eingeben soll. Also Eingabe 1 = Index 0
    // Mann könnte auch den Nutzer das Menu mit 0 beginnend anzeigen. Wäre aber unschön,
    // da alle anderen Menülisten mit 1 beginnen
    print("\nWählen Sie anhand der Nummer eine Kategorie aus oder\nWarenkorb anzeigen die [4] ein:")
    val userInputMainCategory = safeExecute {
        val input = readln()
        val validInput = input?.toIntOrNull()?.minus(1)
            ?: throw IllegalArgumentException("${errorMessage()} ${produktList()}")
        validInput
    }
    // Bei Nutzereingabe für Warenkorbanzeige
    if (userInputMainCategory == 3) {
        showCard()
    // Andernfalls weiter im Programm um Unterkategorie auszuwählen und Produkliste anzuzeigen
    } else {
        // Überprüft ob, die ausgewählte Hauptkategorie in der Datenbank vorhanden ist und wenn ja, dann gib alle
        // Wert (Unterkategorien) aus dieser Hauptkategorie aus
        userInputMainCategory?.let { mainIndex ->
            if (mainIndex in mainCategories.indices) {
                val mainCategoryName = mainCategories[mainIndex].name

                // Ausgabe der Unterkategorien zur ausgewählten Hauptkategorie
                println("\n### Unterkategorie: $mainCategoryName ###")
                val subCategories = SubCategory.getSubcategoriesByMainCategory(mainCategoryName)
                subCategories.forEachIndexed { index, subCategory -> println("${index + 1}. ${subCategory.name}") }

                // Auswahl einer Unterkategorie des Nutzers
                print("\nWählen Sie eine Unterkategorie aus: ")
                // Auswahl der Unterkategorie
                val userInputSubCategory: Int? = safeExecute {
                    val input = readln()
                    input.toIntOrNull()?.minus(1)
                        ?: throw IllegalArgumentException("${errorMessage()} ${produktList()}")
                }

                // Ausgabe der Produkte in der ausgwählten Unterkategorie
                userInputSubCategory?.let { subIndex ->
                    if (subIndex in subCategories.indices) {
                        val subCategoryName = subCategories[subIndex].name

                        // Ausgabe der Produktliste
                        println("\n### Produkte ###")
                        val products = Productlist.getProductsBySubCategory(subCategoryName)
                        products.forEach { product ->
                            println("Produkt-ID: ${product.productId} | Artikel: ${product.name} | Preis: ${product.price} € | Kundenbewertung: ${product.review}")
                        }

                        // Auswahlmenü um ein Produkt in den Warenkorb abzulegen oder wieder zum Menü der Hauptkategorie zu gelangen
                        println("\nBitte geben Sie die Produkt-ID ein um den Artikel in den Warenkorb hinzuzufügen.")
                        println("Andernfalls geben Sie 0 ein um wieder zur Hauptkategorie zu gelangen:")

                        val userInputProduct = safeExecute {
                            val input = readln()
                            val validInput = input.toIntOrNull()
                                ?: throw IllegalArgumentException("${errorMessage()} ${produktList()}")
                            validInput
                        }

                        // Nutzereingabe prüfen und entspreechend code ausführen
                        // Zurück zur Hauptkategorie Ansicht
                        if (userInputProduct == 0) {
                            produktList()
                        // Funktion aufrufen, um das Produkt in den Warenkorb abzulegen
                        } else {
                            addToCard(userInputProduct)
                        }

                    // Falls der Nutzer eine Eingabe tätigt und kein Produkt zu seiner Eingabe vorhanden ist
                    } else {
                        println("\nBitte unterlasse die Falscheingabe! Der Code funktioniert\n:) :) :)\nEs gibt keine Unterkategorie zu der Eingabe!")
                        produktList()
                    }
                }
            // Falls der Nutzer eine Eingabe tätigt und keine Unterkategorie zu seiner Eingabe vorhanden ist
            } else {
                println("\nHmmm bitte genauer hinschauen! Es gibt keine Hauptkategorie zu deiner Eingabe!")
                produktList()
            }
        }
    }
}


// Aufruf der kompletten Produkte als Liste mit Sortierungsfunktionen
fun totalProductList() {
    val productManager = showProductList()

    // Alle Produkte abrufen
    val allProducts = productManager.getAllProducts()

    println("\n### Produktliste ###")
    // Ausgabe der Produktliste
    productManager.printProductList(allProducts)

    // Wiederholung der Menüfunktion, solange bis der Nutzer wieder ins Kundenmenü möchte
    while (true) {
        // Weitere Menü Funktionen
        println("\n### Menü ###")

        println("[1] Sortieren nach Produktbezeichnung\n[2] Sortieren nach Preis\n[3] Filteroptionen\n[4] Zurück zum Menü")
        // Hier Fehlerbehandlung!
        val userInputSorts = safeExecute {
            val input = readln()
            val validInput = input?.toIntOrNull()
                ?: throw IllegalArgumentException("${errorMessage()} ${totalProductList()}")
            validInput
        }

        // Funktionsaufrufe des Nutzers
        when (userInputSorts) {
            1 -> {  // Sortieren der Produktliste nach Produktbezeichnung alphabetisch
                println("\n### Produkte nach Name sortiert ###")
                val productsSortedByName = productManager.sortName(allProducts)
                productManager.printProductList(productsSortedByName)
            }

            2 -> {  // Sortieren der Produktliste nach Preis aufsteigend
                println("\n### Produkte nach Preis sortiert ###")
                val productsSortedByPrice = productManager.sortPrice(allProducts)
                productManager.printProductList(productsSortedByPrice)
            }

            3 -> { // Aufruf der Filterfunktion
                filterOptions(allProducts)
            }

            4 -> {
                customerMenue() // Zurück zum Hauptmenü
                break // Unterbricht die While Schleife
            }
            else -> println("\nEingabe nicht erkannt!")  // Wenn Nutzer keine gültige Eingabe getätigt hat
        }
    }
}


// Funktion zur Anzeige der Filteroptionen
fun filterOptions(products: List<Product>) {
    while (true) {
        println("\n### Filteroptionen ###")

        while (true) {
            println("\n### Filteroptionen ###")
            println("[1] Nach Produktbezeichnung filtern\n[2] Nach Preis filtern\n[3] Beenden")

            // Benutzereingabe für Filteroptionen (mit safeExecute)
            val userInputFilter = safeExecute {
                val input = readLine()?.toIntOrNull()
                    ?: throw IllegalArgumentException("${errorMessage()} ${filterOptions(products)}")
                input
            }

            when (userInputFilter) {
                1 -> {  // Filtern nach Name
                    println("\n### Filter nach Produktbezeichnung ###")
                    println("Produktbezeichnung eingeben:")
                    val name = readln()
                    val filteredByName = showProductList().filterProductname(products, name)
                    if (filteredByName.isEmpty()) {
                        println("Keine Produkte mit dem Namen '$name' vorhanden.")
                    } else {
                        println("\n### Produkte für: '$name' ###")
                        showProductList().printProductList(filteredByName)
                    }
                }

                2 -> {  // Filtern nach Preisbereich
                    println("\n### Filter nach Preis ###")

                    // Eingabe des minimalen Preises, Fehlerbehandlung mit safeExecute
                    val minPrice = safeExecute {
                        println("Preis minimum:")
                        val input = readLine()
                        val validPrice = input?.toDoubleOrNull()
                            //Falls ein Fehlerhafter Wert eingegeben wurde
                            ?: throw IllegalArgumentException("${errorMessage()} ${filterOptions(products)}")
                        // Gibt den Preis zurück, wenn Eingabe korrekt ist
                        validPrice
                    }
                    // Eingabe des minimalen Preises, Fehlerbehandlung mit safeExecute
                    val maxPrice = safeExecute {
                        println("Preis minimum:")
                        val input = readLine()
                        val validPrice = input?.toDoubleOrNull()
                            ?: throw IllegalArgumentException("${errorMessage()} ${filterOptions(products)}")
                        validPrice
                    }
                    // Filter nach Preisbereich anwenden und Ergebnis in einer Liste speichern
                    val filteredProducts = showProductList().filterPrice(products, minPrice, maxPrice)

                    // Ausgabe der gefilterten Produkte
                    if (filteredProducts.isEmpty()) {
                        println("Keine Produkte im Bereich von $minPrice € bis $maxPrice € gefunden.")
                    } else {
                        println("\n### Produkte im Bereich von $minPrice € bis $maxPrice € ###")
                        showProductList().printProductList(filteredProducts)
                    }
                }

                3 -> {
                    customerMenue()
                }

                else -> println("Eingabe nicht erkannt!")
            }
        }
    }
}
























