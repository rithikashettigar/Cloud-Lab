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
