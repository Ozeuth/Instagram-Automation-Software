package com.webDriver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import javax.swing.*;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;


public class botTest {
    static String user = "";
    static String password = "";
    static String[] tags = {};
    static int selectionType;
    boolean isMac = false;

    int likesForToday;
    int commentsForToday;
    int followsForToday;
    final int maxLikes = 500;
    final int maxComments = 100;
    final int maxFollows = 100;
    String userData;
    Random rnd;


    public botTest(boolean isMac) {
        this.isMac = isMac;
    }

    @Test
    public void startWebDriver() {
        while (user == "" || password == "" || tags.length == 0) {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println("This should not be happening");
            }
        }

        this.userData = (String) checkLikeCap(user)[0];
        this.likesForToday = (Integer) checkLikeCap(user)[1];
        this.commentsForToday = (Integer) checkLikeCap(user)[2];
        this.followsForToday = (Integer) checkLikeCap(user)[3];
        Random rnd = new Random();
        rnd.setSeed(1);
        this.rnd = rnd;
        popUpBox(aspect() + " left for today: " + aspectLeft(), 1500);

        if (isMac) {
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver");
        } else {
            String directory = System.getProperty("user.dir").replace("\\", "\\\\");
            System.setProperty("webdriver.chrome.driver", directory + "\\chromedriver.exe");
        }

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 30);

        login(driver, wait, user, password);

        waitForInput(driver, wait);

        machina(driver, wait, tags);
    }

    // Procedure functions

    public Object[] checkLikeCap(String user) {
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
            popUpBox("File not Found", 3000);
        }
        Object[] stringLikesLeft = new Object[4];
        stringLikesLeft[0] = finalData;
        stringLikesLeft[1] = likesLeftForToday;
        stringLikesLeft[2] = commentsLeftForToday;
        stringLikesLeft[3] = followsLeftForToday;
        return stringLikesLeft;
    }

    public static void login(WebDriver driver, WebDriverWait wait, String user, String password) {
        driver.navigate().to("https://www.instagram.com/accounts/login/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        driver.findElement(By.name("username")).sendKeys(user);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("password")).sendKeys(Keys.RETURN);
    }


    public void waitForInput(WebDriver driver, WebDriverWait wait) {
        boolean searchBarExists = (driver.findElements(By.className("XTCLo")).size() > 0);
        while (!searchBarExists) {
            searchBarExists = (driver.findElements(By.className("XTCLo")).size() > 0);
        }
        System.out.println("Well the bar exists");

        fixedWait(1000);
        boolean focusable = false;
        while (!focusable) {
            focusable = (driver.findElements(By.className("piCib")).size() == 0);
        }
        System.out.println("Well it's clickable");
    }

    public void machina(WebDriver driver, WebDriverWait wait, String[] tags) {
        for (int current = 0; current < tags.length; current++) {
            tagSearch(driver, wait, tags[current]);
            fixedWait(1000);
            List<WebElement> inputs = (driver.findElements(By.className("_bz0w"))).stream().collect(Collectors.toList());
            WebElement headInput = inputs.get(0);
            boolean thisTagDepleted = false;
            try {
                headInput.click();
            } catch (Exception e){
                driver.navigate().refresh();
                fixedWait(5000);
                inputs = (driver.findElements(By.className("_bz0w"))).stream().collect(Collectors.toList());
                headInput = inputs.get(0);
                headInput.click();
            }
            while (!thisTagDepleted) {
                fixedWait(5000);
                if (selectionType == 1) {
                    like(wait, driver);
                }
                else if (selectionType == 2) {
                    like(wait, driver);
                } else {
                    follow(wait, driver);
                }
                boolean nextPresentAndClicked = nextImage(wait, driver);
                if (!nextPresentAndClicked) {
                    thisTagDepleted = true;
                    driver.navigate().back();
                    fixedWait(3000);
                }
            }
        }
    }


    public static void tagSearch(WebDriver driver, WebDriverWait wait, String tags) {
        waiter(wait, "XTCLo");
        WebElement searchBar = driver.findElement(By.className("XTCLo"));
        searchBar.clear();
        searchBar.sendKeys(tags);
        searchBar.sendKeys(Keys.ENTER);
        fixedWait(1500);
        searchBar.sendKeys(Keys.ENTER);
        fixedWait(1500);
        searchBar.sendKeys(Keys.RETURN);
        waiter(wait, "_bz0w");
    }

    public void like(WebDriverWait wait, WebDriver driver) {
        if (likesForToday > 0) {
            if (isPresent(driver, "glyphsSpriteHeart__outline__24__grey_9")) {  //Like button empty
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(By.className("coreSpriteHeartOpen")));
                } catch (Exception e) {
                    System.out.println("Item was not clickable");
                }
                System.out.println("I have liked this");
                likesForToday--;
                System.out.println("likes left " + likesForToday);
                likeWrite(userData, user, Integer.toString(likesForToday), Integer.toString(commentsForToday), Integer.toString(followsForToday));
                driver.findElement(By.className("coreSpriteHeartOpen")).click();
            } else if (isPresent(driver, "glyphsSpriteHeart__filled__24__red_5")) {   //Like button already full
                System.out.println("I have already liked");
            } else {
                System.out.println("I couldn't find the button");
            }
        } else {
            popUpBox("Daily like limit reached!", 3000);
            System.exit(0);
        }
    }

    public void comment(WebDriverWait wait, WebDriver driver) {
        if (commentsForToday > 0) {
            if (isPresent(driver, "Ypffh")) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(By.className("Ypffh")));
                } catch (Exception e) {
                    System.out.println("Item was not clickable");
                }
                System.out.println("I have commented");
                commentsForToday--;
                System.out.println("comments left " + commentsForToday);
                likeWrite(userData, user, Integer.toString(likesForToday), Integer.toString(commentsForToday), Integer.toString(followsForToday));
                driver.findElement(By.className("Ypffh")).clear();
                driver.findElement(By.className("Ypffh")).sendKeys(commentWrite());
                driver.findElement(By.className("Ypffh")).sendKeys(Keys.RETURN);
                fixedWait(1500);
            } else {
                System.out.println("I couldn't find the button");
            }
        } else {
            popUpBox("Daily comment limit reached!", 3000);
            System.exit(0);
        }
    }

    public void follow(WebDriverWait wait, WebDriver driver) {
        if (followsForToday > 0) {
            if (buttonPresent(driver, "//button[contains(text(),'Following')]")) {
                System.out.println("I have already followed");
            } else if (buttonPresent(driver, "//button[contains(text(),'Follow')]")) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Follow')]")));
                } catch (Exception e) {
                    System.out.println("Item was not clickable");
                }
                System.out.println("I have followed this");
                followsForToday = followsForToday--;
                System.out.println("Follows left " + followsForToday);
                likeWrite(userData, user, Integer.toString(likesForToday), Integer.toString(commentsForToday), Integer.toString(followsForToday));
                driver.findElement(By.xpath("//button[contains(text(),'Follow')]")).click();
            } else {
                System.out.println("I couldn't find the button");
            }
        } else {
            popUpBox("Daily follow limit reached!", 3000);
            System.exit(0);
        }
    }

    public static void leaveImage(WebDriverWait wait, WebDriver driver) {
        waiter(wait, "ckWGn");
        driver.findElement(By.className("ckWGn")).click();
    }

    public boolean nextImage(WebDriverWait wait, WebDriver driver) {
        List<WebElement> inputs = driver.findElements(By.xpath("//a[@role = 'button']"));
        while (inputs.isEmpty()){
            fixedWait(5000);
            popUpBox("Something wrong with your wifi. Check again", 2000);
            inputs = driver.findElements(By.xpath("//a[@role = 'button']"));
        }
        WebElement rightArrow = inputs.get(0);
        for (int i = 0; i < inputs.size(); i++){
            if (inputs.get(i).getText().equals("Next")){
                rightArrow = inputs.get(i);
            }
        }
        if (rightArrow.getText().equals("Next")) {
            rightArrow.click();
            return true;
        } else {
            return false;
        }
    }


    // Helper functions
    public static boolean isPresent(WebDriver driver, String identifier) {
        return driver.findElements(By.className(identifier)).size() > 0;
    }

    public static boolean buttonPresent(WebDriver driver, String path) {
        return driver.findElements(By.xpath(path)).size() > 0;
    }

    public static void waiter(WebDriverWait wait, String identifier) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(identifier)));
    }

    public static void fixedWait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void writeToFile(String s) {
        try (FileWriter fw = new FileWriter("dateAndLikes.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(s);
        } catch (IOException e) {
            popUpBox("File not Found", 3000);
        }
    }

    public void likeWrite(String userData, String user, String likesLeft, String commentsLeft, String followsLeft) {
        LocalDate today = LocalDate.now();
        String updatedUser = Arrays.asList(userData.split("/")).stream().map(p -> p.split(",")[0].equals(user) ? "/" + user + "," + today + "," + likesLeft + "," + commentsLeft + "," + followsLeft : "/" + p).reduce((s1, s2) -> s1 + s2).orElse(userData);
        writeToFile(updatedUser);
    }


    public String commentWrite() {
        String line;
        List<String> lines = new ArrayList<>();

        try {
            FileReader fr = new FileReader("comments.txt");
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            popUpBox("Comment File not Found", 3000);
        }
        String finalLine = lines.stream().filter(p -> !p.isEmpty()).filter(p -> p.charAt(0) == '1').collect(Collectors.toList()).get(0);
        String[] allComments = finalLine.split(":")[1].split(",");
        int commentRange = allComments.length;
        int selectedComment = rnd.nextInt(commentRange);
        return allComments[selectedComment];
    }

    public void popUpBox(String s, int time) {
        boolean hasDisplayed = false;
        JLabel message = new JLabel(s, SwingConstants.CENTER);
        message.setForeground(Color.WHITE);
        message.setFont(message.getFont().deriveFont(message.getFont().getSize() * 1.5F));
        message.setBounds(30, 50, 150, 30);

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

    public String aspect() {
        if (selectionType == 1) {
            return "Likes";
        } else if (selectionType == 2) {
            return "Comments";
        } else {
            return "Follows";
        }
    }

    public int aspectLeft() {
        if (selectionType == 1) {
            return likesForToday;
        } else if (selectionType == 2) {
            return commentsForToday;
        } else {
            return followsForToday;
        }
    }

}