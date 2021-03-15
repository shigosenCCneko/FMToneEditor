package CustomComponent;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import MyEvent.MyDataEvent;
import MyEvent.MyDataListener;
import MyEvent.eventSource;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


public class OperatorPanel extends Pane
{

	@FXML Label operatorName;
	@FXML MySlider slider1;
	@FXML MySlider slider2;
	@FXML MySlider slider3;
	@FXML MySlider slider4;
	@FXML MySlider slider5;
	@FXML MySlider slider6;
	@FXML MySlider slider7;
	
	@FXML MySlider sliderDT;	
	@FXML MySlider sliderKSL;
	@FXML MySlider sliderDAM;
	@FXML MySlider sliderDVB;	
	
	@FXML RadioButton eamRadioButton;
	@FXML RadioButton evbRadioButton;
	@FXML RadioButton ksrRadioButton;
	@FXML RadioButton xofRadioButton;	
	
	
	@FXML MySlider sliderMorf;	
	@FXML RadioButton invert;
	@FXML RadioButton morph_once;

	@FXML ComboBox<String> waveSelect;
	@FXML ComboBox<String> waveSelect2;	
	
	


	@FXML Label valueLabel;
	@FXML Label valueLabel2;	
	
	
	ObservableList<String> options;
	ObservableList<String> options2;

	private MyDataListener listener;
	private MyDataListener listener2;

	private int operatorNo = 0;	
	
	
	
	
	String slider1Name = "SLD1";
	String slider2Name = "SLD2";
	String slider3Name = "SLD3";
	String slider4Name = "SLD4";
	String slider5Name = "SLD5";
	String slider6Name = "SLD6";
	String slider7Name = "SLD7";
	














    public  OperatorPanel(){

      	super();

      	FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("OperatorPanel.fxml"));
    	fxmlLoader.setRoot(this);
    	fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        /* accessibleTextに値が設定されたらパラメータを読みに行く */
        this.accessibleTextProperty().addListener(accessibleTextListener);
        this.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID
        		,CornerRadii.EMPTY,BorderWidths.DEFAULT)));

    }

    /* aパラメータの処理     "name,min,max,val"   */
	 ChangeListener<String> accessibleTextListener = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<?extends String>observable,String oldValue,String newValue) {
			String data[] = newValue.split(",");
		   	int i = data.length;
		   	if(i == 3){
		   		operatorName.setText(data[0]);
		   		operatorName.setTextFill(Color.valueOf(data[1]));
		   		operatorNo = Integer.parseInt(data[2]);
		   	}
		}
	};


	public void initialize() {

		slider1.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER1)	);

		slider2.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER2)	);

		slider3.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER3)	);

		slider4.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER4)	);

		slider5.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER5)	);

		slider6.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER6)	);

		slider7.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event->	changeValue(event.getEventValue(),eventSource.SLIDER7)	);

//		sliderKSL.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
//				event->	changeValue(event.getEventValue(),eventSource.Ksl)	);

//		sliderDAM.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
//				event->	changeValue(event.getEventValue(),eventSource.Dam)	);

