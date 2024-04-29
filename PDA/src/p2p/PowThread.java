package p2p;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class PowThread extends Thread {
    private List<Block> blockchainL;
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private String IP;

    private JTextArea text_area_pow;

    public PowThread(String host, int port,JTextArea text_area_pow) {
        this.text_area_pow= text_area_pow;
        this.setBlockchainL(new ArrayList<>());
        try {
            this.port = port;
            this.socket = new MulticastSocket(port);
            this.group = InetAddress.getByName(host);
            this.socket.joinGroup(group);
            this.IP = String.valueOf(InetAddress.getLocalHost().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
                socket.receive(receivePacket);
                String address = String.valueOf(receivePacket.getAddress().getHostAddress());
				if(!address.equals(this.IP)){

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    Object receivedObject = objectInputStream.readObject();

                    // Procesar el objeto recibido según su tipo
                    if (receivedObject instanceof List<?>) {
                        // Si el objeto es una lista de bloques, procesarla
                        text_area_pow.append("Cadena de bloques recibida \n");
                        List<Block> receivedChain = (List<Block>) receivedObject;
                        text_area_pow.append("Iniciando validacion \n");
                        handleReceivedChain(receivedChain);
                    } else {
                        text_area_pow.append("Cadena invalida o no es una cadena\n");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean handleReceivedChain(List<Block> chain) {
        // Implementa la lógica para validar si una cadena de bloques es válida
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlocka = chain.get(i - 1);

            if (!currentBlock.isValidBlock(currentBlock, previousBlocka, 3)) {
                text_area_pow.append("El bloque " + currentBlock.getIndex() + " no es válido.\n");
                System.out.println("El bloque " + currentBlock.getIndex() + " no es válido.");
                text_area_pow.append("Validacion fallida el bloque " + currentBlock.getIndex() + "no es valido\n");
                return false;
            }
        }
        System.out.println("Todos los bloques son válidos.");
        text_area_pow.append("Todos los bloques son validos\n");
        setBlockchainL(chain);
        return true;
    }
    public void newCadenadeBloquesEnviar(){
        Transaction transaction1 = new Transaction("sender1", "recipient1", 10.0);
        Transaction transaction2 = new Transaction("sender2", "recipient2", 20.0);
        Transaction transaction3 = new Transaction("sender3", "recipient3", 30.0);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        // Crear el bloque génesis
        Block genesisBlock = new Block(0, System.currentTimeMillis(), "0000000000000000", transactions, 3);
        Block previousBlock = genesisBlock;

        // Minar el bloque génesis
        String block_minado = genesisBlock.mineBlock();
        text_area_pow.append("Block mmined: " + block_minado + "\n");
        text_area_pow.append("se mino el bloque genesis\n");
        System.out.println("se mino el bloque genesis");

        // Agregar el bloque génesis a la cadena de bloques
        genesisBlock.handleRecivedBlock(genesisBlock);
        text_area_pow.append("gGenesis se agrego a la cadena\n");
        System.out.println("genesis se agrego a la cadena");
        text_area_pow.append("Empieza a minar\n");
        System.out.println("empieza a minar");

        // Crear y minar los siguientes dos bloques
        for (int i = 1; i <= 2; i++) {
            Block block = new Block(i, System.currentTimeMillis(), previousBlock.getHash(), transactions, 3);
            String blo = block.mineBlock();
            text_area_pow.append("Block mine: " + blo + "\n");
            block.handleRecivedBlock(block);
            previousBlock = block;
        }

        // Verificar la validez de los bloques
        for (int i = 1; i < Block.getBlockchain().size(); i++) {
            Block currentBlock = Block.getBlockchain().get(i);
            Block previousBlocka = Block.getBlockchain().get(i - 1);

            if (!currentBlock.isValidBlock(currentBlock, previousBlocka, 3)) {
                text_area_pow.append("El bloque " + currentBlock.getIndex() + " no es válido.\n");
                System.out.println("El bloque " + currentBlock.getIndex() + " no es válido.");
                return;
            }
        }
        System.out.println("Todos los bloques son válidos.");
        text_area_pow.append("Se genero una cadena de bloques y se enviara. \n");
        sendChain(Block.getBlockchain());
    }
    public void sendChain(List<Block> chain) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(chain);
            byte[] data = byteArrayOutputStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

	public List<Block> getBlockchainL() {
		return blockchainL;
	}

	public void setBlockchainL(List<Block> blockchainL) {
		this.blockchainL = blockchainL;
	}
}

