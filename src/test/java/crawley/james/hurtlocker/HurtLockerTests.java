package crawley.james.hurtlocker;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jamescrawley on 10/17/16.
 */
public class HurtLockerTests {

    private Main main = new Main ();
    private String raw;
    private JerkSONParser parser;

    @Before
    public void initialize () {

        try {
            raw = main.readRawDataToString();
        } catch (Exception e) {
            raw = "";
        }
        parser = new JerkSONParser(raw);
        try {
            parser.parseItem();
            parser.putItem();

        } catch (DataMissingException e) {

        }
        parser.next();


    }

    @Test
    public void parseItemTest () {

        StringBuilder actual = new StringBuilder();
                for (GroceryItem item : parser.getGroceryItems()) {
                    actual.append("Name: ");
                    actual.append(item.getName());
                    actual.append(", Price: ");
                    actual.append(item.getPrice());
                }
        String expected =  "Name: Milk, Price: 3.23";

        assertEquals("The array should be printed out as " +
                "[naMe:, Milk;, price:, 3.23;, type:, Food;, expiration:, 1/25/2016]", expected, actual.toString());

    }

    @Test
    public void getItemNameTest () {

        List<GroceryItem> items = parser.getGroceryItems();

        String actual = items.get(0).getName();

        assertEquals("The name should be Milk", "Milk", actual);

    }

    @Test
    public void getItemPriceTest () {

        List<GroceryItem> items = parser.getGroceryItems();

        String actual = items.get(0).getPrice();

        assertEquals("The price should be 3.23", "3.23", actual);

    }

    @Test
    public void putItemTest () {


        String expected = "{Apples={0.23=2, 0.25=2}, Cookies={2.25=8}, Milk={1.23=1, 3.23=5}, Bread={1.23=6}}";

        parseAllItems();

        String actual = parser.getInventory().toString();

        assertEquals("The Map should be printed out as {Apples={0.23=2, 0.25=2}, Cookies={2.25=8}, Milk={1.23=1, 3.23=5}, Bread={1.23=6}}", expected, actual);

    }

    @Test
    public void getFoodCount () {

        parseAllItems();
        int actual = parser.getMapSize(parser.getInventory().get("Apples"));

        assertEquals("There should be four apples", 4, actual);

    }

    @Test
    public void getPriceCount () {

        parseAllItems();

        int actual = parser.getInventory().get("Apples").get("0.23");

        assertEquals("There should be two apples priced at 0.23", 2, actual);

    }

    @Test
    public void getErrorsTest () {

        parseAllItems();

        assertEquals("There should be 4 errors", 4, parser.getErrors());
    }

    @Test
    public void formatGroceryListTest () {

        String expected = "name:  Apples \t\t seen: 4 times\n" +
                "============= \t\t =============\n" +
                "Price:   0.23 \t\t seen: 2 times\n" +
                "------------- \t\t -------------\n" +
                "Price:   0.25 \t\t seen: 2 times\n" +
                "------------- \t\t -------------\n" +
                "\n" +
                "name: Cookies \t\t seen: 8 times\n" +
                "============= \t\t =============\n" +
                "Price:   2.25 \t\t seen: 8 times\n" +
                "------------- \t\t -------------\n" +
                "\n" +
                "name:    Milk \t\t seen: 6 times\n" +
                "============= \t\t =============\n" +
                "Price:   1.23 \t\t seen: 1  time\n" +
                "------------- \t\t -------------\n" +
                "Price:   3.23 \t\t seen: 5 times\n" +
                "------------- \t\t -------------\n" +
                "\n" +
                "name:   Bread \t\t seen: 6 times\n" +
                "============= \t\t =============\n" +
                "Price:   1.23 \t\t seen: 6 times\n" +
                "------------- \t\t -------------\n" +
                "\n" +
                "Errors       \t\t seen: 4 times";


        parseAllItems();

        assertEquals("Grocery list should be formatted as it is in output.txt", expected, parser.printGroceryList());

    }

    private void parseAllItems () {

        while (parser.hasNext()) {
            try {
                parser.parseItem();
                parser.putItem();
            } catch (DataMissingException e) {

            }
            parser.next();

        }

    }
}