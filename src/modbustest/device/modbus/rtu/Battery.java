package modbustest.device.modbus.rtu;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.google.common.base.Stopwatch;

import modbustest.util.Log;

public class Battery extends ModbusRtuDevice implements Runnable {
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
			Log.info("My thread " + Thread.currentThread().getName() + Log.HIGH_INTENSITY + Log.RED
					+ " execution time: " + duration + " ms" + Log.ANSI_RESET);
		}
	}

	private void callModbusRegisters(ModbusSerialMaster master, int unitId) {
		try {
			Log.info(Log.HIGH_INTENSITY + Log.GREEN + "Trying Unit-ID [" + unitId + "]" + Log.ANSI_RESET);
			var reg = new SimpleRegister(0x5f01);
			var beat = new SimpleRegister(1200);
			long totalTimeDuration = writeExecute(master, reg, unitId, 18, 1);
			totalTimeDuration += writeExecute(master, beat, unitId, 3064, 2);
			totalTimeDuration += execute(master, unitId, 0, 7);
			totalTimeDuration += execute(master, unitId, 2176, 7);
			totalTimeDuration += execute(master, unitId, 2762, 12);
			totalTimeDuration += execute(master, unitId, 17, 5);
			totalTimeDuration += execute(master, unitId, 161, 1);
			totalTimeDuration += execute(master, unitId, 2628, 101);
			totalTimeDuration += execute(master, unitId, 2730, 46);
			totalTimeDuration += execute(master, unitId, 2785, 30);
			totalTimeDuration += execute(master, unitId, 3064, 35);
			totalTimeDuration += execute(master, unitId, 3080, 7);
			totalTimeDuration += execute(master, unitId, 3072, 47);
			totalTimeDuration += execute(master, unitId, 3072 + 48, 114);
			for (int i = 0; i < 9; i++) {
				totalTimeDuration += execute(master, unitId, 3072 + 128 + i * 20, 19);
			}
			Log.info(Log.HIGH_INTENSITY + Log.WHITE + "Total Time Duration[" + totalTimeDuration + "]"
					+ Log.ANSI_RESET);
			// printWriteLog(unitId, 1, writeExecute(master, registers, reg, unitId, 1,
			// 52));
			// printLog(unitId, 1, execute(master, registers, unitId, 1, 52));
			// printLog(unitId, 101, execute(master, registers, unitId, 101, 70));
			// printLog(unitId, 201, execute(master, registers, unitId, 201, 96));
			// printLog(unitId, 301, execute(master, registers, unitId, 301, 36));
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}

	private void printLog(int unitId, int startAddress, InputRegister[] registers, Long executionDuration) {
		Log.info(Log.HIGH_INTENSITY + Log.CYAN//
				+ "ReadInputRegisters" //
				+ Log.ANSI_RESET//
				+ "[" + unitId + ":" + startAddress + "/0x" + Integer.toHexString(startAddress) + "]: " //
				+ Log.HIGH_INTENSITY + Log.GREEN //
				+ executionDuration + "ms " //
				+ Log.ANSI_RESET//
				+ Arrays.stream(registers) //
						.map(r -> String.format("%4s", Integer.toHexString(r.getValue())).replace(' ', '0')) //
						.collect(Collectors.joining(" ")));
	}

	private void printWriteLog(int unitId, int startAddress, InputRegister[] registers, Long executionDuration) {
		Log.info(Log.HIGH_INTENSITY + Log.YELLOW//
				+ "WriteRegisters" //
				+ Log.ANSI_RESET//
				+ "[" + unitId + ":" + startAddress + "/0x" + Integer.toHexString(startAddress) + "]: " //
				+ Log.HIGH_INTENSITY + Log.GREEN //
				+ executionDuration + "ms " //
				+ Log.ANSI_RESET//
				+ Arrays.stream(registers) //
						.map(r -> String.format("%4s", Integer.toHexString(r.getValue())).replace(' ', '0')) //
						.collect(Collectors.joining(" ")));
	}

	public synchronized long execute(ModbusSerialMaster master, int unitId, int startAdress, int size) {
		this.stopwatch.reset();
		this.stopwatch.start();
		InputRegister[] registers = null;
		Long lastExecuteDuration;
		try {
			registers = master.readMultipleRegisters(unitId, startAdress, size);
		} catch (Exception e) {
			Log.error(e.getMessage());
		} finally {
			lastExecuteDuration = this.stopwatch.elapsed(TimeUnit.MILLISECONDS);
		}
		this.printLog(unitId, startAdress, registers, lastExecuteDuration);
		return lastExecuteDuration;
	}

	public synchronized long writeExecute(ModbusSerialMaster master, Register reg, int unitId, int startAdress,
			int size) {
		this.stopwatch.reset();
		this.stopwatch.start();
		InputRegister[] registers = null;
		Long lastExecuteDuration;
		try {
			master.writeSingleRegister(unitId, startAdress, reg);
			registers = master.readMultipleRegisters(unitId, startAdress, size);
		} catch (Exception e) {
			Log.error(e.getMessage());
		} finally {
			lastExecuteDuration = this.stopwatch.elapsed(TimeUnit.MILLISECONDS);
		}
		this.printWriteLog(unitId, startAdress, registers, lastExecuteDuration);
		return lastExecuteDuration;
	}
}
