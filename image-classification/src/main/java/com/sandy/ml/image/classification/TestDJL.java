package com.sandy.ml.image.classification;

import ai.djl.Device;
import ai.djl.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDJL {
    private static final Logger logger = LoggerFactory.getLogger(TestDJL.class);

    public static void main(String[] args) {
        try {
            // Print system information
            logger.info("JVM Architecture: {}", System.getProperty("os.arch"));
            logger.info("OS Name: {}", System.getProperty("os.name"));
            logger.info("OS Version: {}", System.getProperty("os.version"));
            logger.info("Java Version: {}", System.getProperty("java.version"));

            // Explicitly enable MPS
            System.setProperty("PYTORCH_MPS", "true");
            System.setProperty("PYTORCH_MPS_FALLBACK", "0");

            // Print environment variables
            logger.info("MPS Environment Variable: {}", System.getenv("PYTORCH_MPS"));
            logger.info("MPS Fallback Property: {}", System.getProperty("PYTORCH_MPS_FALLBACK"));

            // Load PyTorch engine
            Engine engine = Engine.getEngine("PyTorch");
            logger.info("Engine loaded: {}", engine.getEngineName());
            logger.info("PyTorch Version: {}", engine.getVersion());

            // Try explicitly creating MPS device
            try {
                Device mpsDevice = Device.gpu(); // Attempts to create MPS device
                logger.info("MPS Device Created: {}", mpsDevice.toString());
            } catch (Exception e) {
                logger.error("Failed to create MPS device", e);
            }

            // Get available devices
            Device[] devices = engine.getDevices();
            logger.info("Available Devices:");
            for (Device device : devices) {
                logger.info("Device: {}", device.toString());
            }
        } catch (Exception e) {
            logger.error("Error initializing PyTorch engine", e);
        }
    }
}