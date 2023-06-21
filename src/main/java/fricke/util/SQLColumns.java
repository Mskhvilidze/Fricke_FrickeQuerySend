package fricke.util;

public enum SQLColumns {
    SALES_ACTION ("Umsatz Aktion"),
    SALES_COMPARISON ("Umsatz Vergleich"),
    AMOUNT_ACTION ("Menge Aktion"),
    AMOUNT_COMPARISON ("Menge Vergleich"),
    COUNTRY ("NACOUN"),
    ARTICLE ("OLPRDC");

    String key;
    SQLColumns(String str) {
        key = str;
    }

    public String value() {
        return this.key;
    }
}
