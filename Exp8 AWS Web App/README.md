# Experiment 8 - Deploy a web application on an EC2 instance

## Aim
To deploy a web application on an EC2 instance on AWS.

## Procedure
1. Open EC2 and click **Launch instance**; name it `web-server`.
2. Select Amazon Linux and instance type `t3.micro`.
3. Create a key pair and allow SSH, HTTP and HTTPS traffic in Network settings.
4. Keep storage and advanced details default and launch the instance.
5. Select the instance > **Connect** > **EC2 Instance Connect** > Connect.
6. Run: `sudo su -`, `yum update -y`, `yum install -y httpd`.
7. Download the template: `wget https://templatemo.com/download/templatemo_596_electric_xtra`, unzip it and `mv * /var/www/html/`.
8. Run `systemctl enable httpd` and `systemctl start httpd`.
9. Copy the instance's public IPv4 address and open it in the browser.
10. Terminate the instance after the experiment.

## Result
The website was deployed on Apache (httpd) on the EC2 instance and was reachable on its public IP address.
