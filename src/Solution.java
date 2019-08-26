import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.net.*;
import com.google.gson.*;
    

public class Solution {
    /*
     * Complete the function below.
     */
    static void openAndClosePrices(String firstDate, String lastDate, String weekDay) {

        String requestUrl;
        String firstMonthYear = firstDate.substring(firstDate.indexOf("-") + 1);
        String lastMonthYear = lastDate.substring(lastDate.indexOf("-") + 1);
        String firstYear = firstDate.substring(firstDate.length() - 4);
        String lastYear = lastDate.substring(lastDate.length() - 4);
        String firstYearFirstThree = firstYear.substring(0,3);
        String lastYearFirstThree = lastYear.substring(0,3);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("d-MMMMM-yyyy", Locale.ENGLISH).parse(firstDate);
            endDate = new SimpleDateFormat("d-MMMMM-yyyy", Locale.ENGLISH).parse(lastDate);
        } catch (ParseException e) {
            System.out.println(e.getMessage()); 
            return;
        }


        //Code to prepare the request based on the parameters
        //in a way to retrieve less data and do less requests
        if (firstDate.equals(lastDate)) { //Query by exact date
            requestUrl = "https://jsonmock.hackerrank.com/api/stocks/?date=" + firstDate;
        } else if (firstMonthYear.equals(lastMonthYear)) { //Query by month
            requestUrl = "https://jsonmock.hackerrank.com/api/stocks/search/?date=" + firstMonthYear;
        } else if (firstYear.equals(lastYear)) { //Query by year
            requestUrl = "https://jsonmock.hackerrank.com/api/stocks/search/?date=" + firstYear;
        } else if (firstYearFirstThree.equals(lastYearFirstThree)) { //Query by year's first 3 positions
            requestUrl = "https://jsonmock.hackerrank.com/api/stocks/search/?date=" + firstYearFirstThree;
        } else { //Retrieve all
            requestUrl = "https://jsonmock.hackerrank.com/api/stocks/";
        }

        try {
            URL url = new URL(requestUrl);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer responseBody = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();

            JsonParser jsonParser = new JsonParser();
            JsonObject responseObject = jsonParser.parse(responseBody.toString()).getAsJsonObject();

            int currentPage = 1;
            int totalPages = responseObject.get("total_pages").getAsInt();

            JsonArray stocksList = responseObject.get("data").getAsJsonArray();

            List<JsonObject> requestResults = new ArrayList<>();

            for (JsonElement element : stocksList) {
                try {
                    JsonObject stock = element.getAsJsonObject();
                    Date currentDate = new SimpleDateFormat("d-MMMMM-yyyy", Locale.ENGLISH).parse(stock.get("date").getAsString());
                    String currentDayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(currentDate);

                    if (!(currentDate.compareTo(startDate) < 0) && !(currentDate.compareTo(endDate) > 0) 
                            && currentDayOfWeek.equals(weekDay))
                        requestResults.add(stock);

                } catch (ParseException e) {
                    
                    System.out.println(e.getMessage());
                    return;
                } 
            }

            //Get next pages if present
            while (currentPage < totalPages) {
                currentPage++;

                String pageUrl;
                if (requestUrl.contains("?")) {
                    pageUrl = requestUrl + ("&page=" + currentPage);
                } else {
                    pageUrl = requestUrl + ("?page=" + currentPage);
                }

                URL url2 = new URL(pageUrl);
                HttpURLConnection pageConnection = (HttpURLConnection) url2.openConnection();

                reader = new BufferedReader(new InputStreamReader(pageConnection.getInputStream()));
                line = null;
                responseBody = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
                reader.close();

                responseObject = jsonParser.parse(responseBody.toString()).getAsJsonObject();
              
                stocksList = responseObject.get("data").getAsJsonArray();

                for (JsonElement element : stocksList) {
                    try {
                        JsonObject stock = element.getAsJsonObject();
                        Date currentDate = new SimpleDateFormat("d-MMMMM-yyyy", Locale.ENGLISH).parse(stock.get("date").getAsString());
                        String currentDayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(currentDate);

                        if (!(currentDate.compareTo(startDate) < 0) && !(currentDate.compareTo(endDate) > 0) 
                                && currentDayOfWeek.equals(weekDay))
                            requestResults.add(stock);

                    } catch (ParseException e) {
                        System.out.println(e.getMessage());
                        return;
                    } 
                }

            }

            for (JsonObject stock : requestResults) {
                System.out.println(stock.get("date").getAsString() + " " 
                    + stock.get("open").getAsString() + " " + stock.get("close").getAsString());
            }

        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        String _firstDate;
        
        try {
            _firstDate = in.nextLine();
        } catch (Exception e) {
            _firstDate = null;
        }
        
        String _lastDate;
        try {
            _lastDate = in.nextLine();
        } catch (Exception e) {
            _lastDate = null;
        }
        
        String _weekDay;
        try {
            _weekDay = in.nextLine();
        } catch (Exception e) {
            _weekDay = null;
        }
        
        openAndClosePrices(_firstDate, _lastDate, _weekDay);
        
    }
}