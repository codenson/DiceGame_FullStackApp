/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.Dicegameserver.DiceGameServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author guero
 */
public class DicegameRestControllerTest {
     
      
    
    public DicegameRestControllerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testHelloWorld() {
    }

    @Test
    public void testCheckCredentials() throws Exception {
    }

    @Test
    public void testNewUser() throws Exception {
    }

    @Test
    public void testSendScore() {
    }

    @Test
    public void testPopulateScore() throws Exception {
           DicegameRestController contoller  = new DicegameRestController(); 
                 JSONObject user = new JSONObject();
                 user.put("userName", "blah909"); 
                 user.put("score", "10"); 
                 
        contoller.currentUser = "blah9090"; 
        Map<String, String> map  = new HashMap();
      
        map.put("score", "10"); 
        try {
               ResponseEntity<JSONObject> populateScore = contoller.populateScore(map);
        } catch (IOException ex) {
            System.out.println("ERR ....."); 
        }
    }
//    @Test
//    public void checkJson() throws IOException{
//           DicegameRestController contoller  = new DicegameRestController(); 
//                 JSONObject user = new JSONObject();
//                 user.put("userName", "blah909"); 
//                 user.put("score", "10"); 
//                 
//        contoller.currentUser = "blah9090"; 
//        Map<String, String> map  = new HashMap();
//      
//        map.put("score", "10");
//      
//        
//         JSONObject result = contoller.checkJson(map);
//         String str1  = result.getAsString("userName") + result.getAsString("score"); 
//         String str2 =  "bla909010"; 
//        assertEquals(str2, str1);
//        
////        assertEquals(user, ); 
//       
//    
//    
//    }

    @Test
    public void testMain() {
    }
    
}
