package application;

import DataClass.FMDEVICE;
import MyEvent.MyDataEvent;
import MyEvent.Observer;
import MyEvent.eventSource;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Window;

public class SoftwareModulationController implements Observer {
	FMDEVICE toneData;


	GraphicsContext wg1,wg2,wg3,wg4;
	GraphicsContext wg[] = new GraphicsContext[4];

	int preX,preY,preX2,preY2;

	//double waveData[][] = new double[4][256];


	final int WAVE_MAX = 127;
	int operator = 0;
	int wave_sin[] = 	{
			  0,  3,  6,  9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45,
			  48, 51, 54, 57, 59, 62, 65, 67, 70, 73, 75, 78, 80, 82, 85, 87,
			  89, 91, 94, 96, 98,100,102,103,105,107,108,110,112,113,114,116,
			 117,118,119,120,121,122,123,123,124,125,125,126,126,126,126,126,
			 127,126,126,126,126,126,125,125,124,123,123,122,121,120,119,118,
			 117,116,114,113,112,110,108,107,105,103,102,100, 98, 96, 94, 91,
			  89, 87, 85, 82, 80, 78, 75, 73, 70, 67, 65, 62, 59, 57, 54, 51,
			  48, 45, 42, 39, 36, 33, 30, 27, 24, 21, 18, 15, 12,  9,  6,  3,
			   0, -3, -6, -9,-12,-15,-18,-21,-24,-27,-30,-33,-36,-39,-42,-45,
			 -48,-51,-54,-57,-59,-62,-65,-67,-70,-73,-75,-78,-80,-82,-85,-87,
			 -89,-91,-94,-96,-98,-100,-102,-103,-105,-107,-108,-110,-112,-113,-114,-116,
			 -117,-118,-119,-120,-121,-122,-123,-123,-124,-125,-125,-126,-126,-126,-126,-126,
			 -127,-126,-126,-126,-126,-126,-125,-125,-124,-123,-123,-122,-121,-120,-119,-118,
			 -117,-116,-114,-113,-112,-110,-108,-107,-105,-103,-102,-100,-98,-96,-94,-91,
			 -89,-87,-85,-82,-80,-78,-75,-73,-70,-67,-65,-62,-59,-57,-54,-51,
			 -48,-45,-42,-39,-36,-33,-30,-27,-24,-21,-18,-15,-12, -9, -6, -3,

	};


			 // 1-3 mix
	int wave_sin_1_3[] = {
			  0,  5, 12, 17, 24, 29, 36, 41, 47, 52, 57, 61, 67, 70, 75, 78,
			  82, 85, 88, 90, 92, 94, 95, 96, 97, 97, 96, 97, 96, 94, 93, 91,
			  88, 86, 84, 81, 78, 75, 72, 67, 64, 60, 57, 54, 50, 46, 42, 39,
			  34, 31, 27, 24, 20, 18, 14, 11, 10,  7,  5,  4,  3,  2,  1,  0,
			   0,  0,  1,  2,  3,  4,  5,  7, 10, 11, 14, 18, 20, 24, 27, 31,
			  34, 39, 42, 46, 50, 54, 57, 60, 64, 67, 72, 75, 78, 81, 84, 86,
			  88, 91, 93, 94, 96, 97, 96, 97, 97, 96, 95, 94, 92, 90, 88, 85,
			  82, 78, 75, 70, 67, 61, 57, 52, 47, 41, 36, 29, 24, 17, 12,  5,
			   0, -5,-12,-17,-24,-29,-36,-41,-47,-52,-57,-61,-67,-70,-75,-78,
			 -82,-85,-88,-90,-92,-94,-95,-96,-97,-97,-96,-97,-96,-94,-93,-91,
			 -88,-86,-84,-81,-78,-75,-72,-67,-64,-60,-57,-54,-50,-46,-42,-39,
			 -34,-31,-27,-24,-20,-18,-14,-11,-10, -7, -5, -4, -3, -2, -1,  0,
			   0,  0, -1, -2, -3, -4, -5, -7,-10,-11,-14,-18,-20,-24,-27,-31,
			 -34,-39,-42,-46,-50,-54,-57,-60,-64,-67,-72,-75,-78,-81,-84,-86,
			 -88,-91,-93,-94,-96,-97,-96,-97,-97,-96,-95,-94,-92,-90,-88,-85,
			 -82,-78,-75,-70,-67,-61,-57,-52,-47,-41,-36,-29,-24,-17,-12, -5,
	};

