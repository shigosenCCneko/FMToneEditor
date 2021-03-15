package application;

import CustomComponent.MySlider;
import CustomComponent.MySliderEvent;
import CustomComponent.OperatorPanel;
import DataClass.FMDEVICE;
import MyEvent.MyDataEvent;
import MyEvent.MyDataListener;
import MyEvent.Observer;
import MyEvent.eventSource;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class FMToneEditorController implements MyDataListener , Observer{

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
	
	FMDEVICE fmDevice;
	int currentChannel = 0;
	
	public void initialize() {
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
		
		fmDevice = FMDEVICE.getInstance();
		setPanel();
		
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
		fmDevice.setValue(source, currentChannel, opNo, val);

	}

	void setPanel() {
		fmDevice.notifyStop(true);
		feedBack1.setValue((double) fmDevice.getValue(currentChannel, 0, eventSource.FeedBK));
		/*					
		
		feedBack2.setValue(
				(double) toneData.getValue(currentChannel, 2, eventSource.FeedBK2));
		sliderBO.setValue(
				(double) toneData.getValue(currentChannel, 0, eventSource.BO));

		AlgoSelectBox.setValue(algoOptions.get(
				toneData.getValue(currentChannel, 0, eventSource.Connect)));
		sliderLFO.setValue(
				(double) toneData.getValue(currentChannel, 0, eventSource.Lfo));
*/
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
			operatorArray[opno].setMorphVal(
					(double) fmDevice.getValue(currentChannel, opno, eventSource.Morf));
//			operatorArray[opno].setDT(
//					(double) toneData.getValue(currentChannel, opno, eventSource.DT));
//			operatorArray[opno].setDAM(
//					(double) toneData.getValue(currentChannel, opno, eventSource.Dam));
//			operatorArray[opno].setDVB(
//					(double) toneData.getValue(currentChannel, opno, eventSource.Dvb));
			
		}
		fmDevice.notifyStop(false);		
		
		
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

	
}
