---
title: Local cluster with Vagrant
order: 40
---

This page will get you started with a local Accio cluster running inside a virtual machine.
After following these instructions, you will be able to quickly test your first commands.
It has the advantage of being cross-platform, as everything happens inside a virtual machine.
While this setup is great to quickly test main Accio features, it is not intended for production usage.

## 1. Spin up a virtual machine
First, you need to install [Virtualbox](https://www.virtualbox.org/) and [Vagrant](https://www.vagrantup.com/) on your computer.
Vagrant is a very helpful tool to create portable and reproducible environments.
We configured it to use Virtualbox to manage virtual machines.
Both are available on Linux, Mac OS and Windows.

Then, you need to download source code of [the latest Accio release](https://github.com/privamov/accio/releases/latest).
```bash
curl -L -O https://github.com/privamov/accio/archive/v0.7.0.tar.gz
tar xzf v0.7.0.tar.gz && rm v0.7.0.tar.gz
cd accio-0.6.0/
vagrant up
```

Decompress the archive file, move to the newly created directory and run a `vagrant up` to start the virtual machine.
Alternatively, if you like to live on the edge, you can [install Git](https://git-scm.com/downloads) and use it to download the source code of the development version.

```bash
git clone git@github.com:privamov/accio.git
cd accio/
vagrant up
```

The first launch may take some time, as Vagrant has to download a base OS image and provision it.
All of the needed dependencies are downloaded, and various Accio components are built from source and then started.
Once the machine boots, you will be able to access the following web interfaces:

  * Gateway: [http://192.168.50.4](http://192.168.50.4)
  * Gateway admin: [http://192.168.50.4:8880](http://192.168.50.4:8880)
  * Agent admin: [http://192.168.50.4:9990](http://192.168.50.4:9990)

When using Vagrant, you may notice a warning about guest additions not being installed.
This warning is not critical, but if you want to get a rid of this error, you can install a plugin that will take care of this:

```bash
vagrant plugin install vagrant-vbguest
```

## 2. Login to your virtual machine
You can start an SSH session on the previously created virtual machine with the `vagrant ssh` command.
You will be logged in as the `ubuntu` user.
Local sources of Accio as mounted under the `/vagrant` directory in the virtual machine.

Once logged onto the virtual machine, you have access to the Accio client.
Type `accio` and look at the built-in help.
Note that the client is only configured to reach the local cluster (named `devcluster`), not external clusters.

## 3. Shut down the virtual machine
When you are done working with Accio, you can stop the virtual machine with `vagrant halt`.
You can then start it again in the same state with `vagrant up`.
If you want to irreversibly destroy your local cluster, you can user the `vagrant destroy` command which will delete the virtual machine.
