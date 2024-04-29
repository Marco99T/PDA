package p2p;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private JTextField text_send_message;
    private JTextArea text_area_chat;
    private JTextArea text_area_pow;
    private JTextArea text_area_chat_algoritmo_bully;
    private JTextArea text_area_chat_algoritmo_anillo;
    private JTextField text_nickname;
    private JTextArea text_area_chat_files;
    private JLabel lbl_IP_to_show;
    private JLabel lbl_ID_to_show;
    private JButton btn_send_message;
    private JCheckBox check_box_bully;
    private JCheckBox check_box_anillo;
    
    private int ID = -1;
	private String IP = "";
	private final String HOST = "224.0.0.4";
	private int PORT = 5000;
    
    private Chat peer;
    private Person person;
    private Manager_Files manager;
    private Bully bully;
    private Anillo anillo;
    private PowThread pow;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setAutoRequestFocus(false);
		setTitle("P2P");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1150, 650);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(39, 189, 240));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel_algoritmos_eleccion = new JPanel();
		panel_algoritmos_eleccion.setBackground(Color.LIGHT_GRAY);
		panel_algoritmos_eleccion.setBounds(10, 80, 240, 480);
		contentPane.add(panel_algoritmos_eleccion);
		panel_algoritmos_eleccion.setLayout(null);
		
		text_area_chat_algoritmo_bully = new JTextArea();
		text_area_chat_algoritmo_bully.setEditable(false);
		text_area_chat_algoritmo_bully.setFont(new Font("Verdana", Font.PLAIN, 12));
		text_area_chat_algoritmo_bully.setBackground(Color.decode("#EE7227"));
		
		JScrollPane scroll_chat_algoritmo_bully = new JScrollPane( text_area_chat_algoritmo_bully);
		scroll_chat_algoritmo_bully.setBounds(0, 30, 240, 210);
		panel_algoritmos_eleccion.add(scroll_chat_algoritmo_bully);
		
		text_area_chat_algoritmo_anillo = new JTextArea();
		text_area_chat_algoritmo_anillo.setFont(new Font("Verdana", Font.PLAIN, 12));
		text_area_chat_algoritmo_anillo.setEditable(false);
		text_area_chat_algoritmo_anillo.setBackground(Color.decode("#EE7227"));
			
		JScrollPane scroll_chat_algoritmo_anillo = new JScrollPane(text_area_chat_algoritmo_anillo);
		scroll_chat_algoritmo_anillo.setBounds(0, 270, 240, 210);
		panel_algoritmos_eleccion.add(scroll_chat_algoritmo_anillo);
		
		
		JButton btn_bully = new JButton("Bully");
		btn_bully.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn_bully.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bully.option_to_send_message(2);
			}
		});
		btn_bully.setBounds(0, 0, 240, 30);
		panel_algoritmos_eleccion.add(btn_bully);
		
		JButton btn_anillo = new JButton("Anillo");
		btn_anillo.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn_anillo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				anillo.options_of_messagess_to_send(2);
			}
		});
		btn_anillo.setBounds(0, 240, 240, 30);
		panel_algoritmos_eleccion.add(btn_anillo);
		
		JPanel panel_algoritmos_consencio = new JPanel();
		panel_algoritmos_consencio.setBackground(Color.decode("#AF27EE"));
		panel_algoritmos_consencio.setBounds(280, 60, 240, 500);
		contentPane.add(panel_algoritmos_consencio);
		panel_algoritmos_consencio.setLayout(null);

		text_area_pow = new JTextArea();
		text_area_pow.setEditable(false);
		text_area_pow.setFont(new Font("Verdana", Font.PLAIN, 12));
		text_area_pow.setBackground(Color.decode("#25EDF3"));
		
		JScrollPane scroll_pow = new JScrollPane(text_area_pow);
		scroll_pow.setBounds(0, 0, 240, 500);
		panel_algoritmos_consencio.add(scroll_pow);
		
		JPanel panel_chat = new JPanel();
		panel_chat.setBounds(550, 60, 280, 500);
		contentPane.add(panel_chat);
		panel_chat.setLayout(null);
		
		text_area_chat = new JTextArea();
		text_area_chat.setEditable(false);
		text_area_chat.setFont(new Font("Verdana", Font.PLAIN, 12));
		text_area_chat.setBackground(Color.decode("#25EDF3"));
		
		JScrollPane scroll_chat = new JScrollPane(text_area_chat);
		scroll_chat.setBounds(0, 0, 280, 440);
		panel_chat.add(scroll_chat);
		
		JPanel panel_send_message = new JPanel();
		panel_send_message.setBounds(0, 470, 230, 30);
		panel_chat.add(panel_send_message);
		panel_send_message.setLayout(new BorderLayout(0, 0));
		
		text_send_message = new JTextField();
		text_send_message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send_message();
			}
		});
		text_send_message.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_send_message.add(text_send_message, BorderLayout.CENTER);
		
		btn_send_message = new JButton("Enviar");
		btn_send_message.setBounds(230, 470, 50, 30);
		btn_send_message.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn_send_message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send_message();
			}
		});
		ImageIcon icon_send = new ImageIcon(Main.class.getResource("resources/enviar.png"));
		int width = btn_send_message.getWidth();
        int height = btn_send_message.getHeight();
        ImageIcon scaledIcon = new ImageIcon(icon_send.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_AREA_AVERAGING));
        btn_send_message.setIcon(scaledIcon);
        panel_chat.add(btn_send_message);
		//panel_send_message.add(btn_send_message, BorderLayout.EAST);
		
		JPanel panel_EOD = new JPanel();
		panel_EOD.setBackground(Color.LIGHT_GRAY);
		panel_EOD.setBounds(860, 60, 240, 500);
		contentPane.add(panel_EOD);
		panel_EOD.setLayout(null);
		
		JButton btn_upload_file = new JButton("Subir");
		btn_upload_file.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn_upload_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    boolean file_validated = validate_file(selectedFile.getName());
                    if(file_validated) {
                    	manager.send_file(selectedFile.getAbsolutePath(), selectedFile.getName());
                    	JOptionPane.showMessageDialog(null, "Enviado ");
                    }
                    else {
                    	JOptionPane.showMessageDialog(null, "Error al enviar archivo ");
                    }
                }
				
			}
		});
		btn_upload_file.setBounds(0, 470, 120, 30);
		ImageIcon icon_upload = new ImageIcon(Main.class.getResource("resources/subir.png"));
		int width_upload = 40;//btn_upload_file.getWidth();
        int height_upload = 40;//btn_upload_file.getHeight();
        ImageIcon scaled_icon_upload = new ImageIcon(icon_upload.getImage().getScaledInstance(width_upload, height_upload, java.awt.Image.SCALE_SMOOTH));
        btn_upload_file.setIcon(scaled_icon_upload);
		panel_EOD.add(btn_upload_file);
		
		JButton btn_show_files = new JButton("Ver");
		btn_show_files.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn_show_files.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ruta = "/files_recived";
				String defau = System.getProperty("user.dir");
				// Verificar si el Desktop es soportado
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					File directorio = new File(defau + ruta);
					
					try {
						// Abrir el directorio en el explorador de archivos
						desktop.open(directorio);
					} catch (IOException error) {
						error.printStackTrace();
					}
				} else {
					System.out.println("El Desktop no es soportado en este sistema.");
				}
			}
		});
		btn_show_files.setBounds(120, 470, 120, 30);
		ImageIcon icon_show_files = new ImageIcon(Main.class.getResource("resources/directorio.png"));
		int width_show_files = 40;//btn_show_files.getWidth();
        int height_show_files = 40;//btn_show_files.getHeight();
        ImageIcon scaled_icon_show_files = new ImageIcon(icon_show_files.getImage().getScaledInstance(width_show_files, height_show_files, java.awt.Image.SCALE_SMOOTH));
        btn_show_files.setIcon(scaled_icon_show_files);
		panel_EOD.add(btn_show_files);
		
		text_area_chat_files = new JTextArea();
		text_area_chat_files.setFont(new Font("Verdana", Font.PLAIN, 12));
		text_area_chat_files.setBackground(Color.decode("#E0B0FF"));
		text_area_chat_files.setEditable(false);
		
		JScrollPane scroll_chat_files = new JScrollPane(text_area_chat_files);
		scroll_chat_files.setBounds(0, 0, 240, 470);
		panel_EOD.add(scroll_chat_files);
		
		
		JLabel lbl_algoritmos_1 = new JLabel("Algoritmos de eleccion");
		lbl_algoritmos_1.setFont(new Font("Verdana", Font.ITALIC, 14));
		lbl_algoritmos_1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_algoritmos_1.setBounds(10, 10, 240, 45);
		contentPane.add(lbl_algoritmos_1);
		
		JLabel lbl_algoritmos_2 = new JLabel("Algortimos de consorcio");
		lbl_algoritmos_2.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_algoritmos_2.setFont(new Font("Verdana", Font.ITALIC, 14));
		lbl_algoritmos_2.setBounds(280, 10, 240, 45);
		contentPane.add(lbl_algoritmos_2);
		
		JLabel lbl_chat = new JLabel("Chat");
		lbl_chat.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_chat.setFont(new Font("Verdana", Font.ITALIC, 14));
		lbl_chat.setBounds(550, 10, 280, 45);
		contentPane.add(lbl_chat);
		
		JLabel lbl_chat_EOD = new JLabel("Espacio de objetos distribuido");
		lbl_chat_EOD.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_chat_EOD.setFont(new Font("Verdana", Font.ITALIC, 14));
		lbl_chat_EOD.setBounds(860, 10, 240, 45);
		contentPane.add(lbl_chat_EOD);
		
		JLabel lbl_nickname = new JLabel("NICKNAME");
		lbl_nickname.setFont(new Font("Arial", Font.BOLD, 12));
		lbl_nickname.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_nickname.setBounds(0, 440, 110, 30);
		panel_chat.add(lbl_nickname);
		
		text_nickname = new JTextField();
		text_nickname.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
		text_nickname.setBounds(110, 440, 170, 30);
		panel_chat.add(text_nickname);
		text_nickname.setColumns(10);
		
		JButton btn_exit = new JButton("Salir");
		btn_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bully.option_to_send_message(4);
				System.exit(0);
			}
		});
		btn_exit.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
		btn_exit.setBounds(10, 570, 150, 30);
		contentPane.add(btn_exit);
		
		JLabel lbl_ID = new JLabel("ID:");
		lbl_ID.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		lbl_ID.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ID.setBounds(200, 570, 60, 30);
		contentPane.add(lbl_ID);
		
		JLabel lbl_IP = new JLabel("IP:");
		lbl_IP.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_IP.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		lbl_IP.setBounds(400, 570, 60, 30);
		contentPane.add(lbl_IP);
		
		lbl_ID_to_show = new JLabel("");
		lbl_ID_to_show.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_ID_to_show.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		lbl_ID_to_show.setBounds(260, 570, 100, 30);
		contentPane.add(lbl_ID_to_show);
		
		lbl_IP_to_show = new JLabel("");
		lbl_IP_to_show.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_IP_to_show.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 15));
		lbl_IP_to_show.setBounds(460, 570, 140, 30);
		contentPane.add(lbl_IP_to_show);
		
		JButton powthread = new JButton("Block");
		powthread.setBounds(600, 570, 140, 30);
		powthread.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pow.newCadenadeBloquesEnviar();
			}
		});
		contentPane.add(powthread);
		
		check_box_bully = new JCheckBox("Bully");
		check_box_bully.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(check_box_bully.isSelected()) {
					check_box_anillo.setEnabled(false);
					btn_bully.setEnabled(true);
					scroll_chat_algoritmo_bully.setEnabled(true);
					bully = new Bully(HOST, PORT + 1500, text_area_chat_algoritmo_bully, ID, IP);
					bully.start();
				}
				else {
					check_box_anillo.setEnabled(true);
					btn_bully.setEnabled(false);
					scroll_chat_algoritmo_bully.setEnabled(false);
					text_area_chat_algoritmo_bully.setText("");
				}
			}
		});
		check_box_bully.setHorizontalAlignment(SwingConstants.CENTER);
		check_box_bully.setBounds(10, 50, 90, 20);
		contentPane.add(check_box_bully);
		
		check_box_anillo = new JCheckBox("Anillo");
		check_box_anillo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(check_box_anillo.isSelected()) {
					check_box_bully.setEnabled(false);
					btn_anillo.setEnabled(true);
					scroll_chat_algoritmo_anillo.setEnabled(true);
					anillo = new Anillo(HOST, PORT + 1000, text_area_chat_algoritmo_anillo, ID, IP);
					anillo.start();
				}
				else {
					check_box_bully.setEnabled(true);
					btn_anillo.setEnabled(false);
					scroll_chat_algoritmo_anillo.setEnabled(false);
					text_area_chat_algoritmo_anillo.setText("");
				}
			}
		});
		check_box_anillo.setHorizontalAlignment(SwingConstants.CENTER);
		check_box_anillo.setBounds(160, 50, 90, 20);
		contentPane.add(check_box_anillo);
		
		start_objects("224.0.0.4", 5000);
		
	}
	
	
	private void start_objects(String host, int port) {
		try {
			ID = Integer.parseInt(InetAddress.getLocalHost().getHostAddress().substring(7).replace(".", ""));
			IP = String.valueOf(InetAddress.getLocalHost().getHostAddress());
			lbl_ID_to_show.setText(String.valueOf(ID));
			lbl_IP_to_show.setText(IP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		peer = new Chat(host, port, text_area_chat, IP);	
		peer.start();
		manager = new Manager_Files(host, port + 500, text_area_chat_files);
		manager.start();
		/*
		anillo = new Anillo(host, port + 1000, text_area_chat_algoritmo_anillo, ID, IP);
		anillo.start();
		bully = new Bully(host, port + 1500, text_area_chat_algoritmo_bully, ID, IP);
		bully.start();
		*/
		pow = new PowThread(host , port, text_area_pow);
		pow.start();
	}
	

	private void send_message() {
		person = new Person(text_nickname.getText(), text_send_message.getText());
		peer.send_message(person);
		text_send_message.setText("");
    }
	
	private boolean validate_file(String name) {
		short size = (short) name.length();
		String extention_file = name.substring(size - 3);
		
		if (	extention_file.equals("pdf")
				|| extention_file.equals("mp3")
				|| extention_file.equals("jpg")
				|| extention_file.equals("png")
				|| extention_file.equals("mp4")
				|| extention_file.equals("txt"))	return true;
		
		return false;
	}
}
