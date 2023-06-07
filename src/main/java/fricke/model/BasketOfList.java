package fricke.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class BasketOfList {
    private List<String> countries = new ArrayList<>();
    private List<String> clients = new ArrayList<>();
    private List<String> date = new ArrayList<>();
    private List<String> ids = new ArrayList<>();

    private List<String> articles = new ArrayList<>();
    private List<String> sales_volume_action = new ArrayList<>();
    private List<String> qty_action = new ArrayList<>();
    private List<String> sales_volume_comparison = new ArrayList<>();
    private List<String> qty_comparison = new ArrayList<>();
    private List<String> CTOTIC = new ArrayList<>();


    public void add(String OLPRDC, String sales_action, String qty_action, String sales_comparison, String qty_comparison){
        this.articles.add(OLPRDC);
        this.sales_volume_action.add(sales_action);
        this.qty_action.add(qty_action);
        this.sales_volume_comparison.add(sales_comparison);
        this.qty_comparison.add(qty_comparison);
    }

    public void add(String country, String client, String date, String id){
        this.countries.add(country);
        this.clients.add(client);
        this.date.add(date);
        this.ids.add(id);
    }
}
