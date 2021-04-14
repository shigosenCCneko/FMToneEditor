package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import com.fazecast.jSerialComm.SerialPort;

import DataClass.FMDEVICE;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;



public class ConectMidi {
	
static int cnt = 0;
//	private final int  YMF825DATLEN = 30;

	private String midiOutDeviceType = "MIDI";
	
	private MidiDevice midiInput;
	private MidiDevice midiOutput;
	private Receiver midiSendReceiver;
	private Transmitter midiReceivTransmitter;
	private int midiIn,midiOut;
	
	private String midiInDeviceName = "Maple";
	private String midiOutDeviceName ="Maple";
	
	
	private MidiDevice midiStream;
	private Transmitter midiStreamTransmitter;
	private int midiSt;


	private static String midiInDevices [];
	private static String midiOutDevices[];
	private byte midiExmesBuff[] = new byte[30];

	private String midiStreamDeviceName = "Trans";
	private String midioutStreamDeviceName = "Maple";
	private String midiComPort = "COM9";
	
	private OutputStream comOutputStream;
	private InputStream  comInputStream;
	private SerialPort port;
	private int baudRate = 2000000;	
	
	public enum Readtype{eeprom,tonememory,softwaremodulation};	

	

	public ConectMidi() {
		//dumpDeviceInfo();
		getProperties();
		
		//openComPort();		//select device		
		connect_midi();
		
		wait_millsec(400);
		resetDevice();
		wait_millsec(100);

	}
	
