package application;

import javax.sound.midi.MidiMessage;

public class MyMidiReceiver implements javax.sound.midi.Receiver {
	ConectMidi stm;
	
	public MyMidiReceiver(ConectMidi stm32){
	stm = stm32;	

	}


	@Override
	public void close() {

	}

	@Override
	public void send(MidiMessage arg0, long arg1) {
		arg0.getMessage();

		//ymf.sys_exmes_recv(buf);
	}

}
