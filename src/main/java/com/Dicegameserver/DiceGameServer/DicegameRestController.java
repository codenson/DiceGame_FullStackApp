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
   
    
     @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex, WebRequest request) {
        // Log the exception (optional)
        // Logger.getLogger(GlobalExceptionHandler.class.getName()).log(Level.SEVERE, null, ex);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loggingPage"); // Name of the view to render
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("message", "An unexpected error occurred: " + ex.getMessage());
        
        return modelAndView;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String helloWorld() {

        return "loggingPage";

    }

//    @PostMapping("/logginCredentials")
    @SuppressWarnings("unused")
    @RequestMapping(path = "/logginCredentials", method = RequestMethod.POST)
    //@ResponseBody
    public String checkCredentials(@RequestParam Map<String, String> credentials) throws IOException {

       // System.out.println("cred: " + credentials);

        String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
       // System.out.println("FIle Name : "+ fileName);
        File userInDatabase = new File(fileName);
       // System.out.println("fileName " + fileName);

        if (!userInDatabase.exists()) {
           // System.out.println("inside ::::");
            return "loggingPage";
        }
        //System.out.println("File : " +userInDatabase.toString() );

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
                currentUser = credentials.get("UserName");
                return "dicee";
            }
        }

        return "loggingPage";
    }
     @RequestMapping(path = "/logginCredentials", method = RequestMethod.GET)
     public String reroute(@RequestParam Map<String, String> credentials){
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
          //  System.out.printf("password1 %s + password2 %s", credentials.get("Password"), credentials.get("PasswordRenter"));
           /// System.out.printf("Password issue ");
            return "loggingPage";

        }
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(obj2.toJSONString());
        fileWriter.close();
         currentUser = credentials.get("UserName");
        return "dicee";
    }

    @PostMapping(path = "/getScore", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> sendScore(@RequestParam Map<String, String> credentials) {
        JSONObject userTotalScore = new JSONObject();

        // String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
                String fileName = userDataPath + File.separator + currentUser+ ".json";
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
                //System.out.println("Score from boot*************** : " + lifeScore.get("score") );

        return ResponseEntity.ok(lifeScore);
    }


    @PostMapping(path = "/saveScore", produces = MediaType.APPLICATION_JSON_VALUE)
   // public ResponseEntity<JSONObject> populateScore(@RequestParam Map<String, String> credentials) throws IOException {
    public ResponseEntity<JSONObject> populateScore(@RequestBody Map<String, String> credentials) throws IOException {
        JSONObject userTotalScore = new JSONObject();

        // String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
                String fileName = userDataPath + File.separator + currentUser+ ".json";
        File f = new File(fileName);
        JSONObject user = new JSONObject();
      

        if (f.exists()) {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(f);
            } catch (IOException ex) {
                Logger.getLogger(DicegameRestController.class.getName()).log(Level.SEVERE, null, ex);
            }
            // System.err.println("jsonNode: 00000000: "+ jsonNode.asText());
            //  int val1 = jsonNode.get("score").asInt() ; 
            System.out.println("File : " + jsonNode.get("score").asText() );
              System.out.println("credentials.get(\"score\"): "+ credentials.get("score"));
              
            String s =  jsonNode.get("score").asText(); 
              int val1 = Integer.parseInt(s);
               String ss =  credentials.get("score"); 
               
              int val2 = Integer.parseInt(ss);
             
       
//            int val2 =Integer.parseInt(credentials.get("score")); 
            
            int  scoree = val1 + val2;  
           /// System.out.println("File val1: "+ val1  +" REST val2 : "+ val2+ " raw Str val2 : "+ credentials.get("score") + "New total : "+ scoree); 
            //System.out.println("vaL : " +  val1  + val2 +"Score: " +scoree ); 
           // scoree = 10; 
             
              String passWord  = jsonNode.get("password").asText(); 
              user.put("password", passWord); 
            user.put("score", scoree+"");
            user.put("userName", currentUser); 
            System.out.println("New Userrrrrrrr : --------------  : "+user); 
         
            // System.err.println("jsonNode: 00000000: "+ jsonNode.asText());

            f.delete(); //deleting the old file   
              FileWriter fileWriter = new FileWriter(fileName);
             fileWriter.write(user.toJSONString());
             fileWriter.close();
              System.err.println("jsonNode: 00000000: "+ jsonNode.asText());
        }
                //System.out.println("Score from boot*************** : " + lifeScore.get("score") );

        return ResponseEntity.ok(user);
    }
    
  
    
    
    
         public  JSONObject checkJson(Map<String, String> credentials) throws IOException{
              JSONObject userTotalScore = new JSONObject();

        // String fileName = userDataPath + File.separator + credentials.get("UserName") + ".json";
                String fileName = userDataPath + File.separator + currentUser+ ".json";
        File f = new File(fileName);
        JSONObject user = new JSONObject();
      

        if (f.exists()) {
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(f);
            } catch (IOException ex) {
                Logger.getLogger(DicegameRestController.class.getName()).log(Level.SEVERE, null, ex);
            }

            //  jsonNode.put(jsonNode.get("score")+  credentials.get("score")); 
            int val1 = jsonNode.get("score").asInt() ; 
       
            int val2 =Integer.parseInt(credentials.get("score")); 
            
            int  scoree = val1 + val2; 
            //System.out.println(); 
            //System.err.print("Err Score : "  +scoree );
            user.put(jsonNode.get("userName").asText(), currentUser);
            user.put("score", scoree+"");

//            f.delete(); //deleting the old file
//            System.out.println("User: " + user);
////            File UpdateFile = new File(fileName);
//            
//              FileWriter fileWriter = new FileWriter(fileName);
//             fileWriter.write(user.toJSONString());
//             fileWriter.close();
            
         

        }
   
            
            return user;  
            
        
        }
    public static void main (String args []){
        DicegameRestController contoller  = new DicegameRestController(); 
        contoller.currentUser = "blah9090"; 
        Map<String, String> map  = new HashMap();
      //  HashMap<String,Integer > map  = new HashMap(); 
        map.put("score", "10"); 
        try {
            contoller.populateScore(map);
        } catch (IOException ex) {
            System.out.println("ERR ....."); 
        }
        
        
    
    }
     


}
