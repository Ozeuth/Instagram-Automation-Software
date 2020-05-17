package com.webDriver;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class bot {
    static String user = "";
    static String password = "";
    static String[] tags = {};
    static int selectionType;
    private boolean isMac;
    private boolean isServer;

    private int likesForToday;
    private int commentsForToday;
    private int followsForToday;
    private final int maxLikes = 500;
    private final int maxComments = 100;
    private final int maxFollows = 100;
    private String userData;
    private Random rnd;

    private boolean rightArrowWorks = false;
    // List of Web Elements
    final private Map<String, String> webElements = new HashMap<String, String>() {{
        put("searchbar on home page", "");
        put("images on tag page", "");
        put("nnGetInstaApp", "");
        put("nnGetNotifs", "");
        put("empty heart color", "");
        put("heart button in image tab", "");
        put("comment bar in image tab", "");
        put("follow button in image tab", "");
        put("right arrow button in image tab", "");
    }};

    bot(boolean isMac, String[] args) {
        // Update the bot with manual information
        /*
        FileReader fr = new FileReader("webElements.txt");
        BufferedReader br = new BufferedReader(fr);
        String userDataStore = br.readLine().substring(1);*/

        this.isMac = isMac;
        // Get the user and password from Server
        if (args.length == 4) {
            isServer = true;
            user = args[0];
            password = args[1];
            tags = args[2].split(",");
            selectionType = Integer.valueOf(args[3]);
        } else {
            // Get the user and password from the UI
            isServer = false;
            while (user == "" || password == "" || tags.length == 0) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println("This should not be happening");
                }
            }
        }
        // Get the web elements
        try {
            FileReader fr = new FileReader("webElements.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String key = line.split(":")[0];
                String value = line.split(":")[1];
                webElements.replace(key, value);
                line = br.readLine();
            }
            br.close();
            fr.close();
            startWebDriver();
        } catch (IOException e) {
            popUpBox("Web Elements File not Found", 3000);
        }
    }

    private void startWebDriver() {
        // Obtains the user and their limit for the day
        // If new user, creates a new user and sets their limit to max
        this.userData = (String) checkLikeCap(user)[0];
        this.likesForToday = (Integer) checkLikeCap(user)[1];
        this.commentsForToday = (Integer) checkLikeCap(user)[2];
        this.followsForToday = (Integer) checkLikeCap(user)[3];
        // Setup random comment generator
        Random rnd = new Random();
        rnd.setSeed(1);
        this.rnd = rnd;
        // User update
        popUpBox(aspect() + " left for today: " + aspectLeft(), 1500);
        // Chromedriver config and run for PC and Mac
        if (isMac) {
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver");
        } else {
            String directory = System.getProperty("user.dir").replace("\\", "\\\\");
            System.setProperty("webdriver.chrome.driver", directory + "\\chromedriver.exe");
        }
        WebDriver driver;
        if (isServer) {
            // Set up the server in headless mode
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            driver = new ChromeDriver(options);
        } else {
            driver = new ChromeDriver();
        }
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        // Login to instagram
        login(driver, wait, user, password);
        // Waits until you have discarded the custom updates to continue
        discardNotifications(driver, wait);
        // Automation begins
        machina(driver, wait, tags);
    }

    // Procedure functions
    private Object[] checkLikeCap(String user) {
        LocalDate today = LocalDate.now();
        int likesLeftForToday = 0;
        int commentsLeftForToday = 0;
        int followsLeftForToday = 0;
        String finalData = "";
        try {
            FileReader fr = new FileReader("dateAndLikes.txt");
            BufferedReader br = new BufferedReader(fr);
            String userDataStore = br.readLine().substring(1);
            String[] allDateAndLikes = userDataStore.split("/");
            List<String> userData = new ArrayList<>();
            br.close();
            fr.close();
            for (int i = 0; i < allDateAndLikes.length; i++) {
                userData.add(allDateAndLikes[i]);
            }
            if (userData.stream().filter(p -> p.split(",")[0].equals(user)).collect(Collectors.toList()).isEmpty()) {
                finalData = "/" + userDataStore + "/" + user + "," + today + "," + maxLikes + "," + maxComments + "," + maxFollows;
                writeToFile(finalData);
                likesLeftForToday = maxLikes;
                commentsLeftForToday = maxComments;
                followsLeftForToday = maxFollows;
            } else {
                finalData = userDataStore;
                String[] dateAndLikes = userData.stream().filter(p -> p.split(",")[0].equals(user)).collect(Collectors.toList()).get(0).split(",");
                LocalDate filesDate = LocalDate.parse(dateAndLikes[1], ISO_LOCAL_DATE);
                if (today.equals(filesDate)) {
                    likesLeftForToday = Integer.parseInt(dateAndLikes[2]);
                    commentsLeftForToday = Integer.parseInt(dateAndLikes[3]);
                    followsLeftForToday = Integer.parseInt(dateAndLikes[4]);
                } else {
                    likesLeftForToday = maxLikes;
                    commentsLeftForToday = maxComments;
                    followsLeftForToday = maxFollows;
                }
            }
        } catch (IOException e) {
            popUpBox("Date And Likes File not Found", 3000);
        }
        Object[] stringLikesLeft = new Object[4];
        stringLikesLeft[0] = finalData;
        stringLikesLeft[1] = likesLeftForToday;
        stringLikesLeft[2] = commentsLeftForToday;
        stringLikesLeft[3] = followsLeftForToday;
        return stringLikesLeft;
    }

    private static void login(WebDriver driver, WebDriverWait wait, String user, String password) {
        driver.navigate().to("https://www.instagram.com/accounts/login/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        driver.findElement(By.name("username")).sendKeys(user);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("password")).sendKeys(Keys.RETURN);
    }

    // This is the automated version
    private void discardNotifications(WebDriver driver, WebDriverWait wait) {
        boolean searchBarExists = isPresent(driver, wait, webElements.get("searchbar on home page"), true);
        while (!searchBarExists) {
            // We cycle through the different possible reasons and try to close them
            fixedWait(1000);
            // 1. Get Instagram App pop-up
            if (isPresent(driver, wait, webElements.get("nnGetInstaApp"), true)) {
                driver.findElement(By.className(webElements.get("nnGetInstaApp"))).click();
                fixedWait(1000);
            }
            // 2. Get Notification pop-up
            if (isPresent(driver, wait, webElements.get("nnGetNotifs"), true)) {
                driver.findElement(By.className(webElements.get("nnGetNotifs"))).click();
            }
            searchBarExists = isPresent(driver, wait, webElements.get("searchbar on home page"), true);
        }

        fixedWait(1000);
        boolean focusable = false;
        while (!focusable) {
            focusable = (driver.findElements(By.className("piCib")).size() == 0);
        }
    }

    private void machina(WebDriver driver, WebDriverWait wait, String[] tags) {
        for (int current = 0; current < tags.length; current++) {
            tagSearch(driver, wait, tags[current]);
            fixedWait(1000);
            List<WebElement> inputs = (driver.findElements(By.className(webElements.get("images on tag page")))).stream().collect(Collectors.toList());
            WebElement headInput = inputs.get(0);
            boolean thisTagDepleted = false;
            try {
                headInput.click();
            } catch (Exception e){
                driver.navigate().refresh();
                fixedWait(5000);
                inputs = (driver.findElements(By.className(webElements.get("images on tag page")))).stream().collect(Collectors.toList());
                headInput = inputs.get(0);
                headInput.click();
            }
            while (!thisTagDepleted) {
                // Human Behavior
                if (rnd.nextBoolean()) {
                    fixedWait(rnd.nextInt(3000) + 2000);
                    if (selectionType == 1) {
                        like(wait, driver);
                    }
                    else if (selectionType == 2) {
                        comment(wait, driver);
                    } else {
                        follow(wait, driver);
                    }
                };
                fixedWait(rnd.nextInt(3000) + 2000);
                boolean nextPresentAndClicked = nextImage(wait, driver);
                if (!nextPresentAndClicked) {
                    thisTagDepleted = true;
                    driver.navigate().back();
                    fixedWait(3000);
                }
            }
        }
    }


    private void tagSearch(WebDriver driver, WebDriverWait wait, String tags) {
        waiter(wait, webElements.get("searchbar on home page"));
        WebElement searchBar = driver.findElement(By.className(webElements.get("searchbar on home page")));
        searchBar.clear();
        searchBar.sendKeys(tags);
        searchBar.sendKeys(Keys.ENTER);
        fixedWait(1500);
        searchBar.sendKeys(Keys.ENTER);
        fixedWait(1500);
        searchBar.sendKeys(Keys.RETURN);
        waiter(wait, webElements.get("images on tag page"));
    }

    private void like(WebDriverWait wait, WebDriver driver) {
        if (likesForToday > 0) {
            if (isPresent(driver, wait, webElements.get("heart button in image tab"), true)) {
                WebElement likeIcon = driver.findElement(By.className(webElements.get("heart button in image tab")));
                List<WebElement> children = likeIcon.findElements(By.tagName("svg"));
                boolean foundEmptyHeart = false;
                for (WebElement child : children) {
                    if (child.getAttribute("fill").equals(webElements.get("empty heart color"))) {
                        foundEmptyHeart = true;
                        likesForToday--;
                        System.out.println("I have liked this: " + likesForToday);
                        likeWrite(userData, user, Integer.toString(likesForToday), Integer.toString(commentsForToday), Integer.toString(followsForToday));
                        likeIcon.click();
                    }
                }
                if (!foundEmptyHeart) {
                    System.out.println("I have already liked this");
                }
            } else {
                diagnose(driver, wait, webElements.get("heart button in image tab"));
            }
        } else {
            popUpBox("Daily like limit reached!", 3000);
            System.exit(0);
        }
    }

    private void comment(WebDriverWait wait, WebDriver driver) {
        if (commentsForToday > 0) {
            if (isPresent(driver, wait, webElements.get("comment bar in image tab"), true)) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(By.className(webElements.get("comment bar in image tab"))));
                } catch (Exception e) {
                    System.out.println("Item was not clickable");
                }
                System.out.println("I have commented");
                commentsForToday--;
                System.out.println("comments left " + commentsForToday);
                likeWrite(userData, user, Integer.toString(likesForToday), Integer.toString(commentsForToday), Integer.toString(followsForToday));
                driver.findElement(By.className(webElements.get("comment bar in image tab"))).clear();
                driver.findElement(By.className(webElements.get("comment bar in image tab"))).sendKeys(commentWrite());
                driver.findElement(By.className(webElements.get("comment bar in image tab"))).sendKeys(Keys.RETURN);
                fixedWait(1500);
            } else {
                diagnose(driver, wait, webElements.get("comment bar in image tab"));
            }
        } else {
            popUpBox("Daily comment limit reached!", 3000);
            System.exit(0);
        }
    }

    private void follow(WebDriverWait wait, WebDriver driver) {
        if (followsForToday > 0) {
            if (isPresent(driver, wait, "//button[contains(text(),'Following')]", false)) {
                System.out.println("I have already followed");
            } else if (isPresent(driver, wait, "//button[contains(text(),'Follow')]", false)) {
                followsForToday--;
                System.out.println("I have followed this: " + followsForToday);
                likeWrite(userData, user, Integer.toString(likesForToday), Integer.toString(commentsForToday), Integer.toString(followsForToday));
                // As of 7/1/2019, follow button is not clickable at the point...
                // This is because there are actually two follow buttons on screen (one of which is hidden). The catch now properly catches this bug.
                try {
                    driver.findElement(By.xpath("//button[contains(text(),'Follow')]")).click();
                } catch (Exception e){
                    driver.findElement(By.className(webElements.get("follow button in image tab"))).click();
                }
            } else {
                System.out.println("I couldn't find the button");
            }
        } else {
            popUpBox("Daily follow limit reached!", 3000);
            System.exit(0);
        }
    }

    private boolean nextImage(WebDriverWait wait, WebDriver driver) {
        if (isPresent(driver, wait, webElements.get("right arrow button in image tab"), true)) {
            driver.findElement(By.className(webElements.get("right arrow button in image tab"))).click();
            rightArrowWorks = true;
            return true;
        } else {
            // If right arrow is known to work, then absence of right arrow element indicates end of page
            if (!rightArrowWorks) {
                diagnose(driver, wait, webElements.get("right arrow button in image tab"));
            }
            return false;
        }
    }


    // Helper functions
    private boolean isPresent(WebDriver driver, WebDriverWait wait, String identifier, boolean isClickable) {
        boolean elementFound = driver.findElements(By.className(identifier)).size() > 0
                || driver.findElements(By.xpath(identifier)).size() > 0;
        if (!elementFound) {
            return false;
        }
        if (isClickable) {
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(By.className(identifier)),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath(identifier))
                ));
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private void diagnose(WebDriver driver, WebDriverWait wait, String identifier) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.className(identifier)),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath(identifier))
            ));
        } catch (Exception e) {
            boolean elementFound = driver.findElements(By.className(identifier)).size() > 0
                    || driver.findElements(By.xpath(identifier)).size() > 0;
            String identifierName =
                    webElements.entrySet().stream().filter(entry -> identifier.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().get();
            if (!elementFound) {
                // Not found, not clickable
                popUpBox("Element no longer valid: " + identifierName + ". Fix in webElements.txt", 3000);
            } else {
                // found, not clickable
                popUpBox("Element found but not clickable: " + identifierName + ". Fix in webElements.txt", 3000);
            }
            System.exit(0);
        }
    }

    private static void waiter(WebDriverWait wait, String identifier) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(identifier)));
    }

    private static void fixedWait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void writeToFile(String s) {
        try (FileWriter fw = new FileWriter("dateAndLikes.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(s);
        } catch (IOException e) {
            popUpBox("File not Found", 3000);
        }
    }

    private void likeWrite(String userData, String user, String likesLeft, String commentsLeft, String followsLeft) {
        LocalDate today = LocalDate.now();
        String updatedUser = Arrays.asList(userData.split("/")).stream().map(p -> p.split(",")[0].equals(user) ? "/" + user + "," + today + "," + likesLeft + "," + commentsLeft + "," + followsLeft : "/" + p).reduce((s1, s2) -> s1 + s2).orElse(userData);
        writeToFile(updatedUser);
    }


    private String commentWrite() {
        String line;
        List<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader("comments.txt");
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            popUpBox("Comment File not Found", 3000);
        }
        String finalLine = lines.stream().filter(p -> !p.isEmpty()).filter(p -> p.charAt(0) == '1').collect(Collectors.toList()).get(0);
        String[] allComments = finalLine.split(":")[1].split(",");
        int commentRange = allComments.length;
        int selectedComment = rnd.nextInt(commentRange);
        return allComments[selectedComment];
    }

    private void popUpBox(String s, int time) {
        boolean hasDisplayed = false;
        JLabel message = new JLabel(s, SwingConstants.CENTER);
        message.setForeground(Color.WHITE);
        message.setFont(message.getFont().deriveFont(message.getFont().getSize() * 1.5F));
        message.setBounds(30, 50, 400, 200);

        // Dynamic Resizing
        JFrame temp = new JFrame();
        temp.setFont(message.getFont());
        FontMetrics fm = temp.getFontMetrics(message.getFont());
        int width = fm.stringWidth(message.getText());
        int height = fm.getHeight();
        while (width >= 400 || height >= 200) {
            message.setFont(message.getFont().deriveFont(message.getFont().getSize() - 1F));
            temp.setFont(message.getFont());
            fm = temp.getFontMetrics(message.getFont());
            width = fm.stringWidth(message.getText());
            height = fm.getHeight();
        }
        temp.dispose();

        JFrame frame = new JFrame("Notice");
        frame.add(message);
        frame.setSize(400, 200);
        frame.getContentPane().setBackground(new Color(38, 142, 169));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        while (hasDisplayed == false) {
            frame.setVisible(true);
            fixedWait(time);
            hasDisplayed = true;
        }
        frame.dispose();

    }

    private String aspect() {
        if (selectionType == 1) {
            return "Likes";
        } else if (selectionType == 2) {
            return "Comments";
        } else {
            return "Follows";
        }
    }

    private int aspectLeft() {
        if (selectionType == 1) {
            return likesForToday;
        } else if (selectionType == 2) {
            return commentsForToday;
        } else {
            return followsForToday;
        }
    }

}