			//1-3-5mix
	int wave_sin_1_3_5[] = {
			  0, 11, 23, 35, 47, 56, 68, 77, 85, 94,101,106,112,117,119,121,
			  123,122,120,117,115,111,106,100, 94, 88, 81, 74, 67, 60, 53, 46,
			   39, 32, 25, 20, 16, 11,  7,  4,  1,  0,  0,  1,  1,  2,  4,  6,
			    9, 13, 15, 20, 22, 27, 31, 34, 39, 42, 45, 48, 50, 53, 54, 54,
			   55, 54, 54, 53, 50, 48, 45, 42, 39, 34, 31, 27, 22, 20, 15, 13,
			    9,  6,  4,  2,  1,  1,  0,  0,  1,  4,  7, 11, 16, 20, 25, 32,
			   39, 46, 53, 60, 67, 74, 81, 88, 94,100,106,111,115,117,120,122,
			  123,121,119,117,112,106,101, 94, 85, 77, 68, 56, 47, 35, 23, 11,
			    0,-11,-23,-35,-47,-56,-68,-77,-85,-94,-101,-106,-112,-117,-119,-121,
			  -123,-122,-120,-117,-115,-111,-106,-100,-94,-88,-81,-74,-67,-60,-53,-46,
			  -39,-32,-25,-20,-16,-11, -7, -4, -1,  0,  0, -1, -1, -2, -4, -6,
			   -9,-13,-15,-20,-22,-27,-31,-34,-39,-42,-45,-48,-50,-53,-54,-54,
			  -55,-54,-54,-53,-50,-48,-45,-42,-39,-34,-31,-27,-22,-20,-15,-13,
			   -9, -6, -4, -2, -1, -1,  0,  0, -1, -4, -7,-11,-16,-20,-25,-32,
			  -39,-46,-53,-60,-67,-74,-81,-88,-94,-100,-106,-111,-115,-117,-120,-122,
			  -123,-121,-119,-117,-112,-106,-101,-94,-85,-77,-68,-56,-47,-35,-23,-11,


	};

			//sin3-5mix
	int wave_sin_3_5[] = {
			  0, 13, 23, 30, 30, 27, 19, 10,  0, -8,-13,-14,-12, -9, -5, -1,
			  0, -1, -5, -9,-12,-14,-13, -8,  0, 10, 19, 27, 30, 30, 23, 13,
			  0,-13,-23,-30,-30,-27,-19,-10,  0,  8, 13, 14, 12,  9,  5,  1,
			  0,  1,  5,  9, 12, 14, 13,  8,  0,-10,-19,-27,-30,-30,-23,-13,
	};

	int wave_rand[] = {
			 -3,-108,-125, 84, 67, 25,-93, 98,-108,-40, 93,-19,-70,-25, 40,  7,
			 87, 54, 20, -6, 18,  7,102,104,111,121, 15, 61, 32, 80,-48,  2,
			 20,-97, 73, 90,-35, 87,-87,113,-98,-52,-22,124,-44, 23,-105,108,
			108, 33,-17,-44,-119, 58,-113,123,115,-14, 15, 98,-125, 48, 55,-74,
			-56,-55,-97, 96, -4, 28,  0, 18,105, 71,-75,-18, 83, -3,-123, 91,
			 18,-88, 31,-76, 57, 32, 10, -4,-10,-121, 67,-27,-73,  4,-119, 27,
			 56,  3,-32, 58, 19, 58, 45, 74,-105, -1, -3, 79,-23, -2,-20,-39,
			121,-23, 59, 67,  0, 12,  9, -2,110,  0,-28,-27,-106, 76,-124,-53,
			 18, 48, 18,-113, 42,-84, 60, 38,-16, 35,-109,100,-29,-25,-61, 83,
			122, 27, 77,  7,-87, 50,-81, 35, 40,-18, 80,-120, 11, 19,-80,103,
			-19, 74,  6,-101, 84,  2,-50,-29, 96,-34,-81, 21,-107, -1, 85, 80,
			-70,116, 57,-70, 69,-47, 76, 60,-67, 30,-123,-102,100,103,-76, 69,
			 -9,-118,-104, 64,106, 39,-48, 62,-29,-12,-38, 58,-93,-28,-71,-76,
			 77, 28,-91,-51,126,-82, 79, -6,101, 55, 62,-53, 92,-114,116,-29,
			-81, 52, 77, 34,-90,-13, 83, 53,-108,-62, 85,105,-23,-64,-111,-54,
			-64,119,-77,118,-56,-106, 80,-114, 43, 46, 70, 44, 20, 71, 13,-122,
	};

