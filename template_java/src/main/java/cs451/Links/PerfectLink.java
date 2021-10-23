// Inspired from https://www.baeldung.com/udp-in-java 
// https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
// https://stackoverflow.com/questions/10055913/set-timeout-for-socket-receive

package cs451.Links;

import cs451.*;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket; 
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.SocketException;


public class PerfectLink  {

    private static final String IP_START_REGEX = "/";
    private static final int RECEIVE_BUFF_LENGTH = 8000;
    private static final int PORT_PREFIX = 11000;

    private FairLossLink flLink;
    private DatagramSocket socket;
    private String ip;
    private int port;
    private int hostId;
    private byte[] sendBuf;
    private byte[] receiveBuf = new byte[RECEIVE_BUFF_LENGTH];
    private ArrayList<String> delivered = new ArrayList<>();
    private ArrayList<String> deliveredLog = new ArrayList<>();
    private ArrayList<String> sentLog = new ArrayList<>();
    private boolean stopReceiving = false;

    public PerfectLink(String ip, int port, int hostId) throws IOException {
        this.flLink = new FairLossLink(ip, port, hostId);
        this.ip = flLink.getIp();
        this.port = flLink.getPort();
        this.socket = flLink.getSocket();
        this.hostId = flLink.getHostId();
    }

    public void send(Message message, String destinationIp, int destinationPort) throws IOException, UnknownHostException {
        connectSocket();
        System.out.println("inside send");
        boolean acked = false;
        while(!acked){
            flLink.send(message, destinationIp, destinationPort);
            acked = checkIfAcked(message, destinationPort);
        }
        sentLog.add("b " + message.getId());
    }

    public void receive() throws SocketException, UnknownHostException, IOException {
        int timeoutCount = 0;
        connectSocket();
        DatagramPacket rcvPacket = new DatagramPacket(receiveBuf, RECEIVE_BUFF_LENGTH);
        socket.setSoTimeout(2000);   // set the timeout in millisecounds.
        while(true){        // recieve data until timeout
            try {
                if(isClosed()) {
                    break;
                }
                socket.receive(rcvPacket);
                deliver(rcvPacket);
                this.receiveBuf = new byte[RECEIVE_BUFF_LENGTH]; 
            }
            catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("Timeout reached!!! ");
                if(stopReceiving){
                    break;
                }
            }
            if(stopReceiving){
                break;
            }
        }
    }

    private boolean checkIfAcked(Message message, int destinationPort) throws SocketException, UnknownHostException, IOException {
        connectSocket();
        DatagramPacket ackPacket = new DatagramPacket(receiveBuf, RECEIVE_BUFF_LENGTH);
        socket.setSoTimeout(2000);
        while(true) {
            try {
                socket.receive(ackPacket);
                String contents = new String(ackPacket.getData(), 0, ackPacket.getLength());
                if((destinationPort == ackPacket.getPort()) && contents.contains("ack")) {
                    System.out.println("Host " + ackPacket.getAddress() + ", " + ackPacket.getPort() + " acked");
                    return true;
                } else {
                    deliver(ackPacket);
                }
                this.receiveBuf = new byte[RECEIVE_BUFF_LENGTH];
            }
            catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("Timeout reached!!! in PerfectLink of host " + String.valueOf(hostId));
                return false;
            }
        }
    }

    private void deliver(DatagramPacket packet) throws UnknownHostException, IOException{
        String rcvd = getRcvdFromPacket(packet);
        if(!delivered.contains(rcvd)) {
            System.out.println(rcvd + " : delivered");
            String data = new String(packet.getData(), 0, packet.getLength());
            int rcvdId = getMessageIdFromData(data);
            sendAck(packet.getAddress().toString(), packet.getPort());
            String senderPort = getSenderIdFromPort(packet.getPort());
            deliveredLog.add("d " + senderPort + " " + String.valueOf(rcvdId));
            delivered.add(rcvd);
        } else {
            System.out.println(rcvd + " : already delivered");
        }
    }

    private String getRcvdFromPacket(DatagramPacket dp) {
        return "rcvd from " + dp.getAddress() + ", " + dp.getPort() + ": "+ new String(dp.getData(), 0, dp.getLength());
    }

    private void sendAck(String senderIp, int senderPort) throws UnknownHostException, IOException {
        String contents = "ack " + String.valueOf(senderPort);
        Message ack = new Message(this.hostId, 0, ip, port, contents);
        connectSocket();
        flLink.send(ack, senderIp, senderPort);
    }

    private int getMessageIdFromData(String data) {
        String[] splitData = data.split("\\s+");
        System.out.println(data.charAt(0));
        return Integer.parseInt(splitData[0]);
    }

    private String getSenderIdFromPort(int port) {
        return String.valueOf(port-PORT_PREFIX);
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public int getHostId() {
        return this.hostId;
    }

    public void close() {
        socket.close();
    }

    public boolean getStopReceiving() {
        return this.stopReceiving;
    }

    public void setStopReceiving(boolean newVal) {
        this.stopReceiving = newVal;
    }

    public void open() throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
    }

    public ArrayList<String> getDelivered() {
        return this.delivered;
    }

    public ArrayList<String> getDeliveredLog() {
        return this.deliveredLog;
    }

    public ArrayList<String> getSentLog() {
        return this.sentLog;
    }

    public void connectSocket() throws UnknownHostException {
        if(socket.isClosed()) {
            socket.connect(InetAddress.getByName(ip), port);
        }
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }


}