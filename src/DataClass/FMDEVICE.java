package DataClass;

import java.util.Enumeration;
import java.util.Vector;

import MyEvent.MyDataEvent;
import MyEvent.MyDataListener;
import MyEvent.Observer;
import MyEvent.eventSource;
import application.ConectMidi;
import javafx.event.EventType;





public class FMDEVICE  {
static  final int  DATA_LEN = 30;
static  final int  TONSET_LEN = 2280;


static final int CHANNEL_VAL = 6;
static final int MAX_OPERATOR = 4;


static final int OFS_AR 		= 0;
static final int OFS_DR			= 1;
static final int OFS_SR 		= 2;
static final int OFS_SL 		= 3;
static final int OFS_RR			= 4;
static final int OFS_MULTI	 	= 5;
static final int OFS_TLV 		= 6;
static final int OFS_FB		 	= 7;
static final int OFS_WS			= 8;
static final int OFS_MOF		= 9;
static final int OFS_WS2		=10;
static final int OFS_MOFONE		=11;


class OPERATOR{
	int feedback;
	int attack;
	int decey;
	int sustain;
	int sustainLevel;
	int release;
	int multiple;
	int waveSelect;		//use opl series
	int waveSelect2;	//use STM32 synthesizer
	int totalLevel;
	
	int ksr;
	int ksl;
	int dt;
	int dt2;			//use YM2151
	
	int eam;
	int dam;
	int evb;
	int dvb;
	
	int morph;			//use STM32synthesizer
	int morph_once;
};

class FMCHANNEL{
	OPERATOR[] operator;
		
};

	private FMCHANNEL[] fmchannel;

    double userWaveData[][] = new double[4][256];

	private byte toneData[] = new byte[TONSET_LEN];
	private MyDataListener listener;
	
	private ConectMidi midiDev;
	private int editChannelNo = 0;

	private boolean notifyStop = false;


	Vector<Observer> observers;
	
	private FMDEVICE() {
		fmchannel = new FMCHANNEL[CHANNEL_VAL];
		for(int i = 0;i < CHANNEL_VAL;i++) {
			fmchannel[i] = new FMCHANNEL();
			fmchannel[i].operator = new OPERATOR[4];
			for(int j = 0; j < MAX_OPERATOR;j++) {
				fmchannel[i].operator[j] = new OPERATOR();
			}
		}
		
		
		midiDev= new ConectMidi();
		toneDataInit();
		observers = null;

	}

/* Singleton 定義 */
	public static class FmToneDataInstanceHolder{
		private static final FMDEVICE INSTANCE = new FMDEVICE();

	}

	public static FMDEVICE getInstance() {


		return FmToneDataInstanceHolder.INSTANCE;
	}


/* 変更通知 */
	public void attach(Observer o) {
		if(observers == null) {
			observers = new Vector<Observer>();
		}
		observers.addElement(o);
	}

	public void detatch(Observer o) {
		observers.removeElement(o);

	}

	public void setEditChannel(int ch){
		notifyChange(MyDataEvent.CHANGECHANNEL,eventSource.ToneChange,0,0,0);
		editChannelNo = ch;
	}
	
	public int getEditChannel() {
		return editChannelNo;
	}


	public void get_tonememory(int ch,byte[] buf){
		//midiDev.get_tonememory(ch, buf);
	}


	public int getAlgorithmNo(int ch){
		int i;
		i =  getValue(ch,0,eventSource.Connect);
		return i;

	}


	public  byte[] getToneDataSet() {
		return toneData;

	}

	public   void getToneData(int no, byte[] buf) {
//			no = no * DATA_LEN;
//			for(int i = 0; i < DATA_LEN ; i++) {
//				buf[i] = toneData[no + i];
//			}
	}

//	public void setOpData(int ch,  byte buf[]){		/* Panelから書き込む場合 */
//		int adr,i;
//		adr = ch * DATA_LEN;
//		for(i = 0;i < DATA_LEN;i++){
//			toneData[adr+ i] = buf[i];
//			midiDev.write_tonearray(adr+i,(0x00ff & buf[i]));
//
//		}
//		midiDev.writeBurstToneReg();
//	}


	public  void setTone(int ch,byte data[]) {
//		int adr = ch * DATA_LEN;
//		for(int i = 0; i < DATA_LEN;i ++) {
//			toneData[adr + i] = data[i];
//			midiDev.write_tonearray(adr + i, data[i]);
//		}
//		midiDev.writeBurstToneReg();
//		notifyChange(MyDataEvent.DATA_UPDATE,eventSource.ToneChange,0,0,0);
	}

	public  void setToneSet(byte data[]) {
//		for(int i = 0; i<TONSET_LEN;i++) {
//			toneData[i] = data[i];
//			midiDev.write_tonearray(i, data[i]);
//		}
//		midiDev.writeBurstToneReg();
//		notifyChange(MyDataEvent.DATA_UPDATE,eventSource.ToneChange,0,0,0);
	}


