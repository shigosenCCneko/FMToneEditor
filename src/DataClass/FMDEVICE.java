package DataClass;

import java.util.Enumeration;
import java.util.Vector;

import MyEvent.MyDataEvent;
import MyEvent.Observer;
import MyEvent.eventSource;
import application.ConectMidi;
import javafx.event.EventType;





public class FMDEVICE  {
static	final int  MAGIC_NO	= 0x60;
static final int MAX_OPERATOR = 4;
static	final int OPERATOR_LEN = 13;
static  final int  DATA_LEN = OPERATOR_LEN * MAX_OPERATOR + 1;
static final int CHANNEL_VAL = 5;
static  final int  TONESET_LEN = DATA_LEN * CHANNEL_VAL;

static final int MAX_WAVE_FORM = 11;

static final int WAVEDATA_LEN = 256;
static final int USERWAVE_VAL = 4;






//static final int OFS_AR 		= 0;
//static final int OFS_DR			= 1;
//static final int OFS_SR 		= 2;
//static final int OFS_SL 		= 3;
//static final int OFS_RR			= 4;
//static final int OFS_MULTI	 	= 5;
//static final int OFS_TLV 		= 6;
//static final int OFS_FB		 	= 7;
//static final int OFS_WS			= 8;
//static final int OFS_MOF		= 9;
//static final int OFS_WS2		=10;
//static final int OFS_MOFONE		=11;



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
	int morph_invert;
	
	int operatorLPF;
	
	
};

class FMCHANNEL{
	int connect = 0;
	OPERATOR[] operator;
		
};

	private FMCHANNEL[] fmchannel = new FMCHANNEL[16];

    double userWaveData[][] = new double[USERWAVE_VAL][WAVEDATA_LEN];

//	private byte toneData[] = new byte[TONESET_LEN];
//	private MyDataListener listener;
	
	private ConectMidi midiDev;
	private int editChannelNo = 0;

	private boolean notifyStop = false;


	Vector<Observer> observers;
	
	private FMDEVICE() {
		//MyDataListener listener;
		fmchannel = new FMCHANNEL[CHANNEL_VAL];
		for(int i = 0;i < CHANNEL_VAL;i++) {
			fmchannel[i] = new FMCHANNEL();
			fmchannel[i].operator = new OPERATOR[4];
			for(int j = 0; j < MAX_OPERATOR;j++) {
				fmchannel[i].operator[j] = new OPERATOR();
			}
		}
		
		
		midiDev= new ConectMidi();
		//toneDataInit();
		observers = null;

	}

/* Singleton ?????? */
	public static class FmToneDataInstanceHolder{
		private static final FMDEVICE INSTANCE = new FMDEVICE();

	}

	public static FMDEVICE getInstance() {


		return FmToneDataInstanceHolder.INSTANCE;
	}

	
