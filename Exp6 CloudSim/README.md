# Experiment 6 - CloudSim simulation with a custom scheduling algorithm

## Aim
To simulate a cloud scenario using CloudSim and run a scheduling algorithm that is not present in CloudSim.

## Procedure
1. Download CloudSim 3.0.3 and extract it.
2. Run the bundled example `CloudSimExample1` to verify the setup.
3. Write a custom broker `SJFDatacenterBroker` (Shortest-Job-First + Earliest-Finish-Time VM selection) - CloudSim only provides FCFS/round-robin submission.
4. Write `SchedulingSimulation`: initialise CloudSim, create a datacenter (2 hosts), a broker, 3 VMs (1000/500/250 MIPS) and 10 cloudlets.
5. Compile with `javac -cp cloudsim-3.0.3.jar`.
6. Run with the default FCFS broker and with the custom SJF broker and compare makespan / turnaround.

## Source code

**`src/lab/SJFDatacenterBroker.java`**

```java
package lab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

/**
 * Custom scheduling policy that is NOT part of CloudSim:
 * Shortest-Job-First (SJF) ordering + Earliest-Finish-Time VM selection.
 *
 * CloudSim's default DatacenterBroker sends cloudlets in submission order
 * (FCFS) and spreads them over VMs round-robin, ignoring cloudlet length
 * and VM speed. This broker:
 *   1. sorts all waiting cloudlets by length (shortest first), then
 *   2. assigns each cloudlet to the VM on which it would finish earliest,
 *      considering the VM's MIPS and the work already queued on it.
 */
public class SJFDatacenterBroker extends DatacenterBroker {

	public SJFDatacenterBroker(String name) throws Exception {
		super(name);
	}

	@Override
	protected void submitCloudlets() {
		List<Cloudlet> waiting = new ArrayList<Cloudlet>(getCloudletList());
		sortByLength(waiting);

		List<Vm> vms = getVmsCreatedList();
		double[] vmReadyTime = new double[vms.size()];

		Log.printLine(CloudSim.clock() + ": " + getName() + ": [SJF] Scheduling " + waiting.size()
				+ " cloudlets on " + vms.size() + " VMs (shortest job first, earliest finish time)");

		for (Cloudlet cloudlet : waiting) {
			int best = 0;
			double bestFinish = Double.MAX_VALUE;
			for (int i = 0; i < vms.size(); i++) {
				Vm vm = vms.get(i);
				double execTime = cloudlet.getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
				double finish = vmReadyTime[i] + execTime;
				if (finish < bestFinish) {
					bestFinish = finish;
					best = i;
				}
			}
			vmReadyTime[best] = bestFinish;
			Vm vm = vms.get(best);

			Log.printLine(CloudSim.clock() + ": " + getName() + ": [SJF] Sending cloudlet "
					+ cloudlet.getCloudletId() + " (length " + cloudlet.getCloudletLength() + " MI) to VM #"
					+ vm.getId() + " (" + (int) vm.getMips() + " MIPS), expected finish "
					+ String.format("%.2f", bestFinish));
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			getCloudletSubmittedList().add(cloudlet);
		}

		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			getCloudletList().remove(cloudlet);
		}
	}

	private static void sortByLength(List<Cloudlet> list) {
		java.util.Collections.sort(list, new Comparator<Cloudlet>() {
			public int compare(Cloudlet a, Cloudlet b) {
				return Long.compare(a.getCloudletLength(), b.getCloudletLength());
			}
		});
	}
}
```

**`src/lab/SchedulingSimulation.java`**

```java
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
```
## Result
The custom SJF scheduler reduced the makespan from 212.1 to 121.1 and the average turnaround time from 80.3 to 40.3 compared with CloudSim's default FCFS broker.