	/* get parameter value */
	public  int getValue(int ch,int opno, eventSource source) {
		int val  = 0;
		
		switch(source) {
		case SLIDER1:
			val = fmchannel[ch].operator[opno].attack;
			break;

		case SLIDER2:
			val = fmchannel[ch].operator[opno].decey;
			break;

		case SLIDER3:
			val = fmchannel[ch].operator[opno].sustain;
			break;
			
		case SLIDER4:
			val = fmchannel[ch].operator[opno].sustainLevel;
			break;

		case SLIDER5:
			val = fmchannel[ch].operator[opno].release;
			break;

		case SLIDER6:
			val = fmchannel[ch].operator[opno].multiple;
			break;

		case SLIDER7:
			val = fmchannel[ch].operator[opno].totalLevel;
			break;

		case Wave:
			val = fmchannel[ch].operator[opno].waveSelect;
			break;
			
		case Wave2:
			val = fmchannel[ch].operator[opno].waveSelect2;
			break;

		case Ksl:
			val = fmchannel[ch].operator[opno].ksl;
			break;
			
		case Ksr:
			val = fmchannel[ch].operator[opno].ksr;
			break;
			
		case DT:
			val = fmchannel[ch].operator[opno].dt;
			break;
			
		case EAM:
			val = fmchannel[ch].operator[opno].eam;
			break;
			
		case Dam:
			val = fmchannel[ch].operator[opno].dam;
			break;
			
		case EVB:
			val = fmchannel[ch].operator[opno].evb;
			break;
			
		case Dvb:
			val = fmchannel[ch].operator[opno].dvb;
			break;
			
			
			
		case Morf:
			val = fmchannel[ch].operator[opno].morph;
			break;
			
		case MorphOnce:
			val = fmchannel[ch].operator[opno].morph_once;
			break;

		default:
			break;

		}
		val =val & 0x00ff;
		return val;
	}

	/* set parameter valur */

	public void notifyStop(boolean i) {
		notifyStop = i;
	}



	public  void setValue(eventSource source,int ch, int opno, int val) {
		val &= 0x00ff;

		if(notifyStop == false) {
			notifyChange(MyDataEvent.OPDATA_CHANGE,source,ch,opno,val);
		}

			switch(source) {

			case SLIDER1:
				fmchannel[ch].operator[opno].attack = val;
				break;

			case SLIDER2:
				fmchannel[ch].operator[opno].decey = val;
				break;

			case SLIDER3:
				fmchannel[ch].operator[opno].sustain = val;
				break;

			case SLIDER4:
				fmchannel[ch].operator[opno].sustainLevel = val;
				break;
				
			case SLIDER5:
				fmchannel[ch].operator[opno].release = val;
				break;

			case SLIDER6:
				fmchannel[ch].operator[opno].multiple = val;
				break;

			case SLIDER7:
				fmchannel[ch].operator[opno].totalLevel = val;
				break;
				
			case Wave:
				fmchannel[ch].operator[opno].waveSelect = val;
				break;

			case Wave2:
				fmchannel[ch].operator[opno].waveSelect2 = val;
				break;

			case Ksl:
				fmchannel[ch].operator[opno].ksl = val;
				break;
			
			case Ksr:
				fmchannel[ch].operator[opno].ksr = val;
				break;
				
			case DT:
				fmchannel[ch].operator[opno].dt = val;
				break;
				
			case EAM:
				fmchannel[ch].operator[opno].eam = val;
				break;
				
			case Dam:
				fmchannel[ch].operator[opno].dam = val;
				break;
				
			case EVB:
				fmchannel[ch].operator[opno].evb = val;
				break;
				
			case Dvb:
				fmchannel[ch].operator[opno].dvb = val;
				break;

			case Morf:
				fmchannel[ch].operator[opno].morph = val;
				break;
			
			case FeedBK:
				fmchannel[ch].operator[opno].feedback = val;
				break;

			case MorphOnce:
				fmchannel[ch].operator[opno].morph_once = val;
				break;
	
			default:
				return;
				//break;

			}
			/* opno  上位2bit */
			midiDev.send_command(10,ch,source.getNumber() +  64 * opno, val);

	}



