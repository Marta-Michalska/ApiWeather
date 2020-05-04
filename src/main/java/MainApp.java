

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainApp implements Runnable {


    private Scanner scanner = new Scanner(System.in);


    private void startApp() {
        String typeInfo;
        System.out.println("Co chcesz zrobić? \n1 - Wyświetlić aktualną pogodę \n2 - Wyświetlić prognozę pogody \n0 - Zakończyć działanie programu");
        int choice = scanner.nextInt();
        switch (choice) {
            case 0:
                break;
            case 1:
                typeInfo = "weather?";
                parseJsonCurrent(getJson(createURL(typeInfo)));
                //TODO ??? rozbić linijkę na mniejsze wyrażenia?...
                break;
            case 2:
                typeInfo = "forecast?";
                parseJson5day(getJson(createURL(typeInfo)));
                break;
        }

    }

    public String getJson(String url) {
        String json = null;
        try {
            json = new HttpService().connect(url);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private String createURL(String typeInfo) {
        //TODO rozbić metodę na medoty zbierające poszczególne fragmenty danych?...
        String typeLoc = null;
        String nameLoc = null;
        System.out.println("Czy chcesz wyszukiwać po \n1 - Nazwie miasta \n2 - Kodzie pocztowym");
        int choice = scanner.nextInt();
        switch (choice) {
            case 0:
                break;
            case 1:
                typeLoc = "q=";
                System.out.println("Wpisz nazwę miasta");
                nameLoc = scanner.next();
                break;
            case 2:
                typeLoc = "zip=";
                System.out.println("Wpisz kod pocztowy");
                nameLoc = scanner.next() + ",pl";
                break;
            default:
                throw new IllegalStateException("Niewłaściwy wybór: " + choice);
        }

        String url = Config.APP_URL + typeInfo + typeLoc + nameLoc + "&appid=" + Config.APP_ID;
        return url;
    }

    private void parseJsonCurrent(String json) {
        double temp;
        int humidity;
        int pressure;
        int clouds;

        JSONObject rootObject = new JSONObject(json);
        if (rootObject.getInt("cod") == 200) {
            JSONObject mainObject = rootObject.getJSONObject("main");
            DecimalFormat df = new DecimalFormat("#.##");
            temp = mainObject.getDouble("temp");
            temp = temp - 273;

            humidity = mainObject.getInt("humidity");
            pressure = mainObject.getInt("pressure");
            JSONObject cloudsObject = rootObject.getJSONObject("clouds");
            clouds = cloudsObject.getInt("all");

            System.out.println("Temperatura: " + df.format(temp) + " \u00b0C");
            System.out.println("Wilgotność: " + humidity + " %");
            System.out.println("Zachmurzenie: " + clouds + "%");
            System.out.println("Ciśnienie: " + pressure + " hPa");

        } else {
            System.out.println("Nie udało się pobrać danych");
        }
    }

    private void parseJson5day(String json) {

        double temp;
        int pressure;
        int humidity;
        int clouds;
        String dt_txt;

        JSONObject rootObject = new JSONObject(json);
        JSONArray listArray = rootObject.getJSONArray("list");
        if (rootObject.getInt("cod") == 200) {


            for (int i = 0; i < listArray.length(); i++) {
                JSONObject dayObject = listArray.getJSONObject(i);
                JSONObject mainObject = dayObject.getJSONObject("main");
                DecimalFormat df = new DecimalFormat("#.##");
                temp = mainObject.getDouble("temp");
                temp = temp - 273;

                pressure = mainObject.getInt("pressure");
                humidity = mainObject.getInt("humidity");
                JSONObject cloudsObject = dayObject.getJSONObject("clouds");
                clouds = cloudsObject.getInt("all");
                dt_txt = dayObject.getString("dt_txt");

                if (dt_txt.contains("12:00:00")) {
                    System.out.println("Data " + dt_txt + "   Temperatura: " + df.format(temp) +
                            " \u00b0C" + "   Wilgotność: " + humidity + " %" +
                            "   Ciśnienie: " + pressure + " hPa" + "   Zachmurzenie: " + clouds + "%");
                }

            }
        } else {
            System.out.println("Nie udało się pobrać danych");
        }
    }

    @Override
    public void run() {
        startApp();
    }

}