/*a ???????????? */
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



	public int getAlgorithmNo(int ch){
		int i;
		i =  getValue(ch,0,eventSource.Connect);
		return i;

	}


	public   void getOperator(int ch, int op ,byte buf[]) {
		int i = 0;
		buf[i++] = (byte) fmchannel[ch].operator[op].feedback;
		buf[i++] = (byte) fmchannel[ch].operator[op].attack;
		buf[i++] = (byte) fmchannel[ch].operator[op].decey;
		buf[i++] = (byte) fmchannel[ch].operator[op].sustain;
		buf[i++] = (byte) fmchannel[ch].operator[op].sustainLevel;
		buf[i++] = (byte) fmchannel[ch].operator[op].release;
		buf[i++] = (byte) fmchannel[ch].operator[op].multiple;

		buf[i++] = (byte) fmchannel[ch].operator[op].totalLevel;
		buf[i++] = (byte) fmchannel[ch].operator[op].waveSelect;
		buf[i++] = (byte) fmchannel[ch].operator[op].waveSelect2;			
		buf[i++] = (byte) fmchannel[ch].operator[op].morph;			//use STM32synthesizer
		buf[i++] = (byte) fmchannel[ch].operator[op].morph_once;
		buf[i++] = (byte) fmchannel[ch].operator[op].operatorLPF;
		
	}
	public  void setOperator(int ch,int op,byte data[]) {
		int i = 0;
		byte a;
		fmchannel[ch].operator[op].feedback = data[i++];
		fmchannel[ch].operator[op].attack = data[i++];
		fmchannel[ch].operator[op].decey = 	data[i++];
		fmchannel[ch].operator[op].sustain = data[i++];
		fmchannel[ch].operator[op].sustainLevel = data[i++];
		fmchannel[ch].operator[op].release = data[i++];
		fmchannel[ch].operator[op].multiple = data[i++];
		fmchannel[ch].operator[op].totalLevel = data[i++];
		fmchannel[ch].operator[op].waveSelect = data[i++];
		a = data[i++];
		fmchannel[ch].operator[op].waveSelect2 = a;
		if(a > 7) {
			fmchannel[ch].operator[op].morph_invert = 1;
		}else {
			fmchannel[ch].operator[op].morph_invert = 0;
		}
		fmchannel[ch].operator[op].morph = data[i++];
		fmchannel[ch].operator[op].morph_once = data[i++];
		fmchannel[ch].operator[op].operatorLPF = data[i++];
		notifyChange(MyDataEvent.DATA_UPDATE,eventSource.ToneChange,0,0,0);
		sendOperator(ch,op,data);
	}
	
	public void sendOperator(int ch, int op,byte data[]) {
		byte a;
		int i = 0;
		setValue(eventSource.FeedBK,ch,op,data[i++]);
		setValue(eventSource.SLIDER1,ch,op,data[i++]);
		setValue(eventSource.SLIDER2 ,ch,op,data[i++]);
		setValue(eventSource.SLIDER3 ,ch,op,data[i++]);
		setValue(eventSource.SLIDER4 ,ch,op,data[i++]);
		setValue(eventSource.SLIDER5 ,ch,op,data[i++]);
		setValue(eventSource.SLIDER6 ,ch,op,data[i++]);
		setValue(eventSource.SLIDER7 ,ch,op,data[i++]);
		setValue(eventSource.Wave ,ch,op,data[i++]);
		a = data[i++];
		if(a <8) {
			setValue(eventSource.Wave2 ,ch,op,a);
		}else {
			setValue(eventSource.Wave2,ch,op,a);
			
		}
		setValue(eventSource.Morf ,ch,op,data[i++]);
		setValue(eventSource.MorphOnce ,ch,op,data[i++]);		
		setValue(eventSource.operatorLPF,ch,op,data[i++]);
		notifyChange(MyDataEvent.DATA_UPDATE,eventSource.ToneChange,0,0,0);		
	}
	
	
	
	public   void getToneData(int ch, byte[] buf) {
		int i = 0;
		byte op_buf[] = new byte[OPERATOR_LEN];
		
		buf[i++] = (byte) fmchannel[ch].connect ;
		
		for(int op = 0; op < MAX_OPERATOR;op++) {
			getOperator(ch,op,op_buf);
			for(int j = 0; j < OPERATOR_LEN;j++) {
				buf[i++] = op_buf[j];
			}
		}
		

	}

	public void getToneSet(byte[] buf) {
		byte tone[] = new byte[DATA_LEN];
		int p = 0;
		for(int ch = 0; ch < CHANNEL_VAL;ch++) {
			getToneData(ch,tone);
			for(int i = 0; i < DATA_LEN;i++) {
				buf[p] = tone[i];
				p++;
			}
		}
	}
	
	public  void setTone(int ch ,byte data[]) {
		int i =0;
		byte op_buf[] = new byte[OPERATOR_LEN];
		
		//byte a;
		fmchannel[ch].connect = (int)data[i++];
		for(int op = 0; op < MAX_OPERATOR; op++) {
			for(int j = 0; j < OPERATOR_LEN;j++) {
				op_buf[j] = data[i++];
			}
			setOperator(ch,op,op_buf);
		}

		notifyChange(MyDataEvent.DATA_UPDATE,eventSource.ToneChange,0,0,0);		
	}	
	

	
	public void setToneSet(byte buf[]) {
		int p = 0;
		byte tone[] = new byte[DATA_LEN];
		for(int ch = 0; ch < CHANNEL_VAL;ch++) {
			for(int i = 0 ; i < DATA_LEN;i++) {
				tone[i] = buf[p];
				p++;
			}
			setTone(ch,tone);
		}

		if(buf.length == (TONESET_LEN + WAVEDATA_LEN * USERWAVE_VAL)) {
			int cnt = TONESET_LEN;
			for(int i = 0; i < USERWAVE_VAL;i++) {
				for(int j = 0;j < WAVEDATA_LEN;j++) {
					userWaveData[i][j] = buf[cnt];
					send_user_wave(i,j,buf[cnt++]);
				}
			}
			notifyChange(MyDataEvent.DATA_UPDATE,eventSource.UserwaveLoad,0,0,0);
		}
		notifyChange(MyDataEvent.DATA_UPDATE,eventSource.ToneChange,0,0,0);		
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
			
		case Invert:
			val = fmchannel[ch].operator[opno].morph_invert;
			break;
			
		case Connect:
			val = fmchannel[ch].connect;
			break;
		case operatorLPF:
			val = fmchannel[ch].operator[opno].operatorLPF;

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
			case Invert:
				fmchannel[ch].operator[opno].morph_invert = val;
	
			case Connect:
				fmchannel[ch].connect = val;
				break;
			case operatorLPF:
				fmchannel[ch].operator[opno].operatorLPF = val;
			
				break;
			default:
				return;
				//break;

			}
			/* operator no  ??????2bit */
			midiDev.send_command(10,ch,source.getNumber() +  64 * opno, val);

	}



	public void toneDataInit(){
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
					

						
					fmchannel[i].operator[j].waveSelect = 0;
					fmchannel[i].operator[j].waveSelect2 = 0;
					
					fmchannel[i].operator[j].morph = 0;
					fmchannel[i].operator[j].morph_once = 0;
					fmchannel[i].operator[j].morph_invert = 0;
					
					fmchannel[i].operator[j].operatorLPF = 0;
					
				}
			}
			
			byte tonedata[] = new byte[TONESET_LEN];
			getToneSet(tonedata);
			setToneSet(tonedata);

			notifyStop(false);
	}

	/* ----------------- ????????????????????? ----------*/

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


//public void noteOn(int ch,int noteNo,int vel){
//	midiDev.send_command(16,ch,noteNo,vel);
//}
//
//public void noteOff(int ch,int noteNo){
//	midiDev.send_command(17,ch,noteNo,0);
//}



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
	
	public int getTonesetLen() {
		return TONESET_LEN;
	}
	
	public int getDatalen() {
		return DATA_LEN;
	}
	public int getOplen() {
		return OPERATOR_LEN;
	}
	public int getChannelVal() {
		return CHANNEL_VAL;
	}
	
	public int getMaxWaveform() {
		return MAX_WAVE_FORM;
	}
	
	public int getWavedataLen() {
		return WAVEDATA_LEN;
	}
	public int getUserWaveVal() {
		return USERWAVE_VAL;
	}

	
}

