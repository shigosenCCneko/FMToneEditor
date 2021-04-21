package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import DataClass.FMDEVICE;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuFieldController {


	@FXML MenuBar	menuBar;
	@FXML MenuItem  loadToneSet;
	@FXML MenuItem  saveToneSet;
//	@FXML MenuItem  deviceToneSet;
	@FXML MenuItem  exitMenu;

	@FXML MenuItem  loadTone;
	@FXML MenuItem  saveTone;
//	@FXML MenuItem  deviceTone;

	@FXML MenuItem  copy12to34;
	@FXML MenuItem  copy12to34clear;
	@FXML MenuItem  copy12to23;
	@FXML MenuItem  copy1to234;
	@FXML MenuItem  viewSoftModulation;
	@FXML MenuItem  reset;

	@FXML MenuItem  monoMode;
	@FXML MenuItem  polyMode;
	@FXML MenuItem  d8polyMode;


	FXMLLoader softModuLoader;
	Parent		softModuRoot;
	Stage	    softModuEditor;
	SoftwareModulationController softModuControll;
	String workDir;
	
	FMDEVICE fmDevice;
	

public MenuFieldController() throws IOException{
	softModuLoader = new FXMLLoader(getClass().getResource("SoftwareModulation.fxml"));

	softModuRoot = (Parent)softModuLoader.load();
	softModuControll = softModuLoader.getController();
	softModuEditor	= new Stage();
	softModuEditor.setScene(new Scene(softModuRoot));
	softModuEditor.setResizable(false);
	softModuEditor.setAlwaysOnTop(true);
	softModuEditor.setTitle("User Wave Editor");
	softModuEditor.setOnCloseRequest((e) -> {
	    e.consume(); //close Event Stop
	});


}

	public void initialize() {
		fmDevice = FMDEVICE.getInstance();
		//fmDevice.addListener(this);

		Properties properties = new Properties();

			try{
				InputStream istream = new FileInputStream("fmToneEditor.properties");
				properties.load(istream);
				workDir =  properties.getProperty("workDir");

				istream.close();
			}catch(IOException e){
				e.printStackTrace();
			}


	}



	@FXML void loadToneSet() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Tone Set");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("SDS", "*.sds"),
		        new FileChooser.ExtensionFilter("ALL", "*.*"));

		File dir = new File(workDir);
		if(dir.exists() == false) {
			dir = new File(System.getProperty("user.home"));
		}
		fileChooser.setInitialDirectory(dir);


        File file = fileChooser.showOpenDialog(null);

		if(file != null) {
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				byte rbuf[] = new byte[fmDevice.getTonesetLen()+ 
				                fmDevice.getWavedataLen() * fmDevice.getUserWaveVal()];
				int len = bis.read(rbuf);
				if(len == fmDevice.getTonesetLen()||len == (fmDevice.getTonesetLen() + 256 * 4)) {
					fmDevice.setToneSet(rbuf);
				}

				bis.close();

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	@FXML void saveToneSet() {
		//System.out.println("write tone set");
		byte wbuf[] = new byte[fmDevice.getTonesetLen()];

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Tone Set");
		fileChooser.getExtensionFilters().addAll(
		        new FileChooser.ExtensionFilter("SDS", "*.sds"));

		File dir = new File(workDir);
		if(dir.exists() == false) {
			dir = new File(System.getProperty("user.home"));
		}
		fileChooser.setInitialDirectory(dir);
		File file = fileChooser.showSaveDialog(null);
		byte sbuf[] = new byte[fmDevice.getUserWaveVal() * fmDevice.getWavedataLen()];
		double sdata[][] = fmDevice.WaveData();
		int cnt = 0;
		if(file != null) {
			fmDevice.getToneSet(wbuf);
			for(int i = 0; i < fmDevice.getUserWaveVal();i++) {
				for(int j = 0; j < fmDevice.getWavedataLen();j++) {
					sbuf[cnt++] = (byte) sdata[i][j];
					
				}
			}
	
			try{

				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(wbuf);
				bos.flush();
				bos.write(sbuf);
			
				bos.flush();
				bos.close();
			}catch(IOException err){
				System.out.println(err);
			}
		}
	}

	@FXML void deviceToneSet() {
//		byte [] buf = new byte[32];
//		int ch = PanelController.getPanelChannel();
//
//		for(int i = 0;i<16;i++){
//			if(ch != i) {
//				toneData.get_tonememory(i, buf);
//				toneData.setOpData(i,  buf);
//				
//				//toneData.setTone(i, buf);
//			}
//		}
//		toneData.get_tonememory(ch, buf);
//		toneData.setTone(ch,  buf);			//Panelの表示のため


	}

	@FXML void exit() {
		   Alert alert = new Alert( AlertType.NONE,"",ButtonType.OK, ButtonType.CANCEL);
		   alert.setTitle("終了");
		   alert.getDialogPane().setContentText( "終了してもよろしいですか？" );

		   Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		   stage.setAlwaysOnTop(true);
		   stage.toFront(); // not sure if necessary



		   ButtonType              button  = alert.showAndWait().orElse( ButtonType.CANCEL );
		 if(button.getButtonData() == ButtonData.OK_DONE) {

			javafx.application.Platform.exit();
		 }
	}

	@FXML void loadTone() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Tone");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("SDS", "*.sd1"),
		        new FileChooser.ExtensionFilter("ALL", "*.*"));

		File dir = new File(workDir);
		if(dir.exists() == false) {
			dir = new File(System.getProperty("user.home"));
		}
		fileChooser.setInitialDirectory(dir);

        File file = fileChooser.showOpenDialog(null);

		if(file != null) {
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				byte rbuf[] = new byte[fmDevice.getDatalen()];
				int len = bis.read(rbuf);

				if(len == fmDevice.getDatalen()){
					fmDevice.setTone(fmDevice.getEditChannel(), rbuf);

				}
				bis.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@FXML void saveTone() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Tone");
		fileChooser.getExtensionFilters().addAll(
		        new FileChooser.ExtensionFilter("SDS", "*.sd1"));

		File dir = new File(workDir);
		if(dir.exists() == false) {
			dir = new File(System.getProperty("user.home"));
		}
		fileChooser.setInitialDirectory(dir);
		File file = fileChooser.showSaveDialog(null);

		if(file != null) {
			
			byte wbuf[] = new byte[fmDevice.getDatalen()];
			fmDevice.getToneData(fmDevice.getEditChannel(), wbuf);
			try{

				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(wbuf);
				bos.flush();
				bos.close();
			}catch(IOException err){
				System.out.println(err);
			}
		}
	}


	@FXML void loadToneFromDevice() {
//		byte [] buf = new byte[32];
//
//		int ch = PanelController.getPanelChannel();
//		toneData.get_tonememory(ch, buf);
//		toneData.setTone(ch,  buf);


	}

	private void copyOp(int source,int target){
		byte buf[] = new byte[fmDevice.getOplen()];
		int ch = fmDevice.getEditChannel();
		fmDevice.getOperator(ch, source, buf);
		fmDevice.setOperator(ch, target, buf);

	}
	private void clearOp(int opno){
		byte source[] = { -1, 0, 0, 0, 0, 0, 0,0,0,0,0,0};
		int ch = fmDevice.getEditChannel();
		fmDevice.setOperator(ch, opno, source);
		
	}



	@FXML void copy12to34() {
		copyOp(0,2);
		copyOp(1,3);
		//toneData.setTone(PanelController.getPanelChannel(),buf);
	}
	@FXML void copy12to34Clear() {

		copyOp(0,2);
		copyOp(1,3);
		clearOp(0);
		clearOp(1);
//		toneData.setTone(PanelController.getPanelChannel(),buf);
	}
	@FXML void copy12to23() {
		copyOp(1,2);
		copyOp(0,1);
		clearOp(0);
		clearOp(3);
//		toneData.setTone(PanelController.getPanelChannel(),buf);

	}
	@FXML void copy1to234() {
		copyOp(0,1);
		copyOp(0,2);
		copyOp(0,3);
//		toneData.setTone(PanelController.getPanelChannel(),buf);
	}
	@FXML void viewSoftwareModulation() {

		softModuEditor.show();

	}
	@FXML void resetYMF825() {
//		toneData.reset();

	}
	@FXML void monoMode() {
//		toneData.monoMode();
	}
	@FXML void polyMode() {
//		toneData.polyMode();
	}
	@FXML void d8polyMode() {
//		toneData.d8polyMode();
	}


}


