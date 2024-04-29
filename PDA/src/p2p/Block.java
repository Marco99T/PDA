package p2p;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Block implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int index;
    private long timestamp;
    private String previousHash;
    private String hash;
    private List<Transaction> transactions;
    private int nonce;
    private int dificultad;
    private static List<Block> blockchain = new ArrayList<>();

    //generacion del bloque
    public Block(int index, long timestamp, String previousHash, List<Transaction> transactions, int dificultad) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.dificultad = dificultad;
        this.nonce = 0;
        this.hash = calculateHash();
    }
    public String calculateHash() {
        StringBuilder data = new StringBuilder();
        data.append(index)
                .append(timestamp)
                .append(previousHash)
                .append(nonce)
                .append(transactionsToString());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hashBytes = digest.digest(data.toString().getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }
    private String transactionsToString() {
        StringBuilder builder = new StringBuilder();
        for (Transaction tx : transactions) {
            builder.append(tx.toString());
        }
        return builder.toString();
    }
    //resolucion de la prueba de trabajo
    public String mineBlock(){
        String target = new String(new char[dificultad]).replace('\0', '0');
        while (!hash.substring(0, dificultad).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined: " + hash);
        return hash;
    }
    //validacion del bloque
    public boolean isValidBlock(Block block, Block previousBlock, int difficulty){
        String target = new String(new char[difficulty]).replace('\0', '0');
        String hash = block.calculateHash();

        System.out.println("Hash calculado: " + hash);
        System.out.println("Objetivo: " + target);
        for (Transaction transaction : block.getTransactions()) {
            if (!transaction.isValid()) {
                System.out.println("bloque invalido1");
                return false;
            }
        }

        // Verificar integridad del bloque anterior
        if (!block.getPreviousHash().equals(previousBlock.getHash())) {
            System.out.println("bloque invalido2");
            return false;
        }

        // Verificar prueba de trabajo (PoW)
        if (!hash.substring(0, difficulty).equals(target)) {
            System.out.println("bloque invalido3");
            return false;
        }

        // Si todas las condiciones se cumplen, el bloque es válido
        System.out.println("bloque valido");
        return true;
    }
    //Consenso y propagacion de bloques
    public void handleRecivedBlock(Block block){
        if(getLatestBlock()==null){
            blockchain.add(block);
            System.out.println("Block genesis añadido a la blockchain: " + block.getHash());
        }else{
            if (isValidBlock(block, getLatestBlock(), dificultad)) {
                // El bloque es válido, así que lo agregamos a la cadena de bloques
                blockchain.add(block);
                System.out.println("Block added to the blockchain: " + block.getHash());
            } else {
                // El bloque recibido no es válido, así que lo descartamos
                System.out.println("Received block is not valid: " + block.getHash());
            }
        }

    }

    //getters y setters
    public String getHash(){
        return hash;
    }
    public String getPreviousHash(){
        return previousHash;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public static Block getLatestBlock() {
        if (blockchain.isEmpty()) {
            // Si la cadena de bloques está vacía, devuelve null o lanza una excepción, según lo desees
            return null; // O lanza una excepción adecuada, como IllegalStateException
        }
        return blockchain.get(blockchain.size() - 1);
    }
    public int getIndex() {
        return index;
    }
    public static List<Block> getBlockchain() {
        return blockchain;
    }
}