	void toneDataInit(){
			notifyStop(true);
			for(int i= 0;i <CHANNEL_VAL;i++){
				for(int j = 0; j < MAX_OPERATOR;j++) {
					setValue(eventSource.SLIDER1, i, j, 15);
					fmchannel[i].operator[j].attack = 255;
					fmchannel[i].operator[j].decey = 0;
					fmchannel[i].operator[j].sustain = 0;
					fmchannel[i].operator[j].sustainLevel = 0;
					fmchannel[i].operator[j].release = 8;
					fmchannel[i].operator[j].multiple = 1;
					fmchannel[i].operator[j].totalLevel = 0;
					
					fmchannel[i].operator[j].ksl = 0;
					fmchannel[i].operator[j].dt = 0;
					fmchannel[i].operator[j].dt2 = 0;
					fmchannel[i].operator[j].eam = 0;
					fmchannel[i].operator[j].dam = 0;
					fmchannel[i].operator[j].evb = 0;
					fmchannel[i].operator[j].dvb = 0;
					
					fmchannel[i].operator[j].morph = 0;
					fmchannel[i].operator[j].morph_once = 0;
						
					fmchannel[i].operator[j].waveSelect = 0;
					fmchannel[i].operator[j].waveSelect2 = 0;
					
					fmchannel[i].operator[j].morph = 0;
					fmchannel[i].operator[j].morph_once = 0;
					
				}
			}
			for( int ch = 0;ch < CHANNEL_VAL;ch++) {
				for(int opno = 0;opno< MAX_OPERATOR;opno++) {
					midiDev.send_command(eventSource.SLIDER1.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].attack);
					midiDev.send_command(eventSource.SLIDER2.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].decey);
					midiDev.send_command(eventSource.SLIDER3.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].sustain);
					midiDev.send_command(eventSource.SLIDER4.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].sustainLevel);
					midiDev.send_command(eventSource.SLIDER5.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].release);
					midiDev.send_command(eventSource.SLIDER6.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].multiple);
					midiDev.send_command(eventSource.SLIDER7.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].totalLevel);
					midiDev.send_command(eventSource.Wave.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].waveSelect);
					midiDev.send_command(eventSource.Wave2.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].waveSelect2);
					midiDev.send_command(eventSource.Ksl.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].ksl);
					midiDev.send_command(eventSource.DT.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].dt);
					midiDev.send_command(eventSource.EAM.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].eam);
					midiDev.send_command(eventSource.Dam.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].dam);
					midiDev.send_command(eventSource.EVB.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].evb);
					midiDev.send_command(eventSource.Dvb.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].dvb);
					midiDev.send_command(eventSource.Morf.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].morph);
					midiDev.send_command(eventSource.MorphOnce.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].morph_once);
					
					midiDev.send_command(eventSource.Wave.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].waveSelect);
					midiDev.send_command(eventSource.Wave2.getNumber(), ch, opno, 
							fmchannel[ch].operator[opno].waveSelect2);
					
				}
			}
			notifyStop(false);
	}

	/* ----------------- 演奏モード設定 ----------*/

	public void monoMode() {
		//midiDev.playMode(0);
	}

	public void polyMode() {
		//midiDev.playMode(1);
	}

	public void d8polyMode() {
		//midiDev.playMode(3);
	}

/* -------------------------------------------- */


public void noteOn(int ch,int noteNo,int vel){
	midiDev.send_command(16,ch,noteNo,vel);
}

public void noteOff(int ch,int noteNo){
	midiDev.send_command(17,ch,noteNo,0);
}



/* ---------- Software modulation function ----- */

public void SmodulationSeneAllParameter(int channel,int sinPitch
		,int sinDepth,int waveNo,int modulateRate,int delayValue) {


	midiDev.send_command(19, channel, sinPitch,0);
	midiDev.send_command(20, channel, sinDepth*2, 0);
	midiDev.send_command(21, channel, waveNo, 0);
	midiDev.send_command(22, channel, modulateRate, 0);
	midiDev.send_command(23, channel, delayValue, 0);
}

public void changeSmodulation(int midiChannelNo,int modulation) {
	midiDev.send_command(18, midiChannelNo, modulation,0);
}

public void changeSinPitch(int midiChannelNo,int sinPitch) {
	midiDev.send_command(19, midiChannelNo, sinPitch,0);
}

public void changeSinDepth(int midiChannelNo,int sinDepth) {
	midiDev.send_command(20, midiChannelNo , sinDepth*2, 0);
}

public void changeSmodulationWaveTable(int midiChannelNo, int waveNo) {
	midiDev.send_command(21,midiChannelNo, waveNo, 0);
}

public void changeSmodulateRate(int midiChannelNo, int modulateRate) {
	midiDev.send_command(22, midiChannelNo, modulateRate, 0);
}

public void changeSmodulateDelay(int midiChannelNo,int delayValue) {
	midiDev.send_command(23, midiChannelNo, delayValue, 0);
}


/* --------------------------------------------- */
	public void close() {
		midiDev.close();
	}

	public void reset() {
//		midiDev.YmReset();
		toneDataInit();

	}




	public void notifyChange(EventType<MyDataEvent> e,eventSource source,int ch, int op,int val) {
		for(Enumeration<Observer> i = observers.elements(); i.hasMoreElements();) {
			Observer o = i.nextElement();
			o.update( e,source ,ch, op, val);
		}
	}
	
	public void change_com(int i) {
		midiDev.change_comportstat(i);
	}
	
	public void send_user_wave(int ch,int i,int d) {
		midiDev.send_user_wave(ch, i,d);
	}
	public double[][] WaveData() {
	return userWaveData;
	}
	
	public int getMaxOperator() {
		return MAX_OPERATOR;
	}
	
}

