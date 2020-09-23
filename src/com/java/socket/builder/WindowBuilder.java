package com.java.socket.builder;

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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;

public class WindowBuilder extends JFrame implements Runnable{

	private JPanel contentPane;
	private JLabel lblNewLabel;
	private JTextArea textArea;
	private JLabel lblNewLabel_1;
	private JTextField sendText;
	private Thread thread = new Thread(this);
	
	private int receivePort;
	private int myPort;
	private int sendPort = 0;
	private DatagramPacket dp = null;
    private DatagramSocket ds = null;
    private ChatUser chatUser = new ChatUser();
    private ClickSendListner click;
    private JTextField ipField;
	/**
	 * Create the frame.
	 * @throws SocketException 
	 * @throws ParseException 
	 */
	public WindowBuilder() throws SocketException, ParseException{
		click = new ClickSendListner();
		if(click.getChatUser() == null) {
			System.out.println("对象为空");
		}
        byte[] buf = new byte[1024];
        for(int i = 0; i < Config.portArr.length; i++) {
        	if(available(Config.portArr[i]) == true) {
        		receivePort = Config.portArr[i];
        		sendPort = Config.portArr[i+1];
        		break;
        	}
        }
        System.out.println("产生的端口号是："+receivePort);
        ds = new DatagramSocket(receivePort);
        dp = new DatagramPacket(buf,buf.length);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 380, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel ipLabel = new JLabel("IP:");
		ipLabel.setFont(new Font("宋体", Font.PLAIN, 18));
		ipLabel.setBounds(30, 10, 27, 18);
		contentPane.add(ipLabel);
		
		lblNewLabel = new JLabel("Chat Message:");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 18));
		lblNewLabel.setBounds(30, 43, 131, 18);
		contentPane.add(lblNewLabel);
		
		textArea = new JTextArea();
		textArea.setBounds(40, 78, 288, 201);
		contentPane.add(textArea);
		
		lblNewLabel_1 = new JLabel("New Message:");
		lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(30, 300, 131, 18);
		contentPane.add(lblNewLabel_1);
		
		sendText = new JTextField();
		sendText.setBounds(30, 342, 213, 24);
		contentPane.add(sendText);
		sendText.setColumns(10);
		
//        JMIPV4AddressField ipFiled = new JMIPV4AddressField("");
//        ipFiled.setIpAddress("");
//        JPanel pl = new JPanel();
//        pl.add(ipFiled);
//        contentPane.add(ipField);
		
		ipField = new JTextField();
		ipField.setBounds(68, 10, 206, 21);
		contentPane.add(ipField);
		ipField.setColumns(10);
		
		JButton sendBtn = new JButton("Send");
		sendBtn.addActionListener(new ClickSendListner(ipField,sendText, textArea, sendPort, receivePort));
		sendBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		sendBtn.setBounds(252, 340, 76, 27);
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
                int port = dp.getPort();
                String message = new String(dp.getData(),0,length);
                textArea.append(address+" "+" said:"+"\n");
                textArea.append(message+"\n");
            }
            catch (Exception e)
            {
            }
        }  
	}
}

class ClickSendListner implements ActionListener{
    private JFrame QQFrame;
    private JTextField sendArea;
    private JTextArea showArea;
    private JTextField ipAndPort;
    private int sendPort;
    private int receivePort;
    private ChatUser chatUser = null;
    
    ClickSendListner(){}
    
	public ClickSendListner(JTextField in_ipAndPort, JTextField in_sendArea, JTextArea in_showArea, int sendPort, int receivePort) {
		ipAndPort = in_ipAndPort;
		sendArea = in_sendArea;
        showArea =in_showArea;
        this.sendPort = sendPort;
        this.receivePort = receivePort;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String userName =  ipAndPort.getText();
		String chatMsg = sendArea.getText();
		System.out.println("目标ip:"+userName+"\n");
		System.out.println("发送的消息："+chatMsg);
        byte[] buf = sendArea.getText().trim().getBytes();
        try
        {
            InetAddress address = InetAddress.getByName(userName);
            InetAddress localAddress = InetAddress.getLocalHost();
            
            String ipAddress = InetAddress.getLocalHost().toString().trim();
            String goalAddress = address.toString().split("/")[1].trim();
            chatUser = new ChatUser();
            chatUser.setFlag(1);
            chatUser.setChatMsg(chatMsg);
            chatUser.setIpAddress(ipAddress);
            chatUser.setUserName(userName);
            chatUser.setChatTime(getTimeStamp());
            //判断要发送消息到目标的地址ip是本机还是其他
            if("127.0.0.1".equals(goalAddress) || ipAddress.equals(goalAddress)) {
                for(int i=0;i<Config.portArr.length;i++) {
                	DatagramPacket dp = new DatagramPacket(buf,buf.length,address,Config.portArr[i]);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ds.close();
                }
            }else {
            	DatagramPacket dp = new DatagramPacket(buf,buf.length,address,receivePort);
                DatagramSocket ds = new DatagramSocket();
                ds.send(dp);
                ds.close();
            }  
            save(chatUser);
            System.out.println("数据保存成功！");
        }
        catch (Exception ee) {}
    }
	
	public ChatUser getChatUser() {
		return chatUser;
	}
	
	//将聊天记录保存到数据库 String chatMsg, Timestamp timeStamp, String ipAddress, String userName
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
	public static int[] portArr = {4000,4001,4002,4003,4004};
}