package modbustest.device.modbus.rtu;

import java.util.Optional;

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.util.SerialParameters;

import modbustest.device.Device;

public abstract class ModbusRtuDevice implements Device {

	private final static int TIMEOUT = 500;
	private final static int RETRIES = 1; // default would be 3

	protected final Optional<Integer> unitId;

	private final String systemportname;

	public ModbusRtuDevice(String systemportname, int id) {
		this.systemportname = systemportname;
		this.unitId = Optional.of(id);
	}

	protected ModbusSerialMaster getModbusSerialMaster() throws Exception {
		var params = new SerialParameters();
		params.setPortName(this.systemportname);
		params.setBaudRate(38400);
		params.setDatabits(8);
		params.setParity("None");
		params.setEncoding("rtu");
		var master = new ModbusSerialMaster(params);
		master.setTimeout(TIMEOUT);
		master.setRetries(RETRIES);
		master.connect();
		return master;
	}
}
