# Experiment 1 - Install VirtualBox and a Linux guest OS

## Aim
To install Oracle VirtualBox on Windows and create a virtual machine running a Linux flavour (Ubuntu 22.04 LTS Desktop).

## Procedure
1. Download the VirtualBox installer (VirtualBox-7.2.20-Win.exe) from virtualbox.org.
2. Run the installer and click **Next** on the welcome screen.
3. Keep the default features in Custom Setup and click **Next**.
4. Click **Yes** for the network-interface warning and **Install** on the Ready to Install screen.
5. Wait for the installation to complete and click **Finish**; VirtualBox Manager opens.
6. Create a VM named `Ubuntu-22.04-Rithika` (Ubuntu 64-bit, 3 GB RAM, 2 CPUs, 25 GB VDI disk).
7. Attach the Ubuntu 22.04.5 Desktop ISO and start the installation (`VBoxManage unattended install`).
8. Ubuntu installs inside the VM and boots to the desktop.
9. Open a terminal in the VM and confirm the Linux flavour with `lsb_release -a`.

## Result
VirtualBox was installed successfully and an Ubuntu 22.04 LTS virtual machine was created and booted on top of Windows.
