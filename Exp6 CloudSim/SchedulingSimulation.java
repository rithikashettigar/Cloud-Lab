package lab;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * Cloud scenario: 1 datacenter, 2 hosts, 3 VMs of different speeds and
 * 10 cloudlets of different lengths.
 *
 * Usage: java lab.SchedulingSimulation FCFS   (CloudSim default broker)
 *        java lab.SchedulingSimulation SJF    (custom SJF broker)
 */
public class SchedulingSimulation {

	private static final int[] VM_MIPS = { 1000, 500, 250 };
	private static final long[] CLOUDLET_LENGTHS = { 40000, 5000, 25000, 2000, 30000, 8000, 15000, 1000, 20000, 10000 };

	public static void main(String[] args) {
		String policy = args.length > 0 ? args[0].toUpperCase() : "SJF";
		Log.printLine("Starting SchedulingSimulation with policy: " + policy);

		try {
			// Step 1: initialise the CloudSim library
			CloudSim.init(1, Calendar.getInstance(), false);

			// Step 2: create the datacenter
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Step 3: create the broker (default FCFS or custom SJF)
			DatacenterBroker broker = policy.equals("FCFS") ? new DatacenterBroker("Broker")
					: new SJFDatacenterBroker("Broker");
			int brokerId = broker.getId();

			// Step 4: create the VMs
			List<Vm> vmList = new ArrayList<Vm>();
			for (int i = 0; i < VM_MIPS.length; i++) {
				vmList.add(new Vm(i, brokerId, VM_MIPS[i], 1, 512, 1000, 10000, "Xen",
						new CloudletSchedulerSpaceShared()));
			}
			broker.submitVmList(vmList);

			// Step 5: create the cloudlets
			List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
			UtilizationModel full = new UtilizationModelFull();
			for (int i = 0; i < CLOUDLET_LENGTHS.length; i++) {
				Cloudlet c = new Cloudlet(i, CLOUDLET_LENGTHS[i], 1, 300, 300, full, full, full);
				c.setUserId(brokerId);
				cloudletList.add(c);
			}
			broker.submitCloudletList(cloudletList);

			// Step 6: start the simulation
			CloudSim.startSimulation();
			List<Cloudlet> result = broker.getCloudletReceivedList();
			CloudSim.stopSimulation();

			printCloudletList(result, policy);
			Log.printLine("SchedulingSimulation (" + policy + ") finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name) throws Exception {
		List<Host> hostList = new ArrayList<Host>();
		for (int h = 0; h < 2; h++) {
			List<Pe> peList = new ArrayList<Pe>();
			for (int p = 0; p < 2; p++) {
				peList.add(new Pe(p, new PeProvisionerSimple(1000)));
			}
			hostList.add(new Host(h, new RamProvisionerSimple(4096), new BwProvisionerSimple(10000), 1000000,
					peList, new VmSchedulerTimeShared(peList)));
		}

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics("x86", "Linux", "Xen", hostList,
				10.0, 3.0, 0.05, 0.001, 0.0);
		return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList),
				new LinkedList<Storage>(), 0);
	}

	private static void printCloudletList(List<Cloudlet> list, String policy) {
		DecimalFormat dft = new DecimalFormat("###0.00");
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT (" + policy + ") ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "VM ID" + indent + "Length" + indent
				+ "Start Time" + indent + "Finish Time" + indent + "Turnaround");

		double totalTurnaround = 0, totalWaiting = 0, makespan = 0;
		for (Cloudlet c : list) {
			String status = c.getCloudletStatus() == Cloudlet.SUCCESS ? "SUCCESS" : "FAILED";
			double turnaround = c.getFinishTime();
			totalTurnaround += turnaround;
			totalWaiting += c.getExecStartTime();
			makespan = Math.max(makespan, c.getFinishTime());
			Log.printLine(String.format("%11d%s%-6s%s%5d%s%6d%s%10s%s%11s%s%10s", c.getCloudletId(), indent,
					status, indent, c.getVmId(), indent, c.getCloudletLength(), indent,
					dft.format(c.getExecStartTime()), indent, dft.format(c.getFinishTime()), indent,
					dft.format(turnaround)));
		}
		Log.printLine();
		Log.printLine("Policy                : " + policy);
		Log.printLine("Makespan              : " + dft.format(makespan));
		Log.printLine("Average waiting time  : " + dft.format(totalWaiting / list.size()));
		Log.printLine("Average turnaround    : " + dft.format(totalTurnaround / list.size()));
	}
}
