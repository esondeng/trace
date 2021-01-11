package com.trace.agent.bootstrap;

import java.lang.instrument.Instrumentation;
import java.util.List;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author dengxiaolin
 * @since 2021/01/11
 */
public class BootstrapAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        AgentLocation agentLocation = new AgentLocation();
        inst.appendToBootstrapClassLoaderSearch(agentLocation.getToolsJarFile());

        // 获取当前系统中所有 运行中的 虚拟机
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            System.out.println("vm name = " + vmd.displayName());

            try {
                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                virtualMachine.loadAgent(agentLocation.getJvmAgentJarPath());
                virtualMachine.detach();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
