package application;
import javax.sound.midi.MidiMessage;



public class MyStreamReciever implements javax.sound.midi.Receiver{
	ConectMidi stm;
	
	public MyStreamReciever(ConectMidi stm32){
	stm = stm32;	

	}


	@Override
	public void close() {

	}

	public void send(MidiMessage arg0, long arg1) {
		stm.sendMidimessage(arg0);
	
	}
}
