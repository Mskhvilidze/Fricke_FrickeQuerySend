package fricke.util;

public class Client {

    public String getClient(String country) {
        String client;
        switch (country) {
            case "AT":
                client = "GA";
                break;
            case "CH":
            case "CH-":
                client = "GC";
                break;
            case "CZ":
                client = "GT";
                break;
            case "SK":
                client = "SL";
                break;
            case "DK":
                client = "GD";
                break;
            case "EE":
            case "LT":
            case "LV":
                client = "EE";
                break;
            case "PT":
            case "ES":
                client = "GE";
                break;
            case "FR":
            case "FR-":
                client = "GF";
                break;
            case "HU":
                client = "GH";
                break;
            case "IT":
                client = "GI";
                break;
            case "NL":
                client = "MP";
                break;
            case "NO":
            case "NO-":
                client = "GN";
                break;
            case "PL":
                client = "GP";
                break;
            case "RO":
                client = "GR";
                break;
            case "SE":
                client = "GS";
                break;
            case "GB":
            case "GB-":
            case "IE":
            case "IE-":
                client = "GG";
                break;
            default:
                client = "WF";
        }
        return client;
    }
}