//		sliderDVB.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
//				event->	changeValue(event.getEventValue(),eventSource.Dvb)	);

		sliderMorf.addEventHandler(MySliderEvent.MYCHANGE_VALUE,
				event-> changeValue(event.getEventValue(),eventSource.Morf));


		/* a波形選択ComboBoxの初期化 */
		options = FXCollections.observableArrayList();
		for(int i = 0;i < 8;i++){
			String target = ("waveImg/img" + i +".png");
			options.add(target);
		}
		System.out.println(options);
		waveSelect.setItems(options);
		waveSelect.setCellFactory(c->new StatusListCell());
		waveSelect.setButtonCell(new StatusListCell());
		waveSelect.setValue(options.get(0));


		options2 = FXCollections.observableArrayList();
		for(int i = 0;i < 8;i++){
			String target2 = ("waveImg/img" + i +".png");
			options2.add(target2);
		}
		waveSelect2.setItems(options2);
		waveSelect2.setCellFactory(c->new StatusListCell());
		waveSelect2.setButtonCell(new StatusListCell());
		waveSelect2.setValue(options2.get(0));
		
		readOperatorProperties();
		
		
	}

	private void readOperatorProperties(){
		Properties properties = new Properties();

		try{
			InputStream istream = new FileInputStream("fmToneEditorOperator.properties");
			properties.load(istream);
			slider1Name = properties.getProperty("slider1Name");
			
			slider2Name = properties.getProperty("slider2Name");
			
			slider3Name = properties.getProperty("slider3Name");
			
			slider4Name = properties.getProperty("slider4Name");
			
			slider5Name = properties.getProperty("slider5Name");
			
			slider6Name = properties.getProperty("slider6Name");
			
			slider7Name = properties.getProperty("slider7Name");
			

			slider1.setLabelName(slider1Name);
			slider2.setLabelName(slider2Name);
			slider3.setLabelName(slider3Name);
			slider4.setLabelName(slider4Name);
			slider5.setLabelName(slider5Name);
			slider6.setLabelName(slider6Name);
			slider7.setLabelName(slider7Name);
			
			slider1.setMax(Double.parseDouble(properties.getProperty("slider1Max")));
			slider2.setMax(Double.parseDouble(properties.getProperty("slider2Max")));
			slider3.setMax(Double.parseDouble(properties.getProperty("slider3Max")));
			slider4.setMax(Double.parseDouble(properties.getProperty("slider4Max")));
			slider5.setMax(Double.parseDouble(properties.getProperty("slider5Max")));
			slider6.setMax(Double.parseDouble(properties.getProperty("slider6Max")));
			slider7.setMax(Double.parseDouble(properties.getProperty("slider7Max")));
			
			

			istream.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}







	@FXML
	void selectedWaveform() {
		int i = options.indexOf(    waveSelect.getValue());
		changeValue(i, eventSource.Wave);
		valueLabel.setText(i+1 + "");

	}

	@FXML
	void selectedWaveform2() {
		int i = options2.indexOf(    waveSelect2.getValue());
		if(invert.isSelected() == true) {
			changeValue(i+8, eventSource.Wave2);
		}else {
			changeValue(i, eventSource.Wave2);
		}
		valueLabel2.setText(i+1 + "");

	}

	void changeValue(int val,eventSource source) {
		//System.out.println((val + "") + (source) );
		if(listener != null)
		listener.changeValue( MyDataEvent.OPDATA_CHANGE,source,operatorNo,val);

	}



	/* set valer to control */

	public void setAtack(Double val){
		slider1.setValue(val);
	}
	public void setDecy(Double val){
		slider2.setValue(val);
	}
	public void setSus(Double val){
		slider3.setValue(val);
	}
	public void setSL(Double val) {
		slider4.setValue(val);
	}
	public void setRel(Double val){
		slider5.setValue(val);
	}
	public void setMul(Double val){
		slider6.setValue(val);
	}
	public void setTlv(Double val){
		slider7.setValue(val);
	}

	public void setWave( int val){
		waveSelect.setValue(options.get(val));
	}
//	public void setDT(Double val){
//		sliderDT.setValue(val);
//	}


//	public void setDAM(Double val){
//		sliderDAM.setValue(val);
//	}
//	public void setDVB(Double val){
//		sliderDVB.setValue(val);
//	}
	public void setMorphVal(Double val) {
		sliderMorf.setValue(val);
	}

	public void addListener(MyDataListener listener) {
		this.listener = listener;
	}

	
	@FXML	public void change_MorphCycle() {
		
		if(morph_once.isSelected() == true) {
			changeValue(1,eventSource.MorphOnce);
		}else {
			changeValue(0,eventSource.MorphOnce);
		}
				
	}



}