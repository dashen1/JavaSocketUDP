package com.java.socket.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.java.socket.dao.SqlServer;
import com.java.socket.ipui.JMIPV4AddressField;
import com.java.socket.modal.ChatUser;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;

public class WindowBuilder3 extends JFrame implements Runnable{

	private JPanel contentPane;
	private JLabel lblNewLabel;
	private JTextArea textArea;
	private JLabel lblNewLabel_1;
	private JTextField sendText;
	private Thread thread = new Thread(this);
	
	private int receivePort;
	private DatagramPacket dp = null;
    private DatagramSocket ds = null;
    private JMIPV4AddressField ipField;
    
    private ChatUser chatUser = new ChatUser();
    private ClickSendListner3 clickSend = new ClickSendListner3();
	/**
	 * Create the frame.
	 * @throws SocketException
	 * @throws ParseException 
	 */
	public WindowBuilder3() throws SocketException, ParseException{
        byte[] buf = new byte[1024];
        for(int i = 0; i < Config.receivePort.length; i++) {
        	if(available(Config.receivePort[i]) == true) {
        		receivePort = Config.receivePort[i];
        		break;
        	}
        }
        ds = new DatagramSocket(receivePort);
        dp = new DatagramPacket(buf,buf.length);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 304, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel ipLabel = new JLabel("IP:");
		ipLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		ipLabel.setBounds(30, 10, 27, 18);
		contentPane.add(ipLabel);
		
		ipField = new JMIPV4AddressField();
		JPanel panel = new JPanel();
		panel.setBounds(67, 0, 142, 33);
		panel.add(ipField);
		contentPane.add(panel);
		
		lblNewLabel = new JLabel("Chat Message:");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(30, 43, 131, 18);
		contentPane.add(lblNewLabel);
		
		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setSize(220, 212);
		scrollPane.setLocation(40, 78);
		textArea.setEditable(false);
		textArea.setBounds(40, 78, 220, 212);
		contentPane.add(scrollPane);
		
		lblNewLabel_1 = new JLabel("New Message:");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(30, 314, 131, 18);
		contentPane.add(lblNewLabel_1);
		
		sendText = new JTextField();
		sendText.setBounds(30, 342, 169, 24);
		sendText.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if((char)e.getKeyChar()==KeyEvent.VK_ENTER) {
					String userName =  ipField.getIpAddress().trim();
					String msg = sendText.getText();		
					String address;
					try {
						address = InetAddress.getLocalHost().toString().split("/")[1].trim();
						ChatShow.showMsg(textArea, address, msg, "发送：");
						sendText.setText("");
						sendText.requestFocus();
						clickSend.sendMsg(userName, msg, receivePort);
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		contentPane.add(sendText);
		sendText.setColumns(10);
		
		JButton sendBtn = new JButton("Send");
		sendBtn.addActionListener(new ClickSendListner3(ipField,sendText, textArea, receivePort));
		sendBtn.setFont(new Font("宋体", Font.PLAIN, 12));
		sendBtn.setBounds(209, 339, 61, 27);
		contentPane.add(sendBtn);
		
		thread.start();
	}
	
    public static boolean available(int port) {
        if (port < 1 || port > 65536) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }
        try {
        	DatagramSocket ds = new DatagramSocket(port);	
        	ds.setReuseAddress(true);
        	ds.close();
        	return true;
	} catch (SocketException e1) {
			return false;
	}
    }

	public void run() {
        while (true)
        {
            try
            {
                ds.receive(dp);
                int length = dp.getLength();
                String address = dp.getAddress().getHostAddress();
                String msg = new String(dp.getData(),0,length);
                ChatShow.showMsg(textArea, address, msg, "said:");
            }
            catch (Exception e)
            {
            }
        }
	}
}

class ChatShow{
	
	public static void showMsg(JTextArea textArea, String address, String msg, String flagMsg) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String chatTime = df.format(new Date());
        textArea.append(address+"  "+chatTime+" "+flagMsg+"\n");
        textArea.append(msg+"\n");
	}
}

class ClickSendListner3 implements ActionListener{
	private int receivePort;
    private JTextField sendArea;
    private JTextArea showArea;
    private JMIPV4AddressField ipField;
    private ChatUser chatUser = null;
    static String chatTime ="";
    
    ClickSendListner3(){}
    
	public ClickSendListner3(JMIPV4AddressField ipField, JTextField sendArea, JTextArea showArea, int receivePort) {
		this.ipField = ipField;
		this.sendArea = sendArea;
        this.showArea =showArea; 
        this.receivePort = receivePort;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String userName =  ipField.getIpAddress().trim();
		String msg = sendArea.getText();
		String address;		
		try {
			address = InetAddress.getLocalHost().toString().split("/")[1].trim();
			ChatShow.showMsg(showArea, address, msg, "发送：");
			sendMsg(userName,msg,receivePort);
			sendArea.setText("");
			sendArea.requestFocus();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
    }
	
	public void sendMsg(String userName, String chatMsg, int receivePort) {      
        try
        {
        	String ipAddress = InetAddress.getLocalHost().toString().split("/")[1].trim();
            if(!"".equals(userName)) {
            	InetAddress address = InetAddress.getByName(userName);              
                DatagramSocket ds = new DatagramSocket();
                byte[] buf = chatMsg.trim().getBytes();
    			if("127.0.0.1".equals(userName) || ipAddress.equals(userName)) {
    				for(int i=0;i<Config.receivePort.length;i++) {
    					if(receivePort != Config.receivePort[i]) {
    						address = InetAddress.getLocalHost();
    		        		DatagramPacket dp = new DatagramPacket(buf,buf.length,address,Config.receivePort[i]);               
    		                ds.send(dp);
    					}
    	            }
    			}else {
    				for(int i=0;i<Config.receivePort.length;i++) {
    	        		DatagramPacket dp = new DatagramPacket(buf,buf.length,address,Config.receivePort[i]);
    	                ds.send(dp);
    	            }
    			} 
                ds.close();
            }
            chatUser = new ChatUser();
            chatUser.setFlag(1);
            chatUser.setChatMsg(chatMsg);
            chatUser.setIpAddress(ipAddress);
            chatUser.setUserName(userName);
            chatUser.setChatTime(getTimeStamp());
            save(chatUser);
        }
        catch (Exception ee) {}
	}
	
	public void save(ChatUser chatUser) throws Exception {
        SqlServer sqlServer = new SqlServer();
    	sqlServer.insert(chatUser);
	}
	
    public static String getDate() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date new_time=new Date(System.currentTimeMillis());
    	String date = sdf.format(new_time);
    	return date;
    }
    
    public static Timestamp getTimeStamp() {
    	Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		return timeStamp;
    }
}

class Config{
	public static int[] receivePort = {4000,4001,4002,4003,4004,4005};
}