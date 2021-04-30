package application;

import CustomComponent.MySlider;
import CustomComponent.MySliderEvent;
import CustomComponent.OperatorPanel;
import CustomComponent.StatusListCell;
import DataClass.FMDEVICE;
import MyEvent.MyDataEvent;
import MyEvent.MyDataListener;
import MyEvent.Observer;
import MyEvent.eventSource;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;



public class FMToneEditorController implements MyDataListener , Observer{
	private final int MAX_ALG = 14;
	@FXML OperatorPanel operator1;
	@FXML OperatorPanel operator2;
	@FXML OperatorPanel operator3;
	@FXML OperatorPanel operator4;
	OperatorPanel[]     operatorArray;

	@FXML MySlider		feedBack1;
	@FXML MySlider		feedBack2;

	@FXML ComboBox<String> toneSelectBox;
	ObservableList<String> toneOptions;

	@FXML ComboBox<String>	channelSelectBox;
	ObservableList<String> channelOptions;

	@FXML ComboBox<String>	AlgoSelectBox;
	ObservableList<String> algoOptions;

	@FXML Button	copyToneButton;
	@FXML Button	swapToneButton;

	@FXML Button	readFromTextButton;
	@FXML Button	writeToTextButton;

	@FXML TextField		toneDataText;
//	@FXML HBox			fourOpBox;
	/*
	 * SeneBuilder用 OperatorPanelの代わり
	 */
	@FXML Pane  dummy1;
	@FXML Pane  dummy2;
	@FXML Pane  dummy3;
	@FXML Pane  dummy4;	
	@FXML private MenuFieldController menuControll;
	static FMToneEditorController parent;

	
	FMDEVICE fmDevice;
	static int currentChannel = 0;
	
	public void initialize() {
		fmDevice = FMDEVICE.getInstance();	
		FMDEVICE.getInstance().attach(this);

		/* アルゴリズム選択ComboBoxの初期化 */
		algoOptions = FXCollections.observableArrayList();
		for(int i = 0;i < MAX_ALG ;i++){
			String target = ("image/" + i +".png");
			algoOptions.add(target);
		}
		AlgoSelectBox.setItems(algoOptions);
		AlgoSelectBox.setCellFactory(c->new StatusListCell());
		AlgoSelectBox.setButtonCell(new StatusListCell());
		AlgoSelectBox.setValue(algoOptions.get(0));	
		
		
        /* channel Select Box 初期化 */
		channelOptions = FXCollections.observableArrayList();
		for(int i = 1;i<= fmDevice.getChannelVal();i++) {
			String target = ("CH" + i);
			channelOptions.add(target);
		}
		channelSelectBox.setItems(channelOptions);
		channelSelectBox.setValue("CH1");	
		
		
		operatorArray = new OperatorPanel[4];
		operatorArray[0] = operator1;
		operatorArray[1] = operator2;
		operatorArray[2] = operator3;
		operatorArray[3] = operator4;
		operator1.addListener(this);
		operator2.addListener(this);
		operator3.addListener(this);
		operator4.addListener(this);		

		feedBack1.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(null,eventSource.FeedBK,0,  event.getEventValue())	);
				
		/*					
		feedBack2.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(null,eventSource.FeedBK2,2,event.getEventValue() 	));
		sliderLFO.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(null,eventSource.Lfo,0,  event.getEventValue()  )	);
		sliderBO.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(null,eventSource.BO,0,  event.getEventValue()    )	);

*/				
		

		setPanel();
		parent = this;
		fmDevice.toneDataInit();
		
	}
	
	
	public void changeValue(EventType<MyDataEvent> e) {
		if(e == MyDataEvent.DATA_UPDATE) {
			setPanel();
		}
	}
	
	public void changeValue(EventType<MyDataEvent> e, eventSource source, int opNo,int val) {
		// TODO 自動生成されたメソッド・スタブ
//		System.out.print(source);
//		System.out.println( opNo + "=" + val);
		
		if(e == MyDataEvent.DATA_UPDATE) {
			setPanel();
		}else {
			fmDevice.setValue(source, currentChannel, opNo, val);
		
		}
	}

	void setPanel() {
		fmDevice.notifyStop(true);
		feedBack1.setValue((double) fmDevice.getValue(currentChannel, 0, eventSource.FeedBK));
					
		
//		feedBack2.setValue(
//				(double) toneData.getValue(currentChannel, 2, eventSource.FeedBK2));
//		sliderBO.setValue(
//				(double) toneData.getValue(currentChannel, 0, eventSource.BO));

		AlgoSelectBox.setValue(algoOptions.get(
				fmDevice.getValue(currentChannel, 0, eventSource.Connect)));
//		sliderLFO.setValue(
//				(double) toneData.getValue(currentChannel, 0, eventSource.Lfo));

		for(int opno = 0; opno < fmDevice.getMaxOperator();opno++) {
			operatorArray[opno].setAtack(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER1));
			operatorArray[opno].setDecy(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER2));
			operatorArray[opno].setSus(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER3));
			operatorArray[opno].setSL(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER4));
			operatorArray[opno].setRel(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER5));
			operatorArray[opno].setMul(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER6));
			operatorArray[opno].setTlv(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.SLIDER7));
			
			

