package p2p;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

public class Anillo extends Thread {
    private MulticastSocket socket;
    private InetAddress group;

    private int port;
    private int ID;
    private String IP;
    private boolean coordinador = false;
    private boolean is_there_coordinador;
    private JTextArea text_area_chat_algorithm_anillo;
    private List<String> nodos;

    private Timer timer_to_be_coordinador;
    private Timer timer_to_convert;

    @SuppressWarnings("deprecation")
    public Anillo(String host, int port, JTextArea text_area_chat_algorithm_anillo, int ID, String IP) {
        try {
            this.port = port;
            this.socket = new MulticastSocket(port);
            this.group = InetAddress.getByName(host);
            this.socket.joinGroup(group);
            this.nodos = new ArrayList<String>();
            this.timer_to_be_coordinador = new Timer();
            this.timer_to_convert = new Timer();
            this.ID = ID;
            this.IP = IP;
            this.text_area_chat_algorithm_anillo = text_area_chat_algorithm_anillo;
            this.nodos.add(this.IP);
            options_of_messagess_to_send(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        try {
            while(true){
                socket.receive(receivePacket);
                String address = String.valueOf(receivePacket.getAddress().getHostAddress());

                if(!address.equals(this.IP)){
                    String data_received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    String[] parts_of_data_received = data_received.split(":");
                    int id_from_nodo = Integer.parseInt(parts_of_data_received[0]);
                    String ip_from_nodo = parts_of_data_received[1];
                    String case_messagge = parts_of_data_received[2];
                    
                    if(!nodos.contains(parts_of_data_received[1])){
                        nodos.add(parts_of_data_received[1]);
                    }

                    if(this.coordinador){
                        options_of_messagess_to_send(3);
                    }
                    else {
                        switch (case_messagge) {
                            case "Coordinador":
                                if(!is_there_coordinador){
                                    this.is_there_coordinador = true;
                                    String message = "El coordinador actual es: " + id_from_nodo + "\n";
                                    text_area_chat_algorithm_anillo.append(message);
                                    timer_to_be_coordinador.cancel();
                                }
                                break;
                            case "Searching":
                                options_of_messagess_to_send(5);
                                break;
                            case "Add":
                                if(!nodos.contains(parts_of_data_received[1])){
                                    nodos.add(parts_of_data_received[1]);
                                }
                                break;
                            case "Eleccion":
                                ArrayList<String> nodo_list = new ArrayList<>();
                                String nodos_on_parts = parts_of_data_received[3];
                                String[] nodo_part = nodos_on_parts.split(",");
                                Collections.addAll(nodo_list, nodo_part);
                                String next_nodo = "";

                                if(nodo_list.contains(this.IP)){
                                    int indice = nodo_list.indexOf(this.IP);
                                    if (indice < nodo_list.size() - 1) {
                                        next_nodo = nodo_list.get(indice + 1);
                                    } else {
                                        next_nodo = nodo_list.get(0);
                                    }
                                }

                                String new_nodo_list = convert_arraylist_to_arraystring(nodo_list);

                                if (id_from_nodo < this.ID) {
                                    text_area_chat_algorithm_anillo.append("Soy candidato ("+ this.ID +") \n");
                                    String new_data_nodo = this.ID + ":" + this.IP + ":" + case_messagge + ":" + new_nodo_list;
                                    DatagramPacket new_packet = new DatagramPacket(new_data_nodo.getBytes(), new_data_nodo.getBytes().length, InetAddress.getByName(next_nodo), this.port);
                                    socket.send(new_packet);

                                } else if (id_from_nodo == this.ID) {
                                    // This node is the coordinator
                                    this.text_area_chat_algorithm_anillo.append("Soy ahora el coordinador: " + ID + "\n");
                                    options_of_messagess_to_send(3);
                                    this.coordinador = true;
                                    this.is_there_coordinador = true;

                                } else {
                                    // This node is not the leader, pass it on
                                    if(!is_there_coordinador){
                                        text_area_chat_algorithm_anillo.append("No soy candidato. \n");
                                    }

                                    String old_data = id_from_nodo + ":" + ip_from_nodo + ":" + case_messagge + ":" + new_nodo_list;
                                    DatagramPacket old_packet = new DatagramPacket(old_data.getBytes(), old_data.getBytes().length, InetAddress.getByName(next_nodo), this.port);
                                    socket.send(old_packet);
                                }
                                break;
                            case "Leader":
                                options_of_messagess_to_send(4);
                                break;
                            case "NoLeader":
                                text_area_chat_algorithm_anillo.append("No hay coordinador. \n");
                                timer_to_be_coordinador.cancel();
                                break;
                            case "Desconectado":
                            if(is_there_coordinador){
                                this.is_there_coordinador = false;
                                String message_to_nodes = "El coordinador: " + id_from_nodo + " se ha desconectado. \n";
                                text_area_chat_algorithm_anillo.append(message_to_nodes);
                            }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convert_arraylist_to_arraystring(List<String> nodos2){
        StringBuilder dataBuilder = new StringBuilder();
        for (String data : nodos2) {
            dataBuilder.append(data).append(",");
        }
        return dataBuilder.toString();
    }

    protected void options_of_messagess_to_send(int option){
        byte[] sendData = null;

        switch (option) {
            //verify leader
            case 1:
                sendData = (this.ID + ":" + this.IP + ":" + "Leader").getBytes();
            break;
            //start election
            case 2:
                sendData = (ID + ":" + IP + ":" + "Searching").getBytes();
            break;
            //message_from_coordinator
            case 3:
                sendData = (this.ID + ":" + this.IP + ":" + "Coordinador").getBytes();
            break;
            //message_no_coordinador
            case 4:
                sendData = (this.ID + ":" + this.IP + ":" + "NoLeader").getBytes();
            break;
            //get_ip_and_id_of_this_nodo
            case 5:
                sendData = (this.ID + ":" + this.IP + ":" + "Add").getBytes();
            break;
            //notify_disconeccted
            case 6:
                sendData = (this.ID + ":" + this.IP + ":" + "Desconectado").getBytes();
            break;
            default:
            break;
        }
        DatagramPacket packet = new DatagramPacket
        (
            sendData, sendData.length, 
            this.group, this.port
        );
        try {
            this.socket.send(packet);
            if(option == 1){
                timer_to_be_coordinador.schedule(new TimerTask() 
                {
                    @Override
                    public void run() {
                        if (!is_there_coordinador && !coordinador){
                            text_area_chat_algorithm_anillo.append("Soy ahora el coordinador: " + ID +".\n");
                            //message_from_coordinator();
                            options_of_messagess_to_send(3);
                            coordinador = true;
                            is_there_coordinador = true;
                        }
                    }
                }, 1200); 
            }
            if(option == 2){
                this.timer_to_convert.schedule(new TimerTask() {
                    public void run(){
                        try {
                            // Convertir ArrayList a String
                            String dataString = convert_arraylist_to_arraystring(nodos);
                            byte[] sendData = (ID + ":" + IP + ":" + "Eleccion" + ":" + dataString).getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, port);
                            socket.send(sendPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