	int wave_saw[] = {
			  0,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
			  15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
			  31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,
			  47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62,
			  63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
			  79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94,
			  95, 96, 97, 98, 99,100,101,102,103,104,105,106,107,108,109,110,
			 111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,
			 -127,-126,-125,-124,-123,-122,-121,-120,-119,-118,-117,-116,-115,-114,-113,-112,
			 -111,-110,-109,-108,-107,-106,-105,-104,-103,-102,-101,-100,-99,-98,-97,-96,
			 -95,-94,-93,-92,-91,-90,-89,-88,-87,-86,-85,-84,-83,-82,-81,-80,
			 -79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,-67,-66,-65,-64,
			 -63,-62,-61,-60,-59,-58,-57,-56,-55,-54,-53,-52,-51,-50,-49,-48,
			 -47,-46,-45,-44,-43,-42,-41,-40,-39,-38,-37,-36,-35,-34,-33,-32,
			 -31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-21,-20,-19,-18,-17,-16,
			 -15,-14,-13,-12,-11,-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, -0,
	};
			//sin3-5mix
	int wave_tri[] = {
			  0,  1,  3,  5,  7,  9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29,
			  31, 33, 35, 37, 39, 41, 43, 45, 47, 49, 51, 53, 55, 57, 59, 61,
			  63, 65, 67, 69, 71, 73, 75, 77, 79, 81, 83, 85, 87, 89, 91, 93,
			  95, 97, 99,101,103,105,107,109,111,113,115,117,119,121,123,125,
			 127,125,123,121,119,117,115,113,111,109,107,105,103,101, 99, 97,
			  95, 93, 91, 89, 87, 85, 83, 81, 79, 77, 75, 73, 71, 69, 67, 65,
			  63, 61, 59, 57, 55, 53, 51, 49, 47, 45, 43, 41, 39, 37, 35, 33,
			  31, 29, 27, 25, 23, 21, 19, 17, 15, 13, 11,  9,  7,  5,  3,  1,
			   0, -1, -3, -5, -7, -9,-11,-13,-15,-17,-19,-21,-23,-25,-27,-29,
			 -31,-33,-35,-37,-39,-41,-43,-45,-47,-49,-51,-53,-55,-57,-59,-61,
			 -63,-65,-67,-69,-71,-73,-75,-77,-79,-81,-83,-85,-87,-89,-91,-93,
			 -95,-97,-99,-101,-103,-105,-107,-109,-111,-113,-115,-117,-119,-121,-123,-125,
			 -127,-125,-123,-121,-119,-117,-115,-113,-111,-109,-107,-105,-103,-101,-99,-97,
			 -95,-93,-91,-89,-87,-85,-83,-81,-79,-77,-75,-73,-71,-69,-67,-65,
			 -63,-61,-59,-57,-55,-53,-51,-49,-47,-45,-43,-41,-39,-37,-35,-33,
			 -31,-29,-27,-25,-23,-21,-19,-17,-15,-13,-11, -9, -7, -5, -3, -1,
	};
	int wave_rect[] = {
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,
			-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127
	};
			double scaleX = 1;
			double scaleY = 1;
			double ofsY = WAVE_MAX ;
			double ofsX = 10;

	@FXML Canvas canvas1;
	@FXML Canvas canvas2;
	@FXML Canvas canvas3;
	@FXML Canvas canvas4;		
	
	Canvas canvas[] = new Canvas[4];

	
	
	
	
	

	@FXML RadioButton operator1;
	@FXML RadioButton operator2;
	@FXML RadioButton operator1_2;
	@FXML RadioButton operator2_2;



	@FXML Button set_sin;
	@FXML Button set_sin_1_3;
	@FXML Button set_sin_1_3_5;

	@FXML Button set_rand;
	@FXML Button set_saw;
	@FXML Button set_tri;
	@FXML Button set_rect;
	@FXML Button wave_clear;

	@FXML CheckBox quarterSel;
	@FXML CheckBox setMinus;

	@FXML TextArea waveText;
	@FXML Button writeText;
	@FXML Button readText;

