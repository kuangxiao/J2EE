package com.siwind.web;

import java.io.IOException;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint 娉ㄨВ鏄竴涓被灞傛鐨勬敞瑙ｏ紝瀹冪殑鍔熻兘涓昏鏄皢鐩墠鐨勭被瀹氫箟鎴愪竴涓獁ebsocket鏈嶅姟鍣ㄧ,
 * 娉ㄨВ鐨勫�煎皢琚敤浜庣洃鍚敤鎴疯繛鎺ョ殑缁堢璁块棶URL鍦板潃,瀹㈡埛绔彲浠ラ�氳繃杩欎釜URL鏉ヨ繛鎺ュ埌WebSocket鏈嶅姟鍣ㄧ
 */
@ServerEndpoint("/WSHello")
public class HelloEndPoint {
	
	/**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println("Session " + session.getId() + " has opened a connection"); 
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from " + session.getId() + ": " + message);
       
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has closed!");
    }
    
    /**
     * 娉ㄦ剰: OnError() 鍙兘鍑虹幇涓�娆�.   鍏朵腑鐨勫弬鏁伴兘鏄彲閫夌殑銆�
     * @param session
     * @param t
     */
    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
    
}