//			operatorArray[opno].setKsl(
//					(double) toneData.getValue(currentChannel, opno, eventSource.Ksl));
			
//			operatorArray[opno].setKSR(
//					1 == (double) toneData.getValue(currentChannel, opno, eventSource.Ksr));
			operatorArray[opno].setWave(
					 fmDevice.getValue(currentChannel, opno, eventSource.Wave));
			operatorArray[opno].setWave2(
					fmDevice.getValue(currentChannel, opno, eventSource.Wave2));
			operatorArray[opno].setMorphVal(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.Morf));
			operatorArray[opno].setMorphOnce(
					fmDevice.getValue(currentChannel, opno, eventSource.MorphOnce));
//			operatorArray[opno].setDT(
//					(double) toneData.getValue(currentChannel, opno, eventSource.DT));
//			operatorArray[opno].setDAM(
//					(double) toneData.getValue(currentChannel, opno, eventSource.Dam));
//			operatorArray[opno].setDVB(
//					(double) toneData.getValue(currentChannel, opno, eventSource.Dvb));
			
			operatorArray[opno].setOperatorLPF(
					fmDevice.getValue(currentChannel, opno, eventSource.operatorLPF));
		}
		fmDevice.notifyStop(false);		
		
		
	}
	
	@FXML void changeChannel() {
		int i = channelOptions.indexOf(channelSelectBox.getValue());
		channelSelectBox.setPromptText(channelOptions.get(i));
		currentChannel = i;
		setPanel();
		fmDevice.setEditChannel(currentChannel);
	}
	
	@FXML private void setcopypastDataRow(){
		FMDEVICE fmdevice = FMDEVICE.getInstance();
		byte [] buf = new byte[fmDevice.getDatalen()];
		StringBuilder st = new StringBuilder();
		fmdevice.getToneData(currentChannel, buf);
		int cnt = 1;

		st.append( String.format("%3d,",buf[0]));
		for(int i = 0;i < fmDevice.getMaxOperator();i++){
			for(int j = 0;j<fmDevice.getOplen();j++){
				st.append(String.format("%3d,",(buf[cnt++]&0x00ff)));
			}
		}
		toneDataText.setText(new String(st));
		System.out.println("text out");
	}
	
	@FXML void readFromText() {
		/* テキストフィールドからカレントチャンネルへ音色データを読み込む　*/

			byte [] buf = new byte[fmDevice.getDatalen()];
			String s;
			String str = toneDataText.getText();
			String[] token = str.split(",",0);
			int cnt = token.length;
			int data,k,l;
			if(cnt == fmDevice.getDatalen()) {
				for(int i =0;i < cnt;i++){
					s = token[i].trim();
					buf[i] =(byte) (0x00ff & Integer.parseInt(s));
				}
				FMDEVICE fmdevice = FMDEVICE.getInstance();
				fmdevice.setTone(currentChannel, buf);
				
			}
			setPanel();
	}
	
	public void update(EventType<MyDataEvent> e, eventSource source, int ch,int op, int val) {
		if(e == MyEvent.MyDataEvent.DATA_UPDATE) {
			setPanel();
		}
	}

	  public void stop() {
		  //envelopeEditor.close();
		  Platform.exit();
		  System.exit(0);
		  System.out.println("env exit");
	  }
	  
	@FXML void changeAlgo() {
			int i = algoOptions.indexOf(AlgoSelectBox.getValue());
			
//System.out.println("change algorithm");
			changeValue(null,eventSource.Connect,0,i);
	


	}
	

	
}
