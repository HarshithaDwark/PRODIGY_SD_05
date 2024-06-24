import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class AmazonProductScraper {

    // List of user agents
    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
    };

    // Method to get a random user agent
    private static String getRandomUserAgent() {
        Random rand = new Random();
        return USER_AGENTS[rand.nextInt(USER_AGENTS.length)];
    }

    public static void main(String[] args) {
        String url = "https://www.amazon.com/Best-Sellers-Electronics/zgbs/electronics";

        try {
            // Connect to the webpage with a random user agent and fetch the document
            Document doc = Jsoup.connect(url)
                    .userAgent(getRandomUserAgent())
                    .timeout(10000) // 10 seconds timeout
                    .get();

            // Select all the product elements
            Elements products = doc.select("div.p13n-sc-truncate");

            // Prepare CSV file to write data
            String csvFile = "amazon_electronics_products.csv";
            FileWriter writer = new FileWriter(csvFile);

            // Write CSV file header
            writer.append("Name,Price,Rating\n");

            // Iterate over each product element
            for (Element product : products) {
                // Extract product name
                String name = product.text();

                // Extract product price
                Element priceElement = product.nextElementSibling();
                String price = priceElement.select("span.p13n-sc-price").text().replaceAll("[^0-9.]", "");

                // Extract product rating
                Element ratingElement = priceElement.nextElementSibling();
                String rating = ratingElement.select("span.a-icon-alt").text().split(" ")[0];

                // Write product information to CSV file
                writer.append(name + "," + price + "," + rating + "\n");

                // Introduce a longer delay to avoid triggering rate limits
                Thread.sleep(5000); // 5 seconds delay
            }

            // Close CSV writer
            writer.close();

            System.out.println("Product information extracted successfully and saved to " + csvFile);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
