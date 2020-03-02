package com.arafeh.jsf.core.utils;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class TextGenerator {
    public static TextGenerator getInstance(int seed) {
        return new TextGenerator(seed);
    }

    public static TextGenerator getInstance() {
        return getInstance(0);
    }

    private static RandomResource names;
    private static RandomResource emails;
    private static RandomResource middles;
    private static RandomResource locations;
    private static RandomResource screens;

    static {
        URL emailsUrl = Resources.getResource("txt/emails.txt");
        emails = new RandomResource(emailsUrl);

        URL namesUrl = Resources.getResource("txt/names.txt");
        names = new RandomResource(namesUrl);

        URL middlesUrl = Resources.getResource("txt/middles.txt");
        middles = new RandomResource(middlesUrl);

        URL locationsUrl = Resources.getResource("txt/places.txt");
        locations = new RandomResource(locationsUrl);

        URL screensUrl = Resources.getResource("txt/screen.txt");
        screens = new RandomResource(screensUrl);
    }

    private final Random random;

    private TextGenerator(int seed) {
        random = seed == 0 ? new Random() : new Random(seed);
    }


    public String getName() {
        return names.get(random);
    }

    public String getEmail() {
        return emails.get(random);
    }

    public String getLocation() {
        return locations.get(random);
    }

    public String getMiddle() {
        return middles.get(random);
    }

    public String getScreenName() {
        return screens.get(random);
    }

    public String getFullName() {
        return getName() + " " + getMiddle() + " " + getName();
    }


    public static class RandomResource {
        private ArrayList<String> resources = new ArrayList<>();
        private URL url;
        private Random random;

        private RandomResource(URL url) {
            random = new Random();
            this.url = url;
            loadInto(url, resources);
        }

        public String get() {
            if (resources.isEmpty()) {
                loadInto(url, resources);
            }
            return resources.remove(random.nextInt(resources.size()));
        }

        public String get(Random random) {
            if (resources.isEmpty()) {
                loadInto(url, resources);
            }
            return resources.remove(random.nextInt(resources.size()));
        }

        public String get(boolean remove) {
            if (remove) return get();
            return resources.get(random.nextInt(resources.size()));
        }

        public String get(boolean remove, Random random) {
            if (remove) return get();
            return resources.get(random.nextInt(resources.size()));
        }

        private void loadInto(URL url, ArrayList<String> list) {
            try (Scanner scanner = new Scanner(url.openStream())) {
                while (scanner.hasNextLine()) {
                    list.add(scanner.nextLine());
                }
            } catch (IOException ignored) {

            }
        }
    }
}
