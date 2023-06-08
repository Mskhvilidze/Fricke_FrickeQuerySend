package fricke.service;

import com.google.common.eventbus.EventBus;
import fricke.message.RequestTab;
import fricke.message.RequestTabPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.File;
import java.util.*;

public class Service {
    private static int count = 0;
    private static EventBus bus;
    private static Map<Object, List<String>> map = new HashMap<>();

    public Service() {
        bus = new EventBus();
        bus.register(this);
    }

    public static Tab createTab() {
        return new Tab();
    }


    public static void counter() {
        count++;
    }

    public static int getCount() {
        return count;
    }

    public static EventBus getBus() {
        return bus;
    }

    public static void onRequestTab(Tab tab) {
        bus.post(new RequestTab(tab));
    }

    public static void onRequestTabPane(TabPane tabPane) {
        bus.post(new RequestTabPane(tabPane));
    }

    public static Map<Object, List<String>> getMap() {
        return map;
    }

    public static void alert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static String getCurrentDate() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return "" + year + "" + month + "" + day;
    }

    public static void deleteAllFiles() {
        File file = new File("files");
        if (file.listFiles().length > 0) {
            Arrays.stream(file.listFiles()).forEach(deletedFile -> deletedFile.delete());
        }
    }
}
