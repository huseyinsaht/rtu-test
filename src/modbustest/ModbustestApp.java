package modbustest;

import modbustest.device.modbus.rtu.Battery;
import modbustest.util.Log;

public class ModbustestApp {

	public static void main(String[] args) throws Exception {
		try {
			tryBridge("ttySC0");
		} catch (Exception e) {
			Log.exception(e);
		}
	}

	private static void tryBridge(String bridgeName) {
		var task = new Runnable() {

			@Override
			public void run() {
				var startTime = System.currentTimeMillis();

				var endTime = startTime + 300 * 1000;
				while (System.currentTimeMillis() < endTime) {
					new Battery("/dev/ttySC0", 1).run();
					new Battery("/dev/ttySC0", 2).run();
				}
			}
		};
		new Thread(task).start();
	}
}