	public void wait_millsec(int i){
		try{
			Thread.sleep(i);

		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public void resetDevice(){ //デバイスへリセットコマンドを送信

		wait_millsec(1);
		send_command(0,0,0,0);
		wait_millsec(400);
		//send_command(99,0,0,0);

	

	}
	
	
	public void send_command(int command,int ch,int dat1,int dat2){	//deviceへコマンドの送信
/* select device */
		midi_send_command(command,ch,dat1,dat2);
		//com_send_command(command,ch,dat1,dat2);	
	}
	

	public void aset_tonedata(int addr,int data){
		int ch;
		ch = 0;
		send_command(10,ch,addr,data);

}
	
//	public void writeBurstToneReg(){
//		send_command(9,0,0,0);
//	}	
//	
//	public void write_tonearray(int addr,int data){
//
//		int ch,adr;
//		ch = addr/YMF825DATLEN;
//		adr = addr - ch * YMF825DATLEN;
//
//		send_command(11,ch,adr,data);
//
//	}	
	


	
	
	
	
	private synchronized void get_memof_ymreg(int ch,byte[] buf,Readtype type){
		switch(type){
		case eeprom:
			send_command(6,ch,0,0);
			break;
		
		case tonememory:
			send_command(7,ch,0,0);
			break;
		case softwaremodulation:
			send_command(8,ch,0,0);
			//wait_millsec(100);
			//return;
			break;
		default:
			return;
		}

		get_comm_ymreg(ch,buf);
		try{
			this.wait();	//返信があるまでスレッド停止

		}catch (InterruptedException e){
			e.printStackTrace();
		}

		for(int i = 0;i < FMDEVICE.getInstance().getDatalen();i++){
			buf[i] = midiExmesBuff[i];
		}

	}

	/* システムエクルシーブメッセージが来たらMyMidiReceiverから呼ばれるメソッド
	 * 音色データの呼び出しに使う
	 */

	public synchronized void sys_exmes_recv(byte[] buf){
		int p = 0;
		for(int i = 1;i < 61;i+=2){
			midiExmesBuff[p++] = (byte) ((buf[i]<<4)+buf[i+1]);
		}

		this.notifyAll();

	}
	
	
	
	
	public static void dumpDeviceInfo(){
		ArrayList<MidiDevice> devices = getDevices();
		midiInDevices = new String[devices.size()];
		midiOutDevices = new String[devices.size()];
		int recev,trans;
		for(int i = 0;i< devices.size();i++){
			MidiDevice device = devices.get(i);
			MidiDevice.Info info = device.getDeviceInfo();
			System.out.println(info.toString());


			recev = device.getMaxReceivers();
			trans = device.getMaxTransmitters();

				if( recev == 0){
					System.out.println("Transmit Only");
					midiInDevices[i] = info.toString();
				}
				if( trans == 0){
					System.out.println("receiv ONLY");
					midiOutDevices[i] = info.toString();
				}

			//System.out.println(info.getDescription());


	        System.out.println("[" + i + "] devinfo: " + info.toString());
	    //    System.out.println("  name:"        + info.getName());
	    //    System.out.println("  vendor:"      + info.getVendor());
	    //    System.out.println("  version:"     + info.getVersion());
	     //   System.out.println("  description:" + info.getDescription());



			System.out.println("");


		}
	}
	
	public static ArrayList<MidiDevice> getDevices(){
		ArrayList<MidiDevice> devices = new ArrayList<MidiDevice>();

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

		for(int i =0;i < infos.length;i++){
			MidiDevice.Info info = infos[i];
			MidiDevice dev = null;

			try{
				dev = MidiSystem.getMidiDevice(info);
				devices.add(dev);
			}catch(SecurityException e){
				System.err.println(e.getMessage());

			}catch (MidiUnavailableException e){
				System.err.println(e.getMessage());
			}
		}
		return devices;

	}
	
	public String[] getMidiInDeviceList(){
		return midiInDevices;
	}
	public String[] getMidiOutDeviceList(){
		return midiOutDevices;
	}


	public static int getMidiDeviceNo(String inf,ArrayList<MidiDevice>devices){

		for(int i = 0;i< devices.size();i++){
			MidiDevice device = devices.get(i);
			MidiDevice.Info info = device.getDeviceInfo();
			if(info.toString().equals(inf)){
				return i;
			}
		}
		return -1;
	}
	
	
	private void connect_midi(){		//MIDI デバイスへ接続
		ArrayList<MidiDevice> devices = getDevices();
		MidiDevice dev;
		MidiDevice devout;
		
		midiIn = getMidiDeviceNo(midiInDeviceName,devices);
		devout = devices.get(midiIn);
		if(midiOutDeviceName.contentEquals(midiInDeviceName)){
			if(devout.getMaxTransmitters() == 0){

				midiOut = midiIn;
				for(int i = midiOut +1 ;i <devices.size();i++){
					MidiDevice device = devices.get(i);
					MidiDevice.Info info = device.getDeviceInfo();
					if(info.toString().contentEquals(midiOutDeviceName)){
						midiIn = i;
						break;
					}
				}
			}else{
				for(int i = midiIn +1 ;i <devices.size();i++){
					MidiDevice device = devices.get(i);
					MidiDevice.Info info = device.getDeviceInfo();
					if(info.toString().contentEquals(midiOutDeviceName)){
						midiOut = i;
						break;
					}
				}

			}

		}else{
			midiOut =  getMidiDeviceNo(midiOutDeviceName,devices);

		}
		midiInput = devices.get(midiIn);
		midiOutput = devices.get(midiOut);
		
		try{

			if(! midiInput.isOpen()){
				midiInput.open();
			}

			if(! midiOutput.isOpen()){
				midiOutput.open();
			}



			//Transmitter trans = midi_input.getTransmitter();
			midiSendReceiver = midiOutput.getReceiver();
			MyMidiReceiver myrecv = new MyMidiReceiver(this);
			//trans.setReceiver(myrecv);
			midiReceivTransmitter = midiInput.getTransmitter();
			midiReceivTransmitter.setReceiver(myrecv);

		} catch(MidiUnavailableException e){
			System.err.println(e.getMessage());
		}
		
		
		
		
		
		
		midiSt = getMidiDeviceNo(midiStreamDeviceName,devices);

		if(midiSt == -1 ){

			Alert dialog = new Alert( AlertType.INFORMATION , "MIDI OPEN ERROR"  , ButtonType.YES );
			dialog.showAndWait();
			System.exit(1);
		}
		dev = devices.get(midiSt);
		if(midiStreamDeviceName.contentEquals(midiStreamDeviceName)){
			if(dev.getMaxTransmitters() == 0){
				for(int i = midiSt +1; i<devices.size();i++){
					MidiDevice device = devices.get(i);
					MidiDevice.Info info = device.getDeviceInfo();
					if(info.toString().contentEquals(midiStreamDeviceName)){
						midiSt = i;
						break;
					}
				}
			}else{
				midiSt = getMidiDeviceNo(midiStreamDeviceName,devices);
			}
				
						
			
		}else{
			midiSt = getMidiDeviceNo(midiStreamDeviceName,devices);
		}

		midiStream = devices.get(midiSt);




		try{

			if(! midiStream.isOpen()){
				midiStream.open();
		}

			MyStreamReciever mystream = new MyStreamReciever(this);
			midiStreamTransmitter = midiStream.getTransmitter();
			
			midiStreamTransmitter.setReceiver(mystream);


		} catch(MidiUnavailableException e){
			System.err.println(e.getMessage());
		}

	}
	
	
	public void close()  {					//device をclose
/* select device */
	midiDeviceClose();
	//comDeviceClose();
	System.out.println("close device");	
	}
	
	private void midiDeviceClose() {
		midiReceivTransmitter.close();
		midiSendReceiver.close();
		midiInput.close();
		midiOutput.close();
		midiStreamTransmitter.close();
		midiStream.close();
	}
	private void comDeviceClose() {
		try {
			comOutputStream.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		port.closePort();
	
	}
	
	void com_send_command(int command,int ch,int dat1,int dat2){
//System.out.println(command + "-" + ch + "-" + dat1 + "-" + dat2);
		byte buff[] = new byte[7];
		buff[0] = (byte)0xf0;
		buff[6] = (byte)0xf7;
		
		buff[1] = (byte)(command & 0x007f);
		buff[2] = (byte)(ch      & 0x007f);
		buff[3] = (byte)(dat1    & 0x007f);
		buff[4] = (byte)(dat2    & 0x007f);	
		buff[5] = (byte)(
				((ch & 0x0080)>>5)|
				((dat1 & 0x0080)>>6)|
				((dat2 & 0x0080)>>7));

		try {
	
			for(int i =0;i < 7;i++){	
				if(comOutputStream != null)
					comOutputStream.write(buff[i]);
			}
		
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	void midi_send_command(int command,int ch,int dat1,int dat2){

		byte buff[] = new byte[7];
		buff[0] = (byte)0xf0;
		buff[6] = (byte)0xf7;
		
		buff[1] = (byte)(command & 0x007f);
		buff[2] = (byte)(ch      & 0x007f);
		buff[3] = (byte)(dat1    & 0x007f);
		buff[4] = (byte)(dat2    & 0x007f);

		buff[5] = (byte)(
				((ch & 0x0080)>>5)|
				((dat1 & 0x0080)>>6)|
				((dat2 & 0x0080)>>7));
		
		SysexMessage sxsm = new SysexMessage();

		
		try {
			sxsm.setMessage(buff,7);
		} catch (InvalidMidiDataException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//sendMidimessage(sxsm);
		send_exmessage(sxsm);
	}
	
	
	
	
	
	

	
	public  void send_exmessage(MidiMessage arg0){
		SysexMessage sxsm = new SysexMessage();
		sxsm = ((SysexMessage)arg0);
		midiSendReceiver.send(sxsm, midiOut);
		
		
	}

	public void send_shortmessage(MidiMessage arg0){
		ShortMessage shortmes = new ShortMessage();
		shortmes = ((ShortMessage)arg0);
		midiSendReceiver.send(shortmes,midiOut);
	}

	public void send_metamessage(MidiMessage arg0){
		MetaMessage meta = new MetaMessage();
		meta = ((MetaMessage)arg0);
		midiSendReceiver.send(meta, midiOut);
		
		
	}




	private void getProperties(){
		Properties properties = new Properties();

		try{
			InputStream istream = new FileInputStream("fmToneEditor.properties");
			properties.load(istream);
			midiInDeviceName = properties.getProperty("midiInDeviceName");
			midiOutDeviceName = properties.getProperty("midiOutDeviceName");
			midiStreamDeviceName = properties.getProperty("midiStreamDeviceName");
			midiComPort = properties.getProperty("midiComPort");
			baudRate = Integer.parseInt(properties.getProperty("baudRate"));
			midiOutDeviceType = properties.getProperty("midiOutDeviceType");
			System.out.println("type=" + midiOutDeviceType);
			istream.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	


	
	public synchronized void sendMidimessage(MidiMessage arg0){
/* select device */
		send_shortmessage(arg0);
		//send_com_shortmessage(arg0);
	}		

	private void sendComShortMessage(MidiMessage arg0) {

		try{
			if(comOutputStream != null)
				comOutputStream.write(arg0.getMessage());

		}catch(IOException e)
		{
			System.out.println("Error:"+ e);
			System.exit(1);

		}

	}
	
	
	
	
	/* Com ストリームからデータ受信*/
	public void get_comm_ymreg(int ch,byte[]buf){
		int cnt = 0;	

		try {
			while(true){
			wait_millsec(10);
			cnt = comInputStream.available();
			while(0== cnt) {

				cnt = comInputStream.available();
				System.out.println(cnt);
			}
			cnt = comInputStream.read(buf);
			if( FMDEVICE.getInstance().getDatalen() == cnt)
				break;
			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();	

	}		
	

}
	public void change_comportstat(int i) {
		if(i == 0) {
			System.out.println("close");
			closeComPort();
		}else {
			System.out.println("open");
			openComPort();
		}
	}
	private void closeComPort() {
		try {
			comOutputStream.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		comOutputStream = null;
		port.closePort();
	}

	
	private void openComPort(){
		
			try{
	
			    for (SerialPort sp : SerialPort.getCommPorts()) {
System.out.println(sp.getSystemPortName());
			        if (sp.getSystemPortName().equals(midiComPort)) {
			          port = sp;
			          break;
			        }
			      }		
			    port.openPort();
			
				port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
				port.setBaudRate(baudRate);
				port.setParity(0);
				port.setNumDataBits(8);
				port.setNumStopBits(1);
				
				System.out.println(port.getSystemPortName() + " OPEN");			
		
			
			comOutputStream = port.getOutputStream();	
			comInputStream = port.getInputStream();

		}catch(Exception e)
		{
			System.out.println("Error-:"+ e);
			System.out.println("port open errot");
			Alert dialog = new Alert( AlertType.INFORMATION , (midiComPort +  " OPEN ERROR" ) , ButtonType.YES );
			dialog.showAndWait();

			System.exit(1);
		}

	}
	
	public void send_user_wave(int ch,int i, int d){
		send_command(16,(char)ch,(char)i,(char)d);
//		System.out.println( i + "-" + d);
	}

}