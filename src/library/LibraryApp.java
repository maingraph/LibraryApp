package library;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.List;

public class LibraryApp {
    // Collection for storing library items using java.util.List
    List<LibraryItem> libraryItems;

    public LibraryApp() {
        libraryItems = new ArrayList<>();
    }

    // Methods for managing library items
    public void addItem(LibraryItem item) {
        libraryItems.add(item);
    }

    public void removeItem(int id) {
        libraryItems.removeIf(item -> item.getId() == id);
    }

    public LibraryItem findItemById(int id) {
        for (LibraryItem item : libraryItems) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    public void updateItem(int id, LibraryItem updatedItem) {
        for (int i = 0; i < libraryItems.size(); i++) {
            if (libraryItems.get(i).getId() == id) {
                libraryItems.set(i, updatedItem);
                return;
            }
        }
    }

    // Import data from a JSON file
    public void importFromJSON(String filename) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filename)));
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LibraryItem.class, new LibraryItemDeserializer())
                    .create();
            List<LibraryItem> items = gson.fromJson(json, new TypeToken<List<LibraryItem>>() {}.getType());
            libraryItems.clear();
            libraryItems.addAll(items);
            System.out.println("Import successful. Loaded items: " + libraryItems.size());
        } catch (IOException e) {
            System.out.println("Error importing JSON: " + e.getMessage());
        }
    }

    // Export data to a JSON file
    public void exportToJSON(String filename) {
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LibraryItem.class, new LibraryItemSerializer())
                    .create();
            String json = gson.toJson(libraryItems, new TypeToken<List<LibraryItem>>() {}.getType());
            Files.write(Paths.get(filename), json.getBytes());
            System.out.println("Export successful. Data written to " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting JSON: " + e.getMessage());
        }
    }

    // Parallel search for items by keyword using ExecutorService
    public List<LibraryItem> searchItems(String keyword) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<List<LibraryItem>>> futures = new ArrayList<>();
        int chunkSize = (libraryItems.size() / 4) + 1;
        for (int i = 0; i < libraryItems.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, libraryItems.size());
            Callable<List<LibraryItem>> task = () -> {
                List<LibraryItem> result = new ArrayList<>();
                for (int j = start; j < end; j++) {
                    LibraryItem item = libraryItems.get(j);
                    if (((Searchable) item).contains(keyword)) {
                        result.add(item);
                    }
                }
                return result;
            };
            futures.add(executor.submit(task));
        }
        List<LibraryItem> searchResults = new ArrayList<>();
        for (Future<List<LibraryItem>> future : futures) {
            try {
                searchResults.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return searchResults;
    }

    // Sort items by the given criterion
    public void sortItems(String criterion) {
        Collections.sort(libraryItems, new Comparator<LibraryItem>() {
            @Override
            public int compare(LibraryItem o1, LibraryItem o2) {
                if (o1 instanceof Sortable && o2 instanceof Sortable) {
                    return ((Sortable) o1).compareTo(o2, criterion);
                }
                return 0;
            }
        });
    }

    // Launch a simple GUI to display the list of items and a bar chart
    public void launchGUI() {
        JFrame frame = new JFrame("Library Items");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create a DefaultListModel and JList to show the library items
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (LibraryItem item : libraryItems) {
            listModel.addElement(item.getInfo());
        }
        JList<String> itemList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(itemList);

        // Create a ChartPanel (a custom JPanel) to show a bar chart by year
        ChartPanel chartPanel = new ChartPanel(libraryItems);
        chartPanel.setPreferredSize(new Dimension(600, 200));

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(chartPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // Main method to process command-line arguments and run the application
    public static void main(String[] args) {
        LibraryApp app = new LibraryApp();
        if (args.length > 0) {
            String command = args[0];
            switch (command.toLowerCase()) {
                case "import":
                    if (args.length >= 2) {
                        app.importFromJSON(args[1]);
                    } else {
                        System.out.println("Please specify a filename for import.");
                    }
                    break;
                case "export":
                    if (args.length >= 2) {
                        app.exportToJSON(args[1]);
                    } else {
                        System.out.println("Please specify a filename for export.");
                    }
                    break;
                case "search":
                    if (args.length >= 2) {
                        List<LibraryItem> results = app.searchItems(args[1]);
                        System.out.println("Search results:");
                        for (LibraryItem item : results) {
                            System.out.println(item.getInfo());
                        }
                    } else {
                        System.out.println("Please specify a keyword for search.");
                    }
                    break;
                case "sort":
                    if (args.length >= 2) {
                        app.sortItems(args[1]);
                        System.out.println("Items sorted by " + args[1] + ":");
                        for (LibraryItem item : app.libraryItems) {
                            System.out.println(item.getInfo());
                        }
                    } else {
                        System.out.println("Please specify a sorting criterion.");
                    }
                    break;
                case "gui":
                    // Add test data if none exists
                    if (app.libraryItems.isEmpty()) {
                        app.addItem(new Book("Effective Java", "Joshua Bloch", 2008));
                        app.addItem(new Magazine("National Geographic", "Susan Goldberg", 2021, 5));
                        app.addItem(new Book("Clean Code", "Robert C. Martin", 2008));
                    }
                    SwingUtilities.invokeLater(app::launchGUI);
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        } else {
            // If no command-line arguments are provided, add test data and launch the GUI
            System.out.println("Usage: java -jar LibraryApp.jar [import/export/search/sort/gui] [parameters]");
            app.addItem(new Book("Effective Java", "Joshua Bloch", 2008));
            app.addItem(new Magazine("National Geographic", "Susan Goldberg", 2021, 5));
            app.addItem(new Book("Clean Code", "Robert C. Martin", 2008));
            app.launchGUI();
        }
    }
}

// Custom JPanel to display a bar chart of the number of items per year
class ChartPanel extends JPanel {
    private List<LibraryItem> items;

    public ChartPanel(List<LibraryItem> items) {
        this.items = items;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Count the number of items per year
        Map<Integer, Integer> yearCounts = new HashMap<>();
        for (LibraryItem item : items) {
            int year = 0;
            if (item instanceof Book) {
                year = ((Book) item).getYear();
            } else if (item instanceof Magazine) {
                year = ((Magazine) item).getYear();
            }
            yearCounts.put(year, yearCounts.getOrDefault(year, 0) + 1);
        }
        int width = getWidth();
        int height = getHeight();
        int margin = 30;
        int barWidth = 40;
        int x = margin;
        int maxCount = yearCounts.values().stream().max(Integer::compareTo).orElse(1);

        for (Map.Entry<Integer, Integer> entry : yearCounts.entrySet()) {
            int year = entry.getKey();
            int count = entry.getValue();
            int barHeight = (int) (((double) count / maxCount) * (height - 2 * margin));
            g.setColor(Color.BLUE);
            g.fillRect(x, height - margin - barHeight, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, height - margin - barHeight, barWidth, barHeight);
            g.drawString(String.valueOf(year), x, height - margin + 15);
            g.drawString(String.valueOf(count), x, height - margin - barHeight - 5);
            x += barWidth + margin;
        }
    }
}
