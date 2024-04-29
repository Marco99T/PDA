package p2p;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Transaction implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sender;
    private String recipient;
    private double amount;
    private Date timestamp;
    private String transactionHash;

    public Transaction(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = new Date();
        this.transactionHash = calculateHash();
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    // Método para calcular el hash de la transacción
    private String calculateHash() {
        String data = sender + recipient + amount + timestamp.getTime(); // Utilizar solo el timestamp en milisegundos
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para verificar la validez de la transacción
    public boolean isValid() {
        //Verificar el timestamp: Comprobar si la transacción es razonablemente reciente
        long currentTime = new Date().getTime();
        long transactionTime = timestamp.getTime();
        long maxTimeDifference = 5 * 60 * 1000; // Permitir un margen de 5 minutos
        if (currentTime - transactionTime > maxTimeDifference) {
            return false; // La transacción es demasiado antigua
        }
        // Verificar la integridad de los datos: Comprobar si el hash de la transacción es válido
        String calculatedHash = calculateHash();
        if (!calculatedHash.equals(transactionHash)) {
            return false; // El hash de la transacción no coincide
        }

        return true;
    }

    // Método toString para representar la transacción como una cadena
    @Override
    public String toString() {
        return "Transaction{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", transactionHash='" + transactionHash + '\'' +
                '}';
    }
}
