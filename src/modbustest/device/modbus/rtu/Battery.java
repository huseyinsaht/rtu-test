package modbustest.device.modbus.rtu;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.google.common.base.Stopwatch;

import modbustest.util.Log;

public class Battery extends ModbusRtuDevice implements Runnable {
	private static final long DEFAULT_EXECUTION_DURATION = 300;
	private long lastExecuteDuration = DEFAULT_EXECUTION_DURATION; // initialize to some default

	private final int id;

	private final Stopwatch stopwatch = Stopwatch.createUnstarted();

	public Battery(String systemportname, int id) {
		super(systemportname, id);
		this.id = id;
	}

	@Override
	public String getName() {
		return "Fenecon Industrial Battery";
	}

	@Override
	public void run() {
		synchronized (Battery.class) {
			var startTime = System.currentTimeMillis();
			ModbusSerialMaster master = null;
			try {
				master = getModbusSerialMaster();
				this.callModbusRegisters(master, this.id);
			} catch (Exception e) {
				Log.error(e.getMessage());
			} finally {
				if (master != null) {
					master.disconnect();
				}
			}
			long duration = System.currentTimeMillis() - startTime;
			Log.info("My thread " + Thread.currentThread().getName() + " execution time: " + duration + " ms");
		}
	}

	private void callModbusRegisters(ModbusSerialMaster master, int unitId) {
		try {
			Log.info(Log.HIGH_INTENSITY + Log.GREEN + "Trying Unit-ID [" + unitId + "]" + Log.ANSI_RESET);

			var registers = master.readInputRegisters(unitId, 1, 21);

			printLog(unitId, 101, execute(master, registers, unitId, 101, 70));

			printLog(unitId, 201, execute(master, registers, unitId, 201, 96));

			printLog(unitId, 301, execute(master, registers, unitId, 301, 36));
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}

	private void printLog(int unitId, int startAddress, InputRegister[] registers) {
		Log.info("ReadInputRegisters" //
				+ "[" + unitId + ":" + startAddress + "/0x" + Integer.toHexString(startAddress) + "]: " //
				+ this.getExecuteDuration() + "ms " //
				+ Arrays.stream(registers) //
						.map(r -> String.format("%4s", Integer.toHexString(r.getValue())).replace(' ', '0')) //
						.collect(Collectors.joining(" ")));
	}

	public InputRegister[] execute(ModbusSerialMaster master, InputRegister[] registers, int unitId, int startAdress,
			int size) {
		this.stopwatch.reset();
		this.stopwatch.start();
		try {
			registers = master.readInputRegisters(unitId, startAdress, size);
		} catch (Exception e) {
			Log.error(e.getMessage());
		} finally {
			this.lastExecuteDuration = this.stopwatch.elapsed(TimeUnit.MILLISECONDS);
		}
		return registers;
	}

	public long getExecuteDuration() {
		return this.lastExecuteDuration;
	}
}
