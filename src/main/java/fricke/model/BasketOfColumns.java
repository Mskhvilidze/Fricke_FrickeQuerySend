package fricke.model;

import lombok.Getter;

@Getter
public class BasketOfColumns {

    private String Land;
    private String Mandant;
    private String Datum;
    private String Newsletter_ID;
    private String Artikelnummer;
    private String QTY_Aktion;
    private String SalesVolume_Aktion;
    private String QTY_Comparison;
    private String SalesVolume_Comparison;

    public BasketOfColumns(String Land, String Mandant, String Datum, String Newsletter_ID) {
        this.Land = Land;
        this.Mandant = Mandant;
        this.Datum = Datum;
        this.Newsletter_ID = Newsletter_ID;
    }

    public void setBasketOfColumns(String Artikelnummer, String QTY_Aktion, String SalesVolume_Aktion, String QTY_Comparison, String SalesVolume_Comparison) {
        this.Artikelnummer = Artikelnummer;
        this.QTY_Aktion = QTY_Aktion;
        this.SalesVolume_Aktion = SalesVolume_Aktion;
        this.QTY_Comparison = QTY_Comparison;
        this.SalesVolume_Comparison = SalesVolume_Comparison;
    }
}