	@FXML void initialize(){
		canvas[0] = canvas1;
		canvas[1] = canvas2;
		canvas[2] = canvas3;
		canvas[3] = canvas4;

		
		toneData = FMDEVICE.getInstance();
		toneData.attach(this);
		
		wg1 = canvas1.getGraphicsContext2D();
		wg2 = canvas2.getGraphicsContext2D();
		wg3 = canvas3.getGraphicsContext2D();
		wg4 = canvas4.getGraphicsContext2D();
		wg[0] = wg1;
		wg[1] = wg2;
		wg[2] = wg3;
		wg[3] = wg4;		
		
		for(int i =0;i < 4;i++) {
			wg[i] = canvas[i].getGraphicsContext2D();
		}

		int stroke = 1;
	
		for(int i = 0;i < 4;i++) {
			wg[i].setLineWidth(stroke);
			wg[i].setStroke(Color.BLUE);
		}
		
			
		for(int j = 0;j < 4;j++) {
			for(int i = 0 ; i < 255;i++) {
				(toneData.WaveData())[j][i] = (double)wave_sin[i];
				toneData.send_user_wave(j   ,i, (int)(toneData.WaveData())[j][i]);
			}
		}
		for(int i = 0; i < 4;i++) {
			wg[i].strokeLine(0, 31*scaleY, 256*scaleX, 31*scaleY);
		}

//---1-----------------------------------------------------------------
		for(int i = 0;i< 4;i++) {
			int j = i;
		canvas[j].addEventHandler(MouseEvent.MOUSE_PRESSED,e ->{

			int x = (int) (e.getX()  /scaleX);
			int y = (int) (e.getY()/scaleY);
			if(x > 255)
				x = 255;
			if(y>WAVE_MAX*2)
				y = WAVE_MAX*2;
			if(y < 0)
				y = 0;
			preX = x ;
			preY = y;

			int val = (int)(toneData.WaveData())[j][x]+WAVE_MAX;

			if(val != y) {
				wg[j].clearRect(x*scaleX, val*scaleY, scaleX, scaleY);
			}
			wg[j].fillRect(x*scaleX,y*scaleY,scaleX,scaleY);
			y = y - WAVE_MAX;
			(toneData.WaveData())[j][x] = y;
			toneData.send_user_wave(j, x, y);
			//toneData.send_user_wave(1, x, y);
		});

		canvas[j].addEventHandler(MouseEvent.MOUSE_DRAGGED, e ->{

			int x = (int)(e.getX()/scaleX);
			int y = (int)(e.getY()/ scaleY);
			if(x > 255)
				x = 255;
			if(y>WAVE_MAX * 2)
				y = WAVE_MAX * 2;
			if( y < 0)
				y = 0;

			if(preX == x) {
				wg[j].clearRect(preX*scaleX,preY*scaleY,scaleX, scaleY);

			}else {
				wg[j].clearRect(x*scaleX, ((toneData.WaveData())[j][x]+WAVE_MAX)*scaleY, scaleX,scaleY);
			}

			wg[j].fillRect(x*scaleX,y*scaleY,scaleX,scaleY);
			preX = x;
			preY = y;
			y = y - WAVE_MAX;
			(toneData.WaveData())[j][x] = y;
			toneData.send_user_wave(j,x, y);

			wg[j].strokeLine(0, ofsY*scaleY, 256*scaleX, ofsY*scaleY);
		});
		}

//-------------------------------------------------------------------
		drawLine();
	}










	@FXML
	void onCloseAction(ActionEvent event) {
		Scene scene = ((Node) event.getSource()).getScene();
		Window window = scene.getWindow();
		window.hide();
	//	Platform.exit();
	}


	void line(GraphicsContext g ,int sx,int sy, int ex, int ey){
		double scale = 0.5;
		//Graphics g = super.getGraphics();
		g.strokeLine((int)(sx*scale)+25, sy+94, (int)(ex*scale)+25, ey+94);
	}





	private void drawLine(){
		int x,y;
		//Color col = Color.DARKGRAY;
		for(int i = 0; i < 4;i++) {
			wg[i].clearRect(0, 0, 256*scaleX, 256*scaleY);	
		}


		for(int j = 0;j < 4;j++) {
			for(int i = 0 ; i < 256;i++) {
				x = i;
				y = (int)(toneData.WaveData())[j][i]+(int)ofsY;
				wg[j].fillRect(x*scaleX,y*scaleY,scaleX,scaleY);
			}
			
		}
		for(int j = 0; j < 4;j++) {
			for(int i = 0 ; i < 256;i++) {
				x = i;
				y = (int)(toneData.WaveData())[j][i]+(int)ofsY;
				wg[j].fillRect(x*scaleX,y*scaleY,scaleX,scaleY);

			}
		}
		for(int j = 0;j < 4;j++) {
		wg[j].strokeLine(0, ofsY*scaleY, 256*scaleX, ofsY*scaleY);
		}
	}

