package p2p;

import java.io.IOException;
import java.net.DatagramPacket;	
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.JTextArea;

public class Bully extends Thread{
	private MulticastSocket socket;
	private InetAddress group;
	
	private boolean coordinador=false;
    private boolean elector_lock=false;
	private int port;
	private int ID;
	private String IP;
    private boolean is_there_coordinador;
    private boolean status = false;
    private Timer timer_to_be_coordinador;
    private Timer timer_to_convert_in_coordinador;
	
	private JTextArea text_are_chat_algoritmo_bully;
	
	@SuppressWarnings("deprecation")
    public Bully(String host, int port, JTextArea text_are_chat_algoritmo_bully, int ID, String IP) {
		timer_to_be_coordinador = new Timer();
        timer_to_convert_in_coordinador = new Timer();
		this.text_are_chat_algoritmo_bully = text_are_chat_algoritmo_bully;
		try {
            this.is_there_coordinador = false;
			this.port = port;
			this.socket = new MulticastSocket(port);
			this.group = InetAddress.getByName(host);
            this.ID = ID;
            this.IP = IP;
			this.socket.joinGroup(group);
            option_to_send_message(3);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	
	public void run() {
		DatagramPacket pack_ = new DatagramPacket(new byte[1024], 1024);
		try {
			//Case when start to search a leader
            while (true) {
                this.socket.receive(pack_);
                String address = String.valueOf(pack_.getAddress().getHostAddress());
                if(!address.equals(this.IP)){
                    
                    String data_received = new String(pack_.getData(), 0, pack_.getLength());
                    String[] parts_of_data_received = data_received.split(":");
                    int id_from_nodo = Integer.parseInt(parts_of_data_received[1]);
                    String case_messagge = parts_of_data_received[0];
                    
                    if(this.coordinador){
                        option_to_send_message(1);
                    }
                    else {
                        switch(case_messagge) {
                            case "Coordinador":
                            //With this i can knoew whos the coordidador
                                if(!is_there_coordinador){
                                    this.elector_lock = false;
                                    this.is_there_coordinador = true;
                                    String message = "El coordinador actual es: " + id_from_nodo + "\n";
                                    text_are_chat_algoritmo_bully.append(message);
                                    timer_to_be_coordinador.cancel();
                                }
                                break;
                            case "Eleccion":
                                if(this.ID > id_from_nodo && !this.elector_lock){
                                    this.elector_lock=true;
                                    text_are_chat_algoritmo_bully.append("Soy candidato(" + this.ID +") \n");
                                    option_to_send_message(2);
                                    message_to_change_status_of_nodo(address);
                                    //White a response, if there is not a response, then this node its convert in leader
                                    timer_to_convert_in_coordinador.schedule(new TimerTask() {
                                        public void run(){
                                            if(!status && !coordinador){
                                                text_are_chat_algoritmo_bully.append("Soy ahora el coordinador: " + ID +".\n");
                                                option_to_send_message(1);
                                                coordinador = true;
                                                is_there_coordinador = true;
                                                elector_lock = false;
                                            }
                                        }
                                    }, 1500);
                                }
                                break;
                            case "Desconectado":
                                if(is_there_coordinador){
                                    this.is_there_coordinador = false;
                                    this.elector_lock = false;
                                    String message_to_nodes = "El coordinador: " + id_from_nodo + " se ha desconectado. \n";
                                    text_are_chat_algoritmo_bully.append(message_to_nodes);
                                }
                                break;
                            case "Leader":
                                option_to_send_message(5);
                                break;
                            case "NoLeader":
                                text_are_chat_algoritmo_bully.append("No hay coordinador. \n");
                                timer_to_be_coordinador.cancel();
                                break;
                            case "Status":
                                this.status = true;
                                timer_to_convert_in_coordinador.cancel();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
		}
        catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void message_to_change_status_of_nodo(String address) {
        byte [] message = ("Status" + ":" + this.ID).getBytes();
        try {
        	DatagramPacket packet = new DatagramPacket
            (
                message, message.length, 
                InetAddress.getByName(address), this.port
            );
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void option_to_send_message(int option){
        byte [] message = null;

        switch (option) {
            //message_from_coordinator
            case 1:
                message = ("Coordinador" + ":" + this.ID).getBytes();
            break;
            //message_to_select
            case 2:
                message = ("Eleccion" + ":" + this.ID).getBytes();
            break;
            //verify_leader
            case 3:
                message = ("Leader" + ":" + this.ID).getBytes();
            break;
            //notify_disconeccted
            case 4:
                message = ("Desconectado" + ":" + this.ID).getBytes();
            break;
            //message_no_leader
            case 5:
                message = ("NoLeader" + ":" + this.ID).getBytes();
            break;
            default:
                break;
        }

        try {
        	DatagramPacket packet = new DatagramPacket
            (
                message, message.length, 
                this.group, this.port
            );
            this.socket.send(packet);
            if(option == 3){
                timer_to_be_coordinador.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if ((!status && elector_lock)
                            || (!is_there_coordinador && !coordinador)) 
                            {
                                text_are_chat_algoritmo_bully.append("Soy ahora el coordinador: " + ID +".\n");
                                option_to_send_message(1);
                                coordinador = true;
                                elector_lock = false;
                                is_there_coordinador = true;
                            }
                        }
                    }, 1500);
                }
            if(option == 2){
                timer_to_convert_in_coordinador.schedule(new TimerTask() {
                    public void run(){
                        if(!status && !coordinador){
                            text_are_chat_algoritmo_bully.append("Soy ahora el coordinador: " + ID +".\n");
                            option_to_send_message(1);
                            coordinador = true;
                            is_there_coordinador = true;
                            elector_lock = false;
                        }
                    }
                }, 1500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
