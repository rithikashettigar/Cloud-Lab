# Experiment 3 - Create an EC2 instance in AWS

## Aim
To create an EC2 instance in Amazon Web Services and connect to it using an SSH key pair.

## Procedure
1. Log in to the AWS Management Console and open **EC2** from Services.
2. Click **Launch instance** and give the instance a name.
3. Select the AMI (Amazon Linux 2023).
4. Select the free-tier eligible instance type `t3.micro`.
5. Create a new key pair (RSA, `.pem`) and download it.
6. Keep default network settings (SSH allowed) and default 8 GiB gp3 storage.
7. Click **Launch instance**; the instance is created.
8. Select the instance, click **Connect** and open the **SSH client** tab.
9. Restrict the key file permissions (Windows equivalent of `chmod 400`) and connect with `ssh -i key.pem ec2-user@<public-dns>`.
10. Terminate the instance after the experiment to avoid charges.

## Screenshots

**Step 1 - aws console home**

![Step 1 - aws console home](screenshots/step01_aws_console_home.png)

**Step 2 - search EC2 in services**

![Step 2 - search EC2 in services](screenshots/step02_search_EC2_in_services.png)

**Step 3 - EC2 dashboard**

![Step 3 - EC2 dashboard](screenshots/step03_EC2_dashboard.png)

**Step 4 - launch instance name and tags**

![Step 4 - launch instance name and tags](screenshots/step04_launch_instance_name_and_tags.png)

**Step 5 - select AMI Amazon Linux**

![Step 5 - select AMI Amazon Linux](screenshots/step05_select_AMI_Amazon_Linux.png)

**Step 6 - instance type t3 micro free tier**

![Step 6 - instance type t3 micro free tier](screenshots/step06_instance_type_t3_micro_free_tier.png)

**Step 7 - create new key pair RSA pem**

![Step 7 - create new key pair RSA pem](screenshots/step07_create_new_key_pair_RSA_pem.png)

**Step 8 - key pair created and selected**

![Step 8 - key pair created and selected](screenshots/step08_key_pair_created_and_selected.png)

**Step 9 - network settings allow SSH**

![Step 9 - network settings allow SSH](screenshots/step09_network_settings_allow_SSH.png)

**Step 10 - configure storage 8GiB gp3 default**

![Step 10 - configure storage 8GiB gp3 default](screenshots/step10_configure_storage_8GiB_gp3_default.png)

**Step 11 - launch instance success**

![Step 11 - launch instance success](screenshots/step11_launch_instance_success.png)

**Step 12 - instances list running**

![Step 12 - instances list running](screenshots/step12_instances_list_running.png)

**Step 13 - select instance details**

![Step 13 - select instance details](screenshots/step13_select_instance_details.png)

**Step 14 - connect to instance page**

![Step 14 - connect to instance page](screenshots/step14_connect_to_instance_page.png)

**Step 15 - SSH client tab instructions**

![Step 15 - SSH client tab instructions](screenshots/step15_SSH_client_tab_instructions.png)

**Step 16 - key permissions chmod400 equivalent**

![Step 16 - key permissions chmod400 equivalent](screenshots/step16_key_permissions_chmod400_equivalent.png)

**Step 17 - ssh connected to EC2 instance**

![Step 17 - ssh connected to EC2 instance](screenshots/step17_ssh_connected_to_EC2_instance.png)

**Step 18 - cleanup instance terminated**

![Step 18 - cleanup instance terminated](screenshots/step18_cleanup_instance_terminated.png)

## Result
An EC2 instance was launched and accessed successfully over SSH using the key pair.
