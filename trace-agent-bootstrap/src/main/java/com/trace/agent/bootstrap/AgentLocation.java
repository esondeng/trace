package com.trace.agent.bootstrap;

import java.io.File;
import java.util.jar.JarFile;

/**
 * @author dengxiaolin
 * @since 2021/01/11
 */
public class AgentLocation {
    /**
     * java agent动态代理jar路径
     */
    private String jvmAgentJarPath;

    /**
     * tools.jar
     */
    private JarFile toolsJarFile;

    public AgentLocation() {
        try {
            this.jvmAgentJarPath = System.getProperty("agent.jar.path");
            System.out.println("agent.jar.path =  " + jvmAgentJarPath);

            String toolsJarPath = System.getProperty("tools.jar.path");
            System.out.println("tools.jar.path =  " + jvmAgentJarPath);

            File toolsFile = new File(toolsJarPath);
            this.toolsJarFile = new JarFile(toolsFile);
        }
        catch (Exception e) {
            throw new RuntimeException("找不到agent.jar", e);
        }
    }

    public String getJvmAgentJarPath() {
        return jvmAgentJarPath;
    }

    public void setJvmAgentJarPath(String jvmAgentJarPath) {
        this.jvmAgentJarPath = jvmAgentJarPath;
    }

    public JarFile getToolsJarFile() {
        return toolsJarFile;
    }

    public void setToolsJarFile(JarFile toolsJarFile) {
        this.toolsJarFile = toolsJarFile;
    }
}
