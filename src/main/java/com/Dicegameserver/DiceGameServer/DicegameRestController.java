package com.Dicegameserver.DiceGameServer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import ch.qos.logback.core.CoreConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Dice Game controller. Deals with endpoints.
 *
 * @author guero
 */
@Controller
public class DicegameRestController {

    /**
     * Keeps track of current user.
     */
    String currentUser = "";
    /**
     * Object mapper to parse data on files into an Object.
     */
    ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Path to where User data (JSON files ) get stored.
     */
    @Value("${user.data.path}")
    private String userDataPath;

    /**
     * Exception error Handler. Redirects the user back to the logging page.
     * @param ex
     * @param request
     * @return logging page.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex, WebRequest request) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loggingPage"); // Name of the view to render
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("message", "An unexpected error occurred: " + ex.getMessage());
        return modelAndView;
    }

    /**
     * Method maps user request to the Website's home page.
     *
     * @return logging page.
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String helloWorld() {
        return "loggingPage";

    }  

    /**
     * User authorization checker. Checks user's username and password against
     * credential on JSON database.
     *
     * @param credentials includes user's username and password.
     * @return dice game webpage in case credentials authorization checks out,
     * otherwise displays logging page again.
     * @throws IOException Exception in case files does not exist.
     */
    @SuppressWarnings("unused")
    @RequestMapping(path = "/logginCredentials", method = RequestMethod.POST)
    public String checkCredentials(@RequestParam Map<String, String> credentials) throws IOException {

        String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
        File userInDatabase = new File(fileName);

        if (!userInDatabase.exists()) {
            return "loggingPage";
        }

        JsonNode currUserStoredInfo = objectMapper.readTree(userInDatabase);

        JSONObject userCred = new JSONObject();
        userCred.put("userName", credentials.get("UserName").trim());
        userCred.put("password", credentials.get("Password").trim());

        String passInDatabase = currUserStoredInfo.get("password").asText().trim();
        String passFromUser = userCred.getAsString("password").trim();

        if (userCred.getAsString("userName").equals(currUserStoredInfo.get("userName").asText())) {
            if (passInDatabase.equals(passFromUser)) {
                currentUser = credentials.get("UserName");
                return "dicee";
            }
        }

        return "loggingPage";
    }

    /**
     * Method to register new user. Makes a new directory to store user's
     * credentials as a JSON if the dir does not exists. It confirms password
     * and password reentry are matching before allowing user to be registered.
     *
     * @param credentials Consists of username, password and password re-entry.
     * @return returns logging page if user registration fails, otherwise it
     * redirects the user to the dice page.
     * @throws IOException exception in case the file system fails.
     */
    @RequestMapping(path = "/signUp", method = RequestMethod.POST)
    public String newUser(@RequestParam Map<String, String> credentials) throws IOException {

        JSONObject obj2 = new JSONObject();
        obj2.put("userName", credentials.get("UserName"));
        obj2.put("password", credentials.get("Password"));
        obj2.put("score", "0");

        String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";

        File file = new File(fileName);
        if (file.exists()) {
            System.out.printf("file exists ");

            return "loggingPage";

        }
        File directory = new File(userDataPath);
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();

        }

        if (!(credentials.get("Password").trim().equals(credentials.get("PasswordRenter").trim()))) {
            return "loggingPage";
        }
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(obj2.toJSONString());
        fileWriter.close();
        currentUser = credentials.get("UserName");
        return "dicee";
    }

    /**
     * Method to update index.js with the user's total score so it can displayed
     * at dicee page at the beggining of the game.
     *
     * @return CurrentUser's life score.
     */
    @PostMapping(path = "/getScore", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<JSONObject> sendScore(@RequestParam Map<String, String> credentials) 
    public ResponseEntity<JSONObject> sendScore() {
        JSONObject userTotalScore = new JSONObject();

        String fileName = userDataPath + File.separator + currentUser + ".json";
        File f = new File(fileName);
        JSONObject lifeScore = new JSONObject();

        if (f.exists()) {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(f);
            } catch (IOException ex) {
                Logger.getLogger(DicegameRestController.class.getName()).log(Level.SEVERE, null, ex);
            }
            String scoree = jsonNode.get("score").asText();
            lifeScore.put("score", scoree);

        }

        return ResponseEntity.ok(lifeScore);
    }

    /**
     * Method is an endpoint that receives currUser's score and updates the
     * total life score.
     *
     * @param credentials values of round's score sent from game's logic
     * index.js.
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/saveScore", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> populateScore(@RequestBody Map<String, String> credentials) throws IOException {
        JSONObject userTotalScore = new JSONObject();

        String fileName = userDataPath + File.separator + currentUser + ".json";
        File f = new File(fileName);
        JSONObject user = new JSONObject();

        if (f.exists()) {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(f);
            } catch (IOException ex) {
                Logger.getLogger(DicegameRestController.class.getName()).log(Level.SEVERE, null, ex);
            }

            String s = jsonNode.get("score").asText();
            int val1 = Integer.parseInt(s);
            String ss = credentials.get("score");

            int val2 = Integer.parseInt(ss);

            int scoree = val1 + val2;

            String passWord = jsonNode.get("password").asText();
            user.put("password", passWord);
            user.put("score", scoree + "");
            user.put("userName", currentUser);

            f.delete(); //deleting the old file   
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(user.toJSONString());
            fileWriter.close();
            System.err.println("jsonNode: 00000000: " + jsonNode.asText());
        }

        return ResponseEntity.ok(user);
    }

    /**
     * Main method for testing.sss
     * @param args
     */
    public static void main(String args[]) {
        DicegameRestController contoller = new DicegameRestController();
        contoller.currentUser = "blah9090";
        Map<String, String> map = new HashMap();
        map.put("score", "10");
        try {
            contoller.populateScore(map);
        } catch (IOException ex) {
            System.out.println("ERR .....");
        }

    }

}
