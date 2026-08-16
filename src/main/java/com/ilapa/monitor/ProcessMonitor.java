package com.ilapa.monitor;

import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Optional;

public class ProcessMonitor {

    private final OperatingSystem os;
    private int targetPid = -1;

    public ProcessMonitor(OperatingSystem os) {
        this.os = os;
    }

    public List<OSProcess> listRunningProcesses() {
        return os.getProcesses();
    }

    public boolean lockOntoProcess(String applicationName) {
        Optional<OSProcess> found = os.getProcesses().stream()
                .filter(p -> p.getName().equalsIgnoreCase(applicationName))
                .findFirst();

        if (found.isPresent()) {
            targetPid = found.get().getProcessID();
            return true;
        }
        return false;
    }

    public OSProcess refresh() {
        if (targetPid == -1) {
            return null;
        }
        // returns null itself if the process already exited
        return os.getProcess(targetPid);
    }

    public int getTargetPid() {
        return targetPid;
    }
}
