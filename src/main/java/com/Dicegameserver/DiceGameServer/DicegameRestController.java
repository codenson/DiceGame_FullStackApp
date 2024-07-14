//package com.server.Dicegameserver;
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
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author guero
 */
@Controller
public class DicegameRestController {

    JSONObject obj = new JSONObject();
    String currentUser = "";
    ObjectMapper objectMapper = new ObjectMapper();
    private String jsonStoragePath;

    @Value("${user.data.path}")
    private String userDataPath;

    @Autowired
    ResourceLoader resourceLoader;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String helloWorld() {

        return "loggingPage";

    }

//    @PostMapping("/logginCredentials")
    @SuppressWarnings("unused")
    @RequestMapping(path = "/logginCredentials", method = RequestMethod.POST)
    //@ResponseBody
    public String checkCredentials(@RequestParam Map<String, String> credentials) throws IOException {

        System.out.println("cred: " + credentials);

        String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
        System.out.println("FIle Name : "+ fileName);
        File userInDatabase = new File(fileName);
        System.out.println("fileName " + fileName);

        if (!userInDatabase.exists()) {
            System.out.println("inside ::::");
            return "loggingPage";
        }
        System.out.println("File : " +userInDatabase.toString() );

        JsonNode currUserStoredInfo = objectMapper.readTree(userInDatabase);

        JSONObject userCred = new JSONObject();
        userCred.put("userName", credentials.get("UserName").trim());
        userCred.put("password", credentials.get("Password").trim());
        
        String pass1 = currUserStoredInfo.get("password").asText().trim(); 
        String pass2 = userCred.getAsString("password").trim(); 

        if (userCred.getAsString("userName").equals(currUserStoredInfo.get("userName").asText())) {
           // if (currUserStoredInfo.get("password").asText().equals(userCred.getAsString("password"))) 
           if (pass1.equals(pass2))
            {
                return "dicee";
            }
        }

        return "loggingPage";
    }

    @RequestMapping(path = "/signUp", method = RequestMethod.POST)
    public String newUser(@RequestParam Map<String, String> credentials) throws IOException {
        JSONObject obj2 = new JSONObject();
        obj2.put("userName", credentials.get("UserName"));
        obj2.put("password", credentials.get("Password"));
        obj2.put("score", "0");
        
//        System.out.println("Credentials from obj : "+ obj2);
        String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
        // System.out.printf("password1 %s + password2 %s", credentials.get("Password" ), credentials.get("PasswordRenter")); 

        File file = new File(fileName);
         // System.out.println("File : " +file.toString() );
        if (file.exists()) {
            System.out.printf("file exists ");

            return "loggingPage";

        }
        //System.out.println("File : " +file.toString() );
        if (!(credentials.get("Password").trim().equals(credentials.get("PasswordRenter").trim()))) {
            System.out.printf("password1 %s + password2 %s", credentials.get("Password"), credentials.get("PasswordRenter"));
            System.out.printf("Password issue ");
            return "loggingPage";

        }
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(obj2.toJSONString());
        fileWriter.close();

        return "dicee";
    }

//@PostMapping(path = "/getScore", produces = MediaType.APPLICATION_JSON_VALUE)
//public ResponseEntity<JSONObject> sendScore(@RequestParam Map<String, String> credentials) {
//    String fileName = userDataPath + File.separator + currentUser + ".json";
//    File f = new File(fileName);
//    JSONObject lifeScore = new JSONObject();
//
//    if (f.exists()) {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(f);
//            String scoree = jsonNode.get("score").asText();
//            lifeScore.put("score", scoree);
//        } catch (IOException ex) {
//            Logger.getLogger(AceOfSpadesRestController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    return ResponseEntity.ok(lifeScore);
//}
//    

    @PostMapping(path = "/getScore", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> sendScore(@RequestParam Map<String, String> credentials) {
        JSONObject userTotalScore = new JSONObject();


        String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
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
        //         System.out.println("Score from boot*************** : " + lifeScore.get("score") );

        return ResponseEntity.ok(lifeScore);
    }

}
