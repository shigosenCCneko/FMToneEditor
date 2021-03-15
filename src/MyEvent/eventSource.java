package MyEvent;

public enum  eventSource {
	SLIDER1(0)  ,SLIDER2(1),SLIDER3(2),SLIDER4(3),SLIDER5(4),SLIDER6(5),SLIDER7(6),
	FeedBK(7),Wave(8),Morf(9),Wave2(10),MorphOnce(11),
	Ksl(12),Ksr(13),
	FeedBK2(14),BO(15),Connect(16),DT(17),Lfo(18),Dam(19),Dvb(20),
	EAM(21),EVB(22),XOF(23),XVB(24),ToneChange(25),Invert(26);
	
	private final int number;
	
	eventSource(int number){
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}

}