	@Override
	public void update(EventType<MyDataEvent> e, eventSource source, int ch, int op, int val) {
		// TODO 自動生成されたメソッド・スタブ

	}
//	private void 	set_wave(int source[]){
//		for(int i = 0; i < 64;i++) {
//			(toneData.WaveData())[i] = source[i];
//			toneData.send_user_wave(i, (int)(toneData.WaveData())[i]);
//		}
//		drawLine();
//	}
	@FXML void wave_clear() {
		wg[operator].clearRect(0, 0, 300, 300);



		for(int i = 0;i < 256;i++) {

				(toneData.WaveData())[operator][i] = 0;
				toneData.send_user_wave(operator,i, (int)(toneData.WaveData())[operator][i]);

		}
		wg[operator].strokeLine(0, ofsY*scaleY, 256*scaleX, ofsY*scaleY);

	}
	void set_wave(int[] wave) {
		wg[operator].clearRect(operator, 0, 300, 300);

		for(int i = 0;i < 256;i++) {
			double d;
			d = wave[i];
			if(quarterSel.isSelected() == true) {
				d = d / 4;

			}
			if(setMinus.isSelected()== true) {
				d = -d;
			}
			
	
			(toneData.WaveData())[operator][i] += d;
			toneData.send_user_wave(operator,i, (int)(toneData.WaveData())[operator][i]);
			wg[operator].fillRect(i*scaleX,((int)(toneData.WaveData())[operator][i]+ofsY)*scaleY,scaleX,scaleY);

		}
		wg[operator].strokeLine(0, ofsY*scaleY, 256*scaleX, ofsY*scaleY);
		
	}


	@FXML void set_sin() {
		set_wave(wave_sin);
	}


	@FXML void set_sin_1_3() {
		set_wave(wave_sin_1_3);

	}

	@FXML void set_sin_1_3_5() {
		set_wave(wave_sin_1_3_5);

	}

	@FXML void set_rand() {
		set_wave(wave_rand);
	}

	@FXML void set_saw() {
		set_wave(wave_saw);
	}

	@FXML void set_tri() {
		set_wave(wave_tri);
	}
	@FXML void set_rect() {
		set_wave(wave_rect);
	}

	@FXML void write_data() {
		StringBuilder buf = new StringBuilder();

//		buf.append(String.format("%3d,", SliderAtk.getValue()  ));
//		buf.append(String.format("%3d,", SliderDec.getValue()  ));
//		buf.append(String.format("%3d,", SliderSL.getValue()  ));
//		buf.append(String.format("%3d,", SliderSR.getValue()  ));
//		buf.append(String.format("%3d,", SliderRR.getValue()  ));
//		buf.append(System.getProperty("line.separator"));



		for(int i = 0;i < 256;i ++) {
				buf.append(String.format("%3d,", (int)(toneData.WaveData())[operator][i]));
		}
		waveText.setText(new String(buf));
		//System.out.println(buf);
	}


	@FXML void read_data() {
		wg[operator].clearRect(0, 0, 300, 300);
		String str = waveText.getText().replaceAll("\n| |\\(|\\)", "");
		String buf[] = str.split(",");

//		SliderAtk.setValue(Integer.parseInt(buf[0]));
//		midiDev.send_command(1,0,0,Integer.parseInt(buf[0]));
//
//		SliderDec.setValue(Integer.parseInt(buf[1]));
//		midiDev.send_command(1,0,1,Integer.parseInt(buf[1]));
//
//		SliderSL.setValue(Integer.parseInt(buf[2]));
//		midiDev.send_command(1,0,3,Integer.parseInt(buf[2]));
//
//		SliderSR.setValue(Integer.parseInt(buf[3]));
//		midiDev.send_command(1,0,2,Integer.parseInt(buf[3]));
//
//		SliderRR.setValue(Integer.parseInt(buf[4]));
//		midiDev.send_command(1,0,4,Integer.parseInt(buf[4]));


		if( buf.length == 256) {

			for(int i = 0;i<256;i++) {
				//System.out.println(Integer.parseInt(buf[i]));
				(toneData.WaveData())[operator][i] = Integer.parseInt(buf[i]);
				toneData.send_user_wave(operator,i, (int)(toneData.WaveData())[operator][i]);
			}
			drawLine();
		}
	}

@FXML	void selectOperator1(){
		operator = 0;
	}
@FXML	void selectOperator1_2() {
		operator = 2;
}

@FXML	void selectOperator2() {
		operator = 1;
	}

@FXML	void selectOperator2_2() {
		operator = 3;
}




}