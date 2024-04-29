package p2p;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.JTextArea;

public class Chat extends Thread{
	
    private MulticastSocket socket;
    private InetAddress group;
    private DatagramPacket packet;
    private int port;
    private String IP;
    private JTextArea text_area_chat;
    
    @SuppressWarnings("deprecation")
    public Chat(String host, int port, JTextArea text_area_chat, String IP){
    	
    	this.text_area_chat = text_area_chat;
        try {
            this.socket = new MulticastSocket(port);
            this.group = InetAddress.getByName(host);
            this.port = port;
            this.IP = IP;
            this.socket.joinGroup(group);
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
    }

    
    public void send_message(Person person){
    	
    	// Convertir el objeto a un array de bytes
		try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(person);
            byte[] data = byteArrayOutputStream.toByteArray();
            this.packet = new DatagramPacket(data, data.length, group, port);
            this.socket.send(packet);
            this.text_area_chat.append(person.getNickname() + ": " + person.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void run() {
        String message_recived = "";
        Person person = null;
        try {
            while(true){
                DatagramPacket packet = new DatagramPacket(new byte[1024] , 1024);
                this.socket.receive(packet);
                String address = String.valueOf(packet.getAddress().getHostAddress());
				if(!address.equals(this.IP)){
                    // Convertir los bytes recibidos de nuevo en un objeto Serializable
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    person = (Person) objectInputStream.readObject();
                    message_recived = new String(person.getNickname() + ": " + person.getMessage()) + "\n";
                    text_area_chat.append(message_recived);
                }
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